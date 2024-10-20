package cz.muni.fi.pv168.project.ui.dialog;

import cz.muni.fi.pv168.project.model.Category;
import cz.muni.fi.pv168.project.model.Color;
import cz.muni.fi.pv168.project.model.TodoEvent;
import cz.muni.fi.pv168.project.model.TimeUnit;

import javax.swing.*;
import java.awt.*;

// TODO: adjust dialog for actual template
public final class TemplateDialog extends EntityDialog<TodoEvent> {

    private final JTextField nameField = new JTextField();
    private final JTextField detailsField = new JTextField();
    private final JTextField intervalField = new JTextField(5);
    private final ComboBoxModel<TimeUnit> timeUnitModel;
    private final ComboBoxModel<Category> categoryModel;
    private final TodoEvent template;

    public TemplateDialog(TodoEvent template) {
        this.template = template;
        this.categoryModel = new DefaultComboBoxModel<>(new Category[]{
                new Category("Work", Color.BLUE),
                new Category("Personal", Color.GREEN),
                new Category("Fitness", Color.RED)
        });

        this.timeUnitModel = new DefaultComboBoxModel<>(new TimeUnit[]{
                new TimeUnit("Minute", "min", 1),
                new TimeUnit("Hour", "hr", 60),
                new TimeUnit("Class", "cl", 90)
        });

        setValues();
        addFields();
    }

    private void setValues() {
        nameField.setText(template.getName());
        detailsField.setText(template.getDetails());
        intervalField.setText(String.valueOf(template.getTimeUnitAmount()));
        categoryModel.setSelectedItem(template.getCategory());
        timeUnitModel.setSelectedItem(template.getTimeUnit());
    }

    private void addFields() {
        var categoryComboBox = new JComboBox<>(categoryModel);
        var timeUnitComboBox = new JComboBox<>(timeUnitModel);
        JPanel intervalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        intervalPanel.add(intervalField);
        intervalPanel.add(timeUnitComboBox);

        add("Name:", nameField);
        add("Details:", detailsField);
        add("Length:", intervalPanel);
        add("Category:", categoryComboBox);
    }

    @Override
    TodoEvent getEntity() {
        template.setName(nameField.getText());
        template.setDetails(detailsField.getText());
        template.setTimeUnitAmount(Integer.parseInt(intervalField.getText()));
        template.setTimeUnit((TimeUnit) timeUnitModel.getSelectedItem());
        template.setCategory((Category) categoryModel.getSelectedItem());
        return template;
    }
}
