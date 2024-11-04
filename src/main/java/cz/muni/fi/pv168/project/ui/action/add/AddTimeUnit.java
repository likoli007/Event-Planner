package cz.muni.fi.pv168.project.ui.action.add;

import cz.muni.fi.pv168.project.data.TestDataGenerator;
import cz.muni.fi.pv168.project.ui.model.AllTableModels;
import cz.muni.fi.pv168.project.ui.model.CategoryTableModel;
import cz.muni.fi.pv168.project.ui.model.TimeUnitTableModel;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.event.KeyEvent;
import java.util.function.Supplier;

public final class AddTimeUnit extends AddAction {
    public AddTimeUnit(Supplier<JTable> tableSupplier, AllTableModels allTableModels) {
        super("Add time unit", tableSupplier, allTableModels);
        putValue(SHORT_DESCRIPTION, "Adds new time unit");
        putValue(MNEMONIC_KEY, KeyEvent.VK_U);
    }

    @Override
    protected TableModel getTableModel() {
        return allTableModels.getTimeUnitTableModel();
    }
}
