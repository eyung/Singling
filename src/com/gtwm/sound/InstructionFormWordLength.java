package com.gtwm.sound;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class InstructionFormWordLength extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox setOperator;
    private JComboBox setSoundMod;
    private JComboBox setTempo;
    private JComboBox setDuration;
    private JComboBox setInstrument;
    private JComboBox setOctave;
    private JSlider setVolume;
    private JComboBox setPercussion;
    private JSpinner setLength;
    private JSpinner changeTempo;
    private JComboBox changeDuration;
    private JComboBox changeOctave;
    private JSpinner changeVolume;

    SpinnerNumberModel model1 = new SpinnerNumberModel(5.0, 0.0, 9.0, 1.0);

    boolean instructionCheck = true;

    public InstructionFormWordLength() {

        changeTempo.setModel(model1);

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                String modoperator = String.valueOf(setOperator.getSelectedItem());
                String modvalue = String.valueOf(setLength.getValue());
                String soundmod = String.valueOf(setSoundMod.getSelectedItem());

                String inputTempo = String.valueOf(setTempo.getSelectedItem());
                String inputDuration = String.valueOf(setDuration.getSelectedItem());
                String inputOctave = String.valueOf(setOctave.getSelectedItem());
                String inputInstrument = String.valueOf(setInstrument.getSelectedItem());
                String inputVolume = String.valueOf(setVolume.getValue());
                String inputPercussion = String.valueOf(setPercussion.getSelectedItem());

                Queue.Instruction instruction = new Queue.Instruction();
                instruction.setMod(Queue.Instruction.Mods.WORDLENGTH);
                instruction.setModOperator(Queue.Instruction.ModOperators.valueOf(modoperator));
                instruction.setModValue(modvalue);
                instruction.setSoundMod(Queue.Instruction.SoundMods.valueOf(soundmod));
                switch (instruction.soundMod) {
                    case TEMPO:
                        instruction.setSoundModValue(inputTempo);
                        break;
                    case NOTEDURATION:
                        instruction.setSoundModValue(inputDuration);
                        break;
                    case OCTAVE:
                        instruction.setSoundModValue(inputOctave);
                        break;
                    case INSTRUMENT:
                        instruction.setSoundModValue(inputInstrument);
                        break;
                    case VOLUME:
                        instruction.setSoundModValue(inputVolume);
                        break;
                    case PERCUSSION:
                        instruction.setSoundModValue(inputPercussion);
                        break;
                }

                for (Queue.Instruction i : TextSound.instructions) {
                    if (i.getMod() == instruction.getMod() && i.getSoundMod() == instruction.getSoundMod()) {
                        System.out.println("already exist   ");
                        instructionCheck = false;
                    }
                }

                if (instructionCheck) {
                    //System.out.println(instruction.toString());
                    TextSound.instructions.add(instruction);
                    MainForm.listAddInstruction(MainForm.model, instruction);
                } else {
                    JOptionPane.showMessageDialog(null,
                            "NO",
                            "NO",
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

        setSoundMod.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (setSoundMod.getSelectedItem() == "TEMPO") {
                    setTempo.setVisible(true);
                    setDuration.setVisible(false);
                    setOctave.setVisible(false);
                    setInstrument.setVisible(false);
                    setVolume.setVisible(false);
                    setPercussion.setVisible(false);
                } else if (setSoundMod.getSelectedItem() == "NOTEDURATION") {
                    setTempo.setVisible(false);
                    setDuration.setVisible(true);
                    setOctave.setVisible(false);
                    setInstrument.setVisible(false);
                    setVolume.setVisible(false);
                    setPercussion.setVisible(false);
                } else if (setSoundMod.getSelectedItem() == "OCTAVE") {
                    setTempo.setVisible(false);
                    setDuration.setVisible(false);
                    setOctave.setVisible(true);
                    setInstrument.setVisible(false);
                    setVolume.setVisible(false);
                    setPercussion.setVisible(false);
                } else if (setSoundMod.getSelectedItem() == "INSTRUMENT") {
                    setTempo.setVisible(false);
                    setDuration.setVisible(false);
                    setOctave.setVisible(false);
                    setInstrument.setVisible(true);
                    setVolume.setVisible(false);
                    setPercussion.setVisible(false);
                } else if (setSoundMod.getSelectedItem() == "VOLUME") {
                    setTempo.setVisible(false);
                    setDuration.setVisible(false);
                    setOctave.setVisible(false);
                    setInstrument.setVisible(false);
                    setVolume.setVisible(true);
                    setPercussion.setVisible(false);
                } else if (setSoundMod.getSelectedItem() == "PERCUSSION") {
                    setTempo.setVisible(false);
                    setDuration.setVisible(false);
                    setOctave.setVisible(false);
                    setInstrument.setVisible(false);
                    setVolume.setVisible(false);
                    setPercussion.setVisible(true);
                }
                InstructionFormWordLength.super.pack();
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
        contentPane.setLayout(new GridLayoutManager(2, 5, new Insets(10, 10, 10, 10), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(6, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel1.setVisible(true);
        contentPane.add(panel1, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
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
        panel1.add(setTempo, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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
        panel1.add(setDuration, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setInstrument = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel3 = new DefaultComboBoxModel();
        defaultComboBoxModel3.addElement("PIANO");
        defaultComboBoxModel3.addElement("GUITAR");
        defaultComboBoxModel3.addElement("TINKLE_BELL");
        setInstrument.setModel(defaultComboBoxModel3);
        setInstrument.setVisible(false);
        panel1.add(setInstrument, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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
        panel1.add(setOctave, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setVolume = new JSlider();
        setVolume.setMajorTickSpacing(1000);
        setVolume.setMaximum(16383);
        setVolume.setValue(8000);
        setVolume.setVisible(false);
        panel1.add(setVolume, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setPercussion = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel5 = new DefaultComboBoxModel();
        defaultComboBoxModel5.addElement("Bass_Drum");
        defaultComboBoxModel5.addElement("Hi_Bongo");
        defaultComboBoxModel5.addElement("Hand_Clap");
        setPercussion.setModel(defaultComboBoxModel5);
        setPercussion.setVisible(false);
        panel1.add(setPercussion, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel2, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        panel2.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonOK = new JButton();
        buttonOK.setText("OK");
        panel3.add(buttonOK, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCancel = new JButton();
        buttonCancel.setText("Cancel");
        panel3.add(buttonCancel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setOperator = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel6 = new DefaultComboBoxModel();
        defaultComboBoxModel6.addElement("EQUALTO");
        defaultComboBoxModel6.addElement("LARGERTHAN");
        defaultComboBoxModel6.addElement("LESSTHAN");
        setOperator.setModel(defaultComboBoxModel6);
        contentPane.add(setOperator, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setSoundMod = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel7 = new DefaultComboBoxModel();
        defaultComboBoxModel7.addElement("TEMPO");
        defaultComboBoxModel7.addElement("NOTEDURATION");
        defaultComboBoxModel7.addElement("OCTAVE");
        defaultComboBoxModel7.addElement("INSTRUMENT");
        defaultComboBoxModel7.addElement("VOLUME");
        setSoundMod.setModel(defaultComboBoxModel7);
        contentPane.add(setSoundMod, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setLength = new JSpinner();
        contentPane.add(setLength, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(4, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel4.setVisible(false);
        contentPane.add(panel4, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        changeTempo = new JSpinner();
        panel4.add(changeTempo, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        changeDuration = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel8 = new DefaultComboBoxModel();
        defaultComboBoxModel8.addElement("1.00");
        defaultComboBoxModel8.addElement("0.50");
        defaultComboBoxModel8.addElement("0.25");
        defaultComboBoxModel8.addElement("0.125");
        defaultComboBoxModel8.addElement("0.0625");
        defaultComboBoxModel8.addElement("0.03125");
        defaultComboBoxModel8.addElement("0.015625");
        changeDuration.setModel(defaultComboBoxModel8);
        changeDuration.setVisible(true);
        panel4.add(changeDuration, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        changeOctave = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel9 = new DefaultComboBoxModel();
        defaultComboBoxModel9.addElement("0");
        defaultComboBoxModel9.addElement("1");
        defaultComboBoxModel9.addElement("2");
        defaultComboBoxModel9.addElement("3");
        defaultComboBoxModel9.addElement("4");
        defaultComboBoxModel9.addElement("5");
        defaultComboBoxModel9.addElement("6");
        defaultComboBoxModel9.addElement("7");
        defaultComboBoxModel9.addElement("8");
        defaultComboBoxModel9.addElement("9");
        defaultComboBoxModel9.addElement("10");
        changeOctave.setModel(defaultComboBoxModel9);
        changeOctave.setVisible(false);
        panel4.add(changeOctave, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        changeVolume = new JSpinner();
        changeVolume.setVisible(false);
        panel4.add(changeVolume, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

}
