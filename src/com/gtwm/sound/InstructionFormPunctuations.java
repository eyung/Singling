package com.gtwm.sound;

import javax.swing.*;
import java.awt.event.*;

public class InstructionFormPunctuations extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox setChangeMode;
    private JComboBox setValue;
    private JComboBox setSoundModTo;
    private JComboBox setSoundModBy;
    private JPanel setPanel;
    private JComboBox setTempo;
    private JComboBox setDuration;
    private JComboBox setInstrument;
    private JComboBox setOctave;
    private JSlider setVolume;
    private JComboBox setPercussion;
    private JComboBox setFrequency;
    private JSlider setAttack;
    private JSlider setDecay;
    private JSlider setPitchbend;
    private JPanel incrementPanel;
    private JSpinner incrementTempo;
    private JSpinner incrementDuration;
    private JSpinner incrementOctave;
    private JSpinner incrementVolume;
    private JSpinner incrementFrequency;
    private JSpinner incrementPitchbend;
    private JSlider setPan;

    boolean instructionCheck = true;

    public InstructionFormPunctuations() {

        // Set swing models for the input controls
        setSoundModTo.setModel(InstructionFormModels.modelSetSoundTo);
        setSoundModBy.setModel(InstructionFormModels.modelSetSoundBy);
        setTempo.setModel(InstructionFormModels.modelSetTempo);
        setOctave.setModel(InstructionFormModels.modelSetOctave);
        setDuration.setModel(InstructionFormModels.modelSetNoteDuration);
        setInstrument.setModel(InstructionFormModels.modelSetInstrument);
        setFrequency.setModel(InstructionFormModels.modelSetFrequency);
        incrementTempo.setModel(InstructionFormModels.modelIncrementTempo);
        incrementOctave.setModel(InstructionFormModels.modelIncrementOctave);
        incrementDuration.setModel(InstructionFormModels.modelIncrementNoteDuration);
        incrementFrequency.setModel(InstructionFormModels.modelIncrementFrequency);
        incrementVolume.setModel(InstructionFormModels.modelIncrementVolume);
        incrementPitchbend.setModel(InstructionFormModels.modelIncrementPitchbend);

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                TransformationManager.Instruction instruction = new TransformationManager.Instruction();
                instruction.setMod(TransformationManager.Instruction.Mods.PUNCTUATION);
                instruction.setModValue(String.valueOf(setValue.getSelectedItem()));
                instruction.setChangeMode(TransformationManager.Instruction.ChangeModes.valueOf(String.valueOf(setChangeMode.getSelectedItem())));

                if (instruction.changeMode == TransformationManager.Instruction.ChangeModes.SET) {
                    instruction.setSoundMod(TransformationManager.Instruction.SoundMods.valueOf(String.valueOf(setSoundModTo.getSelectedItem())));
                    switch (instruction.soundMod) {
                        case TEMPO:
                            instruction.setSoundModValue(String.valueOf(setTempo.getSelectedItem()));
                            break;
                        case NOTE_DURATION:
                            instruction.setSoundModValue(String.valueOf(setDuration.getSelectedItem()));
                            break;
                        case OCTAVE:
                            instruction.setSoundModValue(String.valueOf(setOctave.getSelectedItem()));
                            break;
                        case INSTRUMENT:
                            instruction.setSoundModValue(String.valueOf(setInstrument.getSelectedItem()));
                            break;
                        case VOLUME:
                            instruction.setSoundModValue(String.valueOf(setVolume.getValue()));
                            break;
                        case PERCUSSION:
                            instruction.setSoundModValue(String.valueOf(setPercussion.getSelectedItem()));
                            break;
                        case MIDI_NOTE:
                            instruction.setSoundModValue(String.valueOf(setFrequency.getSelectedItem()));
                            break;
                        case ATTACK:
                            instruction.setSoundModValue(String.valueOf(setAttack.getValue()));
                            break;
                        case DECAY:
                            instruction.setSoundModValue(String.valueOf(setDecay.getValue()));
                            break;
                        case PITCHBEND:
                            instruction.setSoundModValue(String.valueOf(setPitchbend.getValue()));
                            break;
                        case PAN:
                            instruction.setSoundModValue(String.valueOf(setPan.getValue()));
                            break;
                    }
                } else if (instruction.changeMode == TransformationManager.Instruction.ChangeModes.INCREMENT) {
                    instruction.setSoundMod(TransformationManager.Instruction.SoundMods.valueOf(String.valueOf(setSoundModBy.getSelectedItem())));
                    //System.out.println("Increment: " + instruction.soundMod);
                    switch (instruction.soundMod) {
                        case TEMPO:
                            instruction.setSoundModValue(String.valueOf(incrementTempo.getValue()));
                            break;
                        case NOTE_DURATION:
                            instruction.setSoundModValue(String.valueOf(incrementDuration.getValue()));
                            break;
                        case OCTAVE:
                            instruction.setSoundModValue(String.valueOf(incrementOctave.getValue()));
                            break;
                        case VOLUME:
                            instruction.setSoundModValue(String.valueOf(incrementVolume.getValue()));
                            break;
                        case MIDI_NOTE:
                            instruction.setSoundModValue(String.valueOf(incrementFrequency.getValue()));
                            break;
                        case PITCHBEND:
                            instruction.setSoundModValue(String.valueOf(incrementPitchbend.getValue()));
                            break;
                    }
                }

                //TODO Check against duplicate instruction
                for (TransformationManager.Instruction i : TextSound.instructions) {
                    if (i.getModValue() == instruction.getModValue() && i.getSoundMod() == instruction.getSoundMod()) {
                        instructionCheck = false;
                    }
                }

                if (instructionCheck) {
                    //System.out.println(instruction.toString());
                    TextSound.instructions.add(instruction);
                    //Main.listAddInstruction(Main.model, instruction);
                    Main.listAddInstruction(instruction);
                } else {
                    JOptionPane.showMessageDialog(null,
                            "There is already an existing instruction that uses the same modifiers, please use something else.",
                            "Duplicate instruction.",
                            JOptionPane.WARNING_MESSAGE);
                }

                // Reset to first selection
                setSoundModTo.setSelectedIndex(0);

                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        setSoundModTo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (setSoundModTo.getSelectedItem() == "TEMPO") {
                    setTempo.setVisible(true);
                    setDuration.setVisible(false);
                    setOctave.setVisible(false);
                    setInstrument.setVisible(false);
                    setVolume.setVisible(false);
                    setPercussion.setVisible(false);
                    setFrequency.setVisible(false);
                    setAttack.setVisible(false);
                    setDecay.setVisible(false);
                    setPitchbend.setVisible(false);
                    setPan.setVisible(false);
                } else if (setSoundModTo.getSelectedItem() == "NOTE_DURATION") {
                    setTempo.setVisible(false);
                    setDuration.setVisible(true);
                    setOctave.setVisible(false);
                    setInstrument.setVisible(false);
                    setVolume.setVisible(false);
                    setPercussion.setVisible(false);
                    setFrequency.setVisible(false);
                    setAttack.setVisible(false);
                    setDecay.setVisible(false);
                    setPitchbend.setVisible(false);
                    setPan.setVisible(false);
                } else if (setSoundModTo.getSelectedItem() == "OCTAVE") {
                    setTempo.setVisible(false);
                    setDuration.setVisible(false);
                    setOctave.setVisible(true);
                    setInstrument.setVisible(false);
                    setVolume.setVisible(false);
                    setPercussion.setVisible(false);
                    setFrequency.setVisible(false);
                    setAttack.setVisible(false);
                    setDecay.setVisible(false);
                    setPitchbend.setVisible(false);
                    setPan.setVisible(false);
                } else if (setSoundModTo.getSelectedItem() == "INSTRUMENT") {
                    setTempo.setVisible(false);
                    setDuration.setVisible(false);
                    setOctave.setVisible(false);
                    setInstrument.setVisible(true);
                    setVolume.setVisible(false);
                    setPercussion.setVisible(false);
                    setFrequency.setVisible(false);
                    setAttack.setVisible(false);
                    setDecay.setVisible(false);
                    setPitchbend.setVisible(false);
                    setPan.setVisible(false);
                } else if (setSoundModTo.getSelectedItem() == "VOLUME") {
                    setTempo.setVisible(false);
                    setDuration.setVisible(false);
                    setOctave.setVisible(false);
                    setInstrument.setVisible(false);
                    setVolume.setVisible(true);
                    setPercussion.setVisible(false);
                    setFrequency.setVisible(false);
                    setAttack.setVisible(false);
                    setDecay.setVisible(false);
                    setPitchbend.setVisible(false);
                    setPan.setVisible(false);
                } else if (setSoundModTo.getSelectedItem() == "PERCUSSION") {
                    setTempo.setVisible(false);
                    setDuration.setVisible(false);
                    setOctave.setVisible(false);
                    setInstrument.setVisible(false);
                    setVolume.setVisible(false);
                    setPercussion.setVisible(true);
                    setFrequency.setVisible(false);
                    setAttack.setVisible(false);
                    setDecay.setVisible(false);
                    setPitchbend.setVisible(false);
                    setPan.setVisible(false);
                } else if (setSoundModTo.getSelectedItem() == "MIDI_NOTE") {
                    setTempo.setVisible(false);
                    setDuration.setVisible(false);
                    setOctave.setVisible(false);
                    setInstrument.setVisible(false);
                    setVolume.setVisible(false);
                    setPercussion.setVisible(false);
                    setFrequency.setVisible(true);
                    setAttack.setVisible(false);
                    setDecay.setVisible(false);
                    setPitchbend.setVisible(false);
                    setPan.setVisible(false);
                } else if (setSoundModTo.getSelectedItem() == "ATTACK") {
                    setTempo.setVisible(false);
                    setDuration.setVisible(false);
                    setOctave.setVisible(false);
                    setInstrument.setVisible(false);
                    setVolume.setVisible(false);
                    setPercussion.setVisible(false);
                    setFrequency.setVisible(false);
                    setAttack.setVisible(true);
                    setDecay.setVisible(false);
                    setPitchbend.setVisible(false);
                    setPan.setVisible(false);
                } else if (setSoundModTo.getSelectedItem() == "DECAY") {
                    setTempo.setVisible(false);
                    setDuration.setVisible(false);
                    setOctave.setVisible(false);
                    setInstrument.setVisible(false);
                    setVolume.setVisible(false);
                    setPercussion.setVisible(false);
                    setFrequency.setVisible(false);
                    setAttack.setVisible(false);
                    setDecay.setVisible(true);
                    setPitchbend.setVisible(false);
                    setPan.setVisible(false);
                } else if (setSoundModTo.getSelectedItem() == "PITCHBEND") {
                    setTempo.setVisible(false);
                    setDuration.setVisible(false);
                    setOctave.setVisible(false);
                    setInstrument.setVisible(false);
                    setVolume.setVisible(false);
                    setPercussion.setVisible(false);
                    setFrequency.setVisible(false);
                    setAttack.setVisible(false);
                    setDecay.setVisible(false);
                    setPitchbend.setVisible(true);
                    setPan.setVisible(false);
                } else if (setSoundModTo.getSelectedItem() == "PAN") {
                    setTempo.setVisible(false);
                    setDuration.setVisible(false);
                    setOctave.setVisible(false);
                    setInstrument.setVisible(false);
                    setVolume.setVisible(false);
                    setPercussion.setVisible(false);
                    setFrequency.setVisible(false);
                    setAttack.setVisible(false);
                    setDecay.setVisible(false);
                    setPitchbend.setVisible(false);
                    setPan.setVisible(true);
                }
                InstructionFormPunctuations.super.pack();
            }
        });

        setSoundModBy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (setSoundModBy.getSelectedItem() == "TEMPO") {
                    incrementTempo.setVisible(true);
                    incrementDuration.setVisible(false);
                    incrementOctave.setVisible(false);
                    incrementVolume.setVisible(false);
                    incrementFrequency.setVisible(false);
                    incrementPitchbend.setVisible(false);
                } else if (setSoundModBy.getSelectedItem() == "NOTE_DURATION") {
                    incrementTempo.setVisible(false);
                    incrementDuration.setVisible(true);
                    incrementOctave.setVisible(false);
                    incrementVolume.setVisible(false);
                    incrementFrequency.setVisible(false);
                    incrementPitchbend.setVisible(false);
                } else if (setSoundModBy.getSelectedItem() == "OCTAVE") {
                    incrementTempo.setVisible(false);
                    incrementDuration.setVisible(false);
                    incrementOctave.setVisible(true);
                    incrementVolume.setVisible(false);
                    incrementFrequency.setVisible(false);
                    incrementPitchbend.setVisible(false);
                } else if (setSoundModBy.getSelectedItem() == "VOLUME") {
                    incrementTempo.setVisible(false);
                    incrementDuration.setVisible(false);
                    incrementOctave.setVisible(false);
                    incrementVolume.setVisible(true);
                    incrementFrequency.setVisible(false);
                    incrementPitchbend.setVisible(false);
                } else if (setSoundModBy.getSelectedItem() == "MIDI_NOTE") {
                    incrementTempo.setVisible(false);
                    incrementDuration.setVisible(false);
                    incrementOctave.setVisible(false);
                    incrementVolume.setVisible(false);
                    incrementFrequency.setVisible(true);
                    incrementPitchbend.setVisible(false);
                } else if (setSoundModBy.getSelectedItem() == "PITCHBEND") {
                    incrementTempo.setVisible(false);
                    incrementDuration.setVisible(false);
                    incrementOctave.setVisible(false);
                    incrementVolume.setVisible(false);
                    incrementFrequency.setVisible(false);
                    incrementPitchbend.setVisible(true);
                }
                InstructionFormPunctuations.super.pack();
            }
        });

        setChangeMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (setChangeMode.getSelectedItem() == "SET") {
                    setPanel.setVisible(true);
                    incrementPanel.setVisible(false);
                    setSoundModTo.setVisible(true);
                    setSoundModBy.setVisible(false);
                } else {
                    setPanel.setVisible(false);
                    incrementPanel.setVisible(true);
                    setSoundModTo.setVisible(false);
                    setSoundModBy.setVisible(true);
                }
            }
        });
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        InstructionFormPunctuations dialog = new InstructionFormPunctuations();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
