package com.gtwm.sound;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.prefs.Preferences;

public class Main extends JFrame {
    private JButton btnLoadText;
    private JPanel panel1;
    private JTextArea textArea1;
    private JButton btnProcess;
    private JComboBox setInstrument;
    private JComboBox setDuration;
    private JSlider setOctaves;
    private JComboBox setTempo;
    private JButton btnGetDB;
    private JButton btnType;
    private JList<Queue.Instruction> list1;
    private JButton btnRemoveInstruction;
    private JComboBox btnAddInstruction;
    private JPanel panelInstructions;
    private JTextField tfDBpath;

    String inputText = "";

    File workingDirectory = new File(System.getProperty("user.dir"));

    // Set default output file name
    String outFilename = "output.mid";

    // Set default db path
    //String dbFile = "C:/Users/eyung/Downloads/dlc/TextSound/database.csv";
    String dbFile = "C:/Users/Effiam/IdeaProjects/TextSound/database.csv";

    final String dbAdj = "C:/Users/Effiam/IdeaProjects/TextSound/db/xls/Adj.csv";
    final String dbAdv = "C:/Users/Effiam/IdeaProjects/TextSound/db/xls/Adv.csv";
    final String dbModals = "C:/Users/Effiam/IdeaProjects/TextSound/db/csv/Modals.csv";
    final String dbNoun = "C:/Users/Effiam/IdeaProjects/TextSound/db/xls/Noun.csv";
    final String dbPrepositions = "C:/Users/Effiam/IdeaProjects/TextSound/db/csv/Prepositions.csv";
    final String dbSymbols = "C:/Users/Effiam/IdeaProjects/TextSound/db/csv/Symbols.csv";
    final String dbVerbs = "C:/Users/Effiam/IdeaProjects/TextSound/db/xls/Verbs.csv";

    static DefaultListModel model = new DefaultListModel();

    static JTextArea textModel;

    static Preferences prefs = Preferences.userNodeForPackage(Main.class);

