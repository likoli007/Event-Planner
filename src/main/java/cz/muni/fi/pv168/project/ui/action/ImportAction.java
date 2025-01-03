package cz.muni.fi.pv168.project.ui.action;

import cz.muni.fi.pv168.project.business.service.export.ImportService;
import cz.muni.fi.pv168.project.model.DuplicateType;
import cz.muni.fi.pv168.project.ui.dialog.ImportDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class ImportAction extends AbstractAction {

    private final JFrame parentFrame;
    private final ImportService importService;
    private final Runnable callback;

    public ImportAction(JFrame parentFrame, ImportService importService, Runnable callback) {
        super("Import");
        this.parentFrame = parentFrame;
        this.importService = importService;
        this.callback = callback;

        putValue(SHORT_DESCRIPTION, "Imports data from JSON");
        putValue(MNEMONIC_KEY, KeyEvent.VK_I);
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl i"));
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        ImportDialog dialog = new ImportDialog(parentFrame);
        if (dialog.canImport()){
            String filePath = dialog.getResultFilePath();
            DuplicateType defaultHandling = dialog.getDuplicateHandling();
            if (filePath != null) {
                boolean importResult = importService.importData(filePath, parentFrame, defaultHandling);
                if (importResult) {
                    JOptionPane.showMessageDialog(parentFrame, "Import Successful! ",
                            "Import", JOptionPane.INFORMATION_MESSAGE);
                    callback.run();
                } else {
                    JOptionPane.showMessageDialog(parentFrame, importService.getErrorMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
