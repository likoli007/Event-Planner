package cz.muni.fi.pv168.project.business.service.export;

import cz.muni.fi.pv168.project.business.facades.TodoEventsServiceFacade;
import cz.muni.fi.pv168.project.business.service.crud.CrudService;
import cz.muni.fi.pv168.project.business.service.export.batch.*;
import cz.muni.fi.pv168.project.model.*;
import cz.muni.fi.pv168.project.business.service.export.format.Format;
import cz.muni.fi.pv168.project.business.service.export.format.FormatMapping;
import cz.muni.fi.pv168.project.ui.dialog.DuplicateCategoryDialog;
import cz.muni.fi.pv168.project.ui.dialog.DuplicateDialog;

import javax.swing.*;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

public class GenericImportService implements ImportService {

    private final TodoEventsServiceFacade todoEventFacade;
    private final CrudService<Category> categoryCrudService;
    private final CrudService<Template> templateCrudService;
    private final CrudService<TimeUnit> timeUnitCrudService;
    private final FormatMapping<BatchImporter> importers;

    private Batch importedBatch;
    private SingleResult<Batch> importedBatchResult;

    public GenericImportService(
            CrudService<Category> categoryCrudService,
            CrudService<TimeUnit> timeUnitCrudService,
            CrudService<Template> templateCrudService,
            TodoEventsServiceFacade todoEventFacade,
            Collection<BatchImporter> importers
    ) {
        this.todoEventFacade = todoEventFacade;
        this.templateCrudService = templateCrudService;
        this.timeUnitCrudService = timeUnitCrudService;
        this.categoryCrudService = categoryCrudService;
        this.importers = new FormatMapping<>(importers);
    }

    private void changeImportedCategoryReferences(Category category){
        for (TodoEvent e : importedBatch.events()){
            boolean hasCategoryReference = false;
            Iterator<Category> it = e.getCategories().iterator();
            while (it.hasNext()) {
                Category originalCategory = it.next();

                if (category.isDuplicate(originalCategory)) {
                    it.remove();
                    hasCategoryReference = true;
                }
            }
            if (hasCategoryReference){
                e.getCategories().add(category);
            }
        }

        for (Template t : importedBatch.templates()){
            Iterator<Category> it = t.getCategories().iterator();
            boolean hasCategoryReference = false;
            while (it.hasNext()) {
                Category originalCategory = it.next();

                if (category.isDuplicate(originalCategory)) {
                    it.remove();
                    hasCategoryReference = true;
                }
            }
            if (hasCategoryReference){
                t.getCategories().add(category);
            }
        }
    }

    private void changeImportedTimeUnitReferences(TimeUnit timeUnit){
        for (TodoEvent e : importedBatch.events()){
            Interval originalInterval = e.getInterval();
            TimeUnit originalTimeUnit = originalInterval.getTimeUnit();

            if (timeUnit.isDuplicate(originalTimeUnit)){
                e.getInterval().setTimeUnit(timeUnit);
            }
        }
        for (Template t : importedBatch.templates()){
            Interval originalInterval = t.getInterval();
            TimeUnit originalTimeUnit = originalInterval.getTimeUnit();
            if (timeUnit.isDuplicate(originalTimeUnit)){
                t.getInterval().setTimeUnit(timeUnit);
            }
        }
    }


    private void handleDuplicateCategories(Collection<Category> categories, JFrame parentFrame, DuplicateType defaultHandling) {
        for (var category : categories) {
            Optional<Category> original = categoryCrudService.findDuplicate(category);

            if (original.isEmpty()) {
                createCategory(category);
            }
            else if (original.get().isMeaningfullyDifferent(category)) {
                Category crudCategory = original.get();
                DuplicateType result;
                if (defaultHandling == DuplicateType.UNDEFINED){
                    DuplicateCategoryDialog dialog = new DuplicateCategoryDialog(parentFrame, crudCategory, category);
                    result = dialog.getResult();
                    boolean setNewDefault = dialog.getDefaultHandling();
                    if (setNewDefault) {
                        defaultHandling = result;
                    }
                }
                else{
                    result = defaultHandling;
                }

                if (result == DuplicateType.OVERWRITE) {
                    crudCategory.setName(category.getName());
                    crudCategory.setColor(category.getColor());
                    changeImportedCategoryReferences(crudCategory);
                }
                else if (result == DuplicateType.DUPLICATE) {
                    addDuplicateCategory(category);
                }
                else{
                    changeImportedCategoryReferences(crudCategory);
                }
            }
            else{
                //Non-meaningfully different, swap it with the already existing one
                changeImportedCategoryReferences(original.get());
            }
        }
    }

