package com.gtwm.sound;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.*;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import org.jfugue.pattern.Pattern;
import org.jfugue.theory.Note;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 *
 */
public class Composer {

    // Starting settings

    // How long to hold each note for
    private double noteLength; // /1 = whole note (semibreve). /0.25 = crotchet
    private double baseNoteLength;

    // How long to wait before playing the next note
    private double noteGap = 1 / 32d; // 1/32 = good default, 0 = no

    // How long to pause when a rest (space) is encountered
    private double restLength = 1 / 16d; // 1/8 = good default

    // How long to pause when a line break is encountered
    private double restLengthLineBreak = 1 / 16d;

    // Lowest note that can be played
    private double baseFrequency; // 128 Hz = Octave below middle C
    private double frequency;

    // Octave range in which to place notes
    private double baseOctaves;
    private double octaves;

    // Tempo in beats per minute
    private double baseTempo;
    private double tempo;

    // Instrument
    private String baseInstrument;
    private String instrument;

    // Volume
    private double volume;
    private double baseVolume = 10200d;

    // Attack
    private int attack;
    private int baseAttack = 64;

    // Decay
    private int decay;
    private int baseDecay = 64;

    // Pitch Bend
    private long pitchBend = 8192;

    // Pan
    private int pan;
    private int basePan = 64;

    // Note operation types where:
    // LEXNAMEFREQ: using lexnames as the frequency modifier to generate midi note
    // STATICFREQ: using the default frequency that is set by user to create note for all words
    // MUTE: no sound is created except when transformations are encountered
    private enum noteOperationType {LEXNAMEFREQ, STATICFREQ, MUTE}
    private noteOperationType defaultNoteOperation = noteOperationType.LEXNAMEFREQ;

    // To perform text to sound on word or character scale
    private boolean isWord;

    // Which letter ordering (defined above) to use, zero indexed
    private int ordering;
    private List<String> orderings;

    // Initialise pattern
    private Pattern pattern;

    // Keep track of pattern timestamp
    private double patternCurrentTime;

    // Exclusion words that will get a pass from the this processor
    private Set<String> passingWords;

    // List of transformation instructions
    private List<TransformationManager.Instruction> instructions;

    // # of lexnames for words
    private int lexCount;

    // JWI object
    private IDictionary dict;
    private String wordNetDirectory;
    private String path;
    private URL url;

