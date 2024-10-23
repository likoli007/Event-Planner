package cz.muni.fi.pv168.project.ui.action;

import cz.muni.fi.pv168.project.model.Category;
import cz.muni.fi.pv168.project.model.Template;
import cz.muni.fi.pv168.project.model.TimeUnit;
import cz.muni.fi.pv168.project.ui.dialog.CategoryDialog;
import cz.muni.fi.pv168.project.ui.dialog.IntervalDialog;
import cz.muni.fi.pv168.project.ui.dialog.TemplateDialog;
import cz.muni.fi.pv168.project.ui.dialog.TodoEventDialog;
import cz.muni.fi.pv168.project.ui.model.CategoryTableModel;
import cz.muni.fi.pv168.project.ui.model.EventTableModel;
import cz.muni.fi.pv168.project.ui.model.TemplateTableModel;
import cz.muni.fi.pv168.project.ui.model.TimeUnitTableModel;
import cz.muni.fi.pv168.project.ui.resources.Icons;
import cz.muni.fi.pv168.project.model.TodoEvent;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.function.Supplier;

public final class EditAction extends AbstractAction {

    private final Supplier<JTable> tableSupplier;

    public EditAction(Supplier<JTable> tableSupplier) {
        super("Edit", Icons.EDIT_ICON);
        this.tableSupplier = tableSupplier;
        putValue(SHORT_DESCRIPTION, "Edits selected item");
        putValue(MNEMONIC_KEY, KeyEvent.VK_E);
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl E"));
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

        if (model instanceof EventTableModel eventTableModel) {
            TodoEvent todoEvent = eventTableModel.getEntity(modelRow);
            TodoEventDialog dialog = new TodoEventDialog(todoEvent);
            dialog.show(currentTable, "Edit Todo Event");
        } else if (model instanceof CategoryTableModel categoryTableModel) {
            Category category = categoryTableModel.getEntity(modelRow);
            CategoryDialog dialog = new CategoryDialog(category);
            dialog.show(currentTable, "Edit Category");
        } else if (model instanceof TemplateTableModel templateTableModel) {
            Template template = templateTableModel.getEntity(modelRow);
            TemplateDialog dialog = new TemplateDialog(template);
            dialog.show(currentTable, "Edit Template");
        } else if (model instanceof TimeUnitTableModel timeUnitTableModel) {
            TimeUnit timeUnit = timeUnitTableModel.getEntity(modelRow);
            IntervalDialog dialog = new IntervalDialog(timeUnit);
            dialog.show(currentTable, "Edit Time Unit");
        } else {
            JOptionPane.showMessageDialog(currentTable,
                    "Unsupported table model for editing.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
