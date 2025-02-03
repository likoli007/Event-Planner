package cz.muni.fi.pv168.project.ui.action;

import cz.muni.fi.pv168.project.business.service.validation.*;
import cz.muni.fi.pv168.project.model.Category;
import cz.muni.fi.pv168.project.model.TimeUnit;
import cz.muni.fi.pv168.project.storage.sql.db.TransactionException;
import cz.muni.fi.pv168.project.ui.dialog.SuccessDialog;
import cz.muni.fi.pv168.project.ui.model.*;
import cz.muni.fi.pv168.project.ui.resources.Icons;
import cz.muni.fi.pv168.project.ui.window.ToastNotification;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Supplier;

public final class DeleteAction extends AbstractAction {

    private final Supplier<JTable> tableSupplier;
    private final AllTableModels allTableModels;

    public DeleteAction(Supplier<JTable> tableSupplier, AllTableModels allTableModels) {
        super("Delete", Icons.DELETE_ICON);
        this.tableSupplier = tableSupplier;
        this.allTableModels = allTableModels;
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

        int counter = 0;
        try {
            if (model instanceof EventTableModel eventTableModel) {
                for (int viewRow : selectedRows) {
                    int modelRow = currentTable.convertRowIndexToModel(viewRow);
                    eventTableModel.deleteRow(modelRow);
                    counter++;
                }
            } else if (model instanceof TemplateTableModel templateTableModel) {
                for (int viewRow : selectedRows) {
                    int modelRow = currentTable.convertRowIndexToModel(viewRow);
                    templateTableModel.deleteRow(modelRow);
                    counter++;
                }
            } else if (model instanceof CategoryTableModel categoryTableModel) {
                for (int viewRow : selectedRows) {
                    int modelRow = currentTable.convertRowIndexToModel(viewRow);
                    Category categoryToDelete = categoryTableModel.getEntity(modelRow);

                    Validator<Category> validator = new CategoryValidator();
                    ValidationResult result = validator.validateDelete(allTableModels, categoryToDelete);
                    if (!result.isValid()) {
                        throw new ValidationException(result.toString());
                    }

                    categoryTableModel.deleteRow(modelRow);
                    counter++;
                }
            } else if (model instanceof TimeUnitTableModel timeUnitTableModel) {
                for (int viewRow : selectedRows) {
                    int modelRow = currentTable.convertRowIndexToModel(viewRow);
                    TimeUnit timeUnitToDelete = timeUnitTableModel.getEntity(modelRow);

                    Validator<TimeUnit> validator = new TimeUnitValidator();
                    ValidationResult result = validator.validateDelete(allTableModels, timeUnitToDelete);
                    if (!result.isValid()) {
                        throw new ValidationException(result.toString());
                    }

                    timeUnitTableModel.deleteRow(modelRow);
                    counter++;
                }
            } else {
                JOptionPane.showMessageDialog(currentTable,
                        "Unsupported table model for deleting.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (ValidationException | TransactionException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            if (counter > 0) {
                ToastNotification.getInstance().show("Number of successfully deleted entities: " + counter);
            }
        }
    }
}
