package cz.muni.fi.pv168.project.ui.action;

import cz.muni.fi.pv168.project.model.Category;
import cz.muni.fi.pv168.project.model.Color;
import cz.muni.fi.pv168.project.model.TimeUnit;
import cz.muni.fi.pv168.project.model.TodoEvent;
import cz.muni.fi.pv168.project.ui.dialog.CategoryDialog;
import cz.muni.fi.pv168.project.ui.dialog.IntervalDialog;
import cz.muni.fi.pv168.project.ui.dialog.TodoEventDialog;
import cz.muni.fi.pv168.project.ui.model.CategoryTableModel;
import cz.muni.fi.pv168.project.ui.model.EventTableModel;
import cz.muni.fi.pv168.project.ui.model.TimeUnitTableModel;
import cz.muni.fi.pv168.project.ui.resources.Icons;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Supplier;

public final class AddAction extends AbstractAction {

    private final Supplier<JTable> tableSupplier;

    public AddAction(Supplier<JTable> tableSupplier) {
        super("Add", Icons.ADD_ICON);
        this.tableSupplier = tableSupplier;
        putValue(SHORT_DESCRIPTION, "Adds new item");
        putValue(MNEMONIC_KEY, KeyEvent.VK_A);
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl N"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JTable currentTable = tableSupplier.get();
        TableModel model = currentTable.getModel();

        if (model instanceof EventTableModel eventTableModel) {
            TodoEvent newEvent = new TodoEvent(
                    "",
                    "",
                    LocalDateTime.now(),
                    1,
                    new Category("Work", Color.BLUE)
            );
            TodoEventDialog dialog = new TodoEventDialog(newEvent);
            Optional<TodoEvent> result = dialog.show(currentTable, "Add New Event");
        } else if (model instanceof CategoryTableModel categoryTableModel) {
            Category newCategory = new Category("", Color.BLUE);
            CategoryDialog dialog = new CategoryDialog(newCategory);
            Optional<Category> result = dialog.show(currentTable, "Add New Category");
        } else if (model instanceof TimeUnitTableModel timeUnitTableModel) {
            TimeUnit newTimeUnit = new TimeUnit("", "", 0);
            IntervalDialog dialog = new IntervalDialog(newTimeUnit);
            Optional<TimeUnit> result = dialog.show(currentTable, "Add New Time Unit");
        } else {
            JOptionPane.showMessageDialog(currentTable,
                    "Unsupported table model for adding.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
