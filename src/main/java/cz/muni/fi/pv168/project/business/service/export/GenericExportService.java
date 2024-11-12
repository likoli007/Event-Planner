package cz.muni.fi.pv168.project.business.service.export;

import cz.muni.fi.pv168.project.business.facades.TodoEventsServiceFacade;
import cz.muni.fi.pv168.project.business.service.crud.CrudService;
import cz.muni.fi.pv168.project.model.Category;
import cz.muni.fi.pv168.project.model.Template;
import cz.muni.fi.pv168.project.model.TimeUnit;
import cz.muni.fi.pv168.project.model.TodoEvent;
import cz.muni.fi.pv168.project.business.service.export.batch.Batch;
import cz.muni.fi.pv168.project.business.service.export.batch.BatchExporter;
import cz.muni.fi.pv168.project.business.service.export.batch.BatchOperationException;
import cz.muni.fi.pv168.project.business.service.export.format.Format;
import cz.muni.fi.pv168.project.business.service.export.format.FormatMapping;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class GenericExportService implements ExportService {

    private final TodoEventsServiceFacade todoEventCrudService;
    private final CrudService<Category> categoryCrudService;
    private final CrudService<Template> templateCrudService;
    private final CrudService<TimeUnit> timeUnitCrudService;

    private final FormatMapping<BatchExporter> exporters;

    public GenericExportService(
            CrudService<Category> categoryCrudService,
            CrudService<TimeUnit> timeUnitCrudService,
            CrudService<Template> templateCrudService,
            TodoEventsServiceFacade todoEventCrudService,
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
    public void exportData(String filePath, boolean exportFiltered) throws IOException {
        var exporter = getExporter(filePath);

        List<TodoEvent> eventsList = exportFiltered ? todoEventCrudService.getFilteredEvents() : todoEventCrudService.findAll();

        var batch = new Batch(categoryCrudService.findAll(), timeUnitCrudService.findAll(),
                templateCrudService.findAll(), eventsList);
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
