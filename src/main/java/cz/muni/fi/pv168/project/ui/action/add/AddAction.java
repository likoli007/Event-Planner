package cz.muni.fi.pv168.project.ui.action.add;

import cz.muni.fi.pv168.project.business.service.validation.*;
import cz.muni.fi.pv168.project.model.*;
import cz.muni.fi.pv168.project.ui.dialog.*;
import cz.muni.fi.pv168.project.ui.model.*;
import cz.muni.fi.pv168.project.ui.resources.Icons;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.function.Supplier;

public abstract class AddAction extends AbstractAction {
    protected final Supplier<JTable> tableSupplier;
    protected final AllTableModels allTableModels;

    public AddAction(String name, Supplier<JTable> tableSupplier, AllTableModels allTableModels) {
        super(name, Icons.ADD_ICON);
        this.tableSupplier = tableSupplier;
        this.allTableModels = allTableModels;
    }

    protected abstract TableModel getTableModel();

    private void tryAddEvent(JTable currentTable, EventTableModel eventTableModel) {
        TodoEvent newEvent = new TodoEvent(
                "",
                "",
                LocalDateTime.now(),
                1,
                new ArrayList<>()
        );

        TodoEventDialog dialog = new TodoEventDialog(newEvent, allTableModels);

        while (true) {
            var dialogResult = dialog.show(currentTable, "Add New Event");
            if (dialogResult.isEmpty()) {
                break;
            }

            TodoEvent todoEvent = dialogResult.get();
            Validator<TodoEvent> validator = new TodoEventValidator();
            ValidationResult result = validator.validateAdd(allTableModels, todoEvent);

            if (result.isValid()) {
                eventTableModel.addRow(todoEvent);
                SuccessDialog.show("Event created successfully!");
                break;
            } else {
                JOptionPane.showMessageDialog(currentTable,
                        result.toString(),
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void tryAddTemplate(JTable currentTable, TemplateTableModel templateTableModel) {
        Template newTemplate = new Template(
                "",
                "",
                LocalTime.now(),
                1,
                new ArrayList<>()
        );

        TemplateDialog dialog = new TemplateDialog(newTemplate, allTableModels);

        while (true) {
            var dialogResult = dialog.show(currentTable, "Add New Template");
            if (dialogResult.isEmpty()) {
                break;
            }

            Template template = dialogResult.get();
            Validator<Template> validator = new TemplateValidator();
            ValidationResult result = validator.validateAdd(allTableModels, template);

            if (result.isValid()) {
                templateTableModel.addRow(template);
                SuccessDialog.show("Template created successfully!");
                break;
            } else {
                JOptionPane.showMessageDialog(
                        currentTable,
                        result.toString(),
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }


    private void tryAddCategory(JTable currentTable, CategoryTableModel categoryTableModel) {
        Category newCategory = new Category("", Color.BLUE);

        CategoryDialog dialog = new CategoryDialog(newCategory);

        while (true) {
            var dialogResult = dialog.show(currentTable, "Add New Category");
            if (dialogResult.isEmpty()) {
                break;
            }

            Category category = dialogResult.get();
            Validator<Category> validator = new CategoryValidator();
            ValidationResult result = validator.validateAdd(allTableModels, category);

            if (result.isValid()) {
                categoryTableModel.addRow(category);
                SuccessDialog.show("Category created successfully!");
                break;
            } else {
                JOptionPane.showMessageDialog(
                        currentTable,
                        result.toString(),
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void tryAddTimeUnit(JTable currentTable, TimeUnitTableModel timeUnitTableModel) {
        TimeUnit newTimeUnit = new TimeUnit("", "", 0);

        IntervalDialog dialog = new IntervalDialog(newTimeUnit);

        while (true) {
            var dialogResult = dialog.show(currentTable, "Add New Time Unit");
            if (dialogResult.isEmpty()) {
                break;
            }

            TimeUnit timeUnit = dialogResult.get();
            Validator<TimeUnit> validator = new TimeUnitValidator();
            ValidationResult result = validator.validateAdd(allTableModels, timeUnit);

            if (result.isValid()) {
                timeUnitTableModel.addRow(timeUnit);
                SuccessDialog.show("Time unit created successfully!");
                break;
            } else {
                JOptionPane.showMessageDialog(
                        currentTable,
                        result.toString(),
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JTable currentTable = tableSupplier.get();
        TableModel model = getTableModel();

        try {
            if (model instanceof EventTableModel eventTableModel) {
                tryAddEvent(currentTable, eventTableModel);
            } else if (model instanceof TemplateTableModel templateTableModel) {
                tryAddTemplate(currentTable, templateTableModel);
            } else if (model instanceof CategoryTableModel categoryTableModel) {
                tryAddCategory(currentTable, categoryTableModel);
            } else if (model instanceof TimeUnitTableModel timeUnitTableModel) {
                tryAddTimeUnit(currentTable, timeUnitTableModel);
            } else {
                JOptionPane.showMessageDialog(currentTable,
                        "Unsupported table model for adding.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
