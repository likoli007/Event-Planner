package cz.muni.fi.pv168.project.ui.action.add;

import cz.muni.fi.pv168.project.data.TestDataGenerator;
import cz.muni.fi.pv168.project.ui.model.AllTableModels;
import cz.muni.fi.pv168.project.ui.model.EventTableModel;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.event.KeyEvent;
import java.util.function.Supplier;

public final class AddEvent extends AddAction {
    public AddEvent(Supplier<JTable> tableSupplier, AllTableModels allTableModels) {
        super("Add event", tableSupplier, allTableModels);
        putValue(SHORT_DESCRIPTION, "Adds new event");
        putValue(MNEMONIC_KEY, KeyEvent.VK_E);
    }

    @Override
    protected TableModel getTableModel() {
        return allTableModels.getEventTableModel();
    }
}
