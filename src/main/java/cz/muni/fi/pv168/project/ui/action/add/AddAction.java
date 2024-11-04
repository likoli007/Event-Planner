package cz.muni.fi.pv168.project.ui.action.add;

import cz.muni.fi.pv168.project.model.*;
import cz.muni.fi.pv168.project.ui.dialog.CategoryDialog;
import cz.muni.fi.pv168.project.ui.dialog.IntervalDialog;
import cz.muni.fi.pv168.project.ui.dialog.TemplateDialog;
import cz.muni.fi.pv168.project.ui.dialog.TodoEventDialog;
import cz.muni.fi.pv168.project.ui.model.*;
import cz.muni.fi.pv168.project.ui.resources.Icons;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
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

    @Override
    public void actionPerformed(ActionEvent e) {
        JTable currentTable = tableSupplier.get();
        TableModel model = getTableModel();

        if (model instanceof EventTableModel eventTableModel) {
            TodoEvent newEvent = new TodoEvent(
                    "",
                    "",
                    LocalDateTime.now(),
                    1,
                    List.of(new Category("Work", Color.BLUE))
            );
            TodoEventDialog dialog = new TodoEventDialog(newEvent, allTableModels);
            dialog.show(currentTable, "Add New Event").ifPresent(eventTableModel::addRow);
        } else if (model instanceof CategoryTableModel categoryTableModel) {
            Category newCategory = new Category("", Color.BLUE);
            CategoryDialog dialog = new CategoryDialog(newCategory);
            dialog.show(currentTable, "Add New Category").ifPresent(categoryTableModel::addRow);

        } else if (model instanceof TemplateTableModel templateTableModel) {
            Template newTemplate = new Template(
                    "",
                    "",
                    LocalTime.now(),
                    1,
                    List.of(new Category("Work", Color.BLUE))
            );
            TemplateDialog dialog = new TemplateDialog(newTemplate);
            dialog.show(currentTable, "Add New Template").ifPresent(templateTableModel::addRow);
        } else if (model instanceof TimeUnitTableModel timeUnitTableModel) {
            TimeUnit newTimeUnit = new TimeUnit("", "", 0);
            IntervalDialog dialog = new IntervalDialog(newTimeUnit);
            dialog.show(currentTable, "Add New Time Unit").ifPresent(timeUnitTableModel::addRow);
        } else {
            JOptionPane.showMessageDialog(currentTable,
                    "Unsupported table model for adding.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
