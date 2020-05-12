package com.gtwm.sound;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

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
    private JComboBox incrementDuration;
    private JComboBox incrementOctave;
    private JSpinner incrementVolume;
    private JPanel incrementPanel;
    private JPanel setPanel;
    private JComboBox setChangeMode;
    private JComboBox setSoundModBy;
    private JComboBox setFrequency;
    private JComboBox incrementFrequency;

    boolean instructionCheck = true;

    public InstructionFormWordLength() {

        incrementTempo.setModel(InstructionFormModels.modelIncrementTempo);

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                Queue.Instruction instruction = new Queue.Instruction();
                instruction.setMod(Queue.Instruction.Mods.WORDLENGTH);
                instruction.setModOperator(Queue.Instruction.ModOperators.valueOf(String.valueOf(setOperator.getSelectedItem())));
                instruction.setModValue(String.valueOf(setLength.getValue()));
                instruction.setChangeMode(Queue.Instruction.ChangeModes.valueOf(String.valueOf(setChangeMode.getSelectedItem())));

                if (instruction.changeMode == Queue.Instruction.ChangeModes.SET) {
                    instruction.setSoundMod(Queue.Instruction.SoundMods.valueOf(String.valueOf(setSoundModTo.getSelectedItem())));
                    switch (instruction.soundMod) {
                        case TEMPO:
                            instruction.setSoundModValue(String.valueOf(setTempo.getSelectedItem()));
                            break;
                        case NOTEDURATION:
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
                        case FREQUENCY:
                            instruction.setSoundModValue(String.valueOf(setFrequency.getSelectedItem()));
                            break;
                    }
                } else if (instruction.changeMode == Queue.Instruction.ChangeModes.INCREMENT) {
                    instruction.setSoundMod(Queue.Instruction.SoundMods.valueOf(String.valueOf(setSoundModBy.getSelectedItem())));
                    switch (instruction.soundMod) {
                        case TEMPO:
                            instruction.setSoundModValue(String.valueOf(incrementTempo.getValue()));
                            break;
                        case NOTEDURATION:
                            instruction.setSoundModValue(String.valueOf(incrementDuration.getSelectedItem()));
                            break;
                        case OCTAVE:
                            instruction.setSoundModValue(String.valueOf(incrementOctave.getSelectedItem()));
                            break;
                        case VOLUME:
                            instruction.setSoundModValue(String.valueOf(incrementVolume.getValue()));
                            break;
                        case FREQUENCY:
                            instruction.setSoundModValue(String.valueOf(incrementFrequency.getSelectedItem()));
                            break;
                    }
                }

                //TODO Check against duplicate instruction
                for (Queue.Instruction i : TextSound.instructions) {
                    if (i.getSoundMod() == instruction.getSoundMod() &&
                            i.getModOperator() == instruction.getModOperator() &&
                            i.getSoundModValue() == instruction.getSoundModValue()) {
                        instructionCheck = false;
                    }
                }

                if (instructionCheck) {
                    //System.out.println(instruction.toString());
                    TextSound.instructions.add(instruction);
                    Main.listAddInstruction(Main.model, instruction);
                } else {
                    JOptionPane.showMessageDialog(null,
                            "There is already an existing instruction that uses the same modifiers, please use something else.",
                            "Duplicate instruction.",
                            JOptionPane.WARNING_MESSAGE);
                }

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
                } else if (setSoundModTo.getSelectedItem() == "NOTEDURATION") {
                    setTempo.setVisible(false);
                    setDuration.setVisible(true);
                    setOctave.setVisible(false);
                    setInstrument.setVisible(false);
                    setVolume.setVisible(false);
                    setPercussion.setVisible(false);
                    setFrequency.setVisible(false);
                } else if (setSoundModTo.getSelectedItem() == "OCTAVE") {
                    setTempo.setVisible(false);
                    setDuration.setVisible(false);
                    setOctave.setVisible(true);
                    setInstrument.setVisible(false);
                    setVolume.setVisible(false);
                    setPercussion.setVisible(false);
                    setFrequency.setVisible(false);
                } else if (setSoundModTo.getSelectedItem() == "INSTRUMENT") {
                    setTempo.setVisible(false);
                    setDuration.setVisible(false);
                    setOctave.setVisible(false);
                    setInstrument.setVisible(true);
                    setVolume.setVisible(false);
                    setPercussion.setVisible(false);
                    setFrequency.setVisible(false);
                } else if (setSoundModTo.getSelectedItem() == "VOLUME") {
                    setTempo.setVisible(false);
                    setDuration.setVisible(false);
                    setOctave.setVisible(false);
                    setInstrument.setVisible(false);
                    setVolume.setVisible(true);
                    setPercussion.setVisible(false);
                    setFrequency.setVisible(false);
                } else if (setSoundModTo.getSelectedItem() == "PERCUSSION") {
                    setTempo.setVisible(false);
                    setDuration.setVisible(false);
                    setOctave.setVisible(false);
                    setInstrument.setVisible(false);
                    setVolume.setVisible(false);
                    setPercussion.setVisible(true);
                    setFrequency.setVisible(false);
                } else if (setSoundModTo.getSelectedItem() == "FREQUENCY") {
                    setTempo.setVisible(false);
                    setDuration.setVisible(false);
                    setOctave.setVisible(false);
                    setInstrument.setVisible(false);
                    setVolume.setVisible(false);
                    setPercussion.setVisible(false);
                    setFrequency.setVisible(true);
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
                } else if (setSoundModBy.getSelectedItem() == "NOTEDURATION") {
                    incrementTempo.setVisible(false);
                    incrementDuration.setVisible(true);
                    incrementOctave.setVisible(false);
                    incrementVolume.setVisible(false);
                    incrementFrequency.setVisible(false);
                } else if (setSoundModBy.getSelectedItem() == "OCTAVE") {
                    incrementTempo.setVisible(false);
                    incrementDuration.setVisible(false);
                    incrementOctave.setVisible(true);
                    incrementVolume.setVisible(false);
                    incrementFrequency.setVisible(false);
                } else if (setSoundModBy.getSelectedItem() == "VOLUME") {
                    incrementTempo.setVisible(false);
                    incrementDuration.setVisible(false);
                    incrementOctave.setVisible(false);
                    incrementVolume.setVisible(true);
                    incrementFrequency.setVisible(false);
                } else if (setSoundModBy.getSelectedItem() == "FREQUENCY") {
                    incrementTempo.setVisible(false);
                    incrementDuration.setVisible(false);
                    incrementOctave.setVisible(false);
                    incrementVolume.setVisible(false);
                    incrementFrequency.setVisible(true);
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
        setPanel.setLayout(new GridLayoutManager(7, 2, new Insets(0, 0, 0, 0), -1, -1));
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
        defaultComboBoxModel6.addElement("16");
        defaultComboBoxModel6.addElement("32");
        defaultComboBoxModel6.addElement("64");
        defaultComboBoxModel6.addElement("128");
        defaultComboBoxModel6.addElement("256");
        setFrequency.setModel(defaultComboBoxModel6);
        setFrequency.setVisible(false);
        setPanel.add(setFrequency, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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
        defaultComboBoxModel8.addElement("NOTEDURATION");
        defaultComboBoxModel8.addElement("OCTAVE");
        defaultComboBoxModel8.addElement("INSTRUMENT");
        defaultComboBoxModel8.addElement("VOLUME");
        defaultComboBoxModel8.addElement("FREQUENCY");
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
        incrementDuration = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel9 = new DefaultComboBoxModel();
        defaultComboBoxModel9.addElement("1.00");
        defaultComboBoxModel9.addElement("0.50");
        defaultComboBoxModel9.addElement("0.25");
        defaultComboBoxModel9.addElement("0.125");
        defaultComboBoxModel9.addElement("0.0625");
        defaultComboBoxModel9.addElement("0.03125");
        defaultComboBoxModel9.addElement("0.015625");
        incrementDuration.setModel(defaultComboBoxModel9);
        incrementDuration.setVisible(false);
        incrementPanel.add(incrementDuration, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        incrementOctave = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel10 = new DefaultComboBoxModel();
        defaultComboBoxModel10.addElement("0");
        defaultComboBoxModel10.addElement("1");
        defaultComboBoxModel10.addElement("2");
        defaultComboBoxModel10.addElement("3");
        defaultComboBoxModel10.addElement("4");
        defaultComboBoxModel10.addElement("5");
        defaultComboBoxModel10.addElement("6");
        defaultComboBoxModel10.addElement("7");
        defaultComboBoxModel10.addElement("8");
        defaultComboBoxModel10.addElement("9");
        defaultComboBoxModel10.addElement("10");
        incrementOctave.setModel(defaultComboBoxModel10);
        incrementOctave.setVisible(false);
        incrementPanel.add(incrementOctave, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        incrementVolume = new JSpinner();
        incrementVolume.setVisible(false);
        incrementPanel.add(incrementVolume, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        incrementFrequency = new JComboBox();
        incrementFrequency.setVisible(false);
        incrementPanel.add(incrementFrequency, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setChangeMode = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel11 = new DefaultComboBoxModel();
        defaultComboBoxModel11.addElement("SET");
        defaultComboBoxModel11.addElement("INCREMENT");
        setChangeMode.setModel(defaultComboBoxModel11);
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
        final DefaultComboBoxModel defaultComboBoxModel12 = new DefaultComboBoxModel();
        defaultComboBoxModel12.addElement("TEMPO");
        defaultComboBoxModel12.addElement("NOTEDURATION");
        defaultComboBoxModel12.addElement("OCTAVE");
        defaultComboBoxModel12.addElement("VOLUME");
        defaultComboBoxModel12.addElement("FREQUENCY");
        setSoundModBy.setModel(defaultComboBoxModel12);
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
