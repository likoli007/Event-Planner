package cz.muni.fi.pv168.project.ui.dialog;

import cz.muni.fi.pv168.project.model.Category;

import javax.swing.*;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import java.awt.*;

public final class CategoryDialog extends EntityDialog<Category> {

    private final JTextField nameField = new JTextField();

    private static final JColorChooser colorChooser = new JColorChooser();

    private final Category category;
    private Color selectedColor;

    public CategoryDialog(Category category) {
        this.category = new Category(category);

        setupColorChooser();
        setValues();
        addFields();


    }

    private void setupColorChooser(){
        AbstractColorChooserPanel[] panels = colorChooser.getChooserPanels();
        for (int i = 1; i < panels.length; i++) {
            colorChooser.removeChooserPanel(panels[i]);
        }
        colorChooser.setPreviewPanel(new JPanel());
    }

    private void setValues() {
        nameField.setText(category.getName());
        colorChooser.setColor(category.getColor());
    }

    private void addFields() {
        add("Name:", nameField);
        add("Color:", colorChooser);
    }

    @Override
    Category getEntity() {
        String name = nameField.getText();
        category.setName(name);

        Color chosenColor = colorChooser.getColor();
        if (!chosenColor.equals(category.getColor())) {
            category.setColor(chosenColor);
        }

        return category;
    }
}