    private void handleDuplicateTimeUnits(Collection<TimeUnit> timeUnits, JFrame parentFrame, DuplicateType defaultHandling) {
        for (var timeUnit : timeUnits) {
            Optional<TimeUnit> original = timeUnitCrudService.findDuplicate(timeUnit);

            if (original.isEmpty()) {
                createInterval(timeUnit);
            }
            else if (original.get().isMeaningfullyDifferent(timeUnit)) {
                TimeUnit crudTimeUnit = original.get();
                DuplicateType result;
                if (defaultHandling == DuplicateType.UNDEFINED){
                    String originalString =
                            "Name: " + crudTimeUnit.getName() + "\n" +
                                    "Shortcut: " + crudTimeUnit.getShortcut() + "\n" +
                                    "Minutes: " + crudTimeUnit.getMinutes();
                    String duplicateString =
                            "Name: " + timeUnit.getName() + "\n" +
                                    "Shortcut: " + timeUnit.getShortcut() + "\n" +
                                    "Minutes: " + timeUnit.getMinutes();

                    DuplicateDialog dialog = new DuplicateDialog(parentFrame, "Time Unit", originalString, duplicateString);
                    result = dialog.getResult();
                    boolean setNewDefault = dialog.getDefaultHandling();
                    if (setNewDefault) {
                        defaultHandling = result;
                    }
                }
                else{
                    result = defaultHandling;
                }

                if (result == DuplicateType.OVERWRITE) {
                    crudTimeUnit.setName(timeUnit.getName());
                    crudTimeUnit.setMinutes(timeUnit.getMinutes());
                    crudTimeUnit.setShortcut(timeUnit.getShortcut());
                    changeImportedTimeUnitReferences(crudTimeUnit);
                }
                else if (result == DuplicateType.DUPLICATE) {
                    addDuplicateTimeUnit(timeUnit);
                }
                else{
                    changeImportedTimeUnitReferences(crudTimeUnit);
                }
            }
            else{
                //Non-meaningfully different, swap it with the already existing one
                changeImportedTimeUnitReferences(original.get());
            }
        }
    }

