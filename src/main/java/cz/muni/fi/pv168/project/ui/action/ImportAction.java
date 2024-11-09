package cz.muni.fi.pv168.project.ui.action;

import cz.muni.fi.pv168.project.service.export.ImportService;
import cz.muni.fi.pv168.project.ui.dialog.ImportDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class ImportAction extends AbstractAction {

    private final JFrame parentFrame;
    private final ImportService importService;

    public ImportAction(JFrame parentFrame, ImportService importService) {
        super("Import");
        this.parentFrame = parentFrame;
        this.importService = importService;

        putValue(SHORT_DESCRIPTION, "Imports data from JSON");
        putValue(MNEMONIC_KEY, KeyEvent.VK_I);
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl i"));
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        ImportDialog dialog = new ImportDialog(parentFrame);
        try {
            String filePath = dialog.getResultFilePath();
            if (filePath != null) {
                importService.importData(filePath);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
