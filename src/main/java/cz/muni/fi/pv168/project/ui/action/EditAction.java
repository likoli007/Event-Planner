package cz.muni.fi.pv168.project.ui.action;

import cz.muni.fi.pv168.project.business.service.validation.*;
import cz.muni.fi.pv168.project.model.Category;
import cz.muni.fi.pv168.project.model.Template;
import cz.muni.fi.pv168.project.model.TimeUnit;
import cz.muni.fi.pv168.project.ui.dialog.*;
import cz.muni.fi.pv168.project.ui.model.*;
import cz.muni.fi.pv168.project.ui.resources.Icons;
import cz.muni.fi.pv168.project.model.TodoEvent;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.function.Supplier;

public final class EditAction extends AbstractAction {

    private final Supplier<JTable> tableSupplier;
    private final AllTableModels allTableModels;

    public EditAction(Supplier<JTable> tableSupplier, AllTableModels allTableModels) {
        super("Edit", Icons.EDIT_ICON);
        this.tableSupplier = tableSupplier;
        this.allTableModels = allTableModels;
        putValue(SHORT_DESCRIPTION, "Edits selected item");
        putValue(MNEMONIC_KEY, KeyEvent.VK_E);
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl E"));
    }

    private void tryEditEvent(JTable currentTable, EventTableModel eventTableModel, int modelRow) {
        TodoEvent originalEvent = eventTableModel.getEntity(modelRow);
        TodoEventDialog dialog = new TodoEventDialog(originalEvent, allTableModels);
        dialog.show(currentTable, "Edit Todo Event").ifPresent(todoEvent -> {
            ValidatorUtils.validateEditEvent(allTableModels, originalEvent, todoEvent);

            originalEvent.update(todoEvent);
            eventTableModel.updateRow(originalEvent);
            SuccessDialog.show("Event edited successfully!");
        });
    }

    private void tryEditTemplate(JTable currentTable, TemplateTableModel templateTableModel, int modelRow) {
        Template originalTemplate = templateTableModel.getEntity(modelRow);
        TemplateDialog dialog = new TemplateDialog(originalTemplate, allTableModels);
        dialog.show(currentTable, "Edit Template").ifPresent(template -> {
            ValidatorUtils.validateEditTemplate(allTableModels, originalTemplate, template);

            originalTemplate.update(template);
            templateTableModel.updateRow(originalTemplate);
            SuccessDialog.show("Template edited successfully!");
        });
    }

    private void tryEditCategory(JTable currentTable, CategoryTableModel categoryTableModel, int modelRow) {
        Category originalCategory = categoryTableModel.getEntity(modelRow);
        CategoryDialog dialog = new CategoryDialog(originalCategory);
        dialog.show(currentTable, "Edit Category").ifPresent(category -> {
            Validator<Category> validator = new CategoryValidator();
            ValidationResult result = validator.validateEdit(allTableModels, originalCategory, category);
            if (!result.isValid()) {
                throw new ValidationException(result.toString());
            }

            originalCategory.update(category);
            categoryTableModel.updateRow(originalCategory);
            SuccessDialog.show("Category edited successfully!");
        });
    }

    private void tryEditTimeUnit(JTable currentTable, TimeUnitTableModel timeUnitTableModel, int modelRow) {
        TimeUnit originalTimeUnit = timeUnitTableModel.getEntity(modelRow);
        IntervalDialog dialog = new IntervalDialog(originalTimeUnit);
        dialog.show(currentTable, "Edit Time Unit").ifPresent(timeUnit -> {
            ValidatorUtils.validateEditTimeUnit(allTableModels, originalTimeUnit, timeUnit);

            originalTimeUnit.update(timeUnit);
            timeUnitTableModel.updateRow(originalTimeUnit);
            SuccessDialog.show("Time unit edited successfully!");
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JTable currentTable = tableSupplier.get();
        int[] selectedRows = currentTable.getSelectedRows();

        if (selectedRows.length != 1) {
            JOptionPane.showMessageDialog(currentTable,
                    "Please select exactly one item to edit.",
                    "Invalid Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (currentTable.isEditing()) {
            currentTable.getCellEditor().cancelCellEditing();
        }

        TableModel model = currentTable.getModel();
        int modelRow = currentTable.convertRowIndexToModel(selectedRows[0]);

        try {
            if (model instanceof EventTableModel eventTableModel) {
                tryEditEvent(currentTable, eventTableModel, modelRow);
            } else if (model instanceof TemplateTableModel templateTableModel) {
                tryEditTemplate(currentTable, templateTableModel, modelRow);
            } else if (model instanceof CategoryTableModel categoryTableModel) {
                tryEditCategory(currentTable, categoryTableModel, modelRow);
            } else if (model instanceof TimeUnitTableModel timeUnitTableModel) {
                tryEditTimeUnit(currentTable, timeUnitTableModel, modelRow);
            } else {
                JOptionPane.showMessageDialog(currentTable,
                        "Unsupported table model for editing.",
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