    /**
     * TODO
     * @param builder
     */
    private Composer(ComposerBuilder builder) {
        this.baseNoteLength = builder.baseNoteLength;
        this.baseInstrument = builder.baseInstrument;
        this.baseFrequency = builder.baseFrequency;
        this.baseOctaves = builder.baseOctaves;
        this.baseTempo = builder.baseTempo;
        this.restLength = builder.restLength;
        this.restLengthLineBreak = builder.restLengthLineBreak;
        this.isWord = builder.isWord;
        this.defaultNoteOperation = builder.operationType;
        this.ordering = builder.ordering;
        this.instructions = builder.instructions;
        this.passingWords = builder.passingWords;

        //orderings = new ArrayList<>();
        this.orderings = builder.orderings;

        //passingWords = new HashSet<>();
        //instructions = new ArrayList<>();
        patternCurrentTime = 0;
        lexCount = 0;

        // Set settings to base values
        resetSettings();

        // Create JFuge pattern
        pattern = new Pattern();
        pattern.setVoice(0);
        pattern.setInstrument(instrument);
        pattern.setTempo((int) tempo);
        patternCurrentTime = 0;
        volume = 10200d;

        // Construct URL to WordNet Dictionary directory on the computer
        wordNetDirectory = "WordNet-3.0";
        path = wordNetDirectory + File.separator + "dict";
        url = null;
        try {
            url = new URL("file", null, path);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    public static class ComposerBuilder {
        private double baseNoteLength;
        private double baseFrequency;
        private double baseOctaves;
        private double baseTempo;
        private String baseInstrument;
        private double restLength;
        private double restLengthLineBreak ;
        private double baseVolume;
        private int baseAttack;
        private int baseDecay;
        private long pitchBend;
        private int basePan;
        private noteOperationType operationType;
        private boolean isWord;
        private int ordering;
        private List<String> orderings;
        private List<TransformationManager.Instruction> instructions;
        private Set<String> passingWords;

        public ComposerBuilder() {
            this.baseVolume = 10200d;
            this.baseAttack = 64;
            this.baseDecay = 64;
            this.pitchBend = 8192;
            this.basePan = 64;

            orderings = new ArrayList<>();
            this.orderings.add("ETAOINSRHLDCUMFPGWYBVKXJQZ");
            this.orderings.add("ETAONRISHDLFCMUGYPWBVKXJQZ");
            this.orderings.add("EARIOTNSLCUDPMHGBFYWKVXZJQ");

            instructions = new ArrayList<>();

            passingWords = new HashSet<>();
        }

        public ComposerBuilder setNoteLength(double noteLength) {
            this.baseNoteLength = noteLength;
            return this;
        }

        public ComposerBuilder setFrequency(double frequency) {
            this.baseFrequency = frequency;
            return this;
        }

        public ComposerBuilder setOctave(double octave) {
            this.baseOctaves = octave;
            return this;
        }

        public ComposerBuilder setTempo(double tempo) {
            this.baseTempo = tempo;
            return this;
        }

        public ComposerBuilder setInstrument(String instrument) {
            this.baseInstrument = instrument;
            return this;
        }

        public ComposerBuilder setRestLength(double restLength) {
            this.restLength = restLength;
            return this;
        }

        public ComposerBuilder setRestLengthLineBreak(double restLengthLineBreak) {
            this.restLengthLineBreak = restLengthLineBreak;
            return this;
        }

        public ComposerBuilder wantWord(boolean isWord) {
            this.isWord = isWord;
            return this;
        }

        public ComposerBuilder withOperation(String type) {
            this.operationType = noteOperationType.valueOf(type);
            return this;
        }

        public ComposerBuilder withOrdering(int ordering) {
            this.ordering = ordering;
            return this;
        }

        public ComposerBuilder useTransformations(List<TransformationManager.Instruction> instructions) {
            this.instructions = instructions;
            return this;
        }

        public ComposerBuilder excludeWords(Set<String> passingWords) {
            this.passingWords = passingWords;
            return this;
        }

        public Composer build() {
            return new Composer(this);
        }
    }

    /**
     * Set variables to base values
     */
    public void resetSettings() {
        noteLength = baseNoteLength;
        frequency = baseFrequency;
        instrument = baseInstrument;
        octaves = baseOctaves;
        tempo = baseTempo;
        attack = baseAttack;
        decay = baseDecay;
        volume = baseVolume;
        pan = basePan;
    }

    /**
     * Keep parameters within reasonable ranges ie. BASE_FREQUENCY(16.0, 2048)
      */
    enum Setting {
        NOTE_LENGTH(0.01, 8.0), ARPEGGIATE_GAP(0.001, 0.5), REST_LENGTH(0.01, 0.5), BASE_FREQUENCY(16.0, 20000), OCTAVES(
                1.0, 10.0), TEMPO(6, 600), LETTER_ORDERING(0.0, 3.0), VOLUME(1.0, 16383), ATTACK(0, 127);

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
                    //System.out.println(this.toString() + " too low at " + value + ", swapping direction. RA = " + this.directionRollingAverage);
                    this.direction = !this.direction;
                }
                double returnValue = this.min + (this.min - value);
                if (returnValue > this.min && returnValue < this.max) {
                    return returnValue;
                } else {
                    //System.out.println("" + this + " return value " + returnValue + " out of range, instead " + ((returnValue % (this.max - this.min)) + this.min));
                    return (Math.abs(returnValue) % (this.max - this.min)) + this.min;
                }
            } else if (value > this.max) {
                this.directionRollingAverage = (this.directionRollingAverage + 1d) / 2d;
                if (this.directionRollingAverage > 0.8) {
                    //System.out.println(this.toString() + " too high at " + value + ", swapping direction. RA = " + this.directionRollingAverage);
                    this.direction = !this.direction;
                }
                double returnValue = this.max - (value - this.max);
                if (returnValue > this.min && returnValue < this.max) {
                    return returnValue;
                } else {
                    //System.out.println("" + this + " return value " + returnValue + " out of range, instead " + ((returnValue % (this.max - this.min)) + this.min));
                    return (Math.abs(returnValue) % (this.max - this.min)) + this.min;
                }
            } else {
                //System.out.println("" + this + " now " + value);
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

    /**
     * Turn the input string into a sound string that can be played by jFugue
     * @param input
     * @return
     */
    public Pattern processString(String input) {

        // Construct the Dictionary object and open it
        dict = new Dictionary(url);
        try {
            dict.open();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create CoreNLP document object
        Document doc = new Document(input);

        // Position of word
        int wordPosition;

        // Part-of-speech variable
        char posletter;

        // Doing per word operation
        if (isWord) {

            // Iterate sentences in input text
            for (Sentence sent : doc.sentences()) {

                // Find words in sentence
                List<String> words = sent.words();

                // Iterate words in sentence
                for (String word : words) {

                    // Get position of word
                    wordPosition = words.indexOf(word);

                    // First letter of pos tag per Penntree Bank notation is used to query WordNet
                    posletter = sent.posTag(wordPosition).charAt(0);

                    // Give pos tag of "S" if punctuation is found
                    if (java.util.regex.Pattern.matches("[\\p{Punct}\\p{IsPunctuation}]", word)) {
                        sonifyWord(word, sent.lemma(wordPosition), 'S', pattern);

                    // PassingWord is found
                    } else if (passingWords.contains(word)) {

                        pattern.add(" '(" + word + ")");
                        pattern.add(" #(SA[" + getSentimentAnalysis(word) + "])");

                        pattern.add("V0" + " @" + patternCurrentTime);

                        // Convert freq to MIDI music string using reference note and frequency A4 440hz
                        int baseMidiNumber = (int) Math.rint(12 * getLog(baseFrequency / 440.0f, 2) + 69.0f);
                        pattern.add("I[MUSIC_BOX] :PW(" + pitchBend + ") " + baseMidiNumber + "/" + noteLength + "a" + attack + "d" + decay);

                        // Insert at end of musicstring: Note + Resting gap
                        //soundString.append("R/" + String.format("%f", noteGap) + " ");
                        pattern.add("R/" + String.format("%f", noteGap) + " ");

                        // Reset to base settings
                        resetSettings();
                        pattern.add("I[" + instrument + "] ");
                        pattern.add("V0");
                        pattern.add(":CE(935," + (int) volume + ")");
                        pattern.add(":CE(10,64)");

                        patternCurrentTime = Math.round(patternCurrentTime * 100.0) / 100.0;
                        patternCurrentTime += noteLength + noteGap;

                    // Sonify word using WordNet
                    } else {
                        sonifyWord(word, sent.lemma(wordPosition), posletter, pattern);
                    }
                    //System.out.println("Sentiment Analysis (" + word + ") : " + analyse(word));

                    // Add rest between words
                    pattern.add("R/" + String.format("%f", restLength) + " ");
                    patternCurrentTime += restLength;
                    patternCurrentTime = Math.round(patternCurrentTime * 100.0) / 100.0;
                }

                // An extra rest on newlines
                pattern.add("R/" + String.format("%f", restLengthLineBreak) + " ");
                patternCurrentTime += restLengthLineBreak;
                patternCurrentTime = Math.round(patternCurrentTime * 100.0) / 100.0;

                // Sentiment Analysis
                // Add JFugue marker for SinglingPlayer parser to read
                pattern.add(" #(SENTENCE[" + sent.sentiment() + "])");
            }

        // Per character operation
        } else {
            StringBuilder lastWord = new StringBuilder();

            for (int charIndex = 0; charIndex < input.length(); charIndex++) {
                char ch = input.charAt(charIndex);
                char upperCh = Character.toUpperCase(ch);
                String charString = String.valueOf(ch);
                // A = 1, B = 2, ...
                String lastCharString;
                int charNum = orderings.get(ordering).indexOf(upperCh) + 1;

                lastWord.append(upperCh);

                sonifyCharacter(lastWord, pattern, charNum, ch);
            }
        }

        System.out.println(pattern.toString());
        return pattern;
    }

    /**
     *
     * @param originalWord
     * @param wordLemma
     * @param posLetter
     * @param pattern
     */
    public void sonifyWord(String originalWord, String wordLemma, char posLetter, Pattern pattern) {

        // LGCs to use for sonification
        Set<Integer> wordTypes = new HashSet<>();

        int posNumber = 0;

        // Map first letter of PennTree Bank postag to WordNet value
        if ("JNRV".contains(String.valueOf(posLetter))) {
            switch (posLetter) {
                case 'J': posNumber = POS.NUM_ADJECTIVE; break;
                case 'N': posNumber = POS.NUM_NOUN; break;
                case 'R': posNumber = POS.NUM_ADVERB; break;
                case 'V': posNumber = POS.NUM_VERB; break;
            }

            // Get WordNet word indices
            IIndexWord idxWord = dict.getIndexWord(wordLemma, POS.getPartOfSpeech(posNumber));

            // Store associated list of LGCs if found in WordNet
            if (idxWord != null) {

                // Get count of word's LGCs
                int x = idxWord.getWordIDs().size();
                //System.out.println("Number of wordIDs : " + idxWord.getWordIDs().size());

                // Loop to add all LGCs
                for (int i = 0; i < x; i++) {
                    IWordID wordID = idxWord.getWordIDs().get(i);
                    IWord word = dict.getWord(wordID);

                    //System.out.println("Id = " + wordID);
                    //System.out.println(" Lemma = " + word.getLemma());
                    //System.out.println(" Gloss = " + word.getSynset().getGloss());
                    ISynset synset = word.getSynset();
                    //String LexFileName = synset.getLexicalFile().getName();
                    //System.out.println("Lexical Name : "+ LexFileName + ":" + synset.getLexicalFile().getNumber());
                    wordTypes.add(synset.getLexicalFile().getNumber());
                }
            }

        // Other POStags
        } else {
            switch (posLetter) {
                // Determiner
                case 'D': wordTypes.add(46); break;
                // Modal
                case 'M': wordTypes.add(45); break;
                // Pronouns / Predeterminer / Possessive ending
                case 'P': wordTypes.add(51); break;
                // Preposition
                case 'I': wordTypes.add(48); break;
                // Symbols
                case 'S' : wordTypes.add(46); break;
            }
        }

        // First LGC of word will inherit the word as lyric item, sentiment analysis value, and other NLP related data
        if (lexCount == 0) {
            pattern.add(" '(" + originalWord + ")");
            pattern.add(" #(SA[" + getSentimentAnalysis(originalWord) + "], " + "LGC" + wordTypes + ", POS[" + posLetter + "]" +  ")");
        }

        // Iterate through list of LGC
        for (double lexname : wordTypes) {

            // Reset to base settings
            resetSettings();
            pattern.add("I[" + instrument + "] ");
            pattern.add(":CE(935," + (int) volume + ")");
            pattern.add(":CE(10," + (int) basePan + ")");

            // Set voice
            if (wordTypes.size() > 1) {
                // Skip Voice channel 9 as that is for percussion instruments
                if (lexCount == 9) {
                    lexCount++;
                }

                if (lexCount < 15) {
                    pattern.add("V" + lexCount + " @" + patternCurrentTime);
                }
            }

            // lexname + 1 because it starts at 0 in the database
            double targetOctave = Math.ceil(((lexname + 1) / 45d) * octaves); //26
            frequency = baseFrequency; // = convertToArr.toDoubleArr(item.getValue())[0]+1 * baseFrequency;

            switch (defaultNoteOperation) {
                case LEXNAMEFREQ:
                    frequency = (lexname + 1) * baseFrequency;
                    break;
                case STATICFREQ:
                    // If we want a default tone, leave freq as static
                    break;
                case MUTE:
                    // Mute tone
                    //noteLength = 0;
                    pattern.add(":CE(935,0)");
                    //volume = 0;
                    break;
            }

            // Go through the instructions queue
            for (TransformationManager.Instruction i : instructions) {

                // Make changes based on user instructions
                if (i.mod == TransformationManager.Instruction.Mods.WORDTYPE) {
                    switch (posLetter) {
                        case 'J':
                            if (i.modValue.equals("adjective")) { applyMod(i, pattern); }
                            break;
                        case 'N':
                            if (i.modValue.equals("noun")) { applyMod(i, pattern); }
                            break;
                        case 'R':
                            if (i.modValue.equals("adverb")) { applyMod(i, pattern); }
                            break;
                        case 'V':
                            if (i.modValue.equals("verb")) { applyMod(i, pattern); }
                            break;
                        case 'M':
                            if (i.modValue.equals("modal")) { applyMod(i, pattern); }
                            break;
                        case 'S':
                            if (i.modValue.equals("symbol")) { applyMod(i, pattern); }
                            break;
                        case 'D':
                            if (i.modValue.equals("determiner")) { applyMod(i, pattern); }
                            break;
                        case 'I':
                            if (i.modValue.equals("preposition")) { applyMod(i, pattern); }
                            break;
                        case 'P':
                            if (i.modValue.equals("pronoun")) { applyMod(i, pattern); }
                            break;
                    }

                } else if (i.mod == TransformationManager.Instruction.Mods.WORDLENGTH) {
                    switch (i.getModOperator()) {
                        case EQUALTO:
                            if (Double.parseDouble(i.getModValue()) == originalWord.length()) {
                                applyMod(i, pattern);
                            }
                            break;
                        case LARGERTHAN:
                            if (Double.parseDouble(i.getModValue()) < originalWord.length()) {
                                applyMod(i, pattern);
                            }
                            break;
                        case LESSTHAN:
                            if (Double.parseDouble(i.getModValue()) > originalWord.length()) {
                                applyMod(i, pattern);
                            }
                            break;
                    }

                } else if (i.mod == TransformationManager.Instruction.Mods.LGC) {
                    if (lexname == Double.parseDouble(i.modValue)) {
                        //System.out.println("Equal: " + convertToArr.toDoubleArr(item.getValue())[0] + " | " + Double.parseDouble(i.modValue));
                        applyMod(i, pattern);
                    }

                } else if (i.mod == TransformationManager.Instruction.Mods.PUNCTUATION) {
                    //String[] punctuations = convertToArr.toStringArr(item.getValue());

                    //for (String n : punctuations) {
                    if (wordLemma.equals(i.modValue)) {
                        //System.out.println("Equal: " + convertToArr.toDoubleArr(item.getValue())[0] + " | " + Double.parseDouble(i.modValue));
                        applyMod(i, pattern);
                    }
                    //}
                }
            }

            //frequency = Math.round(frequency * 100.0) / 100.0;

            // Make chord based on sentiment analysis value
            String sentimentChord = "";
            double sumSentimentValue;
            //if (item.getSentimentPos() != null && item.getSentimentPos() != "NULL" && !item.getSentimentPos().equalsIgnoreCase("0")) {
            //	//System.out.println(makeMajorChord(Double.parseDouble(item.wordSentimentPos)));
            //	sentimentChord = makeMajorChord(Double.parseDouble(item.wordSentimentPos));
            //} else if (item.getSentimentNeg() != null && item.getSentimentNeg() != "NULL" && !item.getSentimentNeg().equalsIgnoreCase("0")) {
            //	//System.out.println(makeMinorChord(Double.parseDouble(item.wordSentimentNeg)));
            //	sentimentChord = makeMinorChord(Double.parseDouble(item.wordSentimentNeg));
            //}
				/*if (item.getSentimentPos() != null && item.getSentimentPos() != "NULL" && item.getSentimentNeg() != null && item.getSentimentNeg() != "NULL") {
					sumSentimentValue = Double.parseDouble(item.getSentimentPos()) + (Double.parseDouble(item.getSentimentNeg()) * -1);

					if (sumSentimentValue >= 0) {
						sentimentChord = makeMajorChord(sumSentimentValue);
					} else {
						sentimentChord = makeMinorChord(sumSentimentValue * -1);
					}
				}*/

            // Normalise to fit in the range
            double topFrequency = baseFrequency;
            for (int j = 0; j < targetOctave; j++) {
                topFrequency = topFrequency * 2;
            }
            while (frequency > topFrequency) {
                frequency = frequency / 2;
            }

            // Convert freq to MIDI music string using reference note and frequency A4 440hz
            int midiNumber = (int) Math.rint(12 * getLog(frequency / 440.0f, 2) + 69.0f);
            int baseMidiNumber = (int) Math.rint(12 * getLog(baseFrequency / 440.0f, 2) + 69.0f);

            // Find pitch using base midi note number
            pitchBend = Math.round(8192 + 4096 * 12 * getLog(frequency / (440.0f * Math.pow(2.0f, ((double) midiNumber - 69.0f) / 12.0f)), 2));
            //System.out.println("Pitch bend: " + pitchBend);
            //System.out.println("Frequency: " + frequency);
            //System.out.println("Midi Number: " + midiNumber);

            // JFugue's implementation which adds microtones as pitch bend events
            //pattern.add("m" + frequency + sentimentChord + "/" + noteLength + "a" + attack + "d" + decay + "");
            //pattern.add("m" + frequency + "/" + noteLength + "a" + attack + "d" + decay + "");
            //pattern.add("m512.3q");
            //pattern.add(":PitchWheel(5192) 72/0.25 :PitchWheel(8192)");

            //pattern.add(":PW(" + (int) pitchBend + ") " +  midiNumber + sentimentChord + "/" + noteLength + "a" + attack + "d" + decay + ":PW(8192)");
            //pattern.add(":PW(" + pitchBend + ") " +  midiNumber + sentimentChord + "/" + noteLength + "a" + attack + "d" + decay + " '" + lastWord);
            //pattern.add(":PW(" + pitchBend + ") " + midiNumber + sentimentChord + "/" + noteLength + "a" + attack + "d" + decay);

            //System.out.println("sentiment" + sentimentChord);
            // Midi message to send to player
            // If sentiment values are found, use the base (fundamental) frequency without pitchbend to create a chord
            // Otherwise, use the LGC as a variable to create a midi note

            // Sentiment Analysis

			/*if (sentimentState && sentimentChord != "") {
				pattern.add(baseMidiNumber + sentimentChord + "/" + noteLength + "a" + attack + "d" + decay);
			} else {
				pattern.add(":PW(" + pitchBend + ") " + midiNumber + "/" + noteLength + "a" + attack + "d" + decay);
			}*/

            pattern.add(":PW(" + pitchBend + ") " + midiNumber + "/" + noteLength + "a" + attack + "d" + decay);
            //pattern.add(baseMidiNumber + "/" + noteLength + "a" + attack + "d" + decay);

            //	}
            //}

            //System.out.println("Convert frequency: " + frequency + " to note: " + midiNumber);

            lexCount++;
        }

        // Note gap
        pattern.add("R/" + String.format("%f", noteGap) + " ");
        pattern.add("V0");

        patternCurrentTime = Math.round(patternCurrentTime * 100.0) / 100.0;
        patternCurrentTime += noteLength + noteGap;

        lexCount = 0;
    }

    /**
     *
     * @param lastWord
     * @param pattern
     * @param charNum
     * @param ch
     */
    public void sonifyCharacter(StringBuilder lastWord, Pattern pattern, double charNum, char ch) {
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

        // Go through the instructions queue
        for (TransformationManager.Instruction i : instructions) {

            // The main logic part of the program
            // Make changes based on user instructions
            if (i.mod == TransformationManager.Instruction.Mods.CHARACTER) {
                //System.out.println("AEIOUaeiou".indexOf(ch));
                if (i.modValue.equals("vowels") && "AEIOUaeiou".indexOf(ch) != -1) {
                    applyMod(i, pattern);
                } else if (i.modValue.equals("consonants") && "AEIOUaeiou".indexOf(ch) < 0) {
                    applyMod(i, pattern);
                } else if (i.modValue.equals("uppercase") && Character.isUpperCase(ch)) {
                    applyMod(i, pattern);
                    //System.out.println("uppercase");
                } else if (i.modValue.equals("lowercase") && Character.isLowerCase(ch)) {
                    applyMod(i, pattern);
                    //System.out.println("lowercase");
                }

            } else if (i.mod == TransformationManager.Instruction.Mods.PUNCTUATION) {
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
        int midiNumber = (int) Math.rint(12 * getLog(frequency/440.0f, 2) + 69.0f);

        // Find pitch using base midi note number
        pitchBend = Math.round(8192+4096*12 * getLog(frequency/(440.0f*Math.pow(2.0f, ((double)midiNumber-69.0f)/12.0f)), 2));

        // Set frequency of punctuations/symbols to frequency of lexname = 46
        if (java.util.regex.Pattern.matches("[\\p{Punct}\\p{IsPunctuation}]", String.valueOf(ch))) {
            targetOctave = Math.ceil((46/45d) * octaves); //26
            frequency = 46 * baseFrequency;
        }

        //pattern.add("m" + frequency + "/" + noteLength + "a" + attack + "d" + decay);
        //System.out.println("Convert frequency: " + frequency + ", note length: " + noteLength);
        pattern.add(":PW(" + pitchBend + ") " +  midiNumber + "/" + noteLength + "a" + attack + "d" + decay);

        //pattern.add(" '" + ch);

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
        pattern.add("V0");
        pattern.add(":CE(935," + (int) volume + ")");
        pattern.add(":CE(10,64)");
    }


    /**
     *
     * @param i
     * @param pattern
     */
    public void applyMod(TransformationManager.Instruction i, Pattern pattern) {
        // Allow sound instructions to be played if notes are set to mute in default settings
        if (defaultNoteOperation == noteOperationType.MUTE) { pattern.add(":CE(935,10200)"); }

        switch (i.soundMod) {
            case TEMPO:
                Setting settingTempo = Setting.TEMPO;
                //if (lexCount <= 0) {
                if (i.changeMode == TransformationManager.Instruction.ChangeModes.SET) {
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
                if (i.changeMode == TransformationManager.Instruction.ChangeModes.SET) {
                    noteLength = settingNoteDuration.keepInRange(noteLength);
                    noteLength = Double.parseDouble(i.soundModValue);
                } else {
                    noteLength = settingNoteDuration.keepInRange(noteLength);
                    noteLength += Double.parseDouble(i.soundModValue);
                    baseNoteLength = noteLength;
                }
                //baseNoteLength = noteLength;
                break;

            case OCTAVE:
                Setting settingOctaves = Setting.OCTAVES;
                if (i.changeMode == TransformationManager.Instruction.ChangeModes.SET) {
                    octaves = settingOctaves.keepInRange(octaves);
                    octaves = Double.parseDouble(i.soundModValue);
                } else {
                    octaves = settingOctaves.keepInRange(octaves);
                    octaves += Double.parseDouble(i.soundModValue);
                    baseOctaves = octaves;
                }
                //baseOctaves = octaves;
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
                pattern.add(":CE(935," + (int) volume + ")");
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
                //double midiNoteNumber = Double.parseDouble(i.soundModValue);

                //if (i.changeMode == TransformationManager.Instruction.ChangeModes.SET) {
                //frequency = Math.pow(2, (midiNoteNumber - 69) / 12) * 440;
                frequency = Note.getFrequencyForNote(i.soundModValue)*2;
                frequency = settingsFrequency.keepInRange(frequency);
                //} else {
                //double tempFreq = Math.pow(2, (midiNoteNumber - 69) / 12) * 440;
                //frequency += tempFreq;
                //	frequency = settingsFrequency.keepInRange(frequency);
                //	baseFrequency = frequency;
                //}
                //System.out.println("Change freq to: " + i.soundModValue);
                //System.out.println("Note: " + i.soundModValue + ", Freq: " + frequency);
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

            case PITCHBEND:
                if (i.changeMode == TransformationManager.Instruction.ChangeModes.SET) {
                    pitchBend = Long.parseLong(i.soundModValue);
                } else {
                    pitchBend += Double.parseDouble(i.soundModValue);
                }
                break;

            case PAN:
                pan = Integer.parseInt(i.soundModValue);
                pattern.add(":CE(10," + pan + ")");
                break;
        }
    }

    /**
     *
     * @param text
     * @return
     */
    public int getSentimentAnalysis(String text) {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, parse, sentiment");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        Annotation annotation = pipeline.process(text);
        for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
            Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
            return RNNCoreAnnotations.getPredictedClass(tree);
        }
        return 0;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public List<TransformationManager.Instruction> getInstructions() {
        return this.instructions;
    }

    public void addInstruction(TransformationManager.Instruction instruction) {
        this.instructions.add(instruction);
    }

    public void removeInstruction(TransformationManager.Instruction instruction) {
        this.instructions.remove(instruction);
    }

    public double getLog(double x, double base) {
        return (Math.log(x) / Math.log(base));
    }
}