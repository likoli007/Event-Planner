package cz.muni.fi.pv168.project.ui.action;

import cz.muni.fi.pv168.project.model.TodoEvent;
import cz.muni.fi.pv168.project.service.export.ExportService;
import cz.muni.fi.pv168.project.ui.dialog.ExportDialog;

import javax.security.auth.callback.Callback;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;

public class ExportAction extends AbstractAction {

    private final JFrame parentFrame;
    private final ExportService exportService;
    private final Supplier<List<TodoEvent>> filteringCallback;
    private final Supplier<List<TodoEvent>> allCallback;

    public ExportAction(JFrame parentFrame, ExportService exportService, Supplier<List<TodoEvent>> filteringCallback, Supplier<List<TodoEvent>> allCallback) {
        super("Export");
        this.parentFrame = parentFrame;
        this.exportService = exportService;
        this.filteringCallback = filteringCallback;
        this.allCallback = allCallback;

        putValue(SHORT_DESCRIPTION, "Exports data to json");
        putValue(MNEMONIC_KEY, KeyEvent.VK_E);
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl e"));
    }



    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        List<TodoEvent> allEvents = allCallback.get();
        List<TodoEvent> filteredEvents = filteringCallback.get();

        int filteredEventsCount = filteredEvents.size();
        int allEventsCount = allEvents.size();

        ExportDialog dialog = new ExportDialog(parentFrame, allEventsCount, filteredEventsCount);

        try {
            String filePath = dialog.getResultFilePath();
            if (filePath != null) {
                exportService.exportData(dialog.getResultFilePath(), dialog.getExportFiltered());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
