package cz.muni.fi.pv168.project.ui.workers;

import cz.muni.fi.pv168.project.business.service.export.ImportService;
import cz.muni.fi.pv168.project.business.service.export.format.Format;
import cz.muni.fi.pv168.project.model.DuplicateType;
import cz.muni.fi.pv168.project.storage.sql.dao.DataStorageException;
import cz.muni.fi.pv168.project.ui.action.Importer;

import javax.swing.*;
import java.util.Collection;
import java.util.Objects;

/**
 * Implementation of asynchronous importer for UI.
 */
public class AsyncImporter implements Importer {
    private boolean result;
    private final ImportService importService;
    private final Runnable onFinish;

    public AsyncImporter(ImportService importService, Runnable onFinish) {
        this.importService = Objects.requireNonNull(importService);
        this.onFinish = onFinish;
    }

    @Override
    public void importData(String filePath, JFrame frame, DuplicateType defaultHandling) {
        var asyncWorker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                try {
                    importService.importData(filePath, frame, defaultHandling);
                    onFinish.run();
                    JOptionPane.showMessageDialog(frame, "Import Successful! ",
                            "Import", JOptionPane.INFORMATION_MESSAGE);
                } catch (DataStorageException e) {
                    JOptionPane.showMessageDialog(frame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }

                return null;
            }
        };
        asyncWorker.execute();
    }

    @Override
    public Collection<Format> getFormats() {
        return importService.getFormats();
    }
}
