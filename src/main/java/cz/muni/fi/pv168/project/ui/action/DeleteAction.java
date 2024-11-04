package cz.muni.fi.pv168.project.ui.action;

import cz.muni.fi.pv168.project.model.Template;
import cz.muni.fi.pv168.project.ui.model.CategoryTableModel;
import cz.muni.fi.pv168.project.ui.model.EventTableModel;
import cz.muni.fi.pv168.project.ui.model.TemplateTableModel;
import cz.muni.fi.pv168.project.ui.model.TimeUnitTableModel;
import cz.muni.fi.pv168.project.ui.resources.Icons;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Supplier;

public final class DeleteAction extends AbstractAction {

    private final Supplier<JTable> tableSupplier;

    public DeleteAction(Supplier<JTable> tableSupplier) {
        super("Delete", Icons.DELETE_ICON);
        this.tableSupplier = tableSupplier;
        putValue(SHORT_DESCRIPTION, "Deletes selected item(s)");
        putValue(MNEMONIC_KEY, KeyEvent.VK_D);
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl D"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JTable currentTable = tableSupplier.get();
        int[] selectedRows = currentTable.getSelectedRows();

        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(currentTable,
                    "Please select at least one item to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmation = JOptionPane.showConfirmDialog(currentTable,
                "Are you sure you want to delete the selected item(s)?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        TableModel model = currentTable.getModel();
        selectedRows = Arrays.stream(selectedRows)
                .boxed()
                .sorted(Comparator.reverseOrder())
                .mapToInt(Integer::intValue)
                .toArray();
        if (model instanceof EventTableModel eventTableModel) {
            for (int viewRow : selectedRows) {
                int modelRow = currentTable.convertRowIndexToModel(viewRow);
                eventTableModel.deleteRow(modelRow);
            }
        } else if (model instanceof CategoryTableModel categoryTableModel) {
            for (int viewRow : selectedRows) {
                int modelRow = currentTable.convertRowIndexToModel(viewRow);
                categoryTableModel.deleteRow(modelRow);
            }
        } else if (model instanceof TemplateTableModel templateTableModel) {
            for (int viewRow : selectedRows) {
                int modelRow = currentTable.convertRowIndexToModel(viewRow);
                templateTableModel.deleteRow(modelRow);
            }
        } else if (model instanceof TimeUnitTableModel timeUnitTableModel) {
            for (int viewRow : selectedRows) {
                int modelRow = currentTable.convertRowIndexToModel(viewRow);
                timeUnitTableModel.deleteRow(modelRow);
            }
        } else {
            JOptionPane.showMessageDialog(currentTable,
                    "Unsupported table model for deleting.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
