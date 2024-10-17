package cz.muni.fi.pv168.project.ui.action;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class ExportAction extends AbstractAction {
    public ExportAction() {
        super("Export");
        putValue(SHORT_DESCRIPTION, "Exports data from json");
        putValue(MNEMONIC_KEY, KeyEvent.VK_E);
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl e"));
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        // TODO
    }
}
