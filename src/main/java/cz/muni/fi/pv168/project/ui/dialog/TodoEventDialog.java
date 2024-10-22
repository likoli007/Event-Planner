package cz.muni.fi.pv168.project.ui.dialog;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.TimePicker;
import cz.muni.fi.pv168.project.data.TestDataGenerator;
import cz.muni.fi.pv168.project.model.*;
import cz.muni.fi.pv168.project.model.Color;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public final class TodoEventDialog extends EntityDialog<TodoEvent> {

    private final JTextField nameField = new JTextField();
    private final JTextField detailsField = new JTextField();
    private final DatePicker dateField = new DatePicker();
    private final TimePicker timeField = new TimePicker();

    private final JTextField intervalField = new JTextField(5);
    private final ComboBoxModel<TimeUnit> timeUnitModel;
    private final ComboBoxModel<Category> categoryModel;
    private final ComboBoxModel<Template> templateModel;
    private TodoEvent todoEvent;

    public TodoEventDialog(TodoEvent todoEvent) {
        this.todoEvent = todoEvent;
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

        var testData = new TestDataGenerator();
        List<Template> templates = new ArrayList<>();
        templates.add(null);  // Add null as the first "no template" option
        templates.addAll(testData.createTemplates());

        this.templateModel = new DefaultComboBoxModel<>(templates.toArray(new Template[0]));

        setValues();
        addFields();
    }

    private void setValues() {
        nameField.setText(todoEvent.getName());
        detailsField.setText(todoEvent.getDetails());

        LocalDateTime dateTime = todoEvent.getDate();
        if (dateTime != null) {
            dateField.setDate(dateTime.toLocalDate());
            timeField.setTime(dateTime.toLocalTime());
        } else {
            dateField.setDate(LocalDate.now());
            timeField.setTime(LocalTime.now());
        }

        intervalField.setText(String.valueOf(todoEvent.getTimeUnitAmount()));
        categoryModel.setSelectedItem(todoEvent.getCategories());
        timeUnitModel.setSelectedItem(todoEvent.getTimeUnit());
    }

    private void addFields() {
        var templateComboBox = new JComboBox<>(this.templateModel);

        templateComboBox.addActionListener(e -> {
            Template selectedTemplate = (Template) templateComboBox.getSelectedItem();

            if (selectedTemplate != null) {
                todoEvent = selectedTemplate.toTodoEvent();  // Update your todoEvent
                setValues();
            }
        });

        var categoryComboBox = new JComboBox<>(categoryModel);
        var timeUnitComboBox = new JComboBox<>(timeUnitModel);
        JPanel intervalField = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        intervalField.add(this.intervalField);
        intervalField.add(timeUnitComboBox);

        add("Template", templateComboBox);
        add("Name:", nameField);
        add("Details:", detailsField);
        add("Date:", dateField);
        add("Time:", timeField);
        add("Length:", intervalField);
        add("Category:", categoryComboBox);
    }

    @Override
    TodoEvent getEntity() {
        todoEvent.setName(nameField.getText());
        todoEvent.setDetails(detailsField.getText());

        LocalDate date = dateField.getDate();
        LocalTime time = timeField.getTime();
        if (date != null && time != null) {
            todoEvent.setDate(LocalDateTime.of(date, time));
        }

        todoEvent.setTimeUnitAmount(Integer.parseInt(intervalField.getText()));
        todoEvent.setTimeUnit((TimeUnit) timeUnitModel.getSelectedItem());
        todoEvent.setCategories((List<Category>) categoryModel.getSelectedItem());

        return todoEvent;
    }
}
