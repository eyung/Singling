package com.gtwm.sound;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.jfugue.realtime.RealtimePlayer;
import simplenlg.features.Feature;
import simplenlg.features.Form;
import simplenlg.features.Tense;
import simplenlg.framework.InflectedWordElement;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.NLGFactory;
import simplenlg.framework.WordElement;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.phrasespec.VPPhraseSpec;
import simplenlg.realiser.english.Realiser;

import javax.imageio.ImageIO;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
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
    private JList<TransformationManager.Instruction> list1;
    private JButton btnRemoveInstruction;
    private JComboBox btnAddInstruction;
    private JPanel panelTransformationInputs;
    private JComboBox setFrequency;
    private JRadioButton lexnamesRadioButton;
    private JRadioButton staticRadioButton;
    private JRadioButton muteRadioButton;
    private JRadioButton characterRadioButton;
    private JRadioButton wordRadioButton;
    private JRadioButton onRadioButton;
    private JRadioButton offRadioButton;
    private JComboBox setOrdering;
    private JButton btnPlay;
    private JPanel panelSettings;
    private JPanel panelTextInput;
    private JPanel panelTransformations;
    private JPanel panelProcessBtns;
    private JButton btnTogglePause;

    // Set default database directory
    final File workingDirectory = new File(System.getProperty("user.dir"));

    // Input file name
    private String inputText = "";

    // Set default output file name
    private String outFilename = "output.mid";

    // Creating a text model for instructions textarea so that it can be updated
    static DefaultListModel model = new DefaultListModel();
    static JTextArea textModel;

    // To get and load user settings
    static Preferences prefs = Preferences.userNodeForPackage(Main.class);

    // Storing database words and values to list item
    private List<WordMap.Mapping> allItems;

    //static Highlighter highlighter;
    //static HighlightPainter painter;

    // Splash screen coordinates
    final int splashx = 100;
    final int splashy = 450;

    // For streaming text to notes in real-time
    static RealtimePlayer realtimePlayer;

    // Initialising simplenlg classes for parsing and transforming text
    final Lexicon lexicon = Lexicon.getDefaultLexicon();
    final NLGFactory nlgFactory = new NLGFactory(lexicon);
    final Realiser realiser = new Realiser(lexicon);

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
        g.drawString("DLC Singling ver 0.2", splashx, splashy - 20);
        g.drawString("Starting up...", splashx, splashy);
        splash.update();

        // Set models
        list1.setModel(model);
        textModel = this.textArea1;

        //Icon a = new ImageIcon(getClass().getResource("/com/resources/iconfinder_ic_play_circle_fill_48px_352073.png"));
        //btnPlay.setIcon(a);

        // Add docomentListener to input text panel
        textArea1.getDocument().addDocumentListener(documentListener);

        // Initializing things
        JFileChooser fc = new JFileChooser();
        csvparser myParser = new csvparser();
        List<WordMap.Mapping> tempList = new ArrayList<>();

        // Load database files
        try {
            // Update splash screen
            g.drawString("Loading databases...", splashx, splashy + 20);
            splash.update();

            //Load test database
            //allItems = myParser.csvtoSenseMap(workingDirectory.toString() + "/db/test.csv");

            //TODO Read all csv database files when compiled as .jar file

            //ClassLoader classLoader = getClass().getClassLoader();
            //File file = new File(classLoader.getResource("splash.gif").getFile());
            //System.out.println("File found: " + file);

            //URL testurl = Main.class.getClassLoader().getResource("/db");
            //System.out.println("File found: " + testurl);

            // Load all database files in directory 'db'
            Files.walk(Paths.get(workingDirectory.toString() + "/db/"))
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".csv"))
                    .forEach(p -> {
                        try {
                            if (allItems == null) {
                                allItems = myParser.csvtoSenseMap(p.toString());
                            } else {
                                allItems.addAll(myParser.csvtoSenseMap(p.toString()));
                            }
                            System.out.println("Reading " + p.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

            // True if duplicate found in database
            boolean isFound;

            for (WordMap.Mapping i : allItems) {
                isFound = false;

                // To find words that belong to multiple lexnames,
                // loop through the database, add to word type and value if key is found
                for (WordMap.Mapping j : tempList) {
                    if (j.getKey().equalsIgnoreCase(i.getKey())) {

                        j.addType(i.wordType);
                        j.addValue(i.wordValue);

                        isFound = true;
                    }
                }

                // Add a new entry to database using unique word/key
                if (!isFound) {
                    tempList.add(i);
                }

                // Get past tense version of verb and add to list if it doesn't exist already
                if (i.getType().equals(WordMap.Type.v.toString())) {

                    WordMap.Mapping iPastTense = new WordMap.Mapping(doPastTense(i.getKey()),
                            i.getType(), i.getValue(), i.getSentimentPos(), i.getSentimentNeg());
                    if (!tempList.contains(iPastTense)) {
                        tempList.add(iPastTense);
                    }

                    WordMap.Mapping iGerund = new WordMap.Mapping(doGerund(i.getKey()),
                            i.getType(), i.getValue(), i.getSentimentPos(), i.getSentimentNeg());
                    if (!tempList.contains(iGerund)) {
                        tempList.add(iGerund);
                    }

                // Get pluralized noun and add to list if it doesn't exist already
                } else if (i.getType().equals(WordMap.Type.n.toString())) {
                    WordMap.Mapping iPlural = new WordMap.Mapping(doPluralize(i.getKey()),
                            i.getType(), i.getValue(), i.getSentimentPos(), i.getSentimentNeg());
                    if (!tempList.contains(iPlural)) {
                        tempList.add(iPlural);
                    }
                }
            }

            System.out.println(tempList.size() + " words were processed.");

            // Write final results in file for error logging
            //FileWriter writer = new FileWriter("resultlist.txt");
            //for (WordMap.Mapping str : tempList) {
            //    writer.write(str + System.lineSeparator());
            //}
            //writer.close();

            TextSound.items = tempList;

        } catch (
                Exception ex) {
            ex.printStackTrace();
        }

        // Load user preferences
        //loadSettings(this.textArea1, TextSound.prefsFile);

        btnProcess.addActionListener(new ActionListener() {
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
                                 TextSound.runStuff();
                                 TextSound.doSaveAsMidi(textArea1.getText(), outFilename);
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

        btnPlay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle open button action
                if (e.getSource() == btnPlay) {
                    if (textArea1.getLineCount() > 0) {
                        try {
                            // Get initial settings from user inputs
                            setBaseValues();

                            // Process text
                            TextSound.runStuff();
                            TextSound.doStartPlayer(textArea1.getText());
                            //TextSound.doPlay();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });

        btnRemoveInstruction.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               TransformationManager.Instruction selectedInstruction = list1.getSelectedValue();
               TextSound.instructions.remove(selectedInstruction);
               model.removeElement(selectedInstruction);
           }
        });

        btnRemoveInstruction.setMnemonic(KeyEvent.VK_DELETE);

        btnAddInstruction.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            switch (TransformationManager.Instruction.Mods.valueOf(btnAddInstruction.getSelectedItem().toString())) {
                case WORDTYPE:
                    InstructionFormWordType dialogWordType = new InstructionFormWordType();
                    dialogWordType.setTitle("Transformation: Word Type");
                    dialogWordType.pack();
                    dialogWordType.setLocationRelativeTo(panelTransformationInputs);
                    dialogWordType.setVisible(true);
                    btnAddInstruction.setSelectedIndex(0);
                    break;
                case WORDLENGTH:
                    InstructionFormWordLength dialogWordLength = new InstructionFormWordLength();
                    dialogWordLength.setTitle("Transformation: Word Length");
                    dialogWordLength.pack();
                    dialogWordLength.setLocationRelativeTo(panelTransformationInputs);
                    dialogWordLength.setVisible(true);
                    btnAddInstruction.setSelectedIndex(0);
                    break;
                case WORDVALUE:
                    InstructionFormWordValue dialogWordValue = new InstructionFormWordValue();
                    dialogWordValue.setTitle("Transformation: Word LGC");
                    dialogWordValue.pack();
                    dialogWordValue.setLocationRelativeTo(panelTransformationInputs);
                    dialogWordValue.setVisible(true);
                    btnAddInstruction.setSelectedIndex(0);
                    break;
                case PUNCTUATION:
                    InstructionFormSymbols dialogSymbol = new InstructionFormSymbols();
                    dialogSymbol.setTitle("Transformation: Symbols");
                    dialogSymbol.pack();
                    dialogSymbol.setLocationRelativeTo(panelTransformationInputs);
                    dialogSymbol.setVisible(true);
                    btnAddInstruction.setSelectedIndex(0);
                    break;
                case CHARACTER:
                    InstructionFormCharacter dialogCharacter = new InstructionFormCharacter();
                    dialogCharacter.setTitle("Transformation: Character");
                    dialogCharacter.pack();
                    dialogCharacter.setLocationRelativeTo(panelTransformationInputs);
                    dialogCharacter.setVisible(true);
                    btnAddInstruction.setSelectedIndex(0);
                    break;
                case SENTIMENT:
                    InstructionFormSentiment dialogSentiment = new InstructionFormSentiment();
                    dialogSentiment.setTitle("Transformation: Sentiment");
                    dialogSentiment.pack();
                    dialogSentiment.setLocationRelativeTo(panelTransformationInputs);
                    dialogSentiment.setVisible(true);
                    btnAddInstruction.setSelectedIndex(0);
                    break;
            }
        }
    });

        characterRadioButton.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent actionEvent) {
               TextSound.perChar = !TextSound.perChar;

               setOrdering.setVisible(true);
           }
        });

        wordRadioButton.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent actionEvent) {
              TextSound.perWord = !TextSound.perWord;

              setOrdering.setVisible(false);
          }
        });

        onRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    realtimePlayer = new RealtimePlayer();
                } catch (MidiUnavailableException e) {
                    e.printStackTrace();
                }
            }
        });

        offRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                realtimePlayer.close();
            }
        });

        btnTogglePause.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                TextSound.doPause();

            }
        });
    }

    public static void listAddInstruction(DefaultListModel thisModel, TransformationManager.Instruction thisInstruction) {
        thisModel.addElement(thisInstruction);
    }

    // Listen for changes to the text area for real-time processing
    private DocumentListener documentListener = new DocumentListener() {
        int wordLen = 0;
        int lastWordLen;
        StringBuilder currentWord = new StringBuilder();
        //String lastWord;
        String cursor;

        @Override
        public void insertUpdate(DocumentEvent e) {
            try {
                cursor = e.getDocument().getText(e.getOffset(), e.getLength());
                if (!cursor.equals(" ") && !cursor.equals("\n")) {
                    currentWord.append(cursor);
                    wordLen++;
                }
                if (onRadioButton.isSelected()) {
                    streamIt(e);
                }
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            if (wordLen != 0) {
                currentWord.deleteCharAt(wordLen - 1);
                wordLen--;
            }
            //System.out.println(wordLen);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
        }

        private void streamIt(DocumentEvent e) {
            DocumentEvent.EventType type = e.getType();
            try {
                String a = e.getDocument().getText(e.getOffset(), e.getLength());

                if (type == DocumentEvent.EventType.INSERT) {
                    if (wordRadioButton.isSelected()) {
                        if (a.equals(" ") || a.equals("\n")) {
                            //System.out.println(e.getDocument().getText(e.getOffset(), wordLen));

                            try {
                                // Get initial settings from user inputs
                                setBaseValues();
                                // Process text
                                TextSound.streamText(realtimePlayer, currentWord, true, 'x', 0);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                            //lastWord = currentWord.toString();
                            currentWord.setLength(0);
                            lastWordLen = wordLen;
                            wordLen = 0;
                            //System.out.println(lastWord);
                        }
                    } else if (characterRadioButton.isSelected()) {
                        try {
                            // Get initial settings from user inputs
                            setBaseValues();
                            char ch = cursor.charAt(0);
                            char upperCh = Character.toUpperCase(ch);
                            int charNum = TextSound.orderings.get(TextSound.ordering).indexOf(upperCh) + 1;
                            if (!Character.isWhitespace(ch)) {
                                // Process text
                                TextSound.streamText(realtimePlayer, currentWord, false, ch, charNum);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
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
        for (TransformationManager.Instruction i : TextSound.instructions) {
            listAddInstruction(Main.model, i);
        }
        String prefString2 = prefs.get("textPref", "y");
        //textArea.setText(prefString2);
    }

    private void setBaseValues() {
        TextSound.baseInstrument = String.valueOf(setInstrument.getSelectedItem());
        TextSound.baseNoteLength = Double.parseDouble(String.valueOf(setDuration.getSelectedItem()));
        TextSound.baseOctaves = Double.valueOf(setOctaves.getValue());
        TextSound.baseTempo = Double.parseDouble(String.valueOf(setTempo.getSelectedItem()));
        TextSound.baseFrequency = Double.parseDouble(String.valueOf(setFrequency.getSelectedItem()));
        TextSound.perWord = wordRadioButton.isSelected();
        TextSound.perChar = characterRadioButton.isSelected();

        if (lexnamesRadioButton.isSelected()) {
            TextSound.defaultNoteOperation = TextSound.noteOperationType.LEXNAMEFREQ;
        } else if (staticRadioButton.isSelected()) {
            TextSound.defaultNoteOperation = TextSound.noteOperationType.STATICFREQ;
        } else if (muteRadioButton.isSelected()) {
            TextSound.defaultNoteOperation = TextSound.noteOperationType.MUTE;
        }

        TextSound.orderings.add("ETAOINSRHLDCUMFPGWYBVKXJQZ");
        TextSound.orderings.add("ETAONRISHDLFCMUGYPWBVKXJQZ");
        TextSound.orderings.add("EARIOTNSLCUDPMHGBFYWKVXZJQ");
        TextSound.ordering = setOrdering.getSelectedIndex();
    }

    private String doPluralize(String input) {
        WordElement WE = lexicon.getWord(input, LexicalCategory.NOUN);
        InflectedWordElement infl = new InflectedWordElement(WE);
        infl.setPlural(true);
        //System.out.println(realiser.realise(infl));
        return realiser.realise(infl).toString();
    }

    private String doPastTense(String input) {
        WordElement WE = lexicon.getWord(input, LexicalCategory.VERB);
        InflectedWordElement infl = new InflectedWordElement(WE);
        infl.setFeature(Feature.TENSE, Tense.PAST);
        //System.out.println(realiser.realise(infl));
        return realiser.realise(infl).toString();
    }

    private String doGerund(String input) {
        VPPhraseSpec word = nlgFactory.createVerbPhrase(input);
        SPhraseSpec clause = nlgFactory.createClause();
        clause.setVerbPhrase(word);
        clause.setFeature(Feature.FORM, Form.GERUND);
        //System.out.println(realiser.realise(clause));
        return realiser.realise(clause).toString();
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
        JFrame frame = new JFrame("Singling v0.2");
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
        panel1.setLayout(new GridLayoutManager(2, 3, new Insets(20, 20, 20, 20), -1, -1));
        panelSettings = new JPanel();
        panelSettings.setLayout(new GridLayoutManager(8, 4, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panelSettings, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(100, 500), null, 0, false));
        setInstrument = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("PIANO");
        defaultComboBoxModel1.addElement("BRIGHT_ACOUSTIC");
        defaultComboBoxModel1.addElement("ELECTRIC_GRAND");
        defaultComboBoxModel1.addElement("HONKEY_TONK");
        defaultComboBoxModel1.addElement("ELECTRIC_PIANO");
        defaultComboBoxModel1.addElement("ELECTRIC_PIANO2");
        defaultComboBoxModel1.addElement("HARPISCHORD");
        defaultComboBoxModel1.addElement("CLAVINET");
        defaultComboBoxModel1.addElement("CELESTA");
        defaultComboBoxModel1.addElement("GLOCKENSPIEL");
        defaultComboBoxModel1.addElement("MUSIC_BOX");
        defaultComboBoxModel1.addElement("VIBRAPHONE");
        defaultComboBoxModel1.addElement("MARIMBA");
        defaultComboBoxModel1.addElement("XYLOPHONE");
        defaultComboBoxModel1.addElement("TUBULAR_BELLS");
        defaultComboBoxModel1.addElement("DULCIMER");
        defaultComboBoxModel1.addElement("DRAWBAR_ORGAN");
        defaultComboBoxModel1.addElement("PERCUSSIVE_ORGAN");
        defaultComboBoxModel1.addElement("ROCK_ORGAN");
        defaultComboBoxModel1.addElement("CHURCH_ORGAN");
        defaultComboBoxModel1.addElement("REED_ORGAN");
        defaultComboBoxModel1.addElement("ACCORIDAN");
        defaultComboBoxModel1.addElement("HARMONICA");
        defaultComboBoxModel1.addElement("TANGO_ACCORDIAN");
        defaultComboBoxModel1.addElement("GUITAR");
        defaultComboBoxModel1.addElement("STEEL_STRING_GUITAR");
        defaultComboBoxModel1.addElement("ELECTRIC_JAZZ_GUITAR");
        defaultComboBoxModel1.addElement("ELECTRIC_CLEAN_GUITAR");
        defaultComboBoxModel1.addElement("ELECTRIC_MUTED_GUITAR");
        defaultComboBoxModel1.addElement("OVERDRIVEN_GUITAR");
        defaultComboBoxModel1.addElement("DISTORTION_GUITAR");
        defaultComboBoxModel1.addElement("GUITAR_HARMONICS");
        defaultComboBoxModel1.addElement("ACOUSTIC_BASS");
        defaultComboBoxModel1.addElement("ELECTRIC_BASS_FINGER");
        defaultComboBoxModel1.addElement("ELECTRIC_BASS_PICK");
        defaultComboBoxModel1.addElement("FRETLESS_BASS");
        defaultComboBoxModel1.addElement("SLAP_BASS_1");
        defaultComboBoxModel1.addElement("SLAP_BASS_2");
        defaultComboBoxModel1.addElement("SYNTH_BASS_1");
        defaultComboBoxModel1.addElement("SYNTH_BASS_2");
        defaultComboBoxModel1.addElement("VIOLIN");
        defaultComboBoxModel1.addElement("VIOLA");
        defaultComboBoxModel1.addElement("CELLO");
        defaultComboBoxModel1.addElement("CONTRABASS");
        defaultComboBoxModel1.addElement("TREMOLO_STRINGS");
        defaultComboBoxModel1.addElement("PIZZICATO_STRINGS");
        defaultComboBoxModel1.addElement("ORCHESTRAL_STRINGS");
        defaultComboBoxModel1.addElement("TIMPANI");
        defaultComboBoxModel1.addElement("STRING_ENSEMBLE_1");
        defaultComboBoxModel1.addElement("STRING_ENSEMBLE_2");
        defaultComboBoxModel1.addElement("SYNTH_STRINGS_1");
        defaultComboBoxModel1.addElement("SYNTH_STRINGS_2");
        defaultComboBoxModel1.addElement("CHOIR_AAHS");
        defaultComboBoxModel1.addElement("VOICE_OOHS");
        defaultComboBoxModel1.addElement("SYNTH_VOICE");
        defaultComboBoxModel1.addElement("ORCHESTRA_HIT");
        defaultComboBoxModel1.addElement("TRUMPET");
        defaultComboBoxModel1.addElement("TROMBONE");
        defaultComboBoxModel1.addElement("TUBA");
        defaultComboBoxModel1.addElement("MUTED_TRUMPET");
        defaultComboBoxModel1.addElement("FRENCH_HORN");
        defaultComboBoxModel1.addElement("BRASS_SECTION");
        defaultComboBoxModel1.addElement("SYNTHBRASS_1");
        defaultComboBoxModel1.addElement("SOPRANO_SAX");
        defaultComboBoxModel1.addElement("ALTO_SAX");
        defaultComboBoxModel1.addElement("TENOR_SAX");
        defaultComboBoxModel1.addElement("BARITONE_SAX");
        defaultComboBoxModel1.addElement("OBOE");
        defaultComboBoxModel1.addElement("ENGLISH_HORN");
        defaultComboBoxModel1.addElement("BASSOON");
        defaultComboBoxModel1.addElement("CLARINET");
        defaultComboBoxModel1.addElement("PICCOLO");
        defaultComboBoxModel1.addElement("FLUTE");
        defaultComboBoxModel1.addElement("RECORDER");
        defaultComboBoxModel1.addElement("PAN_FLUTE");
        defaultComboBoxModel1.addElement("BLOWN_BOTTLE");
        defaultComboBoxModel1.addElement("SKAKUHACHI");
        defaultComboBoxModel1.addElement("WHISTLE");
        defaultComboBoxModel1.addElement("OCARINA");
        defaultComboBoxModel1.addElement("LEAD_SQUARE");
        defaultComboBoxModel1.addElement("LEAD_SAWTOOTH");
        defaultComboBoxModel1.addElement("LEAD_CALLIOPE");
        defaultComboBoxModel1.addElement("LEAD_CHIFF");
        defaultComboBoxModel1.addElement("LEAD_CHARANG");
        defaultComboBoxModel1.addElement("LEAD_VOICE");
        defaultComboBoxModel1.addElement("LEAD_FIFTHS");
        defaultComboBoxModel1.addElement("LEAD_BASSLEAD");
        defaultComboBoxModel1.addElement("PAD_NEW_AGE");
        defaultComboBoxModel1.addElement("PAD_WARM");
        defaultComboBoxModel1.addElement("PAD_POLYSYNTH");
        defaultComboBoxModel1.addElement("PAD_CHOIR");
        defaultComboBoxModel1.addElement("PAD_BOWED");
        defaultComboBoxModel1.addElement("PAD_METALLIC");
        defaultComboBoxModel1.addElement("PAD_HALO");
        defaultComboBoxModel1.addElement("PAD_SWEEP");
        defaultComboBoxModel1.addElement("FX_RAIN");
        defaultComboBoxModel1.addElement("FX_SOUNDTRACK");
        defaultComboBoxModel1.addElement("FX_CRYSTAL");
        defaultComboBoxModel1.addElement("FX_ATMOSPHERE");
        defaultComboBoxModel1.addElement("FX_BRIGHTNESS");
        defaultComboBoxModel1.addElement("FX_GOBLINS");
        defaultComboBoxModel1.addElement("FX_ECHOES");
        defaultComboBoxModel1.addElement("FX_SCI-FI");
        defaultComboBoxModel1.addElement("SITAR");
        defaultComboBoxModel1.addElement("BANJO");
        defaultComboBoxModel1.addElement("SHAMISEN");
        defaultComboBoxModel1.addElement("KOTO");
        defaultComboBoxModel1.addElement("KALIMBA");
        defaultComboBoxModel1.addElement("BAGPIPE");
        defaultComboBoxModel1.addElement("FIDDLE");
        defaultComboBoxModel1.addElement("SHANAI");
        defaultComboBoxModel1.addElement("TINKLE_BELL");
        defaultComboBoxModel1.addElement("AGOGO");
        defaultComboBoxModel1.addElement("STEEL_DRUMS");
        defaultComboBoxModel1.addElement("WOODBLOCK");
        defaultComboBoxModel1.addElement("TAIKO_DRUM");
        defaultComboBoxModel1.addElement("MELODIC_TOM");
        defaultComboBoxModel1.addElement("SYNTH_DRUM");
        defaultComboBoxModel1.addElement("REVERSE_CYMBAL");
        defaultComboBoxModel1.addElement("GUITAR_FRET_NOISE");
        defaultComboBoxModel1.addElement("BREATH_NOISE");
        defaultComboBoxModel1.addElement("SEASHORE");
        defaultComboBoxModel1.addElement("BIRD_TWEET");
        defaultComboBoxModel1.addElement("TELEPHONE_RING");
        defaultComboBoxModel1.addElement("HELICOPTER");
        defaultComboBoxModel1.addElement("APPLAUSE");
        defaultComboBoxModel1.addElement("GUNSHOT");
        setInstrument.setModel(defaultComboBoxModel1);
        panelSettings.add(setInstrument, new GridConstraints(0, 1, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        setDuration = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        defaultComboBoxModel2.addElement("1.00");
        defaultComboBoxModel2.addElement("0.50");
        defaultComboBoxModel2.addElement("0.25");
        defaultComboBoxModel2.addElement("0.125");
        defaultComboBoxModel2.addElement("0.0625");
        defaultComboBoxModel2.addElement("0.03125");
        defaultComboBoxModel2.addElement("0.015625");
        defaultComboBoxModel2.addElement("0.0078125");
        setDuration.setModel(defaultComboBoxModel2);
        setDuration.setSelectedIndex(1);
        panelSettings.add(setDuration, new GridConstraints(1, 1, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        setOctaves = new JSlider();
        setOctaves.setMaximum(10);
        setOctaves.setOrientation(0);
        setOctaves.setPaintLabels(true);
        setOctaves.setPaintTicks(true);
        setOctaves.setPaintTrack(true);
        setOctaves.setSnapToTicks(false);
        setOctaves.setValue(10);
        setOctaves.setValueIsAdjusting(true);
        panelSettings.add(setOctaves, new GridConstraints(2, 1, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
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
        setTempo.setRequestFocusEnabled(false);
        setTempo.setSelectedIndex(11);
        panelSettings.add(setTempo, new GridConstraints(3, 1, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        final JLabel label1 = new JLabel();
        label1.setText("Octave Range:");
        panelSettings.add(label1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Tempo (BPS):");
        panelSettings.add(label2, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Note Duration:");
        panelSettings.add(label3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Instrument:");
        panelSettings.add(label4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Fundamental Frequency:");
        label5.setVisible(true);
        panelSettings.add(label5, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setFrequency = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel4 = new DefaultComboBoxModel();
        defaultComboBoxModel4.addElement("13289.75");
        defaultComboBoxModel4.addElement("12543.85");
        defaultComboBoxModel4.addElement("11839.82");
        defaultComboBoxModel4.addElement("11175.3");
        defaultComboBoxModel4.addElement("10548.08");
        defaultComboBoxModel4.addElement("9956.06");
        defaultComboBoxModel4.addElement("9397.27");
        defaultComboBoxModel4.addElement("8869.84");
        defaultComboBoxModel4.addElement("8372.02");
        defaultComboBoxModel4.addElement("7902.13");
        defaultComboBoxModel4.addElement("7458.62");
        defaultComboBoxModel4.addElement("7040");
        defaultComboBoxModel4.addElement("6644.88");
        defaultComboBoxModel4.addElement("6271.93");
        defaultComboBoxModel4.addElement("5919.91");
        defaultComboBoxModel4.addElement("5587.65");
        defaultComboBoxModel4.addElement("5274.04");
        defaultComboBoxModel4.addElement("4978.03");
        defaultComboBoxModel4.addElement("4698.64");
        defaultComboBoxModel4.addElement("4434.92");
        defaultComboBoxModel4.addElement("4186.01");
        defaultComboBoxModel4.addElement("3951.07");
        defaultComboBoxModel4.addElement("3729.31");
        defaultComboBoxModel4.addElement("3520");
        defaultComboBoxModel4.addElement("3322.44");
        defaultComboBoxModel4.addElement("3135.96");
        defaultComboBoxModel4.addElement("2959.96");
        defaultComboBoxModel4.addElement("2793.83");
        defaultComboBoxModel4.addElement("2637.02");
        defaultComboBoxModel4.addElement("2489.02");
        defaultComboBoxModel4.addElement("2349.32");
        defaultComboBoxModel4.addElement("2217.46");
        defaultComboBoxModel4.addElement("2093");
        defaultComboBoxModel4.addElement("1975.53");
        defaultComboBoxModel4.addElement("1864.66");
        defaultComboBoxModel4.addElement("1760");
        defaultComboBoxModel4.addElement("1661.22");
        defaultComboBoxModel4.addElement("1567.98");
        defaultComboBoxModel4.addElement("1479.98");
        defaultComboBoxModel4.addElement("1396.91");
        defaultComboBoxModel4.addElement("1318.51");
        defaultComboBoxModel4.addElement("1244.51");
        defaultComboBoxModel4.addElement("1174.66");
        defaultComboBoxModel4.addElement("1108.73");
        defaultComboBoxModel4.addElement("1046.5");
        defaultComboBoxModel4.addElement("987.77");
        defaultComboBoxModel4.addElement("932.33");
        defaultComboBoxModel4.addElement("880");
        defaultComboBoxModel4.addElement("830.61");
        defaultComboBoxModel4.addElement("783.99");
        defaultComboBoxModel4.addElement("739.99");
        defaultComboBoxModel4.addElement("698.46");
        defaultComboBoxModel4.addElement("659.26");
        defaultComboBoxModel4.addElement("622.25");
        defaultComboBoxModel4.addElement("587.33");
        defaultComboBoxModel4.addElement("554.37");
        defaultComboBoxModel4.addElement("523.25");
        defaultComboBoxModel4.addElement("493.88");
        defaultComboBoxModel4.addElement("466.16");
        defaultComboBoxModel4.addElement("440");
        defaultComboBoxModel4.addElement("415.3");
        defaultComboBoxModel4.addElement("392");
        defaultComboBoxModel4.addElement("369.99");
        defaultComboBoxModel4.addElement("349.23");
        defaultComboBoxModel4.addElement("329.63");
        defaultComboBoxModel4.addElement("311.13");
        defaultComboBoxModel4.addElement("293.66");
        defaultComboBoxModel4.addElement("277.18");
        defaultComboBoxModel4.addElement("261.63");
        defaultComboBoxModel4.addElement("246.94");
        defaultComboBoxModel4.addElement("233.08");
        defaultComboBoxModel4.addElement("220");
        defaultComboBoxModel4.addElement("207.65");
        defaultComboBoxModel4.addElement("196");
        defaultComboBoxModel4.addElement("185");
        defaultComboBoxModel4.addElement("174.61");
        defaultComboBoxModel4.addElement("164.81");
        defaultComboBoxModel4.addElement("155.56");
        defaultComboBoxModel4.addElement("146.83");
        defaultComboBoxModel4.addElement("138.59");
        defaultComboBoxModel4.addElement("130.81");
        defaultComboBoxModel4.addElement("123.47");
        defaultComboBoxModel4.addElement("116.54");
        defaultComboBoxModel4.addElement("110");
        defaultComboBoxModel4.addElement("103.83");
        defaultComboBoxModel4.addElement("98");
        defaultComboBoxModel4.addElement("92.5");
        defaultComboBoxModel4.addElement("87.31");
        defaultComboBoxModel4.addElement("82.41");
        defaultComboBoxModel4.addElement("77.78");
        defaultComboBoxModel4.addElement("73.42");
        defaultComboBoxModel4.addElement("69.3");
        defaultComboBoxModel4.addElement("65.41");
        defaultComboBoxModel4.addElement("61.74");
        defaultComboBoxModel4.addElement("58.27");
        defaultComboBoxModel4.addElement("55");
        defaultComboBoxModel4.addElement("51.91");
        defaultComboBoxModel4.addElement("49");
        defaultComboBoxModel4.addElement("46.25");
        defaultComboBoxModel4.addElement("43.65");
        defaultComboBoxModel4.addElement("41.2");
        defaultComboBoxModel4.addElement("38.89");
        defaultComboBoxModel4.addElement("36.71");
        defaultComboBoxModel4.addElement("34.65");
        defaultComboBoxModel4.addElement("32.7");
        defaultComboBoxModel4.addElement("30.87");
        defaultComboBoxModel4.addElement("29.14");
        defaultComboBoxModel4.addElement("27.5");
        defaultComboBoxModel4.addElement("25.96");
        defaultComboBoxModel4.addElement("24.5");
        defaultComboBoxModel4.addElement("23.12");
        defaultComboBoxModel4.addElement("21.83");
        defaultComboBoxModel4.addElement("20.6");
        defaultComboBoxModel4.addElement("19.45");
        defaultComboBoxModel4.addElement("18.35");
        defaultComboBoxModel4.addElement("17.32");
        defaultComboBoxModel4.addElement("16.35");
        defaultComboBoxModel4.addElement("15.43");
        defaultComboBoxModel4.addElement("14.57");
        defaultComboBoxModel4.addElement("13.75");
        defaultComboBoxModel4.addElement("12.98");
        defaultComboBoxModel4.addElement("12.25");
        defaultComboBoxModel4.addElement("11.56");
        defaultComboBoxModel4.addElement("10.91");
        defaultComboBoxModel4.addElement("10.3");
        defaultComboBoxModel4.addElement("9.72");
        defaultComboBoxModel4.addElement("9.18");
        defaultComboBoxModel4.addElement("8.66");
        defaultComboBoxModel4.addElement("8.18");
        setFrequency.setModel(defaultComboBoxModel4);
        setFrequency.setSelectedIndex(80);
        setFrequency.setVisible(true);
        panelSettings.add(setFrequency, new GridConstraints(4, 1, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        lexnamesRadioButton = new JRadioButton();
        lexnamesRadioButton.setSelected(true);
        lexnamesRadioButton.setText("Overtone");
        panelSettings.add(lexnamesRadioButton, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        staticRadioButton = new JRadioButton();
        staticRadioButton.setText("Static");
        panelSettings.add(staticRadioButton, new GridConstraints(6, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        muteRadioButton = new JRadioButton();
        muteRadioButton.setText("Mute");
        panelSettings.add(muteRadioButton, new GridConstraints(6, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        final JLabel label6 = new JLabel();
        label6.setText("Default Note Behaviour:");
        panelSettings.add(label6, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Scope:");
        panelSettings.add(label7, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        wordRadioButton = new JRadioButton();
        wordRadioButton.setSelected(true);
        wordRadioButton.setText("Word");
        panelSettings.add(wordRadioButton, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        characterRadioButton = new JRadioButton();
        characterRadioButton.setText("Character");
        panelSettings.add(characterRadioButton, new GridConstraints(5, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        onRadioButton = new JRadioButton();
        onRadioButton.setSelected(false);
        onRadioButton.setText("On");
        panelSettings.add(onRadioButton, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        offRadioButton = new JRadioButton();
        offRadioButton.setSelected(true);
        offRadioButton.setText("Off");
        panelSettings.add(offRadioButton, new GridConstraints(7, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        final JLabel label8 = new JLabel();
        label8.setText("Stream Mode:");
        panelSettings.add(label8, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setOrdering = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel5 = new DefaultComboBoxModel();
        defaultComboBoxModel5.addElement("Mayzner");
        defaultComboBoxModel5.addElement("Typesetters");
        defaultComboBoxModel5.addElement("Oxford Dictionary");
        setOrdering.setModel(defaultComboBoxModel5);
        setOrdering.setVisible(false);
        panelSettings.add(setOrdering, new GridConstraints(5, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label9 = new JLabel();
        label9.setText("Base Settings");
        panel1.add(label9, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panelTextInput = new JPanel();
        panelTextInput.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panelTextInput, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(400, -1), new Dimension(400, 500), null, 0, false));
        panelProcessBtns = new JPanel();
        panelProcessBtns.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        panelTextInput.add(panelProcessBtns, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        btnPlay = new JButton();
        btnPlay.setText("Play");
        panelProcessBtns.add(btnPlay, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnProcess = new JButton();
        btnProcess.setText("Convert to MIDI");
        panelProcessBtns.add(btnProcess, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panelProcessBtns.add(spacer1, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        btnTogglePause = new JButton();
        btnTogglePause.setText("Pause/Resume");
        panelProcessBtns.add(btnTogglePause, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panelTextInput.add(scrollPane1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        textArea1 = new JTextArea();
        textArea1.setEditable(true);
        textArea1.setLineWrap(true);
        textArea1.setWrapStyleWord(true);
        scrollPane1.setViewportView(textArea1);
        panelTransformations = new JPanel();
        panelTransformations.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panelTransformations, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(300, -1), new Dimension(300, 500), null, 0, false));
        final JScrollPane scrollPane2 = new JScrollPane();
        panelTransformations.add(scrollPane2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        list1 = new JList();
        list1.setBackground(new Color(-1));
        scrollPane2.setViewportView(list1);
        panelTransformationInputs = new JPanel();
        panelTransformationInputs.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panelTransformations.add(panelTransformationInputs, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        btnRemoveInstruction = new JButton();
        btnRemoveInstruction.setText("-");
        btnRemoveInstruction.setToolTipText("Alt+Del");
        panelTransformationInputs.add(btnRemoveInstruction, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(2, 2), null, 0, false));
        btnAddInstruction = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel6 = new DefaultComboBoxModel();
        defaultComboBoxModel6.addElement("Transformation:");
        defaultComboBoxModel6.addElement("WORDTYPE");
        defaultComboBoxModel6.addElement("WORDLENGTH");
        defaultComboBoxModel6.addElement("WORDVALUE");
        defaultComboBoxModel6.addElement("PUNCTUATION");
        defaultComboBoxModel6.addElement("CHARACTER");
        defaultComboBoxModel6.addElement("SENTIMENT");
        btnAddInstruction.setModel(defaultComboBoxModel6);
        btnAddInstruction.setToolTipText("Transformation");
        panelTransformationInputs.add(btnAddInstruction, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panelTransformationInputs.add(spacer2, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
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
        buttonGroup = new ButtonGroup();
        buttonGroup.add(wordRadioButton);
        buttonGroup.add(characterRadioButton);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }


}