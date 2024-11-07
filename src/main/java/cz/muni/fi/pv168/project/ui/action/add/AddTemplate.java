package cz.muni.fi.pv168.project.ui.action.add;

import cz.muni.fi.pv168.project.data.TestDataGenerator;
import cz.muni.fi.pv168.project.ui.model.AllTableModels;
import cz.muni.fi.pv168.project.ui.model.TemplateTableModel;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.event.KeyEvent;
import java.util.function.Supplier;

public final class AddTemplate extends AddAction {
    public AddTemplate(Supplier<JTable> tableSupplier, AllTableModels allTableModels) {
        super("Add template", tableSupplier, allTableModels);
        putValue(SHORT_DESCRIPTION, "Adds new template");
        putValue(MNEMONIC_KEY, KeyEvent.VK_T);
    }

    @Override
    protected TableModel getTableModel() {
        return allTableModels.getTemplateTableModel();
    }
}
