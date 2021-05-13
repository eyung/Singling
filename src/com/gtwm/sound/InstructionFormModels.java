package com.gtwm.sound;

import javax.swing.*;

public class InstructionFormModels {

    final static String setSoundToLabels[] = {"TEMPO", "NOTE_DURATION", "OCTAVE", "VOLUME", "INSTRUMENT", "MIDI_NOTE", "ATTACK", "DECAY", "PITCHBEND", "PAN"};
    final static String setSoundByLabels[] = {"TEMPO", "NOTE_DURATION", "OCTAVE", "VOLUME", "PITCHBEND"};
    final static String tempoLabels[] = {"40", "45", "50", "55", "60", "65", "70", "80", "95", "110", "120", "145", "180", "220"};
    //final static String tempoLabels[] = {"GRAVE", "LARGO", "LARGHETTO", "LENTO", "ADAGIO", "ADAGIETTO", "ANDANTE", "ANDANTINO",
    //                                        "MODERATO", "ALLEGRETTO", "ALLEGRO", "VIVACE", "PRESTO", "PRESTISSIMO"};
    final static String noteDurationLabels[] = {"1.00", "0.50", "0.25", "0.125", "0.0625", "0.03125", "0.015625", "0.0078125"};
    final static String octavesLabel[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    //final static String frequencyLabel[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "90", "91", "92", "93", "94", "95", "96", "97", "98", "99", "100", "101", "102", "103", "104", "105", "106", "107", "108", "109", "110", "111", "112", "113", "114", "115", "116", "117", "118", "119", "120", "121", "122", "123", "124", "125", "126", "127"};
    //final static String frequencyLabel[] ={"A0",	"B0",	"C1",	"D1",	"E1",	"F1",	"G1",	"A1",	"B1",	"C2",	"D2",	"E2",	"F2",	"G2",	"A2",	"B2",	"CX3",	"D3",	"E3",	"F3",	"G3",	"A3",	"B3",	"C4",	"D4",	"E4",	"F4",	"G4",	"A4",	"B4",	"C5",	"D5",	"E5",	"F5",	"G5",	"A5",	"B5",	"C6",	"D6",	"E6",	"F6",	"G6",	"A6",	"B6",	"C7",	"D7",	"E7",	"F7",	"G7",	"A7",	"B7",	"C8",};
    final static String frequencyLabel[] = {"A0",	"A#0",	"B0",	"C1",	"C#1",	"D1",	"D#1",	"E1",	"F1",	"F#1",	"G1",	"G#1",	"A1",	"A#1",	"B1",	"C2",	"C#2",	"D2",	"D#2",	"E2",	"F2",	"F#2",	"G2",	"G#2",	"A2",	"A#2",	"B2",	"C3",	"C#3",	"D3",	"D#3",	"E3",	"F3",	"F#3",	"G3",	"G#3",	"A3",	"A#3",	"B3",	"C4",	"C#4",	"D4",	"D#4",	"E4",	"F4",	"F#4",	"G4",	"G#4",	"A4",	"A#4",	"B4",	"C5",	"C#5",	"D5",	"D#5",	"E5",	"F5",	"F#5",	"G5",	"G#5",	"A5",	"A#5",	"B5",	"C6",	"C#6",	"D6",	"D#6",	"E6",	"F6",	"F#6",	"G6",	"G#6",	"A6",	"A#6",	"B6",	"C7",	"C#7",	"D7",	"D#7",	"E7",	"F7",	"F#7",	"G7",	"G#7",	"A7",	"A#7",	"B7",	"C8"};
    //final static String attackLabel[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "90", "91", "92", "93", "94", "95", "96", "97", "98", "99", "100", "101", "102", "103", "104", "105", "106", "107", "108", "109", "110", "111", "112", "113", "114", "115", "116", "117", "118", "119", "120", "121", "122", "123", "124", "125", "126", "127"};
    //final static String decayLabel[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "90", "91", "92", "93", "94", "95", "96", "97", "98", "99", "100", "101", "102", "103", "104", "105", "106", "107", "108", "109", "110", "111", "112", "113", "114", "115", "116", "117", "118", "119", "120", "121", "122", "123", "124", "125", "126", "127"};
    final static String instrumentLabel[] = {"PIANO",
            "BRIGHT_ACOUSTIC",
            "ELECTRIC_GRAND",
            "HONKEY_TONK",
            "ELECTRIC_PIANO",
            "HARPSICHORD",
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
            "1. all adjective clusters",
            "2. relational adjectives (pertainyms)",
            "3. all adverbs",
            "4. unique beginner for nouns",
            "5. nouns denoting acts or actions",
            "6. nouns denoting animals",
            "7. nouns denoting man-made objects",
            "8. nouns denoting attributes of people and objects",
            "9. nouns denoting body parts",
            "10. nouns denoting cognitive processes and contents",
            "11. nouns denoting communicative processes and contents",
            "12. nouns denoting natural events",
            "13. nouns denoting feelings and emotions",
            "14. nouns denoting foods and drinks",
            "15. nouns denoting groupings of people or objects",
            "16. nouns denoting spatial position",
            "17. nouns denoting goals",
            "18. nouns denoting natural objects (not man-made)",
            "19. nouns denoting people",
            "20. nouns denoting natural phenomena",
            "21. nouns denoting plants",
            "22. nouns denoting possession and transfer of possession",
            "23. nouns denoting natural processes",
            "24. nouns denoting quantities and units of measure",
            "25. nouns denoting relations between people or things or ideas",
            "26. nouns denoting two and three dimensional shapes",
            "27. nouns denoting stable states of affairs",
            "28. nouns denoting substances",
            "29. nouns denoting time and temporal relations",
            "30. verbs of grooming, dressing and bodily care",
            "31. verbs of size, temperature change, intensifying, etc.",
            "32. verbs of thinking, judging, analyzing, doubting",
            "33. verbs of telling, asking, ordering, singing",
            "34. verbs of fighting, athletic activities",
            "35. verbs of eating and drinking",
            "36. verbs of touching, hitting, tying, digging",
            "37. verbs of sewing, baking, painting, performing",
            "38. verbs of feeling",
            "39. verbs of walking, flying, swimming",
            "40. verbs of seeing, hearing, feeling",
            "41. verbs of buying, selling, owning",
            "42. verbs of political and social activities and events",
            "43. verbs of being, having, spatial relations",
            "44. verbs of raining, snowing, thawing, thundering",
            "45. participial adjectives",
            "46. modals all",
            "47. symbols all",
            "48. emojis all",
            "49. agential prepositions",
            "50. relational prepositions",
            "51. time prepositions"
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
    final static DefaultComboBoxModel modelSetBaseInstrument = new DefaultComboBoxModel(instrumentLabel);
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
