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
    //final static String frequencyLabel[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "90", "91", "92", "93", "94", "95", "96", "97", "98", "99", "100", "101", "102", "103", "104", "105", "106", "107", "108", "109", "110", "111", "112", "113", "114", "115", "116", "117", "118", "119", "120", "121", "122", "123", "124", "125", "126", "127"};
    final static String frequencyLabel[] ={"A0",	"B0",	"C1",	"D1",	"E1",	"F1",	"G1",	"A1",	"B1",	"C2",	"D2",	"E2",	"F2",	"G2",	"A2",	"B2",	"CX3",	"D3",	"E3",	"F3",	"G3",	"A3",	"B3",	"C4",	"D4",	"E4",	"F4",	"G4",	"A4",	"B4",	"C5",	"D5",	"E5",	"F5",	"G5",	"A5",	"B5",	"C6",	"D6",	"E6",	"F6",	"G6",	"A6",	"B6",	"C7",	"D7",	"E7",	"F7",	"G7",	"A7",	"B7",	"C8",};
    //final static String attackLabel[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "90", "91", "92", "93", "94", "95", "96", "97", "98", "99", "100", "101", "102", "103", "104", "105", "106", "107", "108", "109", "110", "111", "112", "113", "114", "115", "116", "117", "118", "119", "120", "121", "122", "123", "124", "125", "126", "127"};
    //final static String decayLabel[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "90", "91", "92", "93", "94", "95", "96", "97", "98", "99", "100", "101", "102", "103", "104", "105", "106", "107", "108", "109", "110", "111", "112", "113", "114", "115", "116", "117", "118", "119", "120", "121", "122", "123", "124", "125", "126", "127"};
    final static String instrumentLabel[] = {"PIANO",
            "BRIGHT_ACOUSTIC",
            "ELECTRIC_GRAND",
            "HONKEY_TONK",
            "ELECTRIC_PIANO",
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
    final static String[] LGClist = {
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

    // LGC
    final static DefaultComboBoxModel modelSetLGC = new DefaultComboBoxModel(LGClist);

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
    final static SpinnerNumberModel modelSetLength =
            new SpinnerNumberModel(0.0, 0.0, 50.0, 1.0);
    final static SpinnerNumberModel modelSpinnerPercentage =
            new SpinnerNumberModel(0.0, 0.0, 100.0, 1.0);

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


}
