package cz.muni.fi.pv168.project.ui.action;

import cz.muni.fi.pv168.project.service.export.ExportService;
import cz.muni.fi.pv168.project.ui.dialog.ExportDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class ExportAction extends AbstractAction {

    private final JFrame parentFrame;
    private final ExportService exportService;


    public ExportAction(JFrame parentFrame, ExportService exportService) {
        super("Export");
        this.parentFrame = parentFrame;
        this.exportService = exportService;

        putValue(SHORT_DESCRIPTION, "Exports data to json");
        putValue(MNEMONIC_KEY, KeyEvent.VK_E);
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl e"));
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        ExportDialog dialog = new ExportDialog(parentFrame);

        try {
            exportService.exportData(dialog.getResultFilePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
