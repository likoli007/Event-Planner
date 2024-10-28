package cz.muni.fi.pv168.project.ui.action.add;

import cz.muni.fi.pv168.project.data.TestDataGenerator;
import cz.muni.fi.pv168.project.ui.model.CategoryTableModel;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.event.KeyEvent;
import java.util.function.Supplier;

public final class AddCategory extends AddAction {
    private final TableModel tableModel;

    public AddCategory(Supplier<JTable> tableSupplier, TableModel tableModel) {
        super("Add category", tableSupplier);
        putValue(SHORT_DESCRIPTION, "Adds new category");
        putValue(MNEMONIC_KEY, KeyEvent.VK_C);

        this.tableModel = tableModel;
    }

    @Override
    protected TableModel getTableModel() {
        return tableModel;
    }
}
