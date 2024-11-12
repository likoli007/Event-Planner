package cz.muni.fi.pv168.project.ui.action.add;

import cz.muni.fi.pv168.project.model.*;
import cz.muni.fi.pv168.project.ui.dialog.*;
import cz.muni.fi.pv168.project.ui.model.*;
import cz.muni.fi.pv168.project.ui.resources.Icons;
import cz.muni.fi.pv168.project.validation.Validator;
import cz.muni.fi.pv168.project.validation.ValidationException;

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
        dialog.show(currentTable, "Add New Event").ifPresent(todoEvent -> {
            Validator.validateAddEvent(allTableModels, todoEvent);

            eventTableModel.addRow(todoEvent);
            SuccessDialog.show("Event created successfully!");
        });
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
        dialog.show(currentTable, "Add New Template").ifPresent(template -> {
            Validator.validateAddTemplate(allTableModels, template);

            templateTableModel.addRow(template);
            SuccessDialog.show("Template created successfully!");
        });
    }

    private void tryAddCategory(JTable currentTable, CategoryTableModel categoryTableModel) {
        Category newCategory = new Category("", Color.BLUE);

        CategoryDialog dialog = new CategoryDialog(newCategory);
        dialog.show(currentTable, "Add New Category").ifPresent(category -> {
            Validator.validateAddCategory(allTableModels, category);

            categoryTableModel.addRow(category);
            SuccessDialog.show("Category created successfully!");
        });
    }

    private void tryAddTimeUnit(JTable currentTable, TimeUnitTableModel timeUnitTableModel) {
        TimeUnit newTimeUnit = new TimeUnit("", "", 0);

        IntervalDialog dialog = new IntervalDialog(newTimeUnit);
        dialog.show(currentTable, "Add New Time Unit").ifPresent(timeUnit -> {
            Validator.validateAddTimeUnit(allTableModels, timeUnit);

            timeUnitTableModel.addRow(timeUnit);
            SuccessDialog.show("Time unit created successfully!");
        });
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
