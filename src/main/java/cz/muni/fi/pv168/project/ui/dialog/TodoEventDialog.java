package cz.muni.fi.pv168.project.ui.dialog;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.TimePicker;
import cz.muni.fi.pv168.project.data.TestDataGenerator;
import cz.muni.fi.pv168.project.model.*;
import cz.muni.fi.pv168.project.model.Color;
import cz.muni.fi.pv168.project.ui.model.TemplateTableModel;

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
    private final JList<Category> categoryList;
    private final ComboBoxModel<TimeUnit> timeUnitModel;
    private final DefaultListModel<Category> categoryModel;
    private final ComboBoxModel<Template> templateModel;
    private TodoEvent todoEvent;

    public TodoEventDialog(TodoEvent todoEvent) {
        this.todoEvent = todoEvent;
        this.categoryModel = new DefaultListModel<>();
        this.categoryModel.addElement(new Category("Work", Color.BLUE));
        this.categoryModel.addElement(new Category("Personal", Color.GREEN));
        this.categoryModel.addElement(new Category("Fitness", Color.RED));;
        this.categoryList = new JList<>(categoryModel);

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

        LocalDateTime start = todoEvent.getStart();
        if (start != null) {
            dateField.setDate(start.toLocalDate());
            timeField.setTime(start.toLocalTime());
        } else {
            dateField.setDate(LocalDate.now());
            timeField.setTime(LocalTime.now());
        }

        intervalField.setText(String.valueOf(todoEvent.getInterval().getAmount()));
        // categoryModel.setSelectedItem(todoEvent.getCategories()); TODO set selected items
        timeUnitModel.setSelectedItem(todoEvent.getInterval().getTimeUnit());
    }

    private void addFields() {
        var templateComboBox = new JComboBox<>(this.templateModel);

        JButton createTemplateButton = new JButton("Create Template");
        createTemplateButton.addActionListener(e -> onCreateTemplate());

        templateComboBox.addActionListener(e -> {
            Template selectedTemplate = (Template) templateComboBox.getSelectedItem();
            if (selectedTemplate != null) {
                todoEvent = selectedTemplate.toTodoEvent();  // Update your todoEvent
                setValues();
            }
        });

        var categoryList = new JList<>(categoryModel);
        var timeUnitComboBox = new JComboBox<>(timeUnitModel);
        JPanel intervalField = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        intervalField.add(this.intervalField);
        intervalField.add(timeUnitComboBox);

        JPanel templatePanel = new JPanel(new BorderLayout());
        templatePanel.add(templateComboBox, BorderLayout.CENTER);
        templatePanel.add(createTemplateButton, BorderLayout.EAST);

        add("Template", templatePanel);
        add("Name:", nameField);
        add("Details:", detailsField);
        add("Date:", dateField);
        add("Time:", timeField);
        add("Length:", intervalField);
        add("Categories:", categoryList);
    }

    private void onCreateTemplate() {
        String name = nameField.getText();
        String details = detailsField.getText();
        LocalDate date = dateField.getDate();
        LocalTime time = timeField.getTime();
        int intervalAmount = Integer.parseInt(intervalField.getText());
        TimeUnit selectedTimeUnit = (TimeUnit) timeUnitModel.getSelectedItem();
        List<Category> selectedCategories = categoryList.getSelectedValuesList();

        Template newTemplate = new Template(
                name,
                details,
                LocalDateTime.of(date, time),
                selectedTimeUnit,
                intervalAmount,
                selectedCategories
        );

//        templateTableModel.addTemplate(newTemplate);

        JOptionPane.showMessageDialog(null,
                "Template created successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    TodoEvent getEntity() {
        todoEvent.setName(nameField.getText());
        todoEvent.setDetails(detailsField.getText());

        LocalDate date = dateField.getDate();
        LocalTime time = timeField.getTime();
        if (date != null && time != null) {
            todoEvent.setStart(LocalDateTime.of(date, time));
        }

        todoEvent.getInterval().setAmount(Integer.parseInt(intervalField.getText()));
        todoEvent.getInterval().setTimeUnit((TimeUnit) timeUnitModel.getSelectedItem());
        // todoEvent.setCategories((List<Category>) categoryModel.getSelectedItem()); TODO

        return todoEvent;
    }
}
