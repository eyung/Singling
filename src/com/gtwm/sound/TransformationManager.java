package com.gtwm.sound;

import java.io.Serializable;

public class TransformationManager {

    static class Instruction implements Serializable {

        enum Mods { WORDTYPE, WORDLENGTH, WORDVALUE, PUNCTUATION, CHARACTER, SENTIMENT }

        enum SoundMods { TEMPO, NOTE_DURATION, OCTAVE, INSTRUMENT, VOLUME, PERCUSSION, MIDI_NOTE, ATTACK, DECAY, MAJOR, MINOR }

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
                    mod.equals(Mods.WORDVALUE) ||
                    mod.equals(Mods.CHARACTER)) {
                return mod +
                        ":" + modValue +
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
