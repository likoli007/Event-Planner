package cz.muni.fi.pv168.project.service.export;

import cz.muni.fi.pv168.project.model.Category;
import cz.muni.fi.pv168.project.model.Template;
import cz.muni.fi.pv168.project.model.TimeUnit;
import cz.muni.fi.pv168.project.model.TodoEvent;
import cz.muni.fi.pv168.project.service.crud.CategoryCrudService;
import cz.muni.fi.pv168.project.service.crud.TemplateCrudService;
import cz.muni.fi.pv168.project.service.crud.TimeUnitCrudService;
import cz.muni.fi.pv168.project.service.crud.TodoEventCrudService;
import cz.muni.fi.pv168.project.service.export.batch.BatchImporter;
import cz.muni.fi.pv168.project.service.export.batch.BatchOperationException;
import cz.muni.fi.pv168.project.service.export.format.Format;
import cz.muni.fi.pv168.project.service.export.format.FormatMapping;

import java.io.IOException;
import java.util.Collection;

public class GenericImportService implements ImportService {

    private final TodoEventCrudService todoEventCrudService;
    private final TemplateCrudService templateCrudService;
    private final TimeUnitCrudService timeUnitCrudService;
    private final CategoryCrudService categoryCrudService;
    private final FormatMapping<BatchImporter> importers;

    public GenericImportService(
            CategoryCrudService categoryCrudService,
            TimeUnitCrudService timeUnitCrudService,
            TemplateCrudService templateCrudService,
            TodoEventCrudService todoEventCrudService,
            Collection<BatchImporter> importers
    ) {
        this.todoEventCrudService = todoEventCrudService;
        this.templateCrudService = templateCrudService;
        this.timeUnitCrudService = timeUnitCrudService;
        this.categoryCrudService = categoryCrudService;
        this.importers = new FormatMapping<>(importers);
    }

    @Override
    public void importData(String filePath) throws IOException {
        todoEventCrudService.deleteAll();
        templateCrudService.deleteAll();
        timeUnitCrudService.deleteAll();
        categoryCrudService.deleteAll();

        var batch = getImporter(filePath).importBatch(filePath);

        batch.categories().forEach(this::createCategory);
        batch.timeUnits().forEach(this::createInterval);
        batch.templates().forEach(this::createTemplate);
        batch.events().forEach(this::createEvent);
    }

    private void createCategory(Category category) {
        categoryCrudService.create(category);
    }

    private void createInterval(TimeUnit timeUnit) {
        timeUnitCrudService.create(timeUnit);
    }
    private void createTemplate(Template template) {
        templateCrudService.create(template);
    }
    private void createEvent(TodoEvent event){
        todoEventCrudService.create(event);
    }

    @Override
    public Collection<Format> getFormats() {
        return importers.getFormats();
    }

    private BatchImporter getImporter(String filePath) {
        var extension = filePath.substring(filePath.lastIndexOf('.') + 1);
        var importer = importers.findByExtension(extension);
        if (importer == null) {
            throw new BatchOperationException("Extension %s has no registered formatter".formatted(extension));
        }

        return importer;
    }
}
