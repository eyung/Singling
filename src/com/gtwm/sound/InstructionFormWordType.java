package com.gtwm.sound;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class InstructionFormWordType extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox setType;
    private JComboBox setSoundMod;
    private JComboBox setTempo;
    private JComboBox setDuration;
    private JComboBox setInstrument;
    private JComboBox setOctave;
    private JSlider setVolume;
    private JComboBox setPercussion;

    public InstructionFormWordType() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                String modvalue = String.valueOf(setType.getSelectedItem());
                String soundmod = String.valueOf(setSoundMod.getSelectedItem());

                String inputTempo = String.valueOf(setTempo.getSelectedItem());
                String inputDuration = String.valueOf(setDuration.getSelectedItem());
                String inputOctave = String.valueOf(setOctave.getSelectedItem());
                String inputInstrument = String.valueOf(setInstrument.getSelectedItem());
                String inputVolume = String.valueOf(setVolume.getValue());
                String inputPercussion = String.valueOf(setPercussion.getSelectedItem());

                Queue.Instruction instruction = new Queue.Instruction();
                instruction.setMod(Queue.Instruction.Mods.WORDTYPE);
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

                //System.out.println(instruction.toString());
                TextSound.instructions.add(instruction);
                MainForm.listAddInstruction(MainForm.model, instruction);

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
                InstructionFormWordType.super.pack();
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
        contentPane.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        contentPane.setEnabled(true);
        contentPane.setMinimumSize(new Dimension(461, 101));
        contentPane.setOpaque(false);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        panel1.add(panel2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonOK = new JButton();
        buttonOK.setText("OK");
        panel2.add(buttonOK, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCancel = new JButton();
        buttonCancel.setText("Cancel");
        panel2.add(buttonCancel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(6, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel3.setVisible(true);
        contentPane.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        setType = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("n");
        defaultComboBoxModel1.addElement("v");
        defaultComboBoxModel1.addElement("a");
        setType.setModel(defaultComboBoxModel1);
        panel3.add(setType, new GridConstraints(0, 0, 6, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setSoundMod = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        defaultComboBoxModel2.addElement("TEMPO");
        defaultComboBoxModel2.addElement("NOTEDURATION");
        defaultComboBoxModel2.addElement("OCTAVE");
        defaultComboBoxModel2.addElement("INSTRUMENT");
        defaultComboBoxModel2.addElement("VOLUME");
        setSoundMod.setModel(defaultComboBoxModel2);
        panel3.add(setSoundMod, new GridConstraints(0, 1, 6, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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
        panel3.add(setTempo, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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
        panel3.add(setDuration, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setInstrument = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel5 = new DefaultComboBoxModel();
        defaultComboBoxModel5.addElement("PIANO");
        defaultComboBoxModel5.addElement("GUITAR");
        defaultComboBoxModel5.addElement("TINKLE_BELL");
        setInstrument.setModel(defaultComboBoxModel5);
        setInstrument.setVisible(false);
        panel3.add(setInstrument, new GridConstraints(5, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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
        panel3.add(setOctave, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setVolume = new JSlider();
        setVolume.setMajorTickSpacing(1000);
        setVolume.setMaximum(16383);
        setVolume.setValue(8000);
        setVolume.setVisible(false);
        panel3.add(setVolume, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setPercussion = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel7 = new DefaultComboBoxModel();
        defaultComboBoxModel7.addElement("Bass_Drum");
        defaultComboBoxModel7.addElement("Hi_Bongo");
        defaultComboBoxModel7.addElement("Hand_Clap");
        setPercussion.setModel(defaultComboBoxModel7);
        setPercussion.setVisible(false);
        panel3.add(setPercussion, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

}
