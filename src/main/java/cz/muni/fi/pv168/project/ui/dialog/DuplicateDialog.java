package cz.muni.fi.pv168.project.ui.dialog;

import cz.muni.fi.pv168.project.model.DuplicateType;

import javax.swing.*;
import java.awt.*;



public class DuplicateDialog {
    JPanel mainPanel = new JPanel();

    JPanel duplicatePanel = new JPanel();
    JPanel originalPanel = new JPanel();

    JPanel buttonPanel = new JPanel();
    JButton overwriteButton = new JButton("Overwrite");
    JButton cancelButton = new JButton("Don't import");
    JButton duplicateButton = new JButton("Duplicate");

    DuplicateType result = DuplicateType.UNDEFINED;

    public DuplicateDialog(JFrame parentFrame, String objectType, String originalString, String duplicateString) {
        JDialog dialog = new JDialog(parentFrame, "Duplicate resolve", true);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setResizable(false);


        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(overwriteButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(duplicateButton);

        JLabel aboutLabel = new JLabel ("Duplicate " + objectType + " found! Details:");
        JPanel aboutPanel = new JPanel();
        aboutPanel.add(aboutLabel);

        JTextArea duplicateTextArea = new JTextArea(duplicateString);

        duplicateTextArea.setEditable(false);
        duplicateTextArea.setBackground(null);

        JTextArea originalTextArea = new JTextArea(originalString);
        originalTextArea.setEditable(false);
        originalTextArea.setBackground(null);

        duplicatePanel.setLayout(new BoxLayout(duplicatePanel, BoxLayout.Y_AXIS));
        originalPanel.setLayout(new BoxLayout(originalPanel, BoxLayout.Y_AXIS));
        duplicatePanel.add(duplicateTextArea);
        originalPanel.add(originalTextArea);
        duplicatePanel.setBorder(BorderFactory.createTitledBorder("Duplicate"));
        originalPanel.setBorder(BorderFactory.createTitledBorder("Original"));

        mainPanel.add(aboutPanel);
        mainPanel.add(duplicatePanel);
        mainPanel.add(originalPanel);
        mainPanel.add(buttonPanel);

        overwriteButton.addActionListener(e -> {
            result = DuplicateType.OVERWRITE;
            dialog.dispose();
        });
        cancelButton.addActionListener(e -> {
            result = DuplicateType.CANCEL;
            dialog.dispose();
        });
        duplicateButton.addActionListener(e -> {
            result = DuplicateType.DUPLICATE;
            dialog.dispose();
        });

        dialog.add(mainPanel);
        dialog.pack();
        dialog.setVisible(true);
    }

    public DuplicateType getResult() {
        return result;
    }
}
