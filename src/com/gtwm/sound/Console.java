package com.gtwm.sound;

import javax.swing.*;
import java.awt.*;

public class Console extends javax.swing.JFrame {

    // NLP Console model
    private JTextArea consoleTextModel;

    public Console() {
        super("Console");

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container contentPane = this.getContentPane();

        // Add console text area to frame
        JScrollPane scrollPane = new JScrollPane();
        JPanel consolePanel = new JPanel();

        GridBagLayout layout = new GridBagLayout();
        consolePanel.setLayout(layout);
        this.setLayout(new BorderLayout());

        // TextArea
        JTextArea consoleTextArea = new JTextArea();
        this.consoleTextModel = consoleTextArea;

        contentPane.add(new JScrollPane(consolePanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);
        //contentPane.add(consolePanel);
        consolePanel.add(consoleTextArea);

        //frame.add(consolePanel, BorderLayout.WEST);
        //frame.add(contentPane, BorderLayout.CENTER);

        // Add chart to frame
        //JPanel chartPanel = new XChartPanel<XYChart>(chart);
        //frame.add(chartPanel, BorderLayout.EAST);

        // Labels
        //JLabel label = new JLabel("Blah blah blah.", SwingConstants.CENTER);
        //frame.add(label, BorderLayout.SOUTH);

        // get the screen size as a java dimension
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        // get 2/3 of the height, and 2/3 of the width
        int height = screenSize.height * 1 / 3;
        int width = screenSize.width * 1 / 5;

        // set the jframe height and width
        this.setPreferredSize(new Dimension(width, height));
    }

    public void setConsoleTextModel(JTextArea consoleTextModel) {
        this.consoleTextModel = consoleTextModel;
    }

    public void appendText(String text) {
        consoleTextModel.append(text);
    }

    public void clear() {
        consoleTextModel.selectAll();
        consoleTextModel.replaceSelection("");
    }

    public void doDisplay() {
        // Display the window.
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
}
