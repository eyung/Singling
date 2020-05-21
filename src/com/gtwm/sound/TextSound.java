/*
 *   Copyright 2020
 * 
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.gtwm.sound;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Pattern;

import org.jfugue.*;


/**
 * Turn a stream of text into sound, using the overtone series Letter A = root
 * note B = first overtone, double the frequency, one octave above C = triple
 * the frequency, an octave plus a fifth above the root D = x4 frequency, two
 * octaves up E = x5, two octaves plus a third etc.
 * 
 * Higher notes are normalised down to fit in the octave range specified
 * 
 * @author oliver
 *
 * Turn a stream of text into sound using the overtone series and synset types
 * ie. synset 1 = root, synset 2 = first over tone etc.
 *
 * @author effiam
 */
public class TextSound {

	//static String inputFile;
	static String prefsFile = "usersettings";

	static String instrument;

	static List<String> orderings = new ArrayList<String>();

	static List<SenseMap.Mapping> items;

	// Starting settings
	// NB: If any values are set to exactly zero, they will be unable to
	// change throughout the generation
	//
	// How long to hold each note for
	static double noteLength; // /1 = whole note (semibreve). /0.25 = crotchet
	static double baseNoteLength;

	// How long to wait before playing the next note
	//static double noteGap = 0.0001; // 1 / 32d; // 1/32 = good default, 0 = no
	static double noteGap = 1/32d;

	// gap (chords)
	// How long to pause when a rest (space etc.) is encountered
	static double restLength = 1 / 8d; // 1/8 = good default

	// Lowest note that can be played
	static double baseFrequency; // 128 Hz = Octave below middle C
	static double userBaseFrequency;
	static double frequency;

	// Octave range in which to place notes
	static double octaves;
	static double baseOctaves;

	// Tempo in beats per second
	static double tempo;
	static double baseTempo;

	// Instrument default
	static String baseInstrument;

	// Volume
	static double volume;

	// Attack
	static int attack;
	static int baseAttack = 64;

	// Decay
	static int decay;
	static int baseDecay = 64;

	// Default note operation
	enum noteOperationType { LEXNAMEFREQ, STATICFREQ, MUTE }
	static noteOperationType defaultNoteOperation = noteOperationType.LEXNAMEFREQ;

	// Per key or word operation
	static boolean perChar;
	static boolean perWord;

	// Which letter ordering (defined above) to use, zero indexed
	static int ordering = 1;

	// Initial setting type
	//static Setting setting = Setting.TEMPO;
    //static Setting setting = Setting.NOTE_LENGTH;

	//static EnumSet<Setting> allSettings = EnumSet.allOf(Setting.class);

	// Characters which prompt a change of setting type
	//static String settingChangers = ".";

	// Even characters increase setting values, odd characters decrease.
	// This swaps that behaviour
	//static boolean tempoDirection = false;

	// could use these to change and revert - opening bracket changes,
	// closing changes the same setting in the opposite direction
	//static String containers = "(){}[]<>\"\"";

	// Print out each paragraph as we play (causes a pause each time)
	//static boolean follow = false;
	
	static Set<String> passingWords = new HashSet<String>(Arrays.asList("THE","A","AND","OR","NOT","WITH","THIS","IN","INTO","IS","THAT","THEN","OF","BUT","BY","DID","TO","IT","ALL"));

	static List<Queue.Instruction> instructions = new ArrayList<>();

	// Keeping track of how many categories a word falls under
	static int lexCount = 0;

	enum Setting {
		NOTE_LENGTH(0.01, 8.0), ARPEGGIATE_GAP(0.001, 0.5), REST_LENGTH(0.01, 0.5), BASE_FREQUENCY(16.0, 2048), OCTAVES(
				1.0, 10.0), TEMPO(40, 220), LETTER_ORDERING(0.0,3.0), VOLUME(1.0, 16383), ATTACK(0, 127);
		Setting(double min, double max) {
			if (min == 0) {
				// Don't allow absolute zero as a min otherwise will never
				// recover, i.e. it won't be able to be changed by multiplication
				this.min = 0.0001;
			} else {
				this.min = min;
			}
			this.max = max;
		}

		public double keepInRange(double value) {
			if (value < this.min) {
				this.directionRollingAverage = (this.directionRollingAverage - 1d) / 2d;
				if (this.directionRollingAverage < -0.8) {
					System.out.println(this.toString() + " too low at " + value + ", swapping direction. RA = " + this.directionRollingAverage);
					this.direction = !this.direction;
				}
				double returnValue = this.min + (this.min - value);
				if (returnValue > this.min && returnValue < this.max) {
					return returnValue;
				} else {
					System.out.println("" + this + " return value " + returnValue + " out of range, instead " + ((returnValue % (this.max - this.min)) + this.min));
					return (Math.abs(returnValue) % (this.max - this.min)) + this.min;
				}
			} else if (value > this.max) {
				this.directionRollingAverage = (this.directionRollingAverage + 1d) / 2d;
				if (this.directionRollingAverage > 0.8) {
					System.out.println(this.toString() + " too high at " + value + ", swapping direction. RA = " + this.directionRollingAverage);
					this.direction = !this.direction;
				}
				double returnValue = this.max - (value - this.max);
				if (returnValue > this.min && returnValue < this.max) {
					return returnValue;
				} else {
					System.out.println("" + this + " return value " + returnValue + " out of range, instead " + ((returnValue % (this.max - this.min)) + this.min));
					return (Math.abs(returnValue) % (this.max - this.min)) + this.min;
				}
			} else {
				System.out.println("" + this + " now " + value);
				return value;
			}
		}
		
		public boolean getDirection() {
			return this.direction;
		}

		private double min;

		private double max;
		
		private boolean direction = true;
		
		private double directionRollingAverage = 0d;
		
	}

	public static String loadFile(String inFilename) throws Exception{
		List<String> lines = Files.readAllLines(FileSystems.getDefault().getPath(inFilename),
				StandardCharsets.UTF_8);
		StringBuilder inBuilder = new StringBuilder();

		int lineCount = 0;
		for (String line : lines) {
			lineCount++;
			String theLine = line.replace("\r", "\n") + "\n";
			inBuilder.append(theLine);
		}

		return inBuilder.toString();
	}

