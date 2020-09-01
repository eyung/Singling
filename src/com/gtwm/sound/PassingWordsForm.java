package com.gtwm.sound;

import javax.swing.*;
import java.awt.event.*;

public class PassingWordsForm extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JList list1;
    private JTextField textField1;
    private JButton addButton;

    public PassingWordsForm() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        DefaultListModel listModel = new DefaultListModel();
        list1.setModel(listModel);

        for (String thisWord : TextSound.passingWords) {
            listModel.addElement(thisWord);
        }

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
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

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String userText = textField1.getText();

                if (!userText.equals("")) {
                    listModel.addElement(userText);
                    TextSound.passingWords.add(userText);
                }

                textField1.setText("");
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
        PassingWordsForm dialog = new PassingWordsForm();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
