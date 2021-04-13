package com.gtwm.sound;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class InstructionFormWordLength extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox setOperator;
    private JComboBox setSoundModTo;
    private JComboBox setTempo;
    private JComboBox setDuration;
    private JComboBox setInstrument;
    private JComboBox setOctave;
    private JSlider setVolume;
    private JComboBox setPercussion;
    private JSpinner setLength;
    private JSpinner incrementTempo;
    private JSpinner incrementDuration;
    private JSpinner incrementOctave;
    private JSpinner incrementVolume;
    private JPanel incrementPanel;
    private JPanel setPanel;
    private JComboBox setChangeMode;
    private JComboBox setSoundModBy;
    private JComboBox setFrequency;
    private JSpinner incrementFrequency;
    private JSlider setAttack;
    private JSlider setDecay;
    private JSlider setPitchbend;
    private JSpinner incrementPitchbend;
    private JSlider setPan;

    boolean instructionCheck = true;

    public InstructionFormWordLength() {

        // Set swing models for the input controls
        setSoundModTo.setModel(InstructionFormModels.modelSetSoundTo);
        setSoundModBy.setModel(InstructionFormModels.modelSetSoundBy);
        setTempo.setModel(InstructionFormModels.modelSetTempo);
        setOctave.setModel(InstructionFormModels.modelSetOctave);
        setDuration.setModel(InstructionFormModels.modelSetNoteDuration);
        setInstrument.setModel(InstructionFormModels.modelSetInstrument);
        setFrequency.setModel(InstructionFormModels.modelSetFrequency);
        setLength.setModel(InstructionFormModels.modelSetLength);
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
                instruction.setMod(TransformationManager.Instruction.Mods.WORDLENGTH);
                instruction.setModOperator(TransformationManager.Instruction.ModOperators.valueOf(String.valueOf(setOperator.getSelectedItem())));
                instruction.setModValue(String.valueOf(setLength.getValue()));
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
                //for (TransformationManager.Instruction i : Main.getInstructionsList()) {
                //    if (i.getSoundMod() == instruction.getSoundMod() &&
                //            i.getModOperator() == instruction.getModOperator() &&
                //            i.getSoundModValue() == instruction.getSoundModValue()) {
                //        instructionCheck = false;
                //    }
                //}

                if (instructionCheck) {
                    //System.out.println(instruction.toString());
                    //TextSound.instructions.add(instruction);

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
            public void actionPerformed(ActionEvent e) {
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
                InstructionFormWordLength.super.pack();
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
                InstructionFormWordLength.super.pack();
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
        InstructionFormWordLength dialog = new InstructionFormWordLength();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(2, 7, new Insets(10, 10, 10, 10), -1, -1));
        setPanel = new JPanel();
        setPanel.setLayout(new GridLayoutManager(9, 2, new Insets(0, 0, 0, 0), -1, -1));
        setPanel.setVisible(true);
        contentPane.add(setPanel, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        setTempo = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("40");
        defaultComboBoxModel1.addElement("45");
        defaultComboBoxModel1.addElement("50");
        defaultComboBoxModel1.addElement("55");
        defaultComboBoxModel1.addElement("60");
        defaultComboBoxModel1.addElement("65");
        defaultComboBoxModel1.addElement("70");
        defaultComboBoxModel1.addElement("80");
        defaultComboBoxModel1.addElement("95");
        defaultComboBoxModel1.addElement("110");
        defaultComboBoxModel1.addElement("120");
        defaultComboBoxModel1.addElement("145");
        defaultComboBoxModel1.addElement("180");
        defaultComboBoxModel1.addElement("220");
        setTempo.setModel(defaultComboBoxModel1);
        setPanel.add(setTempo, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setDuration = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        defaultComboBoxModel2.addElement("1.00");
        defaultComboBoxModel2.addElement("0.50");
        defaultComboBoxModel2.addElement("0.25");
        defaultComboBoxModel2.addElement("0.125");
        defaultComboBoxModel2.addElement("0.0625");
        defaultComboBoxModel2.addElement("0.03125");
        defaultComboBoxModel2.addElement("0.015625");
        setDuration.setModel(defaultComboBoxModel2);
        setDuration.setVisible(false);
        setPanel.add(setDuration, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setInstrument = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel3 = new DefaultComboBoxModel();
        defaultComboBoxModel3.addElement("PIANO");
        defaultComboBoxModel3.addElement("GUITAR");
        defaultComboBoxModel3.addElement("TINKLE_BELL");
        setInstrument.setModel(defaultComboBoxModel3);
        setInstrument.setVisible(false);
        setPanel.add(setInstrument, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setOctave = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel4 = new DefaultComboBoxModel();
        defaultComboBoxModel4.addElement("0");
        defaultComboBoxModel4.addElement("1");
        defaultComboBoxModel4.addElement("2");
        defaultComboBoxModel4.addElement("3");
        defaultComboBoxModel4.addElement("4");
        defaultComboBoxModel4.addElement("5");
        defaultComboBoxModel4.addElement("6");
        defaultComboBoxModel4.addElement("7");
        defaultComboBoxModel4.addElement("8");
        defaultComboBoxModel4.addElement("9");
        defaultComboBoxModel4.addElement("10");
        setOctave.setModel(defaultComboBoxModel4);
        setOctave.setVisible(false);
        setPanel.add(setOctave, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setVolume = new JSlider();
        setVolume.setMajorTickSpacing(1000);
        setVolume.setMaximum(16383);
        setVolume.setValue(8000);
        setVolume.setVisible(false);
        setPanel.add(setVolume, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setPercussion = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel5 = new DefaultComboBoxModel();
        defaultComboBoxModel5.addElement("Bass_Drum");
        defaultComboBoxModel5.addElement("Hi_Bongo");
        defaultComboBoxModel5.addElement("Hand_Clap");
        setPercussion.setModel(defaultComboBoxModel5);
        setPercussion.setVisible(false);
        setPanel.add(setPercussion, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setFrequency = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel6 = new DefaultComboBoxModel();
        defaultComboBoxModel6.addElement("50");
        defaultComboBoxModel6.addElement("51");
        defaultComboBoxModel6.addElement("52");
        defaultComboBoxModel6.addElement("53");
        defaultComboBoxModel6.addElement("54");
        defaultComboBoxModel6.addElement("55");
        defaultComboBoxModel6.addElement("56");
        defaultComboBoxModel6.addElement("57");
        defaultComboBoxModel6.addElement("58");
        defaultComboBoxModel6.addElement("59");
        defaultComboBoxModel6.addElement("60");
        setFrequency.setModel(defaultComboBoxModel6);
        setFrequency.setVisible(false);
        setPanel.add(setFrequency, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setAttack = new JSlider();
        setAttack.setVisible(false);
        setPanel.add(setAttack, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setDecay = new JSlider();
        setDecay.setVisible(false);
        setPanel.add(setDecay, new GridConstraints(8, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setOperator = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel7 = new DefaultComboBoxModel();
        defaultComboBoxModel7.addElement("EQUALTO");
        defaultComboBoxModel7.addElement("LARGERTHAN");
        defaultComboBoxModel7.addElement("LESSTHAN");
        setOperator.setModel(defaultComboBoxModel7);
        contentPane.add(setOperator, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setSoundModTo = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel8 = new DefaultComboBoxModel();
        defaultComboBoxModel8.addElement("TEMPO");
        defaultComboBoxModel8.addElement("NOTE_DURATION");
        defaultComboBoxModel8.addElement("OCTAVE");
        defaultComboBoxModel8.addElement("INSTRUMENT");
        defaultComboBoxModel8.addElement("VOLUME");
        defaultComboBoxModel8.addElement("MIDI_NOTE");
        setSoundModTo.setModel(defaultComboBoxModel8);
        contentPane.add(setSoundModTo, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setLength = new JSpinner();
        contentPane.add(setLength, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        incrementPanel = new JPanel();
        incrementPanel.setLayout(new GridLayoutManager(5, 2, new Insets(0, 0, 0, 0), -1, -1));
        incrementPanel.setVisible(false);
        contentPane.add(incrementPanel, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        incrementTempo = new JSpinner();
        incrementPanel.add(incrementTempo, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        incrementDuration = new JSpinner();
        incrementDuration.setVisible(false);
        incrementPanel.add(incrementDuration, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        incrementOctave = new JSpinner();
        incrementOctave.setVisible(false);
        incrementPanel.add(incrementOctave, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        incrementVolume = new JSpinner();
        incrementVolume.setVisible(false);
        incrementPanel.add(incrementVolume, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        incrementFrequency = new JSpinner();
        incrementFrequency.setVisible(false);
        incrementPanel.add(incrementFrequency, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setChangeMode = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel9 = new DefaultComboBoxModel();
        defaultComboBoxModel9.addElement("SET");
        defaultComboBoxModel9.addElement("INCREMENT");
        setChangeMode.setModel(defaultComboBoxModel9);
        contentPane.add(setChangeMode, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        panel1.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonOK = new JButton();
        buttonOK.setText("OK");
        panel2.add(buttonOK, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCancel = new JButton();
        buttonCancel.setText("Cancel");
        panel2.add(buttonCancel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setSoundModBy = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel10 = new DefaultComboBoxModel();
        defaultComboBoxModel10.addElement("TEMPO");
        defaultComboBoxModel10.addElement("NOTEDURATION");
        defaultComboBoxModel10.addElement("OCTAVE");
        defaultComboBoxModel10.addElement("VOLUME");
        defaultComboBoxModel10.addElement("FREQUENCY");
        setSoundModBy.setModel(defaultComboBoxModel10);
        setSoundModBy.setVisible(false);
        contentPane.add(setSoundModBy, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

}
