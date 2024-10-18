package cz.muni.fi.pv168.project.ui.action;

import cz.muni.fi.pv168.project.model.Category;
import cz.muni.fi.pv168.project.model.Color;
import cz.muni.fi.pv168.project.model.TodoEvent;
import cz.muni.fi.pv168.project.ui.dialog.TodoEventDialog;
import cz.muni.fi.pv168.project.ui.resources.Icons;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.time.LocalDateTime;
import java.util.Optional;

public final class AddAction extends AbstractAction {

    private final JTable eventTable;

    public AddAction(JTable eventTable) {
        super("Add", Icons.ADD_ICON);
        this.eventTable = eventTable;
        putValue(SHORT_DESCRIPTION, "Adds new event");
        putValue(MNEMONIC_KEY, KeyEvent.VK_A);
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl N"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        TodoEvent newEvent = new TodoEvent(
                "",
                "",
                LocalDateTime.now(),
                1,
                new Category("Work", Color.BLUE)
        );

        TodoEventDialog dialog = new TodoEventDialog(newEvent);
        Optional<TodoEvent> result = dialog.show(eventTable, "Add New Event");

        if (result.isPresent()) {
            // TODO: add the event to the table model
            // Example:
            // EventTableModel model = (EventTableModel) eventTable.getModel();
            // model.addRow(result.get());
        }
    }
}
