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
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Optional;

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

    private void handleDuplicateCategories(Collection<Category> categories, JFrame parentFrame) {
        for (var category : categories) {
            Optional<Category> original = categoryCrudService.findDuplicate(category);

            if (original.isEmpty()) {
                createCategory(category);
            }
            else {
                Category crudCategory = original.get();
                DuplicateCategoryDialog dialog = new DuplicateCategoryDialog(parentFrame, crudCategory, category);
                DuplicateType result = dialog.getResult();
                if (result == DuplicateType.OVERWRITE) {
                    crudCategory.setName(category.getName());
                    crudCategory.setColor(category.getColor());
                }
                if (result == DuplicateType.DUPLICATE) {
                    category.setName(category.getName() + " (copy)");
                    createCategory(category);
                }
            }
        }
    }

    private void handleDuplicateTimeUnits(Collection<TimeUnit> timeUnits, JFrame parentFrame) {
        for (var timeUnit : timeUnits) {
            Optional<TimeUnit> original = timeUnitCrudService.findDuplicate(timeUnit);

            if (original.isEmpty()) {
                createInterval(timeUnit);
            }
            else{
                TimeUnit crudTimeUnit = original.get();
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
                    createInterval(timeUnit);
                }
            }
        }
    }

    private void handleDuplicateTemplates(Collection<Template> templates, JFrame parentFrame) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        for (var template : templates) {
            Optional<Template> original = templateCrudService.findDuplicate(template);

            if (original.isEmpty()) {
                createTemplate(template);
            }
            else{
                Template crudTemplate = original.get();

                String originalString =
                        "Name: " + crudTemplate.getName() + "\n" +
                                "Details: " + crudTemplate.getDetails() + "\n" +
                                "Start Time: " + crudTemplate.getStartTime().format(formatter) + "\n" +
                                "Interval: " + crudTemplate.getInterval().getAmount() + " " +
                                crudTemplate.getInterval().getTimeUnit().getName() + "\n" +
                                "Categories: " + crudTemplate.getCategories().toString();

                String duplicateString = "Name: " + template.getName() + "\n" +
                        "Details: " + template.getDetails() + "\n" +
                        "Start Time: " + template.getStartTime().format(formatter) + "\n" +
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
                    createTemplate(template);
                }
            }
        }
    }

    private void  handleDuplicateEvents(Collection<TodoEvent> events, JFrame parentFrame){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy EEE");

        for (var event : events) {
            Optional<TodoEvent> original = todoEventCrudService.findDuplicate(event);

            if (original.isEmpty()) {
                createEvent(event);
            }
            else{
                TodoEvent crudEvent = original.get();
                String originalLength = crudEvent.getInterval().getTimeUnit() == null ? "min" : crudEvent.getInterval().getTimeUnit().getShortcut();
                String duplicateLength = event.getInterval().getTimeUnit() == null ? "min" : event.getInterval().getTimeUnit().getShortcut();

                String originalString =
                        "Name: " + crudEvent.getName() + "\n" +
                                "Details: " + crudEvent.getDetails() + "\n" +
                                "Start Time: " + crudEvent.getStart().format(formatter) + "\n" +
                                "Interval: " + crudEvent.getInterval().getAmount() + " " +
                                originalLength + "\n" +
                                "Categories: " + crudEvent.getCategories().toString();

                String duplicateString = "Name: " + event.getName() + "\n" +
                        "Details: " + event.getDetails() + "\n" +
                        "Start Time: " + event.getStart().format(formatter) + "\n" +
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
                    createEvent(event);
                }
            }
        }
    }

    private void handleDuplicates(Batch batch, JFrame parentFrame) {
        //TODO: templated function? but the different classes differ in multiple aspects
        handleDuplicateCategories(batch.categories(), parentFrame);
        handleDuplicateTimeUnits(batch.timeUnits(), parentFrame);
        handleDuplicateTemplates(batch.templates(), parentFrame);
        handleDuplicateEvents(batch.events(), parentFrame);
    }
    @Override
    public void importData(String filePath, JFrame frame) throws IOException {
        var batch = getImporter(filePath).importBatch(filePath);

        handleDuplicates(batch, frame);

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
