package cz.muni.fi.pv168.project.ui.dialog;

import cz.muni.fi.pv168.project.model.Category;

import javax.swing.*;
import java.awt.*;

public final class CategoryDialog extends EntityDialog<Category> {

    private final JTextField nameField = new JTextField();
    //private final JComboBox<Color> colorComboBox = new JComboBox<>(Color.values());

    private final JColorChooser colorChooser = new JColorChooser();

    private final Category category;
    private Color selectedColor;

    public CategoryDialog(Category category) {
        this.category = category;
        setValues();
        addFields();
    }

    private void setValues() {
        nameField.setText(category.getName());
        selectedColor = category.getColor();

    }

    private void addFields() {
        add("Name:", nameField);
        add("Color:", colorChooser);
    }

    @Override
    Category getEntity() {
        category.setName(nameField.getText());
        selectedColor = (Color) colorChooser.getColor();
        category.setColor(selectedColor);
        return category;
    }
}
