package cz.muni.fi.pv168.project.service.export;

import cz.muni.fi.pv168.project.model.Category;
import cz.muni.fi.pv168.project.model.Template;
import cz.muni.fi.pv168.project.model.TimeUnit;
import cz.muni.fi.pv168.project.model.TodoEvent;
import cz.muni.fi.pv168.project.service.crud.*;
import cz.muni.fi.pv168.project.service.export.batch.Batch;
import cz.muni.fi.pv168.project.service.export.batch.BatchExporter;
import cz.muni.fi.pv168.project.service.export.batch.BatchOperationException;
import cz.muni.fi.pv168.project.service.export.format.Format;
import cz.muni.fi.pv168.project.service.export.format.FormatMapping;

import java.io.IOException;
import java.util.Collection;

public class GenericExportService implements ExportService {

    private final TodoEventCrudService todoEventCrudService;
    private final TemplateCrudService templateCrudService;
    private final TimeUnitCrudService timeUnitCrudService;
    private final CategoryCrudService categoryCrudService;

    private final FormatMapping<BatchExporter> exporters;

    public GenericExportService(
            CategoryCrudService categoryCrudService,
            TimeUnitCrudService timeUnitCrudService,
            TemplateCrudService templateCrudService,
            TodoEventCrudService todoEventCrudService,
            Collection<BatchExporter> exporters) {
        this.todoEventCrudService = todoEventCrudService;
        this.templateCrudService = templateCrudService;
        this.timeUnitCrudService = timeUnitCrudService;
        this.categoryCrudService = categoryCrudService;
        this.exporters = new FormatMapping<>(exporters);
    }

    @Override
    public Collection<Format> getFormats() {
        return exporters.getFormats();
    }

    @Override
    public void exportData(String filePath) throws IOException {
        var exporter = getExporter(filePath);

        var batch = new Batch(categoryCrudService.findAll(), timeUnitCrudService.findAll(),
                templateCrudService.findAll(), todoEventCrudService.findAll());
        exporter.exportBatch(batch, filePath);
    }

    private BatchExporter getExporter(String filePath) {
        var extension = filePath.substring(filePath.lastIndexOf('.') + 1);
        var importer = exporters.findByExtension(extension);
        if (importer == null)
            throw new BatchOperationException("Extension %s has no registered formatter".formatted(extension));
        return importer;
    }
}
