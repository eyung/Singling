package com.gtwm.sound;

import javax.swing.*;

public class InstructionFormModels {

    final static String setSoundToLabels[] = {"TEMPO", "NOTE_DURATION", "OCTAVE", "VOLUME", "INSTRUMENT", "MIDI_NOTE", "ATTACK", "DECAY", "PITCHBEND"};
    final static String setSoundByLabels[] = {"TEMPO", "NOTE_DURATION", "OCTAVE", "VOLUME", "PITCHBEND"};
    final static String tempoLabels[] = {"40", "45", "50", "55", "60", "65", "70", "80", "95", "110", "120", "145", "180", "220"};
    //final static String tempoLabels[] = {"GRAVE", "LARGO", "LARGHETTO", "LENTO", "ADAGIO", "ADAGIETTO", "ANDANTE", "ANDANTINO",
    //                                        "MODERATO", "ALLEGRETTO", "ALLEGRO", "VIVACE", "PRESTO", "PRESTISSIMO"};
    final static String noteDurationLabels[] = {"1.00", "0.50", "0.25", "0.125", "0.0625", "0.03125", "0.015625", "0.0078125"};
    final static String octavesLabel[] = {"0", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    final static String frequencyLabel[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "90", "91", "92", "93", "94", "95", "96", "97", "98", "99", "100", "101", "102", "103", "104", "105", "106", "107", "108", "109", "110", "111", "112", "113", "114", "115", "116", "117", "118", "119", "120", "121", "122", "123", "124", "125", "126", "127"};
    //final static String attackLabel[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "90", "91", "92", "93", "94", "95", "96", "97", "98", "99", "100", "101", "102", "103", "104", "105", "106", "107", "108", "109", "110", "111", "112", "113", "114", "115", "116", "117", "118", "119", "120", "121", "122", "123", "124", "125", "126", "127"};
    //final static String decayLabel[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "90", "91", "92", "93", "94", "95", "96", "97", "98", "99", "100", "101", "102", "103", "104", "105", "106", "107", "108", "109", "110", "111", "112", "113", "114", "115", "116", "117", "118", "119", "120", "121", "122", "123", "124", "125", "126", "127"};
    final static String instrumentLabel[] = {"PIANO",
            "BRIGHT_ACOUSTIC",
            "ELECTRIC_GRAND",
            "HONKEY_TONK",
            "ELECTRIC_PIANO",
            "ELECTRIC_PIANO2",
            "HARPISCHORD",
            "CLAVINET",
            "CELESTA",
            "GLOCKENSPIEL",
            "MUSIC_BOX",
            "VIBRAPHONE",
            "MARIMBA",
            "XYLOPHONE",
            "TUBULAR_BELLS",
            "DULCIMER",
            "DRAWBAR_ORGAN",
            "PERCUSSIVE_ORGAN",
            "ROCK_ORGAN",
            "CHURCH_ORGAN",
            "REED_ORGAN",
            "ACCORIDAN",
            "HARMONICA",
            "TANGO_ACCORDIAN",
            "GUITAR",
            "STEEL_STRING_GUITAR",
            "ELECTRIC_JAZZ_GUITAR",
            "ELECTRIC_CLEAN_GUITAR",
            "ELECTRIC_MUTED_GUITAR",
            "OVERDRIVEN_GUITAR",
            "DISTORTION_GUITAR",
            "GUITAR_HARMONICS",
            "ACOUSTIC_BASS",
            "ELECTRIC_BASS_FINGER",
            "ELECTRIC_BASS_PICK",
            "FRETLESS_BASS",
            "SLAP_BASS_1",
            "SLAP_BASS_2",
            "SYNTH_BASS_1",
            "SYNTH_BASS_2",
            "VIOLIN",
            "VIOLA",
            "CELLO",
            "CONTRABASS",
            "TREMOLO_STRINGS",
            "PIZZICATO_STRINGS",
            "ORCHESTRAL_STRINGS",
            "TIMPANI",
            "STRING_ENSEMBLE_1",
            "STRING_ENSEMBLE_2",
            "SYNTH_STRINGS_1",
            "SYNTH_STRINGS_2",
            "CHOIR_AAHS",
            "VOICE_OOHS",
            "SYNTH_VOICE",
            "ORCHESTRA_HIT",
            "TRUMPET",
            "TROMBONE",
            "TUBA",
            "MUTED_TRUMPET",
            "FRENCH_HORN",
            "BRASS_SECTION",
            "SYNTHBRASS_1",
            "SOPRANO_SAX",
            "ALTO_SAX",
            "TENOR_SAX",
            "BARITONE_SAX",
            "OBOE",
            "ENGLISH_HORN",
            "BASSOON",
            "CLARINET",
            "PICCOLO",
            "FLUTE",
            "RECORDER",
            "PAN_FLUTE",
            "BLOWN_BOTTLE",
            "SKAKUHACHI",
            "WHISTLE",
            "OCARINA",
            "LEAD_SQUARE",
            "LEAD_SAWTOOTH",
            "LEAD_CALLIOPE",
            "LEAD_CHIFF",
            "LEAD_CHARANG",
            "LEAD_VOICE",
            "LEAD_FIFTHS",
            "LEAD_BASSLEAD",
            "PAD_NEW_AGE",
            "PAD_WARM",
            "PAD_POLYSYNTH",
            "PAD_CHOIR",
            "PAD_BOWED",
            "PAD_METALLIC",
            "PAD_HALO",
            "PAD_SWEEP",
            "FX_RAIN",
            "FX_SOUNDTRACK",
            "FX_CRYSTAL",
            "FX_ATMOSPHERE",
            "FX_BRIGHTNESS",
            "FX_GOBLINS",
            "FX_ECHOES",
            "FX_SCI-FI",
            "SITAR",
            "BANJO",
            "SHAMISEN",
            "KOTO",
            "KALIMBA",
            "BAGPIPE",
            "FIDDLE",
            "SHANAI",
            "TINKLE_BELL",
            "AGOGO",
            "STEEL_DRUMS",
            "WOODBLOCK",
            "TAIKO_DRUM",
            "MELODIC_TOM",
            "SYNTH_DRUM",
            "REVERSE_CYMBAL",
            "GUITAR_FRET_NOISE",
            "BREATH_NOISE",
            "SEASHORE",
            "BIRD_TWEET",
            "TELEPHONE_RING",
            "HELICOPTER",
            "APPLAUSE",
            "GUNSHOT"
    };

    // Set
    final static DefaultComboBoxModel modelSetSoundTo = new DefaultComboBoxModel(setSoundToLabels);
    final static DefaultComboBoxModel modelSetSoundBy = new DefaultComboBoxModel(setSoundByLabels);
    final static DefaultComboBoxModel modelSetTempo = new DefaultComboBoxModel(tempoLabels);
    final static DefaultComboBoxModel modelSetNoteDuration = new DefaultComboBoxModel(noteDurationLabels);
    final static DefaultComboBoxModel modelSetOctave = new DefaultComboBoxModel(octavesLabel);
    final static DefaultComboBoxModel modelSetFrequency = new DefaultComboBoxModel(frequencyLabel);
    //final static DefaultComboBoxModel modelSetAttack = new DefaultComboBoxModel(attackLabel);
    //final static DefaultComboBoxModel modelSetDecay = new DefaultComboBoxModel(decayLabel);
    final static DefaultBoundedRangeModel modelSetAttack = new DefaultBoundedRangeModel(64, 0, 0, 127);
    final static DefaultBoundedRangeModel modelSetDecay = new DefaultBoundedRangeModel(64, 0, 0, 127);
    final static DefaultComboBoxModel modelSetInstrument = new DefaultComboBoxModel(instrumentLabel);

    // Increment
    final static SpinnerNumberModel modelIncrementTempo =
            new SpinnerNumberModel(5.0, -30.0, 30.0, 5.0);
    final static SpinnerNumberModel modelIncrementNoteDuration =
            new SpinnerNumberModel(0.10, -1.0, 1.0, 0.10);
    final static SpinnerNumberModel modelIncrementOctave =
            new SpinnerNumberModel(1.0, -10.0, 10.0, 1.0);
    final static SpinnerNumberModel modelIncrementVolume =
            new SpinnerNumberModel(1000.0, -10000.0, 10000.0, 1000.0);
    final static SpinnerNumberModel modelIncrementFrequency =
            new SpinnerNumberModel(5.0, -60.0, 60.0, 5.0);
    final static SpinnerNumberModel modelIncrementPitchbend =
            new SpinnerNumberModel(1000.0, -10000.0, 10000.0, 1000.0);

    final static SpinnerNumberModel modelSetLength =
            new SpinnerNumberModel(0.0, 0.0, 50.0, 1.0);
}
