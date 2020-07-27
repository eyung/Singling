package com.gtwm.sound;

import java.io.Serializable;

public class TransformationManager {

    static final String[] LGClist = {
            "all adjective clusters",
            "relational adjectives (pertainyms)",
            "all adverbs",
            "unique beginner for nouns",
            "nouns denoting acts or actions",
            "nouns denoting animals",
            "nouns denoting man-made objects",
            "nouns denoting attributes of people and objects",
            "nouns denoting body parts",
            "nouns denoting cognitive processes and contents",
            "nouns denoting communicative processes and contents",
            "nouns denoting natural events",
            "nouns denoting feelings and emotions",
            "nouns denoting foods and drinks",
            "nouns denoting groupings of people or objects",
            "nouns denoting spatial position",
            "nouns denoting goals",
            "nouns denoting natural objects (not man-made)",
            "nouns denoting people",
            "nouns denoting natural phenomena",
            "nouns denoting plants",
            "nouns denoting possession and transfer of possession",
            "nouns denoting natural processes",
            "nouns denoting quantities and units of measure",
            "nouns denoting relations between people or things or ideas",
            "nouns denoting two and three dimensional shapes",
            "nouns denoting stable states of affairs",
            "nouns denoting substances",
            "nouns denoting time and temporal relations",
            "verbs of grooming, dressing and bodily care",
            "verbs of size, temperature change, intensifying, etc.",
            "verbs of thinking, judging, analyzing, doubting",
            "verbs of telling, asking, ordering, singing",
            "verbs of fighting, athletic activities",
            "verbs of eating and drinking",
            "verbs of touching, hitting, tying, digging",
            "verbs of sewing, baking, painting, performing",
            "verbs of feeling",
            "verbs of walking, flying, swimming",
            "verbs of seeing, hearing, feeling",
            "verbs of buying, selling, owning",
            "verbs of political and social activities and events",
            "verbs of being, having, spatial relations",
            "verbs of raining, snowing, thawing, thundering",
            "participial adjectives",
            "modals all",
            "symbols all",
            "emojis all",
            "agential prepositions",
            "relational prepositions",
            "time prepositions"
    };

    static class Instruction implements Serializable {

        enum Mods { WORDTYPE, WORDLENGTH, LGC, PUNCTUATION, CHARACTER, SENTIMENT }

        enum SoundMods { TEMPO, NOTE_DURATION, OCTAVE, INSTRUMENT, VOLUME, PERCUSSION, MIDI_NOTE, ATTACK, DECAY, MAJOR, MINOR, PITCHBEND }

        enum ModOperators { EQUALTO, LARGERTHAN, LESSTHAN }

        enum ChangeModes { SET, INCREMENT }

        enum SentimentTypes { POSITIVESENTIMENT, NEGATIVESENTIMENT }

        enum InstructionStatus { ACTIVE, INACTIVE }

        Mods mod;
        ModOperators modOperator;
        String modValue;
        SoundMods soundMod;
        String soundModValue;
        ChangeModes changeMode;
        SentimentTypes sentimentType;
        InstructionStatus instructionStatus;

        public Instruction() {

        }

        public Instruction(Mods v, ModOperators w, String x, SoundMods y, String z, ChangeModes a, SentimentTypes b, InstructionStatus c) {
            this.mod = v;
            this.modOperator = w;
            this.modValue = x;
            this.soundMod = y;
            this.soundModValue = z;
            this.changeMode = a;
            this.sentimentType = b;
            this.instructionStatus = c;
        }

        public void setMod(Mods thisMod) {
            mod = thisMod;
        }

        public Mods getMod() {
            return mod;
        }

        public void setModOperator(ModOperators thisModOperator) {
            modOperator = thisModOperator;
        }

        public ModOperators getModOperator() { return modOperator; }

        public void setModValue(String thisModValue) {
            modValue = thisModValue;
        }

        public String getModValue() {
            return modValue;
        }

        public void setSoundMod(SoundMods thisSoundMod) {
            soundMod = thisSoundMod;
        }

        public SoundMods getSoundMod() {
            return soundMod;
        }

        public void setSoundModValue(String thisSoundModValue) {
            soundModValue = thisSoundModValue;
        }

        public String getSoundModValue() {
            return soundModValue;
        }

        public void setChangeMode(ChangeModes thisChangeMode) {
            changeMode = thisChangeMode;
        }

        public ChangeModes getChangeMode() {
            return changeMode;
        }

        public void setSentimentType(SentimentTypes thisSentimentType) { sentimentType = thisSentimentType; }

        public SentimentTypes getSentimentType() { return sentimentType; }

        public void setInstructionStatus(InstructionStatus thisInstructionStatus) {
            instructionStatus = thisInstructionStatus;
        }

        public InstructionStatus getInstructionStatus() {
            return instructionStatus;
        }

        public String toString() {
            if (mod.equals(Mods.WORDLENGTH)) {
                return "WORDLENGTH " + modOperator +
                        " " + modValue +
                        " TO " + changeMode +
                        " " + soundMod +
                        " " + soundModValue;
            } else if (mod.equals(Mods.WORDTYPE) ||
                    mod.equals(Mods.PUNCTUATION) ||
                    mod.equals(Mods.CHARACTER)) {
                return mod +
                        ":" + modValue +
                        " TO " + changeMode +
                        " " + soundMod +
                        " " + soundModValue;
            } else if (mod.equals((Mods.LGC))) {
                return mod +
                        ":" + LGClist[Integer.parseInt(modValue)] +
                        " TO " + changeMode +
                        " " + soundMod +
                        " " + soundModValue;
            } else if (mod.equals(Mods.SENTIMENT)) {
                return "SENTIMENT " + sentimentType + " CHANGING " + soundMod;
            } else {
                return "Instruction:{" +
                        "mod=" + mod +
                        " modoperator=" + modOperator +
                        " modvalue=" + modValue +
                        " soundmod=" + soundMod +
                        " soundmodvalue=" + soundModValue +
                        " changemode=" + changeMode +
                        '}';
            }
        }
    }
}