	public static void runStuff(String input, String output) throws Exception{

		// Reset initial settings
		resetSettings();

		// Verify list data
		//for ( Queue.Instruction i : instructions ) {
		//	System.out.println(i);
		//}

		// Each ordering gives a different character
		// Alphabetic
		orderings.add("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		// Increasing order of scrabble scores
		//orderings.add("AEILNORSTUDGBCMPFHVWYKJXQZ");
		// Decreasing frequency of use in English
		orderings.add("ETAONRISHDLFCMUGYPWBVKXJQZ");

		String ss = "T" + (int) tempo + " I[" + instrument + "] " + processString(input);
		System.out.println(ss);
		Player player = new Player();
		File file = new File(output);
		player.saveMidi(ss, file);
		player.play(ss);
		player.close();
	}

	/**
	 * Turn the input string into a sound string that can be played by jFugue
	 */
	private static String processString(String input) {
		StringBuilder soundString = new StringBuilder();
		// For debugging / printout purposes
		StringBuilder lastSentence = new StringBuilder();
		// To allow word properties to influence sound
		StringBuilder lastWord = new StringBuilder();
		int lastWordLength = 0;

		// Placing a space before punctuations to sound them and the word before
		input = input.replaceAll("\\p{Punct}", " $0");

		for (int charIndex = 0; charIndex < input.length(); charIndex++) {
			char ch = input.charAt(charIndex);
			//char lastCh = input.charAt(charIndex-1);
			char upperCh = Character.toUpperCase(ch);
			lastSentence.append(ch);
			String charString = String.valueOf(ch);
			// A = 1, B = 2, ...
			int charNum = orderings.get(ordering).indexOf(upperCh) + 1;

			// Space
			if (Character.isWhitespace(ch)) {

				double theRestLength = restLength;

				//if (passingWords.contains(lastWord)) {
				//	theRestLength = restLength * (2d/3d);
				//}

				if (perWord) {
					sonifyWord(items, lastWord, soundString, true);
				}

  				lastWordLength = lastWord.length();
				lastWord.setLength(0);
				soundString.append("R/" + String.format("%f", theRestLength) + " ");

				if (charString.equals("\n")) {
					// An extra rest on newlines
					soundString.append("R/" + String.format("%f", restLength) + " ");
				}

				// Last character of word is a punctuation
				//String lastCharString = String.valueOf(input.charAt((charIndex-1)));
				//if (Pattern.matches("[\\p{Punct}\\p{IsPunctuation}]", String.valueOf(input.charAt(charIndex-1)))) {
				if (Pattern.matches("[\\p{Punct}\\p{IsPunctuation}]", String.valueOf(ch))) {
				//	System.out.println("last char: " + lastCharString);
				//	if (lastCharString.equals(".")) {
				//		System.out.println("period");
				//	}
					lastSentence.setLength(0);
				}

				// Highlight text as it plays - possibly using JFugue's StreamingPlayer API
				/*try {
					Main.highlighter.removeAllHighlights();
					Main.highlighter.addHighlight(charIndex-lastWordLength, charIndex, Main.painter);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}*/

/*				if (settingChangers.contains(charString)) {
					changeSetting();
				} */

				/*else if (!Character.isWhitespace(ch)) {
					// punctuation
					System.out.println("PUNCUTATION: " + ch); //testing
					int ascii = (int) ch;
					boolean increase = (ascii % 2 == 0);
					// Stop things getting too slow - see switch statement below
					if (!setting.getDirection()) {
						increase = !increase;
					}
					lastSentence.setLength(0);
					// Factor can be in the range 0.5..2: can half or double the
					// existing value at the most
					double factor = 1 + (ascii / 127d);
					if (!increase) {
						factor = 1 / factor;
					}
					switch (setting) {
					case NOTE_LENGTH:
						noteLength = setting.keepInRange(noteLength * factor);
						break;
					case ARPEGGIATE_GAP:
						double oldNoteGap = noteGap;
						noteGap = setting.keepInRange(noteGap * factor);
						// Stop things getting too slow if we're staying on the
						// slowest. Start to speed up again
						//*
						  if ((oldNoteGap == noteGap) && (noteGap ==
						  setting.keepInRange(99999d))) { noteGap =
						  setting.keepInRange(0d); System.out .println(
						  "Reached largest note gap, reversing direction of travel. Gap = "
						  + noteGap); //directionOfTravel = !directionOfTravel;

						  }
						  //*
						break;
					case REST_LENGTH:
						restLength = setting.keepInRange(restLength * factor);
						break;
					case BASE_FREQUENCY:
						baseFrequency = setting.keepInRange(baseFrequency * factor);
						break;
					case OCTAVES:
						octaves = setting.keepInRange(octaves * factor);
						break;
					case TEMPO:
						double oldTempo = tempo;
						tempo = setting.keepInRange(tempo * factor);
						soundString.append("T" + (int) tempo + " ");
						break;
					case LETTER_ORDERING:
						ordering += 1;
						if (ordering > (orderings.size() - 1)) {
							ordering = 0;
						}
						System.out.println("Changing letter ordering to " + orderings.get(ordering));
						// Only change letter ordering once, then move on to something else
						changeSetting();
						break;
					default:
						throw new IllegalStateException("Setting " + setting + " is not handled");
					}
				}*/

			} else {

				lastWord.append(upperCh);

				// Character
				if (perChar) {
					sonifyCharacter(lastWord, soundString, charNum, ch);
				}

			}
		}

		System.out.println(soundString.toString());
		return soundString.toString();
	}

	public static void streamText(StringBuilder lastWord, boolean isWord, char ch, int charNum) {
		StreamingPlayer streamingPlayer = new StreamingPlayer();
		StringBuilder soundString = new StringBuilder();

		resetSettings();

		soundString.append( (int) tempo + " I[" + instrument + "] " );

		if (isWord) {
			sonifyWord(items, lastWord, soundString, false);
		} else {
			sonifyCharacter(lastWord, soundString, charNum, ch);
		}

		//soundString.append("R/" + String.format("%f", theRestLength) + " ");
		System.out.println(soundString);
		streamingPlayer.stream(soundString.toString());
		streamingPlayer.close();
	}

	/*private static void changeSetting() {
		int newSettingNum = setting.ordinal() + 1;
		if (newSettingNum >= allSettings.size()) {
			newSettingNum = 0;
		}
		for (Setting testSetting : allSettings) {
			if (testSetting.ordinal() == newSettingNum) {
				setting = testSetting;
			}
		}
	}*/

	public static void sonifyWord(List<SenseMap.Mapping> items, StringBuilder lastWord, StringBuilder soundString, boolean doNoteGap) {
		// Lookup database
		for (SenseMap.Mapping item : items) {

			// Match
			if (item.getKey().equalsIgnoreCase(lastWord.toString())) {

				double[] wordValues = convertToArr.toDoubleArr(item.getValue());

				// Iterate through list of lexnames for each word
				for (double thisValue : wordValues) {
					//System.out.println(thisValue);

					// Word value + 1 because it starts at 0 in the database
					double targetOctave = Math.ceil(((thisValue+1) / 45d) * octaves); //26
					frequency = baseFrequency; // = convertToArr.toDoubleArr(item.getValue())[0]+1 * baseFrequency;

					switch (defaultNoteOperation) {
						case LEXNAMEFREQ:
							//System.out.println("freq: " + convertToArr.toDoubleArr(item.getValue())[0]);
							frequency = (thisValue+1) * baseFrequency;
							break;
						case STATICFREQ:
							// If we want a default tone, leave freq as static
							break;
						case MUTE:
							// If we want a mute tone, set duration of each note to be 0
							noteLength = 0;
							break;
					}

					// Go through the instructions queue
					for (Queue.Instruction i : instructions) {

						// The main logic part of the program
						// Make changes based on user instructions
						if (i.mod == Queue.Instruction.Mods.WORDTYPE) {
							SenseMap.Type[] wordtypes = convertToArr.toTypeArr(item.getType());
							for (SenseMap.Type m : wordtypes) {
								if (m != null && m.toString().equals(i.modValue)) {
									applyMod(i, soundString);
								}
							}

						} else if (i.mod == Queue.Instruction.Mods.WORDLENGTH) {
							switch (i.getModOperator()) {
								case EQUALTO:
									if (Integer.parseInt(i.getModValue()) == lastWord.length()) {
										applyMod(i, soundString);
									}
									break;
								case LARGERTHAN:
									if (Integer.parseInt(i.getModValue()) < lastWord.length()) {
										applyMod(i, soundString);
									}
									break;
								case LESSTHAN:
									if (Integer.parseInt(i.getModValue()) > lastWord.length()) {
										applyMod(i, soundString);
									}
									break;
							}

						} else if (i.mod == Queue.Instruction.Mods.WORDVALUE) {
							//double[] lexnames = convertToArr.toDoubleArr(item.getValue() + 1);
							//for (double n : lexnames) {
							//	if (n == Double.parseDouble(i.modValue)) {
									//System.out.println("Equal: " + convertToArr.toDoubleArr(item.getValue())[0] + " | " + Double.parseDouble(i.modValue));
							//		applyMod(i, soundString);
							//	}
							//}
							if (thisValue == Double.parseDouble(i.modValue)) {
							//System.out.println("Equal: " + convertToArr.toDoubleArr(item.getValue())[0] + " | " + Double.parseDouble(i.modValue));
								applyMod(i, soundString);
							}

						} else if (i.mod == Queue.Instruction.Mods.PUNCTUATION) {
							//String[] punctuations = convertToArr.toStringArr(item.getValue());

							//for (String n : punctuations) {
							if (item.getKey().equals(i.modValue)) {
								//System.out.println("Equal: " + convertToArr.toDoubleArr(item.getValue())[0] + " | " + Double.parseDouble(i.modValue));
								applyMod(i, soundString);
							}
							//}
						}
					}

					// Normalise to fit in the range
					double topFrequency = baseFrequency;
					for (int j = 0; j < targetOctave; j++) {
						topFrequency = topFrequency * 2;
					}
					while (frequency > topFrequency) {
						frequency = frequency / 2;
					}

					// Testing
					System.out.println("Frequency for " + lastWord + "=" + thisValue +
							" normalized to octave "
							+ octaves + ", top frequency " + topFrequency + ": " +
							frequency);

					// If word is in multiple lexname categories then set duration to
					//if (wordValues.length>1) {
						//noteLength = 1/64f;
					//}

					// Combine notes together as a harmony if word has more than one category
					if (wordValues.length > 1) {
						double tempNote;
						int musicNote;
						// Convert freq to MIDI music string using reference note and frequency A4 440hz
						tempNote = 12 * logCalc.log(frequency/440, 2) + 69;
						musicNote = (int) Math.rint(tempNote);
						// Note + Duration + Attack + Decay
						soundString.append("[" + musicNote + "]" + "/" + noteLength + "a" + attack + "d" + decay + "+");
						System.out.println("Convert frequency: " + frequency + " to note: " + musicNote);
					} else {
						// Convert freq to music string and append to sound string
						// Note + Duration + Attack + Decay
						soundString.append(MicrotoneNotation.convertFrequencyToMusicString(frequency) + "/" + noteLength + "a" + attack + "d" + decay + "+");
						System.out.println("Convert freq to music string: " + MicrotoneNotation.convertFrequencyToMusicString(frequency));
					}

					lexCount++;
				}

				if (doNoteGap) {
					double theNoteGap = noteGap;
					if (theNoteGap > 0.2) {
						theNoteGap = theNoteGap / lastWord.length();
					} else if ((theNoteGap > 0.1) && passingWords.contains(lastWord.toString())) {
						theNoteGap = theNoteGap * 0.5;
					}

					// Insert at end of musicstring: Note + Resting gap
					soundString.append("R/" + String.format("%f", noteGap) + " ");

					// Reset to base settings
					resetSettings();
					soundString.append("I[" + instrument + "] ");
				} else if (!doNoteGap) {
					//
					//soundString.append(" ");
					soundString.deleteCharAt(soundString.length()-1);
				}

				lexCount = 0;
			}
		}
	}

	public static void sonifyCharacter(StringBuilder lastWord, StringBuilder soundString, double charNum, char ch) {
		double targetOctave = Math.ceil((charNum / 26d) * octaves); //26
		frequency = baseFrequency; // = convertToArr.toDoubleArr(item.getValue())[0]+1 * baseFrequency;

		switch (defaultNoteOperation) {
			case LEXNAMEFREQ:
				//System.out.println("freq: " + convertToArr.toDoubleArr(item.getValue())[0]);
				frequency = charNum * baseFrequency;
				break;
			case STATICFREQ:
				// If we want a default tone, leave freq as static
				break;
			case MUTE:
				// If we want a mute tone, set duration of each note to be 0
				noteLength = 0;
				break;
		}

		// Go through the instructions queue
		for (Queue.Instruction i : instructions) {

			// The main logic part of the program
			// Make changes based on user instructions
			if (i.mod == Queue.Instruction.Mods.CHARACTER) {
				//System.out.println("AEIOUaeiou".indexOf(ch));
				if (i.modValue.equals("vowels") && "AEIOUaeiou".indexOf(ch) != -1) {
					applyMod(i, soundString);
				} else if (i.modValue.equals("consonants") && "AEIOUaeiou".indexOf(ch) < 0) {
					applyMod(i, soundString);
				}

			} else if (i.mod == Queue.Instruction.Mods.PUNCTUATION) {
				//String[] punctuations = convertToArr.toStringArr(item.getValue());

				//for (String n : punctuations) {
				if (ch == i.modValue.charAt(0)) {
					//System.out.println("Equal: " + convertToArr.toDoubleArr(item.getValue())[0] + " | " + Double.parseDouble(i.modValue));
					applyMod(i, soundString);
				}
				//}
			}
		}

		// Normalise to fit in the range
		double topFrequency = baseFrequency;
		for (int j = 0; j < targetOctave; j++) {
			topFrequency = topFrequency * 2;
		}
		while (frequency > topFrequency) {
			frequency = frequency / 2;
		}

		soundString.append(MicrotoneNotation.convertFrequencyToMusicString(frequency));
		System.out.println("Convert freq to music string: " + MicrotoneNotation.convertFrequencyToMusicString(frequency));
		// Convert freq to MIDI music string using reference note and frequency A4 440hz
		//double tempNote;
		//int musicNote;
		//tempNote = 12 * logCalc.log(frequency/440, 2) + 69;
		//musicNote = (int) Math.rint(tempNote);
		//soundString.append("[" + musicNote + "]" + "/" + noteLength + " ");
		//System.out.println("Convert frequency: " + frequency + " to note: " + musicNote);

		// Testing
		System.out.println("Frequency for " + lastWord + "=" + charNum +
				" normalized to octave "
				+ octaves + ", top frequency " + topFrequency + ": " +
				frequency);
		if (Character.isUpperCase(ch)) {
			//System.out.println("notelength: " + noteLength);
			soundString.append("/" + String.format("%f", noteLength * 4)); // If it's an uppercase letter increase note length
			//System.out.println("soundString: " + soundString);
		} else {
			soundString.append("/" + String.format("%f", noteLength));
		}

		// Attack + Decay
		soundString.append("a" + attack + "d" + decay);

		double theNoteGap = noteGap;
		if (theNoteGap > 0.2) {
			theNoteGap = theNoteGap / lastWord.length();
		} else if ((theNoteGap > 0.1) && passingWords.contains(lastWord.toString())) {
			theNoteGap = theNoteGap * 0.5;
		}
		soundString.append("+R/" + String.format("%f", noteGap) + " "); // Note + Resting gap

		// Reset to base settings
		resetSettings();
		soundString.append("I[" + instrument + "] ");
	}

	private static StringBuilder applyMod(Queue.Instruction i, StringBuilder soundString) {
		// Allow sound instructions to be played, if notes are set to mute in default settings
		if (defaultNoteOperation == noteOperationType.MUTE) { noteLength = baseNoteLength; }

		switch (i.soundMod) {
			case TEMPO:
				Setting settingTempo = Setting.TEMPO;
				if (lexCount <= 0) {
					if (i.changeMode == Queue.Instruction.ChangeModes.SET) {
						tempo = Double.parseDouble(i.soundModValue);
						//soundString.append("T" + (int) tempo + " ");
					} else {
						tempo += Double.parseDouble(i.soundModValue);
						//baseTempo = tempo;
						//soundString.append("T" + (int) tempo + " ");
					}
					tempo = settingTempo.keepInRange(tempo);
					baseTempo = tempo;
					soundString.append("T" + (int) tempo + " ");
				}
				break;

			case NOTE_DURATION:
				Setting settingNoteDuration = Setting.NOTE_LENGTH;
				if (i.changeMode == Queue.Instruction.ChangeModes.SET) {
					noteLength = settingNoteDuration.keepInRange(noteLength);
					noteLength = Double.parseDouble(i.soundModValue);
				} else {
					noteLength = settingNoteDuration.keepInRange(noteLength);
					noteLength += Double.parseDouble(i.soundModValue);
				}
				baseNoteLength = noteLength;
				break;

			case OCTAVE:
				Setting settingOctaves = Setting.OCTAVES;
				if (i.changeMode == Queue.Instruction.ChangeModes.SET) {
					octaves = settingOctaves.keepInRange(octaves);
					octaves = Double.parseDouble(i.soundModValue);
				} else {
					octaves = settingOctaves.keepInRange(octaves);
					octaves += Double.parseDouble(i.soundModValue);
				}
				baseOctaves = octaves;
				break;

			case INSTRUMENT:
				if (lexCount <= 0) {
					instrument = i.soundModValue;
					soundString.append("I[" + instrument + "] ");
				}
				break;

			case VOLUME:
				Setting settingVolume = Setting.VOLUME;
				if (lexCount <= 0) {
					volume = Double.parseDouble(i.soundModValue);
					volume = settingVolume.keepInRange(volume);
					soundString.append("X[Volume]=" + volume + " ");
				}
				break;

			case PERCUSSION:
				soundString.append("V9 [" + i.soundModValue + "]q V0 ");
				break;

			case MIDI_NOTE:
				//if (i.changeMode == Queue.Instruction.ChangeModes.SET) {
				//	frequency = Double.parseDouble(i.soundModValue);
				//} else {
				//	frequency += Double.parseDouble(i.soundModValue);
				//	baseFrequency = frequency;
				//}
				Setting settingsFrequency = Setting.BASE_FREQUENCY;
				double midiNoteNumber = Double.parseDouble(i.soundModValue);

				if (i.changeMode == Queue.Instruction.ChangeModes.SET) {
					frequency = Math.pow(2, (midiNoteNumber - 69) / 12) * 440;
					frequency = settingsFrequency.keepInRange(frequency);
				} else {
					double tempFreq = Math.pow(2, (midiNoteNumber - 69) / 12) * 440;
					frequency += tempFreq;
					frequency = settingsFrequency.keepInRange(frequency);
					baseFrequency = frequency;
				}
				//System.out.println("Change freq to: " + i.soundModValue);
				break;

			case ATTACK:
				Setting settingsAttack = Setting.ATTACK;
				attack = Integer.parseInt(i.soundModValue);
				attack = (int) settingsAttack.keepInRange(attack);
				break;

			case DECAY:
				Setting settingsDecay = Setting.ATTACK;
				decay = Integer.parseInt(i.soundModValue);
				decay = (int) settingsDecay.keepInRange(decay);
				break;
		}
		return soundString;
	}

	private static void resetSettings() {
		noteLength = baseNoteLength;
		frequency = baseFrequency;
		instrument = baseInstrument;
		octaves = baseOctaves;
		tempo = baseTempo;
		attack = baseAttack;
		decay = baseDecay;
	}
}

class serialInstructionsQueue {
	static ObjectOutputStream serializeObject(List<Queue.Instruction> thisObjectList) {
		try {
			FileOutputStream fos = new FileOutputStream(TextSound.prefsFile);
			ObjectOutputStream oos = new ObjectOutputStream(fos);

			oos.writeObject(thisObjectList);
			oos.close();
			fos.close();

			System.out.println("\nSerialization Successful\n");

			return oos;
		} catch (FileNotFoundException e) {
			System.out.println("a");
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			System.out.println("b");
			e.printStackTrace();
			return null;
		}
	}

