package cz.muni.fi.pv168.project.ui.action;

import cz.muni.fi.pv168.project.model.TodoEvent;
import cz.muni.fi.pv168.project.ui.dialog.ImportDialog;
import cz.muni.fi.pv168.project.ui.dialog.TodoEventDialog;
import cz.muni.fi.pv168.project.ui.resources.Icons;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Optional;

public class ImportAction extends AbstractAction {
    public ImportAction() {
        super("Import");
        putValue(SHORT_DESCRIPTION, "Imports data from json");
        putValue(MNEMONIC_KEY, KeyEvent.VK_I);
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl i"));
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        ImportDialog dialog = new ImportDialog();
        //Optional<TodoEvent> result = dialog.show(eventTable, "Add New Event");
    }
}
