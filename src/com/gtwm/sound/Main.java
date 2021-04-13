package com.gtwm.sound;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.jfugue.realtime.RealtimePlayer;
import org.jfugue.theory.Note;

import javax.sound.midi.MidiUnavailableException;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class Main extends JFrame {
    private JPanel panel1;
    private JTextArea textArea1;
    private JButton btnSaveMIDI;
    private JComboBox setBaseInstrument;
    private JComboBox setDuration;
    private JSlider setOctaves;
    private JSlider setTempo;
    private JList<TransformationManager.Instruction> list1;
    private JButton btnRemoveInstruction;
    private JComboBox btnAddInstruction;
    private JPanel panelTransformationInputs;
    private JSlider setFrequency;
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
    private JToggleButton btnSentiment;
    private JButton btnAddPassingWords;
    private JComboBox setRestLengthSpace;
    private JComboBox setRestLengthLineBreak;
    private JFormattedTextField tfSetFrequency;
    private JComboBox cbSetFrequency;
    private JButton btnSaveWAV;

    // Set default database directory
    final File workingDirectory = new File(System.getProperty("user.dir"));

    // Version
    private static String currVersion = "Singling ver 0.7";

    // Input file name
    private String inputText = "";

    // Set default output file name
    private String outFilename = "output.mid";

    // Set models
    static DefaultListModel model = new DefaultListModel();
    static JTextArea textModel;
    private MyMouseAdaptor myMouseAdaptor = new MyMouseAdaptor();

    // NLP Console model
    static JTextArea consoleTextModel;

    // Splash screen coordinates
    final int splashx = 100;
    final int splashy = 450;

    // For streaming text to notes in real-time
    static RealtimePlayer realtimePlayer;

    // Prefs
    private static String prefsFilename = "userinstructions";

    static Console console;

    static Composer composer;

    static List<TransformationManager.Instruction> instructions;

    /**
     *
     */
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
        g.drawString(currVersion, splashx, splashy - 20);
        g.drawString("Starting up...", splashx, splashy);
        g.drawString("Loading databases...", splashx, splashy + 20);
        splash.update();

        // Set models
        list1.setModel(model);
        list1.addMouseListener(myMouseAdaptor);
        list1.addMouseMotionListener(myMouseAdaptor);

        setBaseInstrument.setModel(InstructionFormModels.modelSetBaseInstrument);
        //cbSetFrequency.setModel(InstructionFormModels.modelSetFrequency);
        //cbSetFrequency.setSelectedItem("A4");
        textModel = this.textArea1;

        //Icon a = new ImageIcon(getClass().getResource("/com/resources/iconfinder_ic_play_circle_fill_48px_352073.png"));
        //btnPlay.setIcon(a);

        // Init PassingWords
        PassingWordsForm passingWordsForm = new PassingWordsForm();
        passingWordsForm.setTitle("Manage Lexicon");

        // Add docomentListener to input text panel
        textArea1.getDocument().addDocumentListener(documentListener);

        // Create file chooser
        JFileChooser fc = new JFileChooser();

        // Create and init producer
        Producer producer = new Producer();

        console = new Console();

        instructions = new ArrayList<>();

        // Create and init Composer
        //composer = new Composer.ComposerBuilder().build();

        /**
         *
         */
        btnPlay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle open button action
                if (e.getSource() == btnPlay) {
                    if (textArea1.getLineCount() > 0) {
                        try {

                            // Get initial settings from user inputs
                            //setBaseValues();

                            // Get word/character value from user
                            boolean isWord = true;
                            if (wordRadioButton.isSelected()) {
                                isWord = true;
                            } else if (characterRadioButton.isSelected()) {
                                isWord = false;
                            }

                            // Get operation type value from user
                            String operationType = "";
                            if (lexnamesRadioButton.isSelected()) {
                                operationType = "LEXNAMEFREQ";
                            } else if (staticRadioButton.isSelected()) {
                                operationType = "STATICFREQ";
                            } else if (muteRadioButton.isSelected()) {
                                operationType = "MUTE";
                            }

                            // Create and init Composer
                            composer = new Composer
                                    .ComposerBuilder()
                                    .setInstrument(String.valueOf(setBaseInstrument.getSelectedItem()))
                                    .setNoteLength(Double.parseDouble(String.valueOf(setDuration.getSelectedItem())))
                                    .setOctave((double) setOctaves.getValue())
                                    .setTempo((double) setTempo.getValue())
                                    .setFrequency((double) setFrequency.getValue())
                                    .setRestLength(Double.parseDouble(String.valueOf(setRestLengthSpace.getSelectedItem())))
                                    .setRestLengthLineBreak(Double.parseDouble(String.valueOf(setRestLengthLineBreak.getSelectedItem())))
                                    .wantWord(isWord)
                                    .withOperation(operationType)
                                    .withOrdering(setOrdering.getSelectedIndex())
                                    .useTransformations(instructions)
                                    .build();

                            // Process user input text
                            composer.processString(textArea1.getText());

                            // Init Producer using pattern created by Composer
                            producer.setPlayer();

                            // Pass created sound pattern to producer
                            producer.setPattern(composer.getPattern());

                            // Start player
                            producer.doStartPlayer(Double.parseDouble(String.valueOf(setDuration.getSelectedItem())));

                            // Create new NLPConsole
                            //DialogNLPConsole dialogNLPConsole = new DialogNLPConsole();
                            //dialogNLPConsole.setTitle("NLP Console");
                            //dialogNLPConsole.pack();
                            //dialogNLPConsole.setLocationRelativeTo(panelTransformationInputs);
                            //dialogNLPConsole.setVisible(true);

                     /*       // Create Chart
                            final XYChart chart = new XYChartBuilder().width(600).height(400).title("Area Chart").xAxisTitle("X").yAxisTitle("Y").build();

                            // Customize Chart
                            chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
                            chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Area);

                            // Series
                            chart.addSeries("a", new double[] { 0, 3, 5, 7, 9 }, new double[] { -3, 5, 9, 6, 5 });
                            chart.addSeries("b", new double[] { 0, 2, 4, 6, 9 }, new double[] { -1, 6, 4, 0, 4 });
                            chart.addSeries("c", new double[] { 0, 1, 3, 8, 9 }, new double[] { -2, -1, 1, 0, 1 });*/

                            //Console console = new Console();
                            console.clear();
                            console.doDisplay();

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });

        /**
         *
         */
        btnSaveMIDI.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle open button action
                if (e.getSource() == btnSaveMIDI) {
                    if (textArea1.getLineCount() > 0) {

                        fc.setCurrentDirectory(workingDirectory);

                        int returnVal = fc.showSaveDialog(panel1);

                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            File fileToSave = fc.getSelectedFile();
                            outFilename = fileToSave.getAbsoluteFile().toString() + ".mid";
                            System.out.println("Save as file: " + outFilename);

                            try {
                                // Get initial settings from user inputs
                                //setBaseValues();

                                // Process text
                                //TextSound.runStuff();
                                //TextSound.doSaveAsMidi(textArea1.getText(), outFilename);

                                boolean isWord = true;
                                if (wordRadioButton.isSelected()) {
                                    isWord = true;
                                } else if (characterRadioButton.isSelected()) {
                                    isWord = false;
                                }

                                String operationType = "";
                                if (lexnamesRadioButton.isSelected()) {
                                    operationType = "LEXNAMEFREQ";
                                } else if (staticRadioButton.isSelected()) {
                                    operationType = "STATICFREQ";
                                } else if (muteRadioButton.isSelected()) {
                                    operationType = "MUTE";
                                }

                                // Create and init Composer
                                composer = new Composer
                                        .ComposerBuilder()
                                        .setInstrument(String.valueOf(setBaseInstrument.getSelectedItem()))
                                        .setNoteLength(Double.parseDouble(String.valueOf(setDuration.getSelectedItem())))
                                        .setOctave((double) setOctaves.getValue())
                                        .setTempo((double) setTempo.getValue())
                                        .setFrequency((double) setFrequency.getValue())
                                        .setRestLength(Double.parseDouble(String.valueOf(setRestLengthSpace.getSelectedItem())))
                                        .setRestLengthLineBreak(Double.parseDouble(String.valueOf(setRestLengthLineBreak.getSelectedItem())))
                                        .wantWord(isWord)
                                        .withOperation(operationType)
                                        .withOrdering(setOrdering.getSelectedIndex())
                                        .build();

                                // Process user input text
                                composer.processString(textArea1.getText());

                                // Create Producer using pattern created by Composer
                                //Producer producer = new Producer();
                                producer.setPlayer();

                                // Pass created sound pattern to producer
                                producer.setPattern(composer.getPattern());

                                // Start player
                                producer.doSaveAsMidi(textArea1.getText(), outFilename);

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

        /**
         *
         */
        btnSaveWAV.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle open button action
                if (e.getSource() == btnSaveWAV) {
                    if (textArea1.getLineCount() > 0) {

                        fc.setCurrentDirectory(workingDirectory);

                        int returnVal = fc.showSaveDialog(panel1);

                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            File fileToSave = fc.getSelectedFile();
                            outFilename = fileToSave.getAbsoluteFile().toString();
                            System.out.println("Save as file: " + outFilename);

                            try {
                                boolean isWord = true;
                                if (wordRadioButton.isSelected()) {
                                    isWord = true;
                                } else if (characterRadioButton.isSelected()) {
                                    isWord = false;
                                }

                                String operationType = "";
                                if (lexnamesRadioButton.isSelected()) {
                                    operationType = "LEXNAMEFREQ";
                                } else if (staticRadioButton.isSelected()) {
                                    operationType = "STATICFREQ";
                                } else if (muteRadioButton.isSelected()) {
                                    operationType = "MUTE";
                                }

                                // Create and init Composer
                                composer = new Composer
                                        .ComposerBuilder()
                                        .setInstrument(String.valueOf(setBaseInstrument.getSelectedItem()))
                                        .setNoteLength(Double.parseDouble(String.valueOf(setDuration.getSelectedItem())))
                                        .setOctave((double) setOctaves.getValue())
                                        .setTempo((double) setTempo.getValue())
                                        .setFrequency((double) setFrequency.getValue())
                                        .setRestLength(Double.parseDouble(String.valueOf(setRestLengthSpace.getSelectedItem())))
                                        .setRestLengthLineBreak(Double.parseDouble(String.valueOf(setRestLengthLineBreak.getSelectedItem())))
                                        .wantWord(isWord)
                                        .withOperation(operationType)
                                        .withOrdering(setOrdering.getSelectedIndex())
                                        .build();

                                // Process user input text
                                composer.processString(textArea1.getText());

                                // Create Producer using pattern created by Composer
                                //Producer producer = new Producer();

                                producer.setPlayer();

                                // Pass created sound pattern to producer
                                producer.setPattern(composer.getPattern());

                                // Start player
                                producer.doSaveAsWAV(textArea1.getText(), outFilename);

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

        /**
         *
         */
        btnTogglePause.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //TextSound.doPause();
                // Create Producer using pattern created by Composer
                //Producer producer = new Producer();
                producer.doPause();
            }
        });

        /**
         *
         */
        btnRemoveInstruction.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               TransformationManager.Instruction selectedInstruction = list1.getSelectedValue();
               //TextSound.instructions.remove(selectedInstruction);

               //composer.removeInstruction(selectedInstruction);
               instructions.remove(selectedInstruction);
               model.removeElement(selectedInstruction);
           }
        });

        btnRemoveInstruction.setMnemonic(KeyEvent.VK_DELETE);

        /**
         *
         */
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
                    case LGC:
                        InstructionFormWordValue dialogWordValue = new InstructionFormWordValue();
                        dialogWordValue.setTitle("Transformation: Word LGC");
                        dialogWordValue.pack();
                        dialogWordValue.setLocationRelativeTo(panelTransformationInputs);
                        dialogWordValue.setVisible(true);
                        btnAddInstruction.setSelectedIndex(0);
                        break;
                    case PUNCTUATION:
                        InstructionFormPunctuations dialogPunctuations = new InstructionFormPunctuations();
                        dialogPunctuations.setTitle("Transformation: Punctuations");
                        dialogPunctuations.pack();
                        dialogPunctuations.setLocationRelativeTo(panelTransformationInputs);
                        dialogPunctuations.setVisible(true);
                        btnAddInstruction.setSelectedIndex(0);
                        break;
                    case SYMBOL:
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
               //TextSound.perChar = !TextSound.perChar;

               setOrdering.setVisible(true);
           }
        });

        wordRadioButton.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent actionEvent) {
              //TextSound.perWord = !TextSound.perWord;

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

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosed(e);
            }
        });

        // Display updated setOctaves value
        setOctaves.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                //int octaveValue = setOctaves.getValue();
                //labelOctave.setText(String.valueOf(octaveValue));
            }
        });

        // Test
        list1.addMouseListener(new MouseAdapter() { } );

        // Sentiment button toggle state
        btnSentiment.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if(itemEvent.getStateChange()==ItemEvent.SELECTED){
                    //System.out.println("button is selected");
                } else if(itemEvent.getStateChange()==ItemEvent.DESELECTED){
                    //System.out.println("button is not selected");
                }
            }
        });

        // Passing words (exclusion) list
        btnAddPassingWords.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //PassingWordsForm passingWordsForm = new PassingWordsForm();
                //passingWordsForm.setTitle("Manage Lexicon");
                passingWordsForm.pack();
                passingWordsForm.setLocationRelativeTo(panelTransformationInputs);
                passingWordsForm.setVisible(true);
            }
        });

        setFrequency.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                int midiNumber = (int) Math.rint(12 * logCalc.log(setFrequency.getValue() / 440.0f, 2) + 69.0f);
                tfSetFrequency.setText(String.valueOf(setFrequency.getValue()));
                //cbSetFrequency.setSelectedIndex(midiNumber-21);
            }
        });

        tfSetFrequency.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                try {
                    setFrequency.setValue(Integer.parseInt(tfSetFrequency.getText()));
                    //cbSetFrequency.setSelectedItem("");
                } catch (Exception ex) {}
            }
        });

        cbSetFrequency.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!cbSetFrequency.getSelectedItem().equals("")) {
                    double userFrequency = Note.getFrequencyForNote(cbSetFrequency.getSelectedItem().toString())*2;
                    setFrequency.setValue((int)userFrequency);
                    tfSetFrequency.setText(String.valueOf(userFrequency));
                }
            }
        });
    }

    public static void listAddInstruction(TransformationManager.Instruction thisInstruction) {
        model.addElement(thisInstruction);
        instructions.add(thisInstruction);
    }

    public static DefaultListModel getInstructionsListModel() {
        return model;
    }

    /**
     * To enable drag/drop of transformations
     */
    private class MyMouseAdaptor extends MouseInputAdapter {
        private boolean mouseDragging = false;
        private int dragSourceIndex;

        @Override
        public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                dragSourceIndex = list1.getSelectedIndex();
                mouseDragging = true;
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            mouseDragging = false;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (mouseDragging) {
                int currentIndex = list1.locationToIndex(e.getPoint());
                if (currentIndex != dragSourceIndex) {
                    int dragTargetIndex = list1.getSelectedIndex();
                    //String dragElement = model.get(dragSourceIndex).toString();
                    TransformationManager.Instruction dragInstruction = (TransformationManager.Instruction) model.get(dragSourceIndex);
                    model.remove(dragSourceIndex);
                    //model.add(dragTargetIndex, dragElement);
                    model.add(dragTargetIndex, dragInstruction);
                    dragSourceIndex = currentIndex;
                }
            }
        }
    }

    /**
     * Listen for changes to the text area for real-time processing
     */
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
                                //Values();
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
                            //setBaseValues();
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

    private static String serialize(List<TransformationManager.Instruction> thisObjectList) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream so = new ObjectOutputStream(bo);
            so.writeObject(thisObjectList);
            so.flush();
            final byte[] byteArray = bo.toByteArray();
            return Base64.getEncoder().encodeToString(byteArray);
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    private static List<TransformationManager.Instruction> deserialize(String serializedObject) {
        try {
            byte b[] = Base64.getDecoder().decode(serializedObject);
            ByteArrayInputStream bi = new ByteArrayInputStream(b);
            ObjectInputStream si = new ObjectInputStream(bi);
            return (List<TransformationManager.Instruction>) si.readObject();
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    private static String serializeLexicon(Set<String> thisObjectList) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream so = new ObjectOutputStream(bo);
            so.writeObject(thisObjectList);
            so.flush();
            final byte[] byteArray = bo.toByteArray();
            return Base64.getEncoder().encodeToString(byteArray);
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    private static Set<String> deserializeLexicon(String serializedObject) {
        try {
            byte b[] = Base64.getDecoder().decode(serializedObject);
            ByteArrayInputStream bi = new ByteArrayInputStream(b);
            ObjectInputStream si = new ObjectInputStream(bi);
            return (Set<String>) si.readObject();
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    /**
     *
     */
    private static void createAndShowGUI() {
        Main mainForm = new Main();
        JFileChooser fc = new JFileChooser();
        final File workingDirectory = new File(System.getProperty("user.dir"));

        // Menu
        JMenuBar menuBar = new JMenuBar();
        JMenuItem loadSettings, saveSettings, exitItem;

        // File
        JMenu fileMenu = new JMenu("File");

        // Import settings + instructions
        loadSettings = new JMenuItem("Load Settings");
        loadSettings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fc.setCurrentDirectory(workingDirectory);
                String prefsFile = "";

                int returnVal = fc.showOpenDialog(Main.textModel);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();

                    Properties properties = new Properties();

                    // Loading properties file
                    try(FileReader fileReader = new FileReader(file)) {
                        properties.load(fileReader);

                        // Load base settings
                        mainForm.setBaseInstrument.setSelectedItem(properties.getProperty("instrument"));
                        mainForm.setDuration.setSelectedItem(properties.getProperty("noteduration"));
                        mainForm.setOctaves.setValue(Integer.parseInt(properties.getProperty("octave")));
                        mainForm.setTempo.setValue(Integer.parseInt(properties.getProperty("tempo")));
                        mainForm.setFrequency.setValue(Integer.parseInt(properties.getProperty("frequency")));
                        mainForm.cbSetFrequency.setSelectedIndex(0);
                        mainForm.setRestLengthSpace.setSelectedItem(properties.getProperty("restlengthspace"));
                        mainForm.setRestLengthLineBreak.setSelectedItem(properties.getProperty("restlengthlinebreak"));

                        try {
                            // Load scope setting
                            if (properties.getProperty("scope").equalsIgnoreCase("word")) {
                                mainForm.wordRadioButton.setSelected(true);
                                mainForm.characterRadioButton.setSelected(false);
                            } else if (properties.getProperty("scope").equalsIgnoreCase("character")) {
                                mainForm.wordRadioButton.setSelected(false);
                                mainForm.characterRadioButton.setSelected(true);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        try {
                            // Load default note behaviour setting
                            if (properties.getProperty("notebehaviour").equalsIgnoreCase("lexname")) {
                                mainForm.lexnamesRadioButton.setSelected(true);
                                //mainForm.staticRadioButton.setSelected(false);
                                //mainForm.muteRadioButton.setSelected(false);
                            } else if (properties.getProperty("notebehaviour").equalsIgnoreCase("static")) {
                                //mainForm.lexnamesRadioButton.setSelected(false);
                                mainForm.staticRadioButton.setSelected(true);
                                //mainForm.muteRadioButton.setSelected(false);
                            } else if (properties.getProperty("notebehaviour").equalsIgnoreCase("mute")) {
                                //mainForm.lexnamesRadioButton.setSelected(true);
                                //mainForm.staticRadioButton.setSelected(false);
                                mainForm.muteRadioButton.setSelected(true);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        try {
                            // Load stream mode setting
                            if (properties.getProperty("stream").equalsIgnoreCase("on")) {
                                mainForm.onRadioButton.setSelected(true);
                            } else if (properties.getProperty("scope").equalsIgnoreCase("off")) {
                                mainForm.offRadioButton.setSelected(false);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        try {
                            // Load sentiment mode setting
                            if (properties.getProperty("sentiment").equalsIgnoreCase("on")) {
                                mainForm.btnSentiment.setSelected(true);
                            } else {
                                mainForm.btnSentiment.setSelected(false);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        // Load text
                        mainForm.textModel.setText(properties.getProperty("textinput"));

                        // Load instructions
                        TextSound.instructions = deserialize(properties.getProperty("instructions"));
                        //composer.instructions = deserialize(properties.getProperty("instructions"));
                        Main.model.clear();
                        for (TransformationManager.Instruction i : TextSound.instructions) {
                            Main.listAddInstruction(i);
                        }

                        // Load lexicons
                        TextSound.passingWords = deserializeLexicon(properties.getProperty("lexicons"));
                        PassingWordsForm.listModel.clear();
                        for (String i : TextSound.passingWords) {
                            PassingWordsForm.listModel.addElement(i);
                        }

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    System.out.println("Open command cancelled by user.");
                }
            }
        });

        // Export settings + instructions
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

                    Properties properties = new Properties();

                    // Saving base settings
                    properties.setProperty("instrument", String.valueOf(mainForm.setBaseInstrument.getSelectedItem()));
                    properties.setProperty("noteduration", String.valueOf(mainForm.setDuration.getSelectedItem()));
                    properties.setProperty("octave", String.valueOf(mainForm.setOctaves.getValue()));
                    properties.setProperty("tempo", String.valueOf(mainForm.setTempo.getValue()));
                    properties.setProperty("frequency", String.valueOf(mainForm.setFrequency.getValue()));
                    properties.setProperty("restlengthspace", String.valueOf(mainForm.setRestLengthSpace.getSelectedItem()));
                    properties.setProperty("restlengthlinebreak", String.valueOf(mainForm.setRestLengthLineBreak.getSelectedItem()));

                    // Saving set scope
                    if (mainForm.wordRadioButton.isSelected()) {
                        properties.setProperty("scope", "word");
                    } else if (mainForm.characterRadioButton.isSelected()) {
                        properties.setProperty("scope", "character");
                    }

                    // Saving set default note behaviour
                    if (mainForm.lexnamesRadioButton.isSelected()) {
                        properties.setProperty("notebehaviour", "lexname");
                    } else if (mainForm.staticRadioButton.isSelected()) {
                        properties.setProperty("notebehaviour", "static");
                    } else if (mainForm.muteRadioButton.isSelected()) {
                        properties.setProperty("notebehaviour", "mute");
                    }

                    // Saving set stream mode
                    if (mainForm.onRadioButton.isSelected()) {
                        properties.setProperty("stream", "on");
                    } else if (mainForm.offRadioButton.isSelected()) {
                        properties.setProperty("stream", "off");
                    }

                    // Saving set sentiment mode
                    if (mainForm.btnSentiment.isSelected()) {
                        properties.setProperty("sentiment", "on");
                    } else {
                        properties.setProperty("sentiment", "off");
                    }

                    // Saving text
                    properties.setProperty("textinput", Main.textModel.getText());

                    // Saving instructions
                    //ObjectOutputStream instructionsList = serializeObject(TextSound.instructions);
                    //properties.setProperty("instructions", instructionsList.toString());
                    properties.setProperty("instructions", serialize(TextSound.instructions));

                    // Saving lexicons
                    properties.setProperty("lexicons", serializeLexicon(TextSound.passingWords));

                    // Saving to file
                    try(FileWriter output = new FileWriter(prefsFile)) {
                        properties.store(output, "Save user settings: " + prefsFile);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    System.out.println("Save command cancelled by user.");
                }
            }
        });

        // Separators
        JSeparator separatorBar = new JSeparator();

        // Exit
        exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                System.exit(0);
            }
        });

        // Adding menu items to menu
        fileMenu.add(saveSettings);
        fileMenu.add(loadSettings);
        fileMenu.add(separatorBar);
        fileMenu.add(exitItem);

        // Adding menu to menu bar
        menuBar.add(fileMenu);

        // Frame
        JFrame frame = new JFrame(currVersion);
        frame.setJMenuBar(menuBar);
        frame.setContentPane(mainForm.panel1);
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
        setBaseInstrument = new JComboBox();
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
        setBaseInstrument.setModel(defaultComboBoxModel1);
        panelSettings.add(setBaseInstrument, new GridConstraints(0, 1, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
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
        btnSaveMIDI = new JButton();
        btnSaveMIDI.setText("Convert to MIDI");
        panelProcessBtns.add(btnSaveMIDI, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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