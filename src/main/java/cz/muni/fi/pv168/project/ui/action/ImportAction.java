package cz.muni.fi.pv168.project.ui.action;

import cz.muni.fi.pv168.project.business.service.export.ImportService;
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
            try {
                String filePath = dialog.getResultFilePath();
                if (filePath != null) {
                    importService.importData(filePath, parentFrame);
                    JOptionPane.showMessageDialog(parentFrame, "Import Successful! ",
                            "Import", JOptionPane.INFORMATION_MESSAGE);
                    callback.run();
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(parentFrame, "Error during import:\n" + e.getMessage(),
                        "Import Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
