package cz.muni.fi.pv168.project.ui.action.add;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.event.KeyEvent;
import java.util.function.Supplier;

public final class AddContextual extends AddAction {
    public AddContextual(Supplier<JTable> tableSupplier) {
        super("Add", tableSupplier);
        putValue(SHORT_DESCRIPTION, "Adds new item");
        putValue(MNEMONIC_KEY, KeyEvent.VK_A);
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl N"));
    }

    @Override
    protected TableModel getTableModel() {
        return tableSupplier.get().getModel();
    }
}
