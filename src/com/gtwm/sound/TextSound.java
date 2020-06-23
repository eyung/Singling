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

import org.jfugue.midi.MidiFileManager;
import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;
import org.jfugue.realtime.RealtimePlayer;
import org.jfugue.theory.Note;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.*;


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

	// Starting settings
	// NB: If any values are set to exactly zero, they will be unable to
	// change throughout the generation

	// Instrument
	static String instrument;

	// How long to hold each note for
	static double noteLength; // /1 = whole note (semibreve). /0.25 = crotchet
	static double baseNoteLength;

	// How long to wait before playing the next note
	//static double noteGap = 0.0001; // 1 / 32d; // 1/32 = good default, 0 = no
	static double noteGap = 1/32d;

	// How long to pause when a rest (space etc.) is encountered
	static double restLength = 1 / 16d; // 1/8 = good default

	// Lowest note that can be played
	static double baseFrequency; // 128 Hz = Octave below middle C
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
	static int ordering;
	static List<String> orderings = new ArrayList<String>();

	static Player player;

	static Pattern pattern;

	static double patternCurrentTime = 0;
	
	static Set<String> passingWords = new HashSet<String>(Arrays.asList("THE","A","AND","OR","NOT","WITH","THIS","IN","INTO","IS","THAT","THEN","OF","BUT","BY","DID","TO","IT","ALL"));

	static List<WordMap.Mapping> items;

	static List<Queue.Instruction> instructions = new ArrayList<>();

	// Keeping track of how many categories a word falls under
	static int lexCount = 0;

	enum Setting {
		NOTE_LENGTH(0.01, 8.0), ARPEGGIATE_GAP(0.001, 0.5), REST_LENGTH(0.01, 0.5), BASE_FREQUENCY(16.0, 2048), OCTAVES(
				1.0, 10.0), TEMPO(40, 400), LETTER_ORDERING(0.0,3.0), VOLUME(1.0, 16383), ATTACK(0, 127);
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

	public static void runStuff() throws Exception{

		// Reset initial settings
		resetSettings();

		// Verify list data
		//for ( Queue.Instruction i : instructions ) {
		//	System.out.println(i);
		//}

		// Each ordering gives a different character
		// Mayzner's
		orderings.add("ETAOINSRHLDCUMFPGWYBVKXJQZ");
		// standard order frequency used by typesetters (https://en.wikipedia.org/wiki/Letter_frequency)
		orderings.add("ETAONRISHDLFCMUGYPWBVKXJQZ");
		// letter frequency based upon Oxford dictionary (https://languages.oup.com/)
		orderings.add("EARIOTNSLCUDPMHGBFYWKVXZJQ");

		//String ss = "T" + (int) tempo + " I[" + instrument + "] " + processString(input);

		pattern = new Pattern();
		pattern.setVoice(0);
		pattern.setInstrument(instrument);
		pattern.setTempo((int)tempo);
		patternCurrentTime = 0;

		//String ss = processString(input, pattern);
		//System.out.println("Pattern = " + ss);
		//pattern = processString(input, pattern);

		//File file = new File(output);
		//MidiFileManager midiFileManager = new MidiFileManager();
		//midiFileManager.savePatternToMidi(pattern, file);

		//Player player = new Player();
		//player.play(ss);
		//player.play(pattern);
		//player.getManagedPlayer().finish();
	}

	public static void doStartPlayer(String input) {
		pattern = processString(input, pattern);
		player = new Player();
	}

	public static void doPlay() {
		//pattern = processString(input, pattern);

		//Player player = new Player();
		player.play(pattern);

		if (player.getManagedPlayer().isFinished()) {
			player.getManagedPlayer().finish();
		}
	}

	public static void doSaveAsMidi(String input, String output) throws Exception{
		pattern = processString(input, pattern);

		File file = new File(output);
		MidiFileManager midiFileManager = new MidiFileManager();
		midiFileManager.savePatternToMidi(pattern, file);
	}

	public static void doPause() {
		player.getManagedPlayer().pause();
	}

	/**
	 * Turn the input string into a sound string that can be played by jFugue
	 */
	private static Pattern processString(String input, Pattern pattern) {
		//StringBuilder soundString = new StringBuilder();
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
					//sonifyWord(items, lastWord, soundString, true);
					sonifyWord(items, lastWord, pattern, true);
				}

  				lastWordLength = lastWord.length();
				lastWord.setLength(0);
				//soundString.append("R/" + String.format("%f", theRestLength) + " ");
				pattern.add("R/" + String.format("%f", theRestLength) + " ");
				patternCurrentTime += theRestLength;

				if (charString.equals("\n")) {
					// An extra rest on newlines
					//soundString.append("R/" + String.format("%f", restLength) + " ");
					pattern.add("R/" + String.format("%f", restLength) + " ");
					patternCurrentTime += restLength;
				}

				patternCurrentTime = Math.round(patternCurrentTime * 100.0) / 100.0;

				// Last character of word is a punctuation
				//String lastCharString = String.valueOf(input.charAt((charIndex-1)));
				//if (Pattern.matches("[\\p{Punct}\\p{IsPunctuation}]", String.valueOf(input.charAt(charIndex-1)))) {
				if (java.util.regex.Pattern.matches("[\\p{Punct}\\p{IsPunctuation}]", String.valueOf(ch))) {
				//	System.out.println("last char: " + lastCharString);
				//	if (lastCharString.equals(".")) {
				//		System.out.println("period");
				//	}
					lastSentence.setLength(0);
				}

			} else {

				lastWord.append(upperCh);

				// Character
				if (perChar) {
					//sonifyCharacter(lastWord, soundString, charNum, ch);
					sonifyCharacter(lastWord, pattern, charNum, ch);
				}

			}
		}

		//System.out.println(soundString.toString());
		//return soundString.toString();
		System.out.println(pattern.toString());
		return pattern;
	}

	public static void streamText(RealtimePlayer realtimePlayer, StringBuilder lastWord, boolean isWord, char ch, int charNum) {
		//StreamingPlayer streamingPlayer = new StreamingPlayer();
		try {
			//RealtimePlayer realtimePlayer = new RealtimePlayer();
			StringBuilder soundString = new StringBuilder();
			//double patternTimeStamp = 0;

			resetSettings();

			//soundString.append( (int) tempo + " I[" + instrument + "] " );
			Pattern pattern = new Pattern();
			pattern.setTempo((int)tempo);
			pattern.setInstrument(instrument);
			patternCurrentTime = 0;

			if (isWord) {
				//sonifyWord(items, lastWord, soundString, false);
				sonifyWord(items, lastWord, pattern,false);
			} else {
				//sonifyCharacter(lastWord, soundString, charNum, ch);
				sonifyCharacter(lastWord, pattern, charNum, ch);
			}

			pattern = pattern.add(soundString.toString());

			//soundString.append("R/" + String.format("%f", theRestLength) + " ");
			System.out.println("Staccato: " + pattern);

			//realtimePlayer.stream(soundString.toString());
			//realtimePlayer.play("T" + soundString);
			realtimePlayer.play(pattern);
			//realtimePlayer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void sonifyWord(List<WordMap.Mapping> items, StringBuilder lastWord, Pattern pattern, boolean doNoteGap) {
		Pattern transformedPattern = new Pattern();

		// Lookup database
		for (WordMap.Mapping item : items) {

			// Match
			if (item.getKey().equalsIgnoreCase(lastWord.toString())) {

				double[] wordValues = convertToArr.toDoubleArr(item.getValue());

				// Iterate through list of lexnames for each word
				for (double thisValue : wordValues) {
					//System.out.println(thisValue);

					// Set voice
					if (wordValues.length > 1) {
						// Skip Voice channel 9 as that is for percussion instruments
						if (lexCount == 9) {
							lexCount++;
						}

						if (lexCount < 15) {
							pattern.add("V" + lexCount + " @" + patternCurrentTime);
						}

						//if (lexCount > 15) {
						//	lexCount = 0;
						//}
						//pattern.add("V" + lexCount + " @" + patternCurrentTime);
					}

					// Word value + 1 because it starts at 0 in the database
					double targetOctave = Math.ceil(((thisValue + 1) / 45d) * octaves); //26
					frequency = baseFrequency; // = convertToArr.toDoubleArr(item.getValue())[0]+1 * baseFrequency;

					switch (defaultNoteOperation) {
						case LEXNAMEFREQ:
							//System.out.println("freq: " + convertToArr.toDoubleArr(item.getValue())[0]);
							frequency = (thisValue + 1) * baseFrequency;
							break;
						case STATICFREQ:
							// If we want a default tone, leave freq as static
							break;
						case MUTE:
							// Mute tone
							//noteLength = 0;
							pattern.add(":CE(935,0)");
							break;
					}

					// Go through the instructions queue
					for (Queue.Instruction i : instructions) {

						transformedPattern.clear();

						// The main logic part of the program
						// Make changes based on user instructions
						if (i.mod == Queue.Instruction.Mods.WORDTYPE) {
							WordMap.Type[] wordtypes = convertToArr.toTypeArr(item.getType());
							for (WordMap.Type m : wordtypes) {
								if (m != null && m.toString().equals(i.modValue)) {
									applyMod(i, pattern);
								}
							}

						} else if (i.mod == Queue.Instruction.Mods.WORDLENGTH) {
							switch (i.getModOperator()) {
								case EQUALTO:
									if (Integer.parseInt(i.getModValue()) == lastWord.length()) {
										applyMod(i, pattern);
									}
									break;
								case LARGERTHAN:
									if (Integer.parseInt(i.getModValue()) < lastWord.length()) {
										applyMod(i, pattern);
									}
									break;
								case LESSTHAN:
									if (Integer.parseInt(i.getModValue()) > lastWord.length()) {
										applyMod(i, pattern);
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
								applyMod(i, pattern);
							}

						} else if (i.mod == Queue.Instruction.Mods.PUNCTUATION) {
							//String[] punctuations = convertToArr.toStringArr(item.getValue());

							//for (String n : punctuations) {
							if (item.getKey().equals(i.modValue)) {
								//System.out.println("Equal: " + convertToArr.toDoubleArr(item.getValue())[0] + " | " + Double.parseDouble(i.modValue));
								applyMod(i, pattern);
							}
							//}
						} else if (i.mod == Queue.Instruction.Mods.SENTIMENT) {
							if (i.getSentimentType().equals(Queue.Instruction.SentimentTypes.POSITIVESENTIMENT)) {
								if (item.getSentimentPos() != null && item.getSentimentPos() != "NULL") {
									transformedPattern = applySentimentMod(i, thisValue, item.getSentimentPos(), pattern);
								}
							} else if (i.getSentimentType().equals(Queue.Instruction.SentimentTypes.NEGATIVESENTIMENT)) {
								if (item.getSentimentNeg() != null && item.getSentimentNeg() != "NULL") {
									transformedPattern = applySentimentMod(i, thisValue, item.getSentimentNeg(), pattern);
								}
							}
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
					//System.out.println("Frequency for " + lastWord + "=" + thisValue +
					//		" normalized to octave "
					//		+ octaves + ", top frequency " + topFrequency + ": " +
					//		frequency);

					// Convert freq to MIDI music string using reference note and frequency A4 440hz
					double tempNote;
					int musicNote;
					tempNote = 12 * logCalc.log(frequency/440, 2) + 69;
					musicNote = (int) Math.rint(tempNote);

					frequency = Math.round(frequency * 100.0) / 100.0;

					// Hacky hack hack so that we are capping voices at 16
					if (lexCount < 15) {
						if (transformedPattern != null && !transformedPattern.toString().equals("")) {
							// Note + Duration + Attack + Decay
							pattern.add(transformedPattern + "/" + noteLength + "a" + attack + "d" + decay + "");
						} else {
							// Note + Duration + Attack + Decay
							pattern.add("m" + frequency + "/" + noteLength + "a" + attack + "d" + decay + "");
						}
					}

					System.out.println("Convert frequency: " + frequency + " to note: " + musicNote);

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
					//soundString.append("R/" + String.format("%f", noteGap) + " ");
					pattern.add("R/" + String.format("%f", noteGap) + " ");

					// Reset to base settings
					resetSettings();
					pattern.add("I[" + instrument + "] ");
					pattern.add("V0");
					//pattern.setInstrument(instrument);
				} else if (!doNoteGap) {
					//
					//soundString.append(" ");
					//soundString.deleteCharAt(soundString.length()-1);
					//pattern.
				}

				patternCurrentTime = Math.round(patternCurrentTime * 100.0) / 100.0;
				patternCurrentTime += noteLength + noteGap;
				//System.out.println("Current time in pattern: " + patternTimeStamp);

				lexCount = 0;
			}
		}
	}

	public static void sonifyCharacter(StringBuilder lastWord, Pattern pattern, double charNum, char ch) {
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
				// Mute tone
				pattern.add(":CE(935,0)");
				break;
		}

		// Set frequency of punctuations/symbols to frequency of lexname = 46
		if (java.util.regex.Pattern.matches("[\\p{Punct}\\p{IsPunctuation}]", String.valueOf(ch))) {
			targetOctave = Math.ceil((46/45d) * octaves); //26
			frequency = 46 * baseFrequency;
		}

		// Go through the instructions queue
		for (Queue.Instruction i : instructions) {

			// The main logic part of the program
			// Make changes based on user instructions
			if (i.mod == Queue.Instruction.Mods.CHARACTER) {
				//System.out.println("AEIOUaeiou".indexOf(ch));
				if (i.modValue.equals("vowels") && "AEIOUaeiou".indexOf(ch) != -1) {
					applyMod(i, pattern);
				} else if (i.modValue.equals("consonants") && "AEIOUaeiou".indexOf(ch) < 0) {
					applyMod(i, pattern);
				}

			} else if (i.mod == Queue.Instruction.Mods.PUNCTUATION) {
				//String[] punctuations = convertToArr.toStringArr(item.getValue());

				//for (String n : punctuations) {
				if (ch == i.modValue.charAt(0)) {
					//System.out.println("Equal: " + convertToArr.toDoubleArr(item.getValue())[0] + " | " + Double.parseDouble(i.modValue));
					applyMod(i, pattern);
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

		// Convert freq to MIDI music string using reference note and frequency A4 440hz
		//double tempNote;
		//int musicNote;
		//tempNote = 12 * logCalc.log(frequency/440, 2) + 69;
		//musicNote = (int) Math.rint(tempNote);
		//soundString.append("m" + frequency + "/" + noteLength);
		pattern.add("m" + frequency + "/" + noteLength + "a" + attack + "d" + decay);
		//System.out.println("Convert frequency: " + frequency + ", note length: " + noteLength);

		// Testing
		System.out.println("Frequency for " + lastWord + "=" + charNum +
				" normalized to octave "
				+ octaves + ", top frequency " + topFrequency + ": " +
				frequency);
		//if (Character.isUpperCase(ch)) {
			//System.out.println("notelength: " + noteLength);
			//soundString.append("/" + String.format("%f", noteLength * 4)); // If it's an uppercase letter increase note length
			//System.out.println("soundString: " + soundString);
		//} else {
			//soundString.append("/" + String.format("%f", noteLength));
		//}

		// Attack + Decay
		//soundString.append("a" + attack + "d" + decay);
		//pattern.add("a" + attack + "d" + decay);

		//double theNoteGap = noteGap;
		//if (theNoteGap > 0.2) {
		//	theNoteGap = theNoteGap / lastWord.length();
		//} else if ((theNoteGap > 0.1) && passingWords.contains(lastWord.toString())) {
		//	theNoteGap = theNoteGap * 0.5;
		//}
		//soundString.append("+R/" + String.format("%f", noteGap) + " "); // Note + Resting gap
		pattern.add("+R/" + String.format("%f", noteGap) + " "); // Note + Resting gap

		// Reset to base settings
		resetSettings();
		//soundString.append("I[" + instrument + "] ");
		pattern.add("I[" + instrument + "] ");
	}

	private static void applyMod(Queue.Instruction i, Pattern pattern) {
		// Allow sound instructions to be played if notes are set to mute in default settings
		if (defaultNoteOperation == noteOperationType.MUTE) { pattern.add(":CE(935,10200)"); }

		switch (i.soundMod) {
			case TEMPO:
				Setting settingTempo = Setting.TEMPO;
				//if (lexCount <= 0) {
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
					//soundString.append("T" + (int) tempo + " ");
					pattern.add("T" + (int)tempo);
				//}
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
				//if (lexCount <= 0) {
				//	instrument = i.soundModValue;
				//	soundString.append("I[" + instrument + "] ");
				//}
				//pattern.setInstrument(i.soundModValue);
				pattern.add("I[" + i.soundModValue + "] ");
				break;

			case VOLUME:
				Setting settingVolume = Setting.VOLUME;
				//if (lexCount <= 0) {
					volume = Double.parseDouble(i.soundModValue);
					volume = settingVolume.keepInRange(volume);
					//soundString.append("X[Volume]=" + volume + " ");
					//pattern.add("X[Volume]=" + volume + " ");
					pattern.add(":CE(935," + volume + ")");
				//}
				break;

			case PERCUSSION:
				//soundString.append("V9 [" + i.soundModValue + "]q V0 ");
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

	}

	private static Pattern applySentimentMod(Queue.Instruction i, double wordValue, String sentimentValue, Pattern pattern) {
		Pattern sentimentPattern = new Pattern();
		Setting settingsFrequency = Setting.BASE_FREQUENCY;

		double tempNote;
		int midiNumber;
		double x;
		String thisNote;

		// Allow sound instructions to be played if notes are set to mute in default settings
		if (defaultNoteOperation == noteOperationType.MUTE) {
			pattern.add(":CE(935,10200)");
		}

		switch (i.soundMod) {
			case TEMPO:
				break;

			case MIDI_NOTE:
				double newWordValue;

				//if (i.sentimentType.equals(Queue.Instruction.SentimentTypes.POSITIVESENTIMENT)) {
					newWordValue = (Double.parseDouble(sentimentValue) * 100) * wordValue;
					frequency = Math.pow(2, (newWordValue - 69) / 12) * 440;
					frequency = settingsFrequency.keepInRange(frequency);
					//System.out.println("FREQUENCY= " + frequency);
					//System.out.println("SENTIMENTVALUE= " + sentimentValue);
				//} else if (i.sentimentType.equals(Queue.Instruction.SentimentTypes.NEGATIVESENTIMENT)) {

				//}
				break;

			case MAJOR:
				x = (Double.parseDouble(sentimentValue) * 100) * wordValue;
				frequency = Math.pow(2, (x - 69) / 12) * 440;
				frequency = settingsFrequency.keepInRange(frequency);

				// Convert freq to MIDI music string using reference note and frequency A4 440hz
				tempNote = 12 * logCalc.log(frequency/440, 2) + 69;
				midiNumber = (int) Math.rint(tempNote);

				frequency = Math.round(frequency * 100.0) / 100.0;

				sentimentPattern.add(midiNumber + "maj");
				break;

			case MINOR:
				x = (Double.parseDouble(sentimentValue) * 100) * wordValue;
				frequency = Math.pow(2, (x - 69) / 12) * 440;
				frequency = settingsFrequency.keepInRange(frequency);

				// Convert freq to MIDI music string using reference note and frequency A4 440hz
				tempNote = 12 * logCalc.log(frequency/440, 2) + 69;
				midiNumber = (int) Math.rint(tempNote);

				frequency = Math.round(frequency * 100.0) / 100.0;

				sentimentPattern.add(midiNumber + "min");
				break;
		}

		return sentimentPattern;
	}

	private static void resetSettings() {
		noteLength = baseNoteLength;
		frequency = baseFrequency;
		instrument = baseInstrument;
		octaves = baseOctaves;
		tempo = baseTempo;
		attack = baseAttack;
		decay = baseDecay;
		//ordering =
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

	static WordMap.Type[] toTypeArr(String inString) {
		String[] tokens = inString.split(",");
		WordMap.Type[] arr = new WordMap.Type[tokens.length];
		int i=0;
		for (String st : tokens) {
			arr[i++] = WordMap.Type.valueOf(st);
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