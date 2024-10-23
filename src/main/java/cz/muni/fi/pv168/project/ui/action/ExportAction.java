package cz.muni.fi.pv168.project.ui.action;

import cz.muni.fi.pv168.project.ui.dialog.ExportDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class ExportAction extends AbstractAction {

    private final JFrame parentFrame;

    public ExportAction(JFrame parentFrame) {
        super("Export");
        this.parentFrame = parentFrame;
        putValue(SHORT_DESCRIPTION, "Exports data to json");
        putValue(MNEMONIC_KEY, KeyEvent.VK_E);
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl e"));
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        ExportDialog dialog = new ExportDialog(parentFrame);
    }
}
