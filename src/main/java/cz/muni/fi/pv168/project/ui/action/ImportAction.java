package cz.muni.fi.pv168.project.ui.action;

import cz.muni.fi.pv168.project.ui.dialog.ImportDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class ImportAction extends AbstractAction {

    private final JFrame parentFrame;

    public ImportAction(JFrame parentFrame) {
        super("Import");
        this.parentFrame = parentFrame;
        putValue(SHORT_DESCRIPTION, "Imports data from JSON");
        putValue(MNEMONIC_KEY, KeyEvent.VK_I);
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl i"));
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        ImportDialog dialog = new ImportDialog(parentFrame);
    }
}
