package cz.muni.fi.pv168.project.ui.dialog;

import cz.muni.fi.pv168.project.model.Category;
import cz.muni.fi.pv168.project.model.DuplicateType;
import cz.muni.fi.pv168.project.ui.renderer.CategoryColorRectangle;

import javax.swing.*;
import java.awt.*;



public class DuplicateCategoryDialog {
    JPanel mainPanel = new JPanel();
    JPanel buttonPanel = new JPanel();
    JButton overwriteButton = new JButton("Overwrite");
    JButton cancelButton = new JButton("Don't import");
    JButton duplicateButton = new JButton("Duplicate");

    DuplicateType result = DuplicateType.UNDEFINED;

    public DuplicateCategoryDialog(JFrame parentFrame, Category originalCategory, Category duplicateCategory) {
        JDialog dialog = new JDialog(parentFrame, "Duplicate resolve", true);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setResizable(false);

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(overwriteButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(duplicateButton);

        JLabel aboutLabel = new JLabel ("Duplicate Category found! Details:");
        JPanel aboutPanel = new JPanel();
        aboutPanel.add(aboutLabel);

        JPanel duplicatePanel = new JPanel();
        duplicatePanel.setLayout(new BoxLayout(duplicatePanel, BoxLayout.Y_AXIS));
        JPanel originalPanel = new JPanel();
        originalPanel.setLayout(new BoxLayout(originalPanel, BoxLayout.Y_AXIS));
        JPanel duplicateColorPanel = new JPanel();
        duplicateColorPanel.setLayout(new FlowLayout());
        JPanel originalColorPanel = new JPanel();
        originalColorPanel.setLayout(new FlowLayout());
        JPanel duplicateNamePanel = new JPanel();
        JPanel originalNamePanel = new JPanel();


        JLabel duplicateNameLabel = new JLabel("Name: " + duplicateCategory.getName());
        duplicateNamePanel.add(duplicateNameLabel);
        JLabel duplicateColorLabel = new JLabel("Color: ");
        CategoryColorRectangle duplicateColorRectangle = new CategoryColorRectangle(duplicateCategory.getColor());
        duplicateColorPanel.add(duplicateColorLabel);
        duplicateColorPanel.add(duplicateColorRectangle);
        duplicatePanel.add(duplicateNamePanel);
        duplicatePanel.add(duplicateColorPanel);

        JLabel originalNameLabel = new JLabel("Name: " + originalCategory.getName());
        originalNamePanel.add(originalNameLabel);
        JLabel originalColorLabel = new JLabel("Color: ");
        CategoryColorRectangle originalColorRectangle = new CategoryColorRectangle(originalCategory.getColor());
        originalColorPanel.add(originalColorLabel);
        originalColorPanel.add(originalColorRectangle);
        originalPanel.add(originalNamePanel);
        originalPanel.add(originalColorPanel);

        duplicatePanel.setBorder(BorderFactory.createTitledBorder("Duplicate"));
        originalPanel.setBorder(BorderFactory.createTitledBorder("Original"));

        mainPanel.add(aboutPanel);
        mainPanel.add(originalPanel);
        mainPanel.add(duplicatePanel);
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
