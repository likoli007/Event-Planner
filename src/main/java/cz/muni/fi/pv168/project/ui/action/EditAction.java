package cz.muni.fi.pv168.project.ui.action;

import cz.muni.fi.pv168.project.ui.dialog.TodoEventDialog;
import cz.muni.fi.pv168.project.ui.model.EventTableModel;
import cz.muni.fi.pv168.project.ui.resources.Icons;
import cz.muni.fi.pv168.project.model.TodoEvent;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public final class EditAction extends AbstractAction {

    private final JTable eventTable;

    public EditAction(JTable eventTable) {
        super("Edit", Icons.EDIT_ICON);
        this.eventTable = eventTable;
        putValue(SHORT_DESCRIPTION, "Edits selected event");
        putValue(MNEMONIC_KEY, KeyEvent.VK_E);
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl E"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int[] selectedRows = eventTable.getSelectedRows();

        if (selectedRows.length != 1) {
            JOptionPane.showMessageDialog(eventTable,
                    "Please select exactly one event to edit.",
                    "Invalid Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (eventTable.isEditing()) {
            eventTable.getCellEditor().cancelCellEditing();
        }

        var eventTableModel = (EventTableModel) eventTable.getModel();
        int modelRow = eventTable.convertRowIndexToModel(selectedRows[0]);
        var todoEvent = eventTableModel.getEntity(modelRow);
        var dialog = new TodoEventDialog(todoEvent);
        dialog.show(eventTable, "Edit Todo Event");
    }
}