	static List<Queue.Instruction> deserializeObject(String thisOutStream) {
		try {
			FileInputStream fis = new FileInputStream(TextSound.prefsFile);
			ObjectInputStream ois = new ObjectInputStream(fis);

			TextSound.instructions = (ArrayList) ois.readObject();

			ois.close();
			fis.close();

			return TextSound.instructions;
		} catch (IOException e) {
			System.out.println("No user saved instructions to load.");
			//e.printStackTrace();
			return TextSound.instructions;
		} catch (ClassNotFoundException e) {
			System.out.println("d");
			e.printStackTrace();
			return null;
		}
	}
}

class convertToArr {
	static double[] toDoubleArr(String inString) {
		String[] tokens = inString.split(",");
		double[] arr = new double[tokens.length];
		int i=0;
		for (String st : tokens) {
			arr[i++] = Double.valueOf(st);
		}
		return arr;
	}

	static SenseMap.Type[] toTypeArr(String inString) {
		String[] tokens = inString.split(",");
		SenseMap.Type[] arr = new SenseMap.Type[tokens.length];
		int i=0;
		for (String st : tokens) {
			arr[i++] = SenseMap.Type.valueOf(st);
		}
		return arr;
	}

	static String[] toStringArr(String inString) {
		String[] tokens = inString.split(",");
		String[] arr = new String[tokens.length];
		int i=0;
		for (String st : tokens) {
			arr[i++] = st;
		}
		return arr;
	}
}

class logCalc {
	static double log(double x, double base)
	{
		return (Math.log(x) / Math.log(base));
	}
}