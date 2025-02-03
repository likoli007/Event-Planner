package cz.muni.fi.pv168.project.ui.action;

import cz.muni.fi.pv168.project.business.service.export.ImportService;
import cz.muni.fi.pv168.project.model.DuplicateType;
import cz.muni.fi.pv168.project.ui.dialog.ImportDialog;
import cz.muni.fi.pv168.project.ui.workers.AsyncImporter;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class ImportAction extends AbstractAction {

    private final JFrame parentFrame;
    private final Importer importer;

    public ImportAction(JFrame parentFrame, ImportService importService, Runnable callback) {
        super("Import");
        this.parentFrame = parentFrame;
        this.importer = new AsyncImporter(
                importService,
                () -> {
                    if (callback != null) {
                        callback.run();
                    }
                });

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
                importer.importData(filePath, parentFrame, defaultHandling);
            }
        }
    }
}
