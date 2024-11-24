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
        boolean success = false;

        while (!success) {
            TodoEvent newEvent = new TodoEvent("", "", LocalDateTime.now(), 1, new ArrayList<>());
            TodoEventDialog dialog = new TodoEventDialog(newEvent, allTableModels);

            var result = dialog.show(currentTable, "Add New Event");
            if (result.isEmpty()) {
                break;
            }

            TodoEvent todoEvent = result.get();
            Validator<TodoEvent> validator = new TodoEventValidator();
            ValidationResult validationResult = validator.validateAdd(allTableModels, todoEvent);

            if (validationResult.isValid()) {
                eventTableModel.addRow(todoEvent);
                SuccessDialog.show("Event created successfully!");
                success = true;
            } else {
                JOptionPane.showMessageDialog(currentTable, validationResult.toString(), "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void tryAddTemplate(JTable currentTable, TemplateTableModel templateTableModel) {
        boolean success = false;

        while (!success) {
            Template newTemplate = new Template("", "", LocalTime.now(), 1, new ArrayList<>());
            TemplateDialog dialog = new TemplateDialog(newTemplate, allTableModels);

            var result = dialog.show(currentTable, "Add New Template");
            if (result.isEmpty()) {
                break;
            }

            Template template = result.get();
            Validator<Template> validator = new TemplateValidator();
            ValidationResult validationResult = validator.validateAdd(allTableModels, template);

            if (validationResult.isValid()) {
                templateTableModel.addRow(template);
                SuccessDialog.show("Template created successfully!");
                success = true;
            } else {
                JOptionPane.showMessageDialog(currentTable, validationResult.toString(), "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void tryAddCategory(JTable currentTable, CategoryTableModel categoryTableModel) {
        boolean success = false;

        while (!success) {
            Category newCategory = new Category("", Color.BLUE);
            CategoryDialog dialog = new CategoryDialog(newCategory);

            var result = dialog.show(currentTable, "Add New Category");
            if (result.isEmpty()) {
                break;
            }

            Category category = result.get();
            Validator<Category> validator = new CategoryValidator();
            ValidationResult validationResult = validator.validateAdd(allTableModels, category);

            if (validationResult.isValid()) {
                categoryTableModel.addRow(category);
                SuccessDialog.show("Category created successfully!");
                success = true;
            } else {
                JOptionPane.showMessageDialog(currentTable, validationResult.toString(), "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void tryAddTimeUnit(JTable currentTable, TimeUnitTableModel timeUnitTableModel) {
        boolean success = false;

        while (!success) {
            TimeUnit newTimeUnit = new TimeUnit("", "", 0);
            IntervalDialog dialog = new IntervalDialog(newTimeUnit);

            var result = dialog.show(currentTable, "Add New Time Unit");
            if (result.isEmpty()) {
                break;
            }

            TimeUnit timeUnit = result.get();
            Validator<TimeUnit> validator = new TimeUnitValidator();
            ValidationResult validationResult = validator.validateAdd(allTableModels, timeUnit);

            if (validationResult.isValid()) {
                timeUnitTableModel.addRow(timeUnit);
                SuccessDialog.show("Time unit created successfully!");
                success = true;
            } else {
                JOptionPane.showMessageDialog(currentTable, validationResult.toString(), "Validation Error", JOptionPane.ERROR_MESSAGE);
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