    public Main() {

        list1.setModel(model);
        textModel = this.textArea1;

        JFileChooser fc = new JFileChooser();

        csvparser myParser = new csvparser();

        // Preload database for testing
        try {
            //TextSound.items = myParser.csvtoSenseMap(dbFile);
            TextSound.items = (myParser.csvtoSenseMap(dbAdj));
            TextSound.items.addAll(myParser.csvtoSenseMap(dbAdv));
            //TextSound.items.addAll(myParser.csvtoSenseMap(dbModals));
            TextSound.items.addAll(myParser.csvtoSenseMap(dbNoun));
            //TextSound.items.addAll(myParser.csvtoSenseMap(dbPrepositions));
            //TextSound.items.addAll(myParser.csvtoSenseMap(dbSymbols));
            TextSound.items.addAll(myParser.csvtoSenseMap(dbVerbs));
            tfDBpath.setText("OK");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Get user settings
        String prefString = prefs.get("instructionsPref", "x");
        TextSound.instructions = serialInstructionsQueue.deserializeObject(prefString.toString());
        for (Queue.Instruction i : TextSound.instructions) {
            listAddInstruction(Main.model, i);
        }
        String prefString2 = prefs.get("textPref", "y");
        this.textArea1.setText(prefString2);

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

                            TextSound.tempo = Double.parseDouble(String.valueOf(setTempo.getSelectedItem()));
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

                // Handle open button action
                if (e.getSource() == btnGetDB) {
                    File workingDirectory = new File(System.getProperty("user.dir"));
                    fc.setCurrentDirectory(workingDirectory);

                    int returnVal = fc.showOpenDialog(panel1);

                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = fc.getSelectedFile();
                        //This is where a real application would open the file.
                        try {
                            //csvparser myParser = new csvparser();
                            TextSound.items = myParser.csvtoSenseMap(file.getPath());
                            tfDBpath.setText(file.getPath());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            tfDBpath.setText("File not found or unsupported file type");
                        }

                    } else {
                        System.out.println("Open command cancelled by user.");
                    }
                }
            }
        });

        btnRemoveInstruction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Queue.Instruction selectedInstruction = list1.getSelectedValue();
                TextSound.instructions.remove(selectedInstruction);
                model.removeElement(selectedInstruction);
            }
        });

        btnAddInstruction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (btnAddInstruction.getSelectedItem() == "WORDTYPE") {
                    InstructionFormWordType dialog = new InstructionFormWordType();
                    dialog.setTitle("Instruction: Word Type");
                    dialog.pack();
                    dialog.setLocationRelativeTo(panelInstructions);
                    dialog.setVisible(true);
                    btnAddInstruction.setSelectedIndex(0);
                } else if (btnAddInstruction.getSelectedItem() == "WORDLENGTH") {
                    InstructionFormWordLength dialog = new InstructionFormWordLength();
                    dialog.setTitle("Instruction: Word Length");
                    dialog.pack();
                    dialog.setLocationRelativeTo(panelInstructions);
                    dialog.setVisible(true);
                    btnAddInstruction.setSelectedIndex(0);
                } else if (btnAddInstruction.getSelectedItem() == "LEXNAME") {
                    InstructionFormWordValue dialog = new InstructionFormWordValue();
                    dialog.setTitle("Instruction: Lexname");
                    dialog.pack();
                    dialog.setLocationRelativeTo(panelInstructions);
                    dialog.setVisible(true);
                    btnAddInstruction.setSelectedIndex(0);
                }
            }
        });
    }

    public static void listAddInstruction(DefaultListModel thisModel, Queue.Instruction thisInstruction) {
        thisModel.addElement(thisInstruction);
    }

    public static void createAndShowGUI() {

        // Menu
        JMenuBar menuBar = new JMenuBar();
        JMenuItem saveItem, exitItem;

        // File
        JMenu fileMenu = new JMenu("File");

        // Menu Item (Drop down menus)
        saveItem = new JMenuItem("Save");
        saveItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Save instructions to file
                ObjectOutputStream x = serialInstructionsQueue.serializeObject(TextSound.instructions);
                prefs.put("instructionsPref", x.toString());
                prefs.put("textPref", Main.textModel.getText());
            }
        });
        exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                System.exit(0);
            }
        });

        // Adding menu items to menu
        fileMenu.add(saveItem);
        fileMenu.add(exitItem);

        // Adding menu to menu bar
        menuBar.add(fileMenu);

        // Frame
        JFrame frame = new JFrame("TextSound");
        frame.setJMenuBar(menuBar);
        frame.setContentPane(new Main().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
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
        panel1.setLayout(new GridLayoutManager(3, 3, new Insets(20, 20, 20, 20), -1, -1, true, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(5, 2, new Insets(0, 50, 0, 50), -1, -1));
        panel1.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(250, 500), null, 0, false));
        setInstrument = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("PIANO");
        defaultComboBoxModel1.addElement("GUITAR");
        defaultComboBoxModel1.addElement("TINKLE_BELL");
        setInstrument.setModel(defaultComboBoxModel1);
        panel2.add(setInstrument, new GridConstraints(0, 1, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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
        setDuration.setSelectedIndex(2);
        panel2.add(setDuration, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setOctaves = new JSlider();
        setOctaves.setMaximum(10);
        setOctaves.setOrientation(0);
        setOctaves.setPaintLabels(true);
        setOctaves.setPaintTicks(true);
        setOctaves.setPaintTrack(true);
        setOctaves.setSnapToTicks(false);
        setOctaves.setValue(5);
        setOctaves.setValueIsAdjusting(true);
        panel2.add(setOctaves, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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
        setTempo.setSelectedIndex(5);
        panel2.add(setTempo, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Octave range:");
        panel2.add(label1, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Tempo (BPS):");
        panel2.add(label2, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Note duration:");
        panel2.add(label3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Instrument:");
        panel2.add(label4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel1.add(scrollPane1, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(500, 500), null, 0, false));
        textArea1 = new JTextArea();
        textArea1.setEditable(true);
        textArea1.setLineWrap(true);
        textArea1.setWrapStyleWord(true);
        scrollPane1.setViewportView(textArea1);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel3, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        btnLoadText = new JButton();
        btnLoadText.setText("Load file");
        panel3.add(btnLoadText, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(5, 5), null, 0, false));
        tfDBpath = new JTextField();
        tfDBpath.setEditable(false);
        panel3.add(tfDBpath, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        btnGetDB = new JButton();
        btnGetDB.setText("...");
        panel3.add(btnGetDB, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(5, 5), null, 3, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel4, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        btnProcess = new JButton();
        btnProcess.setText("Start");
        panel4.add(btnProcess, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Settings");
        panel1.add(label5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane2 = new JScrollPane();
        panel1.add(scrollPane2, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        list1 = new JList();
        list1.setBackground(new Color(-1));
        scrollPane2.setViewportView(list1);
        panelInstructions = new JPanel();
        panelInstructions.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panelInstructions, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        btnRemoveInstruction = new JButton();
        btnRemoveInstruction.setText("-");
        panelInstructions.add(btnRemoveInstruction, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(2, 2), null, 0, false));
        btnAddInstruction = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel4 = new DefaultComboBoxModel();
        defaultComboBoxModel4.addElement("");
        defaultComboBoxModel4.addElement("WORDTYPE");
        defaultComboBoxModel4.addElement("WORDLENGTH");
        defaultComboBoxModel4.addElement("LEXNAME");
        btnAddInstruction.setModel(defaultComboBoxModel4);
        panelInstructions.add(btnAddInstruction, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Instruction");
        panelInstructions.add(label6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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