package com.gtwm.sound;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import org.w3c.dom.Text;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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
    private JComboBox setFrequency;
    private JRadioButton lexnamesRadioButton;
    private JRadioButton staticRadioButton;
    private JRadioButton muteRadioButton;
    private JCheckBox characterCheckBox;
    private JCheckBox wordCheckBox;
    private JRadioButton onRadioButton;
    private JRadioButton offRadioButton;

    // Set default database directory
    final File workingDirectory = new File(System.getProperty("user.dir"));

    // Input file name
    String inputText = "";

    // Set default output file name
    String outFilename = "output.mid";

    // Creating a text model for instructions textarea so that it can be updated
    static DefaultListModel model = new DefaultListModel();
    static JTextArea textModel;

    // To get and load user settings
    static Preferences prefs = Preferences.userNodeForPackage(Main.class);

    // Storing database words and values to list item
    List<SenseMap.Mapping> allItems;

    //static Highlighter highlighter;
    //static HighlightPainter painter;

    // Splash screen coordinates
    final int splashx = 100;
    final int splashy = 450;

    public Main() {

        // Load splash
        final SplashScreen splash = SplashScreen.getSplashScreen();
        if (splash == null) {
            System.out.println("SplashScreen.getSplashScreen() returned null");
            return;
        }
        Graphics2D g = splash.createGraphics();
        if (g == null) {
            System.out.println("g is null");
            return;
        }
        g.drawString("DLC TextSound ver 0.1", splashx, splashy - 20);
        g.drawString("Starting up...", splashx, splashy);
        splash.update();

        list1.setModel(model);
        textModel = this.textArea1;

        textArea1.getDocument().addDocumentListener(documentListener);

        JFileChooser fc = new JFileChooser();
        csvparser myParser = new csvparser();

        //highlighter = this.textArea1.getHighlighter();
        //painter = new DefaultHighlighter.DefaultHighlightPainter(Color.pink);

        List<SenseMap.Mapping> tempList = new ArrayList<>();

        // Preload database
        try {
            // Update splash screen
            g.drawString("Loading databases...", splashx, splashy + 20);
            splash.update();

            //Load test database
            //allItems = myParser.csvtoSenseMap(workingDirectory.toString() + "/db/test.csv");

            // Load all database files in directory 'db'
            Files.walk(Paths.get(workingDirectory.toString() + "/db/"))
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".csv"))
                    .forEach(p -> {
                        try {
                            if (allItems == null) {
                                allItems = myParser.csvtoSenseMap(p.toString());
                                System.out.println("Initializing database using: " + p.toString());
                            } else {
                                allItems.addAll(myParser.csvtoSenseMap(p.toString()));
                                System.out.println("Reading " + p.toString());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

            //tfDBpath.setText("OK");

            // True if duplicate found in database
            boolean dupeCheck;

            // To find words that belong to multiple word categories
            // Loop through the database, add to word type and value if key is found
            for (SenseMap.Mapping i : allItems) {
                dupeCheck = false;
                for (SenseMap.Mapping j : tempList) {
                    if (j.getKey().equalsIgnoreCase(i.getKey())) {
                        //if (j.toString().equalsIgnoreCase(i.toString())) {
                        //System.out.println("Exists already: " + j.getKey());

                        j.addType(i.wordType);
                        j.addValue(i.wordValue);

                        dupeCheck = true;
                    }
                }
                if (!dupeCheck) {
                    tempList.add(i);
                }
            }

            //System.out.println(tempList.toString());

            // Write final results in file for error logging
            FileWriter writer = new FileWriter("resultlist.txt");
            for (SenseMap.Mapping str : tempList) {
                writer.write(str + System.lineSeparator());
            }
            writer.close();

            TextSound.items = tempList;

        } catch (
                Exception ex) {
            ex.printStackTrace();
        }

        // Load user preferences
        //loadSettings(this.textArea1, TextSound.prefsFile);

        // Can remove (??)
        btnLoadText.addActionListener(new

                                              ActionListener() {
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

        btnProcess.addActionListener(new

                                             ActionListener() {
                                                 @Override
                                                 public void actionPerformed(ActionEvent e) {

                                                     // Handle open button action
                                                     if (e.getSource() == btnProcess) {
                                                         if (textArea1.getLineCount() > 0) {

                                                             fc.setCurrentDirectory(workingDirectory);

                                                             int returnVal = fc.showSaveDialog(panel1);

                                                             if (returnVal == JFileChooser.APPROVE_OPTION) {
                                                                 File fileToSave = fc.getSelectedFile();
                                                                 outFilename = fileToSave.getAbsoluteFile().toString() + ".mid";
                                                                 System.out.println("Save as file: " + outFilename);

                                                                 try {
                                                                     // Get initial settings from user inputs
                                                                     setBaseValues();

                                                                     // Process text
                                                                     TextSound.runStuff(textArea1.getText(), outFilename);
                                                                 } catch (Exception ex) {
                                                                     ex.printStackTrace();
                                                                 }
                                                             } else {
                                                                 System.out.println("Save command cancelled by user.");
                                                             }
                                                         }
                                                     }
                                                 }
                                             });

        // Safe to delete
        btnGetDB.addActionListener(new

                                           ActionListener() {
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

        btnRemoveInstruction.addActionListener(new

                                                       ActionListener() {
                                                           @Override
                                                           public void actionPerformed(ActionEvent e) {
                                                               Queue.Instruction selectedInstruction = list1.getSelectedValue();
                                                               TextSound.instructions.remove(selectedInstruction);
                                                               model.removeElement(selectedInstruction);
                                                           }
                                                       });
        btnRemoveInstruction.setMnemonic(KeyEvent.VK_DELETE);

        btnAddInstruction.addActionListener(new

                                                    ActionListener() {
                                                        @Override
                                                        public void actionPerformed(ActionEvent e) {
                                                            if (btnAddInstruction.getSelectedItem() == "WORDTYPE") {
                                                                InstructionFormWordType dialog = new InstructionFormWordType();
                                                                dialog.setTitle("Transformation: Word Type");
                                                                dialog.pack();
                                                                dialog.setLocationRelativeTo(panelInstructions);
                                                                dialog.setVisible(true);
                                                                btnAddInstruction.setSelectedIndex(0);
                                                            } else if (btnAddInstruction.getSelectedItem() == "WORDLENGTH") {
                                                                InstructionFormWordLength dialog = new InstructionFormWordLength();
                                                                dialog.setTitle("Transformation: Word Length");
                                                                dialog.pack();
                                                                dialog.setLocationRelativeTo(panelInstructions);
                                                                dialog.setVisible(true);
                                                                btnAddInstruction.setSelectedIndex(0);
                                                            } else if (btnAddInstruction.getSelectedItem() == "LEXNAME") {
                                                                InstructionFormWordValue dialog = new InstructionFormWordValue();
                                                                dialog.setTitle("Transformation: Lexname");
                                                                dialog.pack();
                                                                dialog.setLocationRelativeTo(panelInstructions);
                                                                dialog.setVisible(true);
                                                                btnAddInstruction.setSelectedIndex(0);
                                                            } else if (btnAddInstruction.getSelectedItem() == "PUNCTUATION") {
                                                                InstructionFormPunctuation dialog = new InstructionFormPunctuation();
                                                                dialog.setTitle("Transformation: Punctuation");
                                                                dialog.pack();
                                                                dialog.setLocationRelativeTo(panelInstructions);
                                                                dialog.setVisible(true);
                                                                btnAddInstruction.setSelectedIndex(0);
                                                            } else if (btnAddInstruction.getSelectedItem() == "CHARACTER") {
                                                                InstructionFormCharacter dialog = new InstructionFormCharacter();
                                                                dialog.setTitle("Transformation: Character");
                                                                dialog.pack();
                                                                dialog.setLocationRelativeTo(panelInstructions);
                                                                dialog.setVisible(true);
                                                                btnAddInstruction.setSelectedIndex(0);
                                                            }
                                                        }
                                                    });

        characterCheckBox.addActionListener(new

                                                    ActionListener() {
                                                        @Override
                                                        public void actionPerformed(ActionEvent actionEvent) {
                                                            TextSound.perChar = !TextSound.perChar;
                                                        }
                                                    });

        wordCheckBox.addActionListener(new

                                               ActionListener() {
                                                   @Override
                                                   public void actionPerformed(ActionEvent actionEvent) {
                                                       TextSound.perWord = !TextSound.perWord;
                                                   }
                                               });
    }

    public static void listAddInstruction(DefaultListModel thisModel, Queue.Instruction thisInstruction) {
        thisModel.addElement(thisInstruction);
    }

    // Listen for changes to the text area for real-time processing
    private DocumentListener documentListener = new DocumentListener() {
        int wordLen = 0;
        int lastWordLen;
        StringBuilder currentWord = new StringBuilder();
        String lastWord;

        @Override
        public void insertUpdate(DocumentEvent e) {
            try {
                String a = e.getDocument().getText(e.getOffset(), e.getLength());
                if (!a.equals(" ") && !a.equals("\n")) {
                    currentWord.append(a);
                    wordLen++;
                }
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
            //System.out.println(wordLen);
            if (onRadioButton.isSelected()) {
                streamIt(e);
            }
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            if (wordLen != 0) {
                currentWord.deleteCharAt(wordLen - 1);
                wordLen--;
            }
            //System.out.println(wordLen);
            //printIt(e);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            //printIt(e);
        }

        private void streamIt(DocumentEvent e) {
            DocumentEvent.EventType type = e.getType();
            try {
                String a = e.getDocument().getText(e.getOffset(), e.getLength());
                if (type == DocumentEvent.EventType.INSERT) {
                    if (a.equals(" ") || a.equals("\n")) {
                        //System.out.println(e.getDocument().getText(e.getOffset(), wordLen));

                        try {
                            // Get initial settings from user inputs
                            setBaseValues();

                            // Process text
                            TextSound.streamText(currentWord);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        lastWord = currentWord.toString();
                        currentWord.setLength(0);
                        lastWordLen = wordLen;
                        wordLen = 0;
                        //System.out.println(lastWord);
                    }
                }
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        }
    };

    private void renderSplashFrame(Graphics2D g, int frame) {
        final String[] comps = {".", "..", "..."};
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(120, 140, 200, 40);
        g.setPaintMode();
        g.setColor(Color.WHITE);
        g.drawString("Loading " + comps[(frame / 5) % 3], 120, 150);
    }

    private static void saveSettings(String saveFile) {
        TextSound.prefsFile = saveFile;
        // Save instructions to file
        ObjectOutputStream x = serialInstructionsQueue.serializeObject(TextSound.instructions);
        prefs.put("instructionsPref", x.toString());
        prefs.put("textPref", Main.textModel.getText());
    }

    private static void loadSettings(JTextArea textArea, String loadFile) {
        TextSound.prefsFile = loadFile;
        // Get user settings
        String prefString = prefs.get("instructionsPref", "x");
        TextSound.instructions = serialInstructionsQueue.deserializeObject(prefString);
        Main.model.clear();
        for (Queue.Instruction i : TextSound.instructions) {
            listAddInstruction(Main.model, i);
        }
        String prefString2 = prefs.get("textPref", "y");
        //textArea.setText(prefString2);
    }

    private void setBaseValues() {
        TextSound.userInstrument = String.valueOf(setInstrument.getSelectedItem());
        System.out.println("Instrument: " + TextSound.userInstrument);

        TextSound.userNoteLength = Double.parseDouble(String.valueOf(setDuration.getSelectedItem()));
        System.out.println("Note Length: " + TextSound.userNoteLength);

        TextSound.userOctaves = Double.valueOf(setOctaves.getValue());
        System.out.println("Octaves: " + TextSound.userOctaves);

        TextSound.userTempo = Double.parseDouble(String.valueOf(setTempo.getSelectedItem()));
        System.out.println("Tempo: " + TextSound.userTempo);

        TextSound.userBaseFrequency = Double.parseDouble(String.valueOf(setFrequency.getSelectedItem()));
        System.out.println("Frequency: " + TextSound.userBaseFrequency);

        TextSound.perWord = wordCheckBox.isSelected();
        TextSound.perChar = characterCheckBox.isSelected();

        if (lexnamesRadioButton.isSelected()) {
            TextSound.defaultNoteOperation = TextSound.noteOperationType.LEXNAMEFREQ;
        } else if (staticRadioButton.isSelected()) {
            TextSound.defaultNoteOperation = TextSound.noteOperationType.STATICFREQ;
        } else if (muteRadioButton.isSelected()) {
            TextSound.defaultNoteOperation = TextSound.noteOperationType.MUTE;
        }
    }

    private static void createAndShowGUI() {
        JFileChooser fc = new JFileChooser();
        final File workingDirectory = new File(System.getProperty("user.dir"));

        // Menu
        JMenuBar menuBar = new JMenuBar();
        JMenuItem loadText, saveText, loadSettings, saveSettings, exitItem;

        // File
        JMenu fileMenu = new JMenu("File");

        // Menu Item (Drop down menus)
        // Load text file
        loadText = new JMenuItem("Import Text");
        loadText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                fc.setCurrentDirectory(workingDirectory);
                String inputText = "";

                int returnVal = fc.showOpenDialog(Main.textModel);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    //This is where a real application would open the file.
                    try {
                        inputText = TextSound.loadFile(file.getAbsolutePath());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    Main.textModel.setText(inputText);
                } else {
                    System.out.println("Open command cancelled by user.");
                }
            }
        });

        // Save text as file
        saveText = new JMenuItem("Export Text as...");
        saveText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fc.setCurrentDirectory(workingDirectory);
                String outFile = "";

                int returnVal = fc.showSaveDialog(Main.textModel);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fc.getSelectedFile();
                    outFile = fileToSave.getAbsoluteFile().toString() + ".txt";
                    System.out.println("Save text as file: " + outFile);
                    try {
                        FileWriter fw = new FileWriter(outFile, true);
                        Main.textModel.write(fw);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    System.out.println("Save command cancelled by user.");
                }
            }
        });

        // Import instructions
        loadSettings = new JMenuItem("Load Settings");
        loadSettings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fc.setCurrentDirectory(workingDirectory);
                String prefsFile = "";

                int returnVal = fc.showOpenDialog(Main.textModel);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    prefsFile = file.getAbsoluteFile().toString();
                    System.out.println("Load user settings from file: " + prefsFile);
                    loadSettings(Main.textModel, prefsFile);
                } else {
                    System.out.println("Open command cancelled by user.");
                }
            }
        });

        // Export instructions
        saveSettings = new JMenuItem("Save Settings as...");
        saveSettings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fc.setCurrentDirectory(workingDirectory);
                String prefsFile = "";

                int returnVal = fc.showSaveDialog(Main.textModel);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fc.getSelectedFile();
                    prefsFile = fileToSave.getAbsoluteFile().toString();
                    System.out.println("Save user settings as file: " + prefsFile);
                    saveSettings(prefsFile);
                } else {
                    System.out.println("Save command cancelled by user.");
                }
            }
        });

        // Separators
        JSeparator separatorBar1 = new JSeparator();
        JSeparator separatorBar2 = new JSeparator();

        // Exit
        exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                System.exit(0);
            }
        });

        // Adding menu items to menu
        fileMenu.add(saveText);
        fileMenu.add(loadText);
        fileMenu.add(separatorBar1);
        fileMenu.add(saveSettings);
        fileMenu.add(loadSettings);
        fileMenu.add(separatorBar2);
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
        panel2.setLayout(new GridLayoutManager(9, 4, new Insets(0, 50, 0, 50), -1, -1));
        panel1.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(250, 500), null, 0, false));
        setInstrument = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("PIANO");
        defaultComboBoxModel1.addElement("GUITAR");
        defaultComboBoxModel1.addElement("TINKLE_BELL");
        setInstrument.setModel(defaultComboBoxModel1);
        panel2.add(setInstrument, new GridConstraints(0, 1, 2, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        setDuration = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        defaultComboBoxModel2.addElement("1.00");
        defaultComboBoxModel2.addElement("0.50");
        defaultComboBoxModel2.addElement("0.25");
        defaultComboBoxModel2.addElement("0.125");
        defaultComboBoxModel2.addElement("0.0625");
        defaultComboBoxModel2.addElement("0.03125");
        defaultComboBoxModel2.addElement("0.015625");
        defaultComboBoxModel2.addElement("1.00");
        setDuration.setModel(defaultComboBoxModel2);
        setDuration.setSelectedIndex(1);
        panel2.add(setDuration, new GridConstraints(2, 1, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        setOctaves = new JSlider();
        setOctaves.setMaximum(10);
        setOctaves.setOrientation(0);
        setOctaves.setPaintLabels(true);
        setOctaves.setPaintTicks(true);
        setOctaves.setPaintTrack(true);
        setOctaves.setSnapToTicks(false);
        setOctaves.setValue(2);
        setOctaves.setValueIsAdjusting(true);
        panel2.add(setOctaves, new GridConstraints(3, 1, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
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
        setTempo.setRequestFocusEnabled(true);
        setTempo.setSelectedIndex(6);
        panel2.add(setTempo, new GridConstraints(4, 1, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        final JLabel label1 = new JLabel();
        label1.setText("Octave Range:");
        panel2.add(label1, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Tempo (BPS):");
        panel2.add(label2, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Note Duration:");
        panel2.add(label3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Instrument:");
        panel2.add(label4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Frequency:");
        panel2.add(label5, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setFrequency = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel4 = new DefaultComboBoxModel();
        defaultComboBoxModel4.addElement("110");
        defaultComboBoxModel4.addElement("111");
        defaultComboBoxModel4.addElement("112");
        defaultComboBoxModel4.addElement("113");
        defaultComboBoxModel4.addElement("114");
        defaultComboBoxModel4.addElement("115");
        defaultComboBoxModel4.addElement("116");
        defaultComboBoxModel4.addElement("117");
        defaultComboBoxModel4.addElement("118");
        defaultComboBoxModel4.addElement("119");
        defaultComboBoxModel4.addElement("120");
        defaultComboBoxModel4.addElement("121");
        defaultComboBoxModel4.addElement("122");
        defaultComboBoxModel4.addElement("123");
        defaultComboBoxModel4.addElement("124");
        defaultComboBoxModel4.addElement("125");
        defaultComboBoxModel4.addElement("126");
        defaultComboBoxModel4.addElement("127");
        defaultComboBoxModel4.addElement("128");
        defaultComboBoxModel4.addElement("129");
        defaultComboBoxModel4.addElement("130");
        defaultComboBoxModel4.addElement("131");
        defaultComboBoxModel4.addElement("132");
        defaultComboBoxModel4.addElement("133");
        defaultComboBoxModel4.addElement("134");
        defaultComboBoxModel4.addElement("135");
        defaultComboBoxModel4.addElement("136");
        defaultComboBoxModel4.addElement("137");
        defaultComboBoxModel4.addElement("138");
        defaultComboBoxModel4.addElement("139");
        defaultComboBoxModel4.addElement("140");
        defaultComboBoxModel4.addElement("141");
        defaultComboBoxModel4.addElement("142");
        defaultComboBoxModel4.addElement("143");
        defaultComboBoxModel4.addElement("144");
        defaultComboBoxModel4.addElement("145");
        defaultComboBoxModel4.addElement("146");
        setFrequency.setModel(defaultComboBoxModel4);
        setFrequency.setSelectedIndex(18);
        panel2.add(setFrequency, new GridConstraints(5, 1, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        lexnamesRadioButton = new JRadioButton();
        lexnamesRadioButton.setSelected(true);
        lexnamesRadioButton.setText("Overtone");
        panel2.add(lexnamesRadioButton, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        staticRadioButton = new JRadioButton();
        staticRadioButton.setText("Static");
        panel2.add(staticRadioButton, new GridConstraints(7, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        muteRadioButton = new JRadioButton();
        muteRadioButton.setText("Mute");
        panel2.add(muteRadioButton, new GridConstraints(7, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        final JLabel label6 = new JLabel();
        label6.setText("Default Note Behaviour:");
        panel2.add(label6, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Scope:");
        panel2.add(label7, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        wordCheckBox = new JCheckBox();
        wordCheckBox.setSelected(true);
        wordCheckBox.setText("Word");
        panel2.add(wordCheckBox, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        characterCheckBox = new JCheckBox();
        characterCheckBox.setText("Character");
        panel2.add(characterCheckBox, new GridConstraints(6, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        onRadioButton = new JRadioButton();
        onRadioButton.setSelected(false);
        onRadioButton.setText("On");
        panel2.add(onRadioButton, new GridConstraints(8, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        offRadioButton = new JRadioButton();
        offRadioButton.setSelected(true);
        offRadioButton.setText("Off");
        panel2.add(offRadioButton, new GridConstraints(8, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        final JLabel label8 = new JLabel();
        label8.setText("Stream Mode:");
        panel2.add(label8, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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
        tfDBpath = new JTextField();
        tfDBpath.setEditable(false);
        tfDBpath.setVisible(false);
        panel3.add(tfDBpath, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnGetDB = new JButton();
        btnGetDB.setText("...");
        btnGetDB.setVisible(false);
        panel3.add(btnGetDB, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(5, 5), null, 3, false));
        btnLoadText = new JButton();
        btnLoadText.setText("Load file");
        btnLoadText.setVisible(false);
        panel3.add(btnLoadText, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(5, 5), null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel4, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        btnProcess = new JButton();
        btnProcess.setText("Start");
        panel4.add(btnProcess, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label9 = new JLabel();
        label9.setText("Base Settings");
        panel1.add(label9, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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
        btnRemoveInstruction.setToolTipText("Alt+Del");
        panelInstructions.add(btnRemoveInstruction, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(2, 2), null, 0, false));
        btnAddInstruction = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel5 = new DefaultComboBoxModel();
        defaultComboBoxModel5.addElement("");
        defaultComboBoxModel5.addElement("WORDTYPE");
        defaultComboBoxModel5.addElement("WORDLENGTH");
        defaultComboBoxModel5.addElement("LEXNAME");
        defaultComboBoxModel5.addElement("PUNCTUATION");
        defaultComboBoxModel5.addElement("CHARACTER");
        btnAddInstruction.setModel(defaultComboBoxModel5);
        panelInstructions.add(btnAddInstruction, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label10 = new JLabel();
        label10.setText("Transformation");
        panelInstructions.add(label10, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        label2.setLabelFor(setTempo);
        label3.setLabelFor(setDuration);
        label4.setLabelFor(setInstrument);
        ButtonGroup buttonGroup;
        buttonGroup = new ButtonGroup();
        buttonGroup.add(lexnamesRadioButton);
        buttonGroup.add(staticRadioButton);
        buttonGroup.add(muteRadioButton);
        buttonGroup = new ButtonGroup();
        buttonGroup.add(onRadioButton);
        buttonGroup.add(offRadioButton);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }


}