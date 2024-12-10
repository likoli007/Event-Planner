package cz.muni.fi.pv168.project.storage.sql;

import cz.muni.fi.pv168.project.business.service.export.ImportService;
import cz.muni.fi.pv168.project.business.service.export.format.Format;
import cz.muni.fi.pv168.project.model.DuplicateType;
import cz.muni.fi.pv168.project.storage.sql.db.TransactionExecutor;

import javax.swing.*;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

public class TransactionalImportService implements ImportService {

    private final ImportService importService;
    private final TransactionExecutor transactionExecutor;

    public TransactionalImportService(ImportService importService, TransactionExecutor transactionExecutor) {
        this.importService = importService;
        this.transactionExecutor = transactionExecutor;
    }

    @Override
    public boolean importData(String filePath, JFrame frame, DuplicateType defaultHandling) {
        AtomicReference<Boolean> result = new AtomicReference<>(false);

        transactionExecutor.executeInTransaction(() -> {
            boolean success = importService.importData(filePath, frame, defaultHandling);
            result.set(success);
        });

        return result.get();
    }

    @Override
    public Collection<Format> getFormats() {
        return importService.getFormats();
    }

    @Override
    public String getErrorMessage() {
        return importService.getErrorMessage();
    }
}
