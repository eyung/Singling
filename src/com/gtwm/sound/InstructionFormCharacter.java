package com.gtwm.sound;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class InstructionFormCharacter extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox setChangeMode;
    private JComboBox setType;
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
    private JPanel incrementPanel;
    private JSpinner incrementTempo;
    private JSpinner incrementDuration;
    private JSpinner incrementOctave;
    private JSpinner incrementVolume;
    private JSpinner incrementFrequency;
    private JSlider setAttack;
    private JSlider setDecay;
    private JSlider setPitchbend;
    private JSpinner incrementPitchbend;
    private JSlider setPan;

    boolean instructionCheck = true;

    public InstructionFormCharacter() {

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
                instruction.setMod(TransformationManager.Instruction.Mods.CHARACTER);
                instruction.setModValue(String.valueOf(setType.getSelectedItem()));
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
                for (TransformationManager.Instruction i : Main.instructions) {
                    if (i.getModValue() == instruction.getModValue() && i.getSoundMod() == instruction.getSoundMod()) {
                        //System.out.println("already exist   ");
                        instructionCheck = false;
                    }
                }

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
                InstructionFormCharacter.super.pack();
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
                InstructionFormCharacter.super.pack();
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
        InstructionFormCharacter dialog = new InstructionFormCharacter();
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
        contentPane.setLayout(new GridLayoutManager(1, 1, new Insets(10, 10, 10, 10), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 6, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        setChangeMode = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("SET");
        defaultComboBoxModel1.addElement("INCREMENT");
        setChangeMode.setModel(defaultComboBoxModel1);
        panel1.add(setChangeMode, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setType = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        defaultComboBoxModel2.addElement("consonants");
        defaultComboBoxModel2.addElement("vowels");
        setType.setModel(defaultComboBoxModel2);
        panel1.add(setType, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setSoundModTo = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel3 = new DefaultComboBoxModel();
        defaultComboBoxModel3.addElement("TEMPO");
        defaultComboBoxModel3.addElement("NOTE_DURATION");
        defaultComboBoxModel3.addElement("OCTAVE");
        defaultComboBoxModel3.addElement("INSTRUMENT");
        defaultComboBoxModel3.addElement("VOLUME");
        defaultComboBoxModel3.addElement("MIDI_NOTE");
        setSoundModTo.setModel(defaultComboBoxModel3);
        panel1.add(setSoundModTo, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setSoundModBy = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel4 = new DefaultComboBoxModel();
        defaultComboBoxModel4.addElement("TEMPO");
        defaultComboBoxModel4.addElement("NOTEDURATION");
        defaultComboBoxModel4.addElement("OCTAVE");
        defaultComboBoxModel4.addElement("VOLUME");
        setSoundModBy.setModel(defaultComboBoxModel4);
        setSoundModBy.setVisible(false);
        panel1.add(setSoundModBy, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setPanel = new JPanel();
        setPanel.setLayout(new GridLayoutManager(9, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(setPanel, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        setTempo = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel5 = new DefaultComboBoxModel();
        defaultComboBoxModel5.addElement("40");
        defaultComboBoxModel5.addElement("45");
        defaultComboBoxModel5.addElement("50");
        defaultComboBoxModel5.addElement("55");
        defaultComboBoxModel5.addElement("60");
        defaultComboBoxModel5.addElement("65");
        defaultComboBoxModel5.addElement("70");
        defaultComboBoxModel5.addElement("80");
        defaultComboBoxModel5.addElement("95");
        defaultComboBoxModel5.addElement("110");
        defaultComboBoxModel5.addElement("120");
        defaultComboBoxModel5.addElement("145");
        defaultComboBoxModel5.addElement("180");
        defaultComboBoxModel5.addElement("220");
        setTempo.setModel(defaultComboBoxModel5);
        setPanel.add(setTempo, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setDuration = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel6 = new DefaultComboBoxModel();
        defaultComboBoxModel6.addElement("1.00");
        defaultComboBoxModel6.addElement("0.50");
        defaultComboBoxModel6.addElement("0.25");
        defaultComboBoxModel6.addElement("0.125");
        defaultComboBoxModel6.addElement("0.0625");
        defaultComboBoxModel6.addElement("0.03125");
        defaultComboBoxModel6.addElement("0.015625");
        setDuration.setModel(defaultComboBoxModel6);
        setDuration.setVisible(false);
        setPanel.add(setDuration, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setInstrument = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel7 = new DefaultComboBoxModel();
        defaultComboBoxModel7.addElement("PIANO");
        defaultComboBoxModel7.addElement("GUITAR");
        defaultComboBoxModel7.addElement("TINKLE_BELL");
        setInstrument.setModel(defaultComboBoxModel7);
        setInstrument.setVisible(false);
        setPanel.add(setInstrument, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setOctave = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel8 = new DefaultComboBoxModel();
        defaultComboBoxModel8.addElement("0");
        defaultComboBoxModel8.addElement("1");
        defaultComboBoxModel8.addElement("2");
        defaultComboBoxModel8.addElement("3");
        defaultComboBoxModel8.addElement("4");
        defaultComboBoxModel8.addElement("5");
        defaultComboBoxModel8.addElement("6");
        defaultComboBoxModel8.addElement("7");
        defaultComboBoxModel8.addElement("8");
        defaultComboBoxModel8.addElement("9");
        defaultComboBoxModel8.addElement("10");
        setOctave.setModel(defaultComboBoxModel8);
        setOctave.setVisible(false);
        setPanel.add(setOctave, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setVolume = new JSlider();
        setVolume.setMajorTickSpacing(1000);
        setVolume.setMaximum(16383);
        setVolume.setValue(8000);
        setVolume.setVisible(false);
        setPanel.add(setVolume, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setPercussion = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel9 = new DefaultComboBoxModel();
        defaultComboBoxModel9.addElement("Bass_Drum");
        defaultComboBoxModel9.addElement("Hi_Bongo");
        defaultComboBoxModel9.addElement("Hand_Clap");
        setPercussion.setModel(defaultComboBoxModel9);
        setPercussion.setVisible(false);
        setPanel.add(setPercussion, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setFrequency = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel10 = new DefaultComboBoxModel();
        defaultComboBoxModel10.addElement("16");
        defaultComboBoxModel10.addElement("32");
        defaultComboBoxModel10.addElement("64");
        defaultComboBoxModel10.addElement("128");
        defaultComboBoxModel10.addElement("256");
        setFrequency.setModel(defaultComboBoxModel10);
        setFrequency.setVisible(false);
        setPanel.add(setFrequency, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setAttack = new JSlider();
        setAttack.setVisible(false);
        setPanel.add(setAttack, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setDecay = new JSlider();
        setDecay.setVisible(false);
        setPanel.add(setDecay, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        incrementPanel = new JPanel();
        incrementPanel.setLayout(new GridLayoutManager(5, 2, new Insets(0, 0, 0, 0), -1, -1));
        incrementPanel.setVisible(false);
        panel1.add(incrementPanel, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
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
        incrementPanel.add(incrementVolume, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        incrementFrequency = new JSpinner();
        incrementFrequency.setVisible(false);
        incrementPanel.add(incrementFrequency, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        panel1.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonOK = new JButton();
        buttonOK.setText("OK");
        panel2.add(buttonOK, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCancel = new JButton();
        buttonCancel.setText("Cancel");
        panel2.add(buttonCancel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

}
