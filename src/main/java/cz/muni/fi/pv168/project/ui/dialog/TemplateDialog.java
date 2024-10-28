package cz.muni.fi.pv168.project.ui.dialog;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.TimePicker;
import cz.muni.fi.pv168.project.model.*;
import cz.muni.fi.pv168.project.model.Color;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public final class TemplateDialog extends EntityDialog<Template> {

    private final JTextField nameField = new JTextField();
    private final JTextField detailsField = new JTextField();
    private final DatePicker dateField = new DatePicker();
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

        LocalDateTime dateTime = template.getDate();
        if (dateTime != null) {
            dateField.setDate(dateTime.toLocalDate());
            timeField.setTime(dateTime.toLocalTime());
        } else {
            dateField.setDate(LocalDate.now());
            timeField.setTime(LocalTime.now());
        }

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
        add("Date:", dateField);
        add("Time:", timeField);
        add("Length:", intervalPanel);
        add("Categories:", categoryComboBox);
    }

    @Override
    Template getEntity() {
        template.setName(nameField.getText());
        template.setDetails(detailsField.getText());

        LocalDate date = dateField.getDate();
        LocalTime time = timeField.getTime();
        if (date != null && time != null) {
            template.setDate(LocalDateTime.of(date, time));
        }

        template.getInterval().setAmount(Integer.parseInt(intervalField.getText()));
        template.getInterval().setTimeUnit((TimeUnit) timeUnitModel.getSelectedItem());
        // template.setCategories((List<Category>) categoryModel.getSelectedItem()); TODO

        return template;
    }
}
