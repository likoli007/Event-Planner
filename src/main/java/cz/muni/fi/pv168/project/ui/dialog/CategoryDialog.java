package cz.muni.fi.pv168.project.ui.dialog;

import cz.muni.fi.pv168.project.model.Category;
import cz.muni.fi.pv168.project.model.Color;

import javax.swing.*;

public final class CategoryDialog extends EntityDialog<Category> {

    private final JTextField nameField = new JTextField();
    private final JComboBox<Color> colorComboBox = new JComboBox<>(Color.values());

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
        colorComboBox.setSelectedItem(selectedColor);
    }

    private void addFields() {
        add("Name:", nameField);
        add("Color:", colorComboBox);
    }

    @Override
    Category getEntity() {
        category.setName(nameField.getText());
        selectedColor = (Color) colorComboBox.getSelectedItem();
        category.setColor(selectedColor);
        return category;
    }
}
