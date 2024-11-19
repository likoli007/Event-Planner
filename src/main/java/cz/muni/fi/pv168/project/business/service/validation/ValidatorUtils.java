package cz.muni.fi.pv168.project.business.service.validation;

import cz.muni.fi.pv168.project.model.*;
import cz.muni.fi.pv168.project.ui.model.AllTableModels;

import java.util.List;

public class ValidatorUtils {
    public static int parseIntOld(String fieldName, String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldName + "must be number, not \"" + input + "\".");
        }
    }

    public static void validateNonemptyStringOld(String fieldName, String input) {
        if (input.isEmpty()) {
            throw new ValidationException(fieldName + " must not be empty.");
        }
    }

    public static void validateCategoryListOld(List<Category> categoryList) {
        if (categoryList.isEmpty()) {
            throw new ValidationException("No categories selected.");
        }
    }

    public static int parseInt(ValidationResult validationResult, String fieldName, String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            validationResult.add(fieldName + "must be number, not \"" + input + "\".");
            return 0;
        }
    }

    public static void validateNonemptyString(ValidationResult validationResult, String fieldName, String input) {
        if (input.isEmpty()) {
            validationResult.add(fieldName + " must not be empty.");
        }
    }

    public static void validateCategoryList(ValidationResult validationResult, List<Category> categoryList) {
        if (categoryList.isEmpty()) {
            validationResult.add("No categories selected.");
        }
    }

    public static void validateAddEvent(AllTableModels allTableModels, TodoEvent newTodoEvent) {
        if (allTableModels.getEventTableModel().getTodoEventCrudService().findDuplicate(newTodoEvent).isPresent()) {
            throw new ValidationException("Event with given name for given date and time is already present.");
        }
    }

    public static void validateEditEvent(AllTableModels allTableModels, TodoEvent originalEvent, TodoEvent editedEvent) {
        var possibleDuplicate = allTableModels.getEventTableModel().getTodoEventCrudService().findDuplicate(editedEvent);
        // Name, date or time changed - the result would be a duplicate
        if (possibleDuplicate.isPresent() && !possibleDuplicate.get().isDuplicate(originalEvent)) {
            throw new ValidationException("Event with given name for given date and time is already present.");
        }
    }

    public static void validateAddTemplate(AllTableModels allTableModels, Template newTemplate) {
        if (allTableModels.getTemplateTableModel().getTemplateCrudService().findDuplicate(newTemplate).isPresent()) {
            throw new ValidationException("Template with given name is already present.");
        }
    }

    public static void validateEditTemplate(AllTableModels allTableModels, Template originalTemplate, Template editedTemplate) {
        var possibleDuplicate = allTableModels.getTemplateTableModel().getTemplateCrudService().findDuplicate(editedTemplate);
        if (possibleDuplicate.isPresent() && !possibleDuplicate.get().isDuplicate(originalTemplate)) {
            throw new ValidationException("Template with given name is already present.");
        }
    }

    public static void validateAddTimeUnit(AllTableModels allTableModels, TimeUnit newTimeUnit) {
        if (allTableModels.getTimeUnitTableModel().getTimeUnitCrudService().findDuplicate(newTimeUnit).isPresent()) {
            throw new ValidationException("Time unit with given name or shortcut is already present.");
        }
    }

    public static void validateEditTimeUnit(AllTableModels allTableModels, TimeUnit originalTimeUnit, TimeUnit editedTimeUnit) {
        var possibleDuplicate = allTableModels.getTimeUnitTableModel().getTimeUnitCrudService().findDuplicate(editedTimeUnit);
        if (possibleDuplicate.isPresent() && !possibleDuplicate.get().isDuplicate(originalTimeUnit)) {
            throw new ValidationException("Time unit with given name or shortcut is already present.");
        }
    }

    public static void validateTimeUnitDeletion(AllTableModels allTableModels, TimeUnit timeUnit) {
        for (TodoEvent todoEvent : allTableModels.getEventTableModel().getTodoEventCrudService().findAll()) {
            if (todoEvent.getInterval().getTimeUnit().isDuplicate(timeUnit)) {
                throw new ValidationException("Time unit with name \"" + timeUnit.getName()
                        + "\" cannot be deleted, it is used in event \"" + todoEvent.getName() + "\".");
            }
        }

        for (Template template : allTableModels.getTemplateTableModel().getTemplateCrudService().findAll()) {
            if (template.getInterval().getTimeUnit().isDuplicate(timeUnit)) {
                throw new ValidationException("Time unit with name \"" + timeUnit.getName()
                        + "\" cannot be deleted, it is used in template \"" + template.getName() + "\".");
            }
        }
    }
}
