package cz.muni.fi.pv168.project.service.export;

import cz.muni.fi.pv168.project.model.*;
import cz.muni.fi.pv168.project.service.crud.CategoryCrudService;
import cz.muni.fi.pv168.project.service.crud.TemplateCrudService;
import cz.muni.fi.pv168.project.service.crud.TimeUnitCrudService;
import cz.muni.fi.pv168.project.service.crud.TodoEventCrudService;
import cz.muni.fi.pv168.project.service.export.batch.Batch;
import cz.muni.fi.pv168.project.service.export.batch.BatchImporter;
import cz.muni.fi.pv168.project.service.export.batch.BatchOperationException;
import cz.muni.fi.pv168.project.service.export.format.Format;
import cz.muni.fi.pv168.project.service.export.format.FormatMapping;
import cz.muni.fi.pv168.project.ui.dialog.DuplicateCategoryDialog;
import cz.muni.fi.pv168.project.ui.dialog.DuplicateDialog;

import javax.swing.*;
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

    private void handleDuplicates(Batch batch, JFrame parentFrame) {
        //BIG CYCLES which will be taken away after CRUD services introduce isUnique()
        System.out.println(parentFrame.getTitle());
        System.out.println(batch.categories().size() + " " + categoryCrudService.findAll().size());
        for (var category : batch.categories()) {
            for (var crudCategory : categoryCrudService.findAll()) {
                System.out.println(crudCategory.getName() + " " + category.getName());
                if (crudCategory.getName().equals(category.getName())) {
                    DuplicateCategoryDialog dialog = new DuplicateCategoryDialog(parentFrame, crudCategory, category);
                    DuplicateType result = dialog.getResult();
                    if (result == DuplicateType.OVERWRITE) {
                        crudCategory.setName(category.getName());
                        crudCategory.setColor(category.getColor());
                    }
                    if (result == DuplicateType.DUPLICATE) {
                        category.setName(category.getName() + " (copy)");
                    }
                }
            }
        }

        for (var timeUnit : batch.timeUnits()) {
            for (var crudTimeUnit : timeUnitCrudService.findAll()) {
                if (crudTimeUnit.getName().equals(timeUnit.getName())) {
                    String originalString =
                            "Name: " + crudTimeUnit.getName() + "\n" +
                            "Shortcut: " + crudTimeUnit.getShortcut() + "\n" +
                            "Minutes: " + crudTimeUnit.getMinutes();
                    String duplicateString =
                            "Name: " + timeUnit.getName() + "\n" +
                            "Shortcut: " + timeUnit.getShortcut() + "\n" +
                            "Minutes: " + timeUnit.getMinutes();

                    DuplicateDialog dialog = new DuplicateDialog(parentFrame, "Interval", originalString, duplicateString);
                    DuplicateType result = dialog.getResult();
                    if (result == DuplicateType.OVERWRITE) {
                        crudTimeUnit.setName(timeUnit.getName());
                        crudTimeUnit.setMinutes(timeUnit.getMinutes());
                        crudTimeUnit.setShortcut(timeUnit.getShortcut());
                    }
                    if (result == DuplicateType.DUPLICATE) {
                        timeUnit.setName(timeUnit.getName() + " (copy)");
                    }
                }
            }
        }

        for (var template : batch.templates()) {
            for (var crudTemplate : templateCrudService.findAll()) {
                if (crudTemplate.getName().equals(template.getName())) {
                    String originalString =
                            "Name: " + crudTemplate.getName() +
                                    "Details: " + crudTemplate.getDetails() + "\n" +
                                    "Start Time: " + crudTemplate.getStartTime().toString() + "\n" +
                                    "Interval: " + crudTemplate.getInterval().getAmount() + " " +
                                    crudTemplate.getInterval().getTimeUnit().getName() + "\n" +
                                    "Categories: " + crudTemplate.getCategories().toString();

                    String duplicateString = "Name: " + template.getName() + "\n" +
                            "Details: " + template.getDetails() + "\n" +
                            "Start Time: " + template.getStartTime().toString() + "\n" +
                            "Interval: " + template.getInterval().getAmount() + " " +
                            template.getInterval().getTimeUnit().getName() + "\n" +
                            "Categories: " + template.getCategories().toString();


                    DuplicateDialog dialog = new DuplicateDialog(parentFrame, "Template", originalString, duplicateString);
                    DuplicateType result = dialog.getResult();
                    if (result == DuplicateType.OVERWRITE) {
                        crudTemplate.setName(template.getName());
                        crudTemplate.setDetails(template.getDetails());
                        crudTemplate.setStartTime(template.getStartTime());
                        crudTemplate.setInterval(template.getInterval());
                        crudTemplate.setCategories(template.getCategories());
                    }
                    if (result == DuplicateType.DUPLICATE) {
                        template.setName(template.getName() + " (copy)");
                    }
                }
            }
        }

        for (var event : batch.events()) {
            for (var crudEvent: todoEventCrudService.findAll()) {
                if (crudEvent.getName().equals(event.getName()) && crudEvent.getStart().equals(event.getStart())) {
                    String originalLength = crudEvent.getInterval().getTimeUnit() == null ? "min" : crudEvent.getInterval().getTimeUnit().getShortcut();
                    String duplicateLength = event.getInterval().getTimeUnit() == null ? "min" : event.getInterval().getTimeUnit().getShortcut();

                    String originalString =
                            "Name: " + crudEvent.getName() + "\n" +
                                    "Details: " + crudEvent.getDetails() + "\n" +
                                    "Start Time: " + crudEvent.getStart().toString() + "\n" +
                                    "Interval: " + crudEvent.getInterval().getAmount() + " " +
                                    originalLength + "\n" +
                                    "Categories: " + crudEvent.getCategories().toString();

                    String duplicateString = "Name: " + event.getName() + "\n" +
                            "Details: " + event.getDetails() + "\n" +
                            "Start Time: " + event.getStart().toString() + "\n" +
                            "Interval: " + event.getInterval().getAmount() + " " +
                            duplicateLength + "\n" +
                            "Categories: " + event.getCategories().toString();

                    DuplicateDialog dialog = new DuplicateDialog(parentFrame, "Event", originalString, duplicateString);
                    DuplicateType result = dialog.getResult();
                    if (result == DuplicateType.OVERWRITE) {
                        crudEvent.setName(event.getName());
                        crudEvent.setDetails(event.getDetails());
                        crudEvent.setStart(event.getStart());
                        crudEvent.setInterval(event.getInterval());
                        crudEvent.setCategories(event.getCategories());
                    }
                    if (result == DuplicateType.DUPLICATE) {
                        event.setName(event.getName() + " (copy)");
                    }
                }
            }
        }

    }
    @Override
    public void importData(String filePath, JFrame frame) throws IOException {
        var batch = getImporter(filePath).importBatch(filePath);

        System.out.println("handling dupes");
        handleDuplicates(batch, frame);

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
