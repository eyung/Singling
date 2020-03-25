package com.gtwm.sound;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class InstructionFormWordType extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox setType;
    private JComboBox setSoundModTo;
    private JComboBox setTempo;
    private JComboBox setDuration;
    private JComboBox setInstrument;
    private JComboBox setOctave;
    private JSlider setVolume;
    private JComboBox setPercussion;
    private JPanel incrementPanel;
    private JSpinner incrementTempo;
    private JComboBox incrementDuration;
    private JComboBox incrementOctave;
    private JSpinner incrementVolume;
    private JPanel setPanel;
    private JComboBox setChangeMode;
    private JComboBox setSoundModBy;

    boolean instructionCheck = true;

    public InstructionFormWordType() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                Queue.Instruction instruction = new Queue.Instruction();
                instruction.setMod(Queue.Instruction.Mods.WORDTYPE);
                instruction.setModValue(String.valueOf(setType.getSelectedItem()));
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
                    }
                } else if (instruction.changeMode == Queue.Instruction.ChangeModes.INCREMENT) {
                    instruction.setSoundMod(Queue.Instruction.SoundMods.valueOf(String.valueOf(setSoundModBy.getSelectedItem())));
                    //System.out.println("Increment: " + instruction.soundMod);
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
                    }
                }

                //TODO Check against duplicate instruction
                for (Queue.Instruction i : TextSound.instructions) {
                    if (i.getModValue() == instruction.getModValue() && i.getSoundMod() == instruction.getSoundMod()) {
                        //System.out.println("already exist   ");
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
                } else if (setSoundModTo.getSelectedItem() == "NOTEDURATION") {
                    setTempo.setVisible(false);
                    setDuration.setVisible(true);
                    setOctave.setVisible(false);
                    setInstrument.setVisible(false);
                    setVolume.setVisible(false);
                    setPercussion.setVisible(false);
                } else if (setSoundModTo.getSelectedItem() == "OCTAVE") {
                    setTempo.setVisible(false);
                    setDuration.setVisible(false);
                    setOctave.setVisible(true);
                    setInstrument.setVisible(false);
                    setVolume.setVisible(false);
                    setPercussion.setVisible(false);
                } else if (setSoundModTo.getSelectedItem() == "INSTRUMENT") {
                    setTempo.setVisible(false);
                    setDuration.setVisible(false);
                    setOctave.setVisible(false);
                    setInstrument.setVisible(true);
                    setVolume.setVisible(false);
                    setPercussion.setVisible(false);
                } else if (setSoundModTo.getSelectedItem() == "VOLUME") {
                    setTempo.setVisible(false);
                    setDuration.setVisible(false);
                    setOctave.setVisible(false);
                    setInstrument.setVisible(false);
                    setVolume.setVisible(true);
                    setPercussion.setVisible(false);
                } else if (setSoundModTo.getSelectedItem() == "PERCUSSION") {
                    setTempo.setVisible(false);
                    setDuration.setVisible(false);
                    setOctave.setVisible(false);
                    setInstrument.setVisible(false);
                    setVolume.setVisible(false);
                    setPercussion.setVisible(true);
                }
                InstructionFormWordType.super.pack();
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
                } else if (setSoundModBy.getSelectedItem() == "NOTEDURATION") {
                    incrementTempo.setVisible(false);
                    incrementDuration.setVisible(true);
                    incrementOctave.setVisible(false);
                    incrementVolume.setVisible(false);
                } else if (setSoundModBy.getSelectedItem() == "OCTAVE") {
                    incrementTempo.setVisible(false);
                    incrementDuration.setVisible(false);
                    incrementOctave.setVisible(true);
                    incrementVolume.setVisible(false);
                } else if (setSoundModBy.getSelectedItem() == "VOLUME") {
                    incrementTempo.setVisible(false);
                    incrementDuration.setVisible(false);
                    incrementOctave.setVisible(false);
                    incrementVolume.setVisible(true);
                }
                InstructionFormWordType.super.pack();
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
        InstructionFormWordType dialog = new InstructionFormWordType();
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
        contentPane.setLayout(new GridLayoutManager(2, 6, new Insets(10, 10, 10, 10), -1, -1));
        contentPane.setEnabled(true);
        contentPane.setMinimumSize(new Dimension(461, 101));
        contentPane.setOpaque(false);
        incrementPanel = new JPanel();
        incrementPanel.setLayout(new GridLayoutManager(4, 2, new Insets(0, 0, 0, 0), -1, -1));
        incrementPanel.setVisible(false);
        contentPane.add(incrementPanel, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        incrementTempo = new JSpinner();
        incrementPanel.add(incrementTempo, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        incrementDuration = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("1.00");
        defaultComboBoxModel1.addElement("0.50");
        defaultComboBoxModel1.addElement("0.25");
        defaultComboBoxModel1.addElement("0.125");
        defaultComboBoxModel1.addElement("0.0625");
        defaultComboBoxModel1.addElement("0.03125");
        defaultComboBoxModel1.addElement("0.015625");
        incrementDuration.setModel(defaultComboBoxModel1);
        incrementDuration.setVisible(false);
        incrementPanel.add(incrementDuration, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        incrementOctave = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        defaultComboBoxModel2.addElement("0");
        defaultComboBoxModel2.addElement("1");
        defaultComboBoxModel2.addElement("2");
        defaultComboBoxModel2.addElement("3");
        defaultComboBoxModel2.addElement("4");
        defaultComboBoxModel2.addElement("5");
        defaultComboBoxModel2.addElement("6");
        defaultComboBoxModel2.addElement("7");
        defaultComboBoxModel2.addElement("8");
        defaultComboBoxModel2.addElement("9");
        defaultComboBoxModel2.addElement("10");
        incrementOctave.setModel(defaultComboBoxModel2);
        incrementOctave.setVisible(false);
        incrementPanel.add(incrementOctave, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        incrementVolume = new JSpinner();
        incrementVolume.setVisible(false);
        incrementPanel.add(incrementVolume, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setPanel = new JPanel();
        setPanel.setLayout(new GridLayoutManager(6, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(setPanel, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        setTempo = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel3 = new DefaultComboBoxModel();
        defaultComboBoxModel3.addElement("40");
        defaultComboBoxModel3.addElement("45");
        defaultComboBoxModel3.addElement("50");
        defaultComboBoxModel3.addElement("55");
        defaultComboBoxModel3.addElement("60");
        defaultComboBoxModel3.addElement("65");
        defaultComboBoxModel3.addElement("70");
        defaultComboBoxModel3.addElement("80");
        defaultComboBoxModel3.addElement("95");
        defaultComboBoxModel3.addElement("110");
        defaultComboBoxModel3.addElement("120");
        defaultComboBoxModel3.addElement("145");
        defaultComboBoxModel3.addElement("180");
        defaultComboBoxModel3.addElement("220");
        setTempo.setModel(defaultComboBoxModel3);
        setPanel.add(setTempo, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setDuration = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel4 = new DefaultComboBoxModel();
        defaultComboBoxModel4.addElement("1.00");
        defaultComboBoxModel4.addElement("0.50");
        defaultComboBoxModel4.addElement("0.25");
        defaultComboBoxModel4.addElement("0.125");
        defaultComboBoxModel4.addElement("0.0625");
        defaultComboBoxModel4.addElement("0.03125");
        defaultComboBoxModel4.addElement("0.015625");
        setDuration.setModel(defaultComboBoxModel4);
        setDuration.setVisible(false);
        setPanel.add(setDuration, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setInstrument = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel5 = new DefaultComboBoxModel();
        defaultComboBoxModel5.addElement("PIANO");
        defaultComboBoxModel5.addElement("GUITAR");
        defaultComboBoxModel5.addElement("TINKLE_BELL");
        setInstrument.setModel(defaultComboBoxModel5);
        setInstrument.setVisible(false);
        setPanel.add(setInstrument, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setOctave = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel6 = new DefaultComboBoxModel();
        defaultComboBoxModel6.addElement("0");
        defaultComboBoxModel6.addElement("1");
        defaultComboBoxModel6.addElement("2");
        defaultComboBoxModel6.addElement("3");
        defaultComboBoxModel6.addElement("4");
        defaultComboBoxModel6.addElement("5");
        defaultComboBoxModel6.addElement("6");
        defaultComboBoxModel6.addElement("7");
        defaultComboBoxModel6.addElement("8");
        defaultComboBoxModel6.addElement("9");
        defaultComboBoxModel6.addElement("10");
        setOctave.setModel(defaultComboBoxModel6);
        setOctave.setVisible(false);
        setPanel.add(setOctave, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setVolume = new JSlider();
        setVolume.setMajorTickSpacing(1000);
        setVolume.setMaximum(16383);
        setVolume.setValue(8000);
        setVolume.setVisible(false);
        setPanel.add(setVolume, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setPercussion = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel7 = new DefaultComboBoxModel();
        defaultComboBoxModel7.addElement("Bass_Drum");
        defaultComboBoxModel7.addElement("Hi_Bongo");
        defaultComboBoxModel7.addElement("Hand_Clap");
        setPercussion.setModel(defaultComboBoxModel7);
        setPercussion.setVisible(false);
        setPanel.add(setPercussion, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setSoundModTo = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel8 = new DefaultComboBoxModel();
        defaultComboBoxModel8.addElement("TEMPO");
        defaultComboBoxModel8.addElement("NOTEDURATION");
        defaultComboBoxModel8.addElement("OCTAVE");
        defaultComboBoxModel8.addElement("INSTRUMENT");
        defaultComboBoxModel8.addElement("VOLUME");
        setSoundModTo.setModel(defaultComboBoxModel8);
        contentPane.add(setSoundModTo, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setType = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel9 = new DefaultComboBoxModel();
        defaultComboBoxModel9.addElement("n");
        defaultComboBoxModel9.addElement("v");
        defaultComboBoxModel9.addElement("a");
        defaultComboBoxModel9.addElement("r");
        setType.setModel(defaultComboBoxModel9);
        contentPane.add(setType, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setChangeMode = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel10 = new DefaultComboBoxModel();
        defaultComboBoxModel10.addElement("SET");
        defaultComboBoxModel10.addElement("INCREMENT");
        setChangeMode.setModel(defaultComboBoxModel10);
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
        final DefaultComboBoxModel defaultComboBoxModel11 = new DefaultComboBoxModel();
        defaultComboBoxModel11.addElement("TEMPO");
        defaultComboBoxModel11.addElement("NOTEDURATION");
        defaultComboBoxModel11.addElement("OCTAVE");
        defaultComboBoxModel11.addElement("VOLUME");
        setSoundModBy.setModel(defaultComboBoxModel11);
        setSoundModBy.setVisible(false);
        contentPane.add(setSoundModBy, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

}
