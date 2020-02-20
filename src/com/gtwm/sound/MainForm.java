package com.gtwm.sound;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Map;
import java.util.List;

public class MainForm extends JFrame {
    private JButton btnLoadText;
    private JPanel panel1;
    private JTextArea textArea1;
    private JButton btnProcess;
    private JComboBox setInstrument;
    private JComboBox setDuration;
    private JSlider setOctaves;
    private JComboBox setTempo;
    private JButton btnGetDB;
    private JButton button1;
    private JButton btnWordClass;

    String inputText = "";

    File workingDirectory = new File(System.getProperty("user.dir"));
    // Set default
    String outFilename = "output.mid";

    JFileChooser fc = new JFileChooser();

    Map<String, Double> thisMap;

    public MainForm() {

        btnLoadText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // Handle open button action
                if (e.getSource() == btnLoadText) {
                    //File workingDirectory = new File(System.getProperty("user.dir"));
                    fc.setCurrentDirectory(workingDirectory);

                    int returnVal = fc.showOpenDialog(panel1);

                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = fc.getSelectedFile();
                        //This is where a real application would open the file.
                        try {
                            inputText = TextSound.loadFile(file.getAbsolutePath());
                            outFilename = file.getAbsoluteFile() + ".mid";
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        textArea1.setText(inputText);
                    } else {
                        System.out.println("Open command cancelled by user.");
                    }
                }
            }
        });

        btnProcess.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // Handle open button action
                if (e.getSource() == btnProcess) {
                    if (textArea1.getLineCount() > 0) {
                        try {
                            // Get settings from the form
                            TextSound.instrument = String.valueOf(setInstrument.getSelectedItem());
                            System.out.println("Instrument: " + TextSound.instrument);

                            TextSound.noteLength = Double.parseDouble(String.valueOf(setDuration.getSelectedItem()));
                            System.out.println("Note Length: " + TextSound.noteLength);

                            TextSound.octaves = Double.valueOf(setOctaves.getValue());
                            System.out.println("Octaves: " + TextSound.octaves);

                            /*switch (String.valueOf(setTempo.getSelectedItem())) {
                                case "40":
                                    TextSound.tempo = 40d;
                                case "45":
                                    TextSound.tempo = 45d;
                                case "50":
                                    TextSound.tempo = 50d;
                                case "120":
                                    TextSound.tempo = 120d;
                                case "220":
                                    TextSound.tempo = 220d;
                            }*/

                            TextSound.tempo = Double.parseDouble((String.valueOf(setTempo.getSelectedItem())));
                            System.out.println("Tempo: " + TextSound.tempo);

                            // Process text
                            TextSound.runStuff(textArea1.getText(), outFilename);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });

        btnGetDB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                List<SenseMap.Mapping> items;

                // Handle open button action
                if (e.getSource() == btnGetDB) {
                    File workingDirectory = new File(System.getProperty("user.dir"));
                    fc.setCurrentDirectory(workingDirectory);

                    int returnVal = fc.showOpenDialog(panel1);

                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = fc.getSelectedFile();
                        //This is where a real application would open the file.
                        try {

                            //thisMap = TextSound.getMapFromCSV(file.getAbsolutePath());

                            //thisMap.entrySet().forEach(entry -> {
                            //    System.out.println(entry.getKey() + " " + entry.getValue());
                            //});

                            csvparser myParser = new csvparser();
                            items = myParser.csvtoSenseMap(file.getPath());

                            for (SenseMap.Mapping item : items) {
                                //System.out.println(item.getWordKey() + ", " + item.getWordClass() + ", " + item.getWordValue());

                                if (item.getWordClass() == SenseMap.Classification.n) {
                                    //System.out.println("Noun: " + item.getWordKey());
                                    TextSound.wordTypes.add(item.getWordKey());
                                }
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    } else {
                        System.out.println("Open command cancelled by user.");
                    }
                }
            }
        });

        btnWordClass.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // Handle open button action
                if (e.getSource() == btnWordClass) {
                   
                }
            }
        });

    }

    public static void createAndShowGUI() {
        JFrame frame = new JFrame("TextSound");
        frame.setContentPane(new MainForm().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel"); //Windows Look and feel
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                    ex.printStackTrace();
                }
                createAndShowGUI();
            }
        });
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
        panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(3, 2, new Insets(20, 20, 20, 20), -1, -1));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(5, 3, new Insets(0, 50, 0, 50), -1, -1));
        panel1.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(500, 500), null, null, 0, false));
        setInstrument = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("PIANO");
        defaultComboBoxModel1.addElement("GUITAR");
        defaultComboBoxModel1.addElement("TINKLE_BELL");
        setInstrument.setModel(defaultComboBoxModel1);
        panel2.add(setInstrument, new GridConstraints(0, 1, 2, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 10, false));
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
        panel2.add(setDuration, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 10, false));
        setOctaves = new JSlider();
        setOctaves.setMaximum(10);
        setOctaves.setOrientation(0);
        setOctaves.setPaintLabels(true);
        setOctaves.setPaintTicks(true);
        setOctaves.setPaintTrack(true);
        setOctaves.setSnapToTicks(false);
        setOctaves.setValue(5);
        setOctaves.setValueIsAdjusting(true);
        panel2.add(setOctaves, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 10, false));
        setTempo = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel3 = new DefaultComboBoxModel();
        defaultComboBoxModel3.addElement("40");
        defaultComboBoxModel3.addElement("45");
        defaultComboBoxModel3.addElement("50");
        defaultComboBoxModel3.addElement("120");
        defaultComboBoxModel3.addElement("220");
        setTempo.setModel(defaultComboBoxModel3);
        setTempo.setSelectedIndex(0);
        panel2.add(setTempo, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 10, false));
        final JLabel label1 = new JLabel();
        label1.setText("Octave range:");
        panel2.add(label1, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Tempo (BPS):");
        panel2.add(label2, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Note duration:");
        panel2.add(label3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Instrument:");
        panel2.add(label4, new GridConstraints(0, 0, 2, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnWordClass = new JButton();
        btnWordClass.setText("Word Type");
        panel2.add(btnWordClass, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel1.add(scrollPane1, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(500, 500), null, null, 0, false));
        textArea1 = new JTextArea();
        textArea1.setEditable(true);
        textArea1.setLineWrap(true);
        textArea1.setWrapStyleWord(true);
        scrollPane1.setViewportView(textArea1);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel3, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        btnLoadText = new JButton();
        btnLoadText.setText("Load");
        panel3.add(btnLoadText, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(5, 5), null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel3.add(spacer1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        btnGetDB = new JButton();
        btnGetDB.setText("Button");
        panel3.add(btnGetDB, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel4, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        btnProcess = new JButton();
        btnProcess.setText("Start");
        panel4.add(btnProcess, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Settings");
        panel1.add(label5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        label2.setLabelFor(setTempo);
        label3.setLabelFor(setDuration);
        label4.setLabelFor(setInstrument);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }

}