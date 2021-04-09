package com.gtwm.sound;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DialogNLPConsole extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JTextArea textArea1;
    private JPanel chartpanel;

    static JTextArea textModel;
    static JPanel chartModel;

    public DialogNLPConsole() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        textModel = this.textArea1;
        chartModel = this.chartpanel;

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    public static void main(String[] args) {
        DialogNLPConsole dialog = new DialogNLPConsole();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
