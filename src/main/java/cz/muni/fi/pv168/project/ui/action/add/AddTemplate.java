package cz.muni.fi.pv168.project.ui.action.add;

import cz.muni.fi.pv168.project.data.TestDataGenerator;
import cz.muni.fi.pv168.project.ui.model.TemplateTableModel;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.event.KeyEvent;
import java.util.function.Supplier;

public final class AddTemplate extends AddAction {
    private final TableModel tableModel;

    public AddTemplate(Supplier<JTable> tableSupplier, TableModel tableModel) {
        super("Add template", tableSupplier);
        putValue(SHORT_DESCRIPTION, "Adds new template");
        putValue(MNEMONIC_KEY, KeyEvent.VK_T);

        this.tableModel = tableModel;
    }

    @Override
    protected TableModel getTableModel() {
        return tableModel;
    }
}
