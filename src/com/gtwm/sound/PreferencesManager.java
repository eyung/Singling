package com.gtwm.sound;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

public class PreferencesManager {

    // To get and load user settings
    private Preferences prefs = Preferences.userNodeForPackage(Main.class);

    //static String inputFile;
    private String prefsFile = "usersettings";

    public void setUserInputs(List<Object> userInputs) {
        prefs.put("instrumentPref", userInputs.get(0).toString());
        prefs.put("notedurationPref", userInputs.get(1).toString());
    }

    public void saveSettings(String saveFile) {
        prefsFile = saveFile;
        // Save instructions to file
        ObjectOutputStream x = serializeObject(TextSound.instructions);
        prefs.put("instructionsPref", x.toString());
        prefs.put("textPref", Main.textModel.getText());
    }

    public void loadSettings(JTextArea textArea, String loadFile) {
        prefsFile = loadFile;
        // Get user settings
        String prefString = prefs.get("instructionsPref", "x");
        TextSound.instructions = deserializeObject(prefString);
        Main.model.clear();
        for (TransformationManager.Instruction i : TextSound.instructions) {
            Main.listAddInstruction(Main.model, i);
        }
        String prefString2 = prefs.get("textPref", "y");
        //textArea.setText(prefString2);

        String prefInstrument = prefs.get("instrumentPref", "a");
        
    }

    private ObjectOutputStream serializeObject(List<TransformationManager.Instruction> thisObjectList) {
        try {
            FileOutputStream fos = new FileOutputStream(prefsFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(thisObjectList);
            oos.close();
            fos.close();

            System.out.println("\nSerialization Successful\n");

            return oos;
        } catch (FileNotFoundException e) {
            System.out.println("a");
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            System.out.println("b");
            e.printStackTrace();
            return null;
        }
    }

    private List<TransformationManager.Instruction> deserializeObject(String thisOutStream) {
        try {
            FileInputStream fis = new FileInputStream(prefsFile);
            ObjectInputStream ois = new ObjectInputStream(fis);

            TextSound.instructions = (ArrayList) ois.readObject();

            ois.close();
            fis.close();

            return TextSound.instructions;
        } catch (IOException e) {
            System.out.println("No user saved instructions to load.");
            //e.printStackTrace();
            return TextSound.instructions;
        } catch (ClassNotFoundException e) {
            System.out.println("d");
            e.printStackTrace();
            return null;
        }
    }
}
