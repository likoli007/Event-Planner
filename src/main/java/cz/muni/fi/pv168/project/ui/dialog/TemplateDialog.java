package cz.muni.fi.pv168.project.ui.dialog;

import com.github.lgooddatepicker.components.TimePicker;
import cz.muni.fi.pv168.project.model.*;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.util.Objects;

public final class TemplateDialog extends EntityDialog<Template> {

    private final JTextField nameField = new JTextField();
    private final JTextField detailsField = new JTextField();
    private final TimePicker timeField = new TimePicker();

    private final JTextField intervalField = new JTextField(5);
    private final ComboBoxModel<TimeUnit> timeUnitModel;
    private final DefaultListModel<Category> categoryModel;
    private final Template template;

    public TemplateDialog(Template template) {
        this.template = template;
        this.categoryModel = new DefaultListModel<>();
        this.categoryModel.addElement(new Category("Work", Color.BLUE));
        this.categoryModel.addElement(new Category("Personal", Color.GREEN));
        this.categoryModel.addElement(new Category("Fitness", Color.RED));

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

        LocalTime startTime = template.getStartTime();
        timeField.setTime(Objects.requireNonNullElseGet(startTime, LocalTime::now));

        intervalField.setText(String.valueOf(template.getInterval().getAmount()));
        // categoryModel.setSelectedItem(template.getCategories()); TODO
        timeUnitModel.setSelectedItem(template.getInterval().getTimeUnit());
    }

    private void addFields() {
        var categoryComboBox = new JList<>(categoryModel);
        var timeUnitComboBox = new JComboBox<>(timeUnitModel);
        JPanel intervalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        intervalPanel.add(intervalField);
        intervalPanel.add(timeUnitComboBox);

        add("Name:", nameField);
        add("Details:", detailsField);
        add("Time:", timeField);
        add("Length:", intervalPanel);
        add("Categories:", categoryComboBox);
    }

    @Override
    Template getEntity() {
        template.setName(nameField.getText());
        template.setDetails(detailsField.getText());

        LocalTime time = timeField.getTime();
        if (time != null) {
            template.setStartTime(time);
        }

        template.getInterval().setAmount(Integer.parseInt(intervalField.getText()));
        template.getInterval().setTimeUnit((TimeUnit) timeUnitModel.getSelectedItem());
        // template.setCategories((List<Category>) categoryModel.getSelectedItem()); TODO

        return template;
    }
}