    private void handleDuplicateTemplates(Collection<Template> templates, JFrame parentFrame, DuplicateType defaultHandling) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        for (var template : templates) {
            Optional<Template> original = templateCrudService.findDuplicate(template);

            if (original.isEmpty()) {
                createTemplate(template);
            }
            else if (original.get().isMeaningfullyDifferent(template)) {
                Template crudTemplate = original.get();

                DuplicateType result;
                if (defaultHandling == DuplicateType.UNDEFINED){
                    String originalString =
                            "Name: " + crudTemplate.getName() + "\n" +
                                    "Details: " + crudTemplate.getDetails() + "\n" +
                                    "Start Time: " + crudTemplate.getStartTime().format(formatter) + "\n" +
                                    "Time Unit: " + crudTemplate.getInterval().getAmount() + " " +
                                    crudTemplate.getInterval().getTimeUnit().getName() + "\n" +
                                    "Categories: " + crudTemplate.getCategories().toString();

                    String duplicateString = "Name: " + template.getName() + "\n" +
                            "Details: " + template.getDetails() + "\n" +
                            "Start Time: " + template.getStartTime().format(formatter) + "\n" +
                            "Time Unit: " + template.getInterval().getAmount() + " " +
                            template.getInterval().getTimeUnit().getName() + "\n" +
                            "Categories: " + template.getCategories().toString();


                    DuplicateDialog dialog = new DuplicateDialog(parentFrame, "Template", originalString, duplicateString);
                    result = dialog.getResult();
                    boolean setNewDefault = dialog.getDefaultHandling();
                    if (setNewDefault) {
                        defaultHandling = result;
                    }
                }
                else{
                    result = defaultHandling;
                }


                if (result == DuplicateType.OVERWRITE) {
                    crudTemplate.setName(template.getName());
                    crudTemplate.setDetails(template.getDetails());
                    crudTemplate.setStartTime(template.getStartTime());
                    crudTemplate.setInterval(template.getInterval());
                    crudTemplate.setCategories(template.getCategories());
                }
                if (result == DuplicateType.DUPLICATE) {
                    addDuplicateTemplate(template);
                }
            }
        }
    }

    private void  handleDuplicateEvents(Collection<TodoEvent> events, JFrame parentFrame, DuplicateType defaultHandling){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy EEE");

        for (var event : events) {
            Optional<TodoEvent> original = todoEventFacade.findDuplicate(event);

            if (original.isEmpty()) {
                createEvent(event);
            }
            else if (original.get().isMeaningfullyDifferent(event)){
                TodoEvent crudEvent = original.get();

                DuplicateType result;
                if (defaultHandling == DuplicateType.UNDEFINED){
                    String originalLength = crudEvent.getInterval().getTimeUnit() == null ? "min" : crudEvent.getInterval().getTimeUnit().getShortcut();
                    String duplicateLength = event.getInterval().getTimeUnit() == null ? "min" : event.getInterval().getTimeUnit().getShortcut();

                    String originalString =
                            "Name: " + crudEvent.getName() + "\n" +
                                    "Details: " + crudEvent.getDetails() + "\n" +
                                    "Done Status: " + crudEvent.isDone() + "\n" +
                                    "Start Time: " + crudEvent.getStart().format(formatter) + "\n" +
                                    "Time Unit: " + crudEvent.getInterval().getAmount() + " " +
                                    originalLength + "\n" +
                                    "Categories: " + crudEvent.getCategories().toString();


                    String duplicateString = "Name: " + event.getName() + "\n" +
                            "Details: " + event.getDetails() + "\n" +
                            "Done Status: " + event.isDone() + "\n" +
                            "Start Time: " + event.getStart().format(formatter) + "\n" +
                            "Time Unit: " + event.getInterval().getAmount() + " " +
                            duplicateLength + "\n" +
                            "Categories: " + event.getCategories().toString();

                    DuplicateDialog dialog = new DuplicateDialog(parentFrame, "Event", originalString, duplicateString);
                    result = dialog.getResult();

                    boolean setNewDefault = dialog.getDefaultHandling();
                    if (setNewDefault) {
                        defaultHandling = result;
                    }
                }
                else{
                    result = defaultHandling;
                }


                if (result == DuplicateType.OVERWRITE) {
                    crudEvent.setName(event.getName());
                    crudEvent.setDetails(event.getDetails());
                    crudEvent.setStart(event.getStart());
                    crudEvent.setInterval(event.getInterval());
                    crudEvent.setCategories(event.getCategories());
                    crudEvent.setDone(event.isDone());
                }
                if (result == DuplicateType.DUPLICATE) {
                    System.out.println("calldin duplicate function with: " + event.getInterval().getTimeUnit().getId());
                    addDuplicateTodoEvent(event);
                }
            }
        }
    }

    //TODO: the 4 functions below could be templated provided we push some stuff into 'entity' base class
    private void addDuplicateCategory(Category category){
        int lastLeftIndex = category.getName().lastIndexOf("(");
        int lastRightIndex = category.getName().lastIndexOf(")");
        int number = 1;
        boolean isValidNumber = false;
        if (lastLeftIndex != -1 && lastRightIndex != -1 && lastRightIndex == category.getName().length() - 1) {
            isValidNumber = true;
            try{
                number = Integer.parseInt(category.getName().substring(lastLeftIndex+1, lastRightIndex));
                // Replace only the rightmost occurrence

            } catch (NumberFormatException e) {
                number = 1;
                isValidNumber = false;
            }
        }
        if (!isValidNumber){
            lastLeftIndex = category.getName().length();
        }

        while (true){
            category.setName(category.getName().substring(0, lastLeftIndex) + "(" + number + ")");
            Optional<Category> original = categoryCrudService.findDuplicate(category);
            if (original.isEmpty()) {
                createCategory(category);
                return;
            }
            number++;
        }
    }

    private void addDuplicateTimeUnit(TimeUnit timeUnit){
        int lastLeftIndex = timeUnit.getName().lastIndexOf("(");
        int lastRightIndex = timeUnit.getName().lastIndexOf(")");
        int number = 1;
        boolean isValidNumber = false;
        if (lastLeftIndex != -1 && lastRightIndex != -1 && lastRightIndex == timeUnit.getName().length() - 1) {
            isValidNumber = true;
            try{
                number = Integer.parseInt(timeUnit.getName().substring(lastLeftIndex+1, lastRightIndex));
                // Replace only the rightmost occurrence

            } catch (NumberFormatException e) {
                number = 1;
                isValidNumber = false;
            }
        }
        if (!isValidNumber){
            lastLeftIndex = timeUnit.getName().length();
        }

        while (true){
            timeUnit.setName(timeUnit.getName().substring(0, lastLeftIndex) + "(" + number + ")");
            Optional<TimeUnit> original = timeUnitCrudService.findDuplicate(timeUnit);
            if (original.isEmpty()) {
                createInterval(timeUnit);
                return;
            }
            number++;
        }
    }

    private void addDuplicateTemplate(Template template){
        int lastLeftIndex = template.getName().lastIndexOf("(");
        int lastRightIndex = template.getName().lastIndexOf(")");
        int number = 1;
        boolean isValidNumber = false;
        if (lastLeftIndex != -1 && lastRightIndex != -1 && lastRightIndex == template.getName().length() - 1) {
            isValidNumber = true;
            try{
                number = Integer.parseInt(template.getName().substring(lastLeftIndex+1, lastRightIndex));
                // Replace only the rightmost occurrence

            } catch (NumberFormatException e) {
                number = 1;
                isValidNumber = false;
            }
        }
        if (!isValidNumber){
            lastLeftIndex = template.getName().length();
        }

        while (true){
            template.setName(template.getName().substring(0, lastLeftIndex) + "(" + number + ")");
            Optional<Template> original = templateCrudService.findDuplicate(template);
            if (original.isEmpty()) {
                createTemplate(template);
                return;
            }
            number++;
        }
    }

    private void addDuplicateTodoEvent(TodoEvent event){
        int lastLeftIndex = event.getName().lastIndexOf("(");
        int lastRightIndex = event.getName().lastIndexOf(")");
        int number = 1;
        boolean isValidNumber = false;
        if (lastLeftIndex != -1 && lastRightIndex != -1 && lastRightIndex == event.getName().length() - 1) {
            isValidNumber = true;
            try{
                number = Integer.parseInt(event.getName().substring(lastLeftIndex+1, lastRightIndex));
                // Replace only the rightmost occurrence

            } catch (NumberFormatException e) {
                number = 1;
                isValidNumber = false;
            }
        }
        if (!isValidNumber){
            lastLeftIndex = event.getName().length();
        }


        System.out.println("Attempting to add: " + event.getName() + " with ref:" + event.getInterval().getTimeUnit().getId());
        while (true){
            event.setName(event.getName().substring(0, lastLeftIndex) + "(" + number + ")");
            Optional<TodoEvent> original = todoEventFacade.findDuplicate(event);
            if (original.isEmpty()) {
                createEvent(event);
                System.out.println("Added: " + event.getName() + " with ref:" + event.getInterval().getTimeUnit().getId());
                return;
            }
            number++;
        }
    }



    private void handleDuplicates(Batch batch, JFrame parentFrame, DuplicateType defaultHandling) {
        //TODO: templated function? but the different classes differ in multiple aspects

        importedBatch = batch;
        handleDuplicateCategories(importedBatch.categories(), parentFrame, defaultHandling);
        handleDuplicateTimeUnits(importedBatch.timeUnits(), parentFrame, defaultHandling);
        handleDuplicateTemplates(importedBatch.templates(), parentFrame, defaultHandling);
        handleDuplicateEvents(importedBatch.events(), parentFrame, defaultHandling);
    }
    @Override
    public boolean importData(String filePath, JFrame frame, DuplicateType defaultHandling) {
        importedBatchResult = getImporter(filePath).importBatch(filePath);

        if (importedBatchResult.isSuccess()) {
            handleDuplicates(importedBatchResult.getData(), frame, defaultHandling);
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public String getErrorMessage(){
        return importedBatchResult.getMessage();
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
        todoEventFacade.create(event);
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
