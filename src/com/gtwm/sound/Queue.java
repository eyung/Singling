package com.gtwm.sound;

public class Queue {

    static class Instruction {

        enum Mods {
            WORDTYPE, WORDLENGTH
        }

        enum SoundMods {
            TEMPO, NOTEDURATION, OCTAVE, INSTRUMENT, VOLUME, PERCUSSION
        }

        enum ModOperators {
            EQUALTO, LARGERTHAN, LESSTHAN
        }

        Mods mod;

        ModOperators modOperator;

        String modValue;

        SoundMods soundMod;

        String soundModValue;

        public Instruction() {

        }

        public Instruction(Mods v, ModOperators w, String x, SoundMods y, String z) {
            this.mod = v;
            this.modOperator = w;
            this.modValue = x;
            this.soundMod = y;
            this.soundModValue = z;
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

        public String toString() {
            return "Instruction:{" +
                    "mod=" + mod +
                    " modoperator=" + modOperator +
                    " modvalue=" + modValue +
                    " soundmod=" + soundMod +
                    " soundmodvalue=" + soundModValue +
                    '}';
        }
    }
}
