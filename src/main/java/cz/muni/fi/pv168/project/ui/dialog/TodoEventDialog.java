package cz.muni.fi.pv168.project.ui.dialog;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.TimePicker;
import cz.muni.fi.pv168.project.model.Category;
import cz.muni.fi.pv168.project.model.Color;
import cz.muni.fi.pv168.project.model.TimeUnit;
import cz.muni.fi.pv168.project.model.TodoEvent;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public final class TodoEventDialog extends EntityDialog<TodoEvent> {

    private final JTextField nameField = new JTextField();
    private final JTextField detailsField = new JTextField();
    private final DatePicker dateField = new DatePicker();
    private final TimePicker timeField = new TimePicker();

    private final JTextField intervalField = new JTextField(5);
    private final ComboBoxModel<TimeUnit> timeUnitModel;
    private final ComboBoxModel<Category> categoryModel;
    private final TodoEvent todoEvent;

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
        categoryModel.setSelectedItem(todoEvent.getCategory());
        timeUnitModel.setSelectedItem(todoEvent.getTimeUnit());
    }

    private void addFields() {
        var categoryComboBox = new JComboBox<>(categoryModel);
        var timeUnitComboBox = new JComboBox<>(timeUnitModel);
        JPanel intervalField = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        intervalField.add(this.intervalField);
        intervalField.add(timeUnitComboBox);

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
        todoEvent.setCategory((Category) categoryModel.getSelectedItem());

        return todoEvent;
    }
}
