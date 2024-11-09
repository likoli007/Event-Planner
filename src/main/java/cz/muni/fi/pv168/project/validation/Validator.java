package cz.muni.fi.pv168.project.validation;

import cz.muni.fi.pv168.project.model.Category;
import cz.muni.fi.pv168.project.model.TodoEvent;
import cz.muni.fi.pv168.project.ui.model.AllTableModels;

import java.util.List;

public class Validator {
    public static int parseInt(String fieldName, String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldName + "must be number, not \"" + input + "\".");
        }
    }

    public static void validateCategoryList(List<Category> categoryList) {
        if (categoryList.isEmpty()) {
            throw new ValidationException("No categories selected.");
        }
    }

    public static void validateAddEvent(AllTableModels allTableModels, TodoEvent todoEvent) {
        if (allTableModels.getEventTableModel().getTodoEventCrudService().findDuplicate(todoEvent).isPresent()) {
            throw new ValidationException("Event with given name for given date and time is already present.");
        }
    }

    public static void validateEditEvent(AllTableModels allTableModels, TodoEvent originalEvent, TodoEvent editedEvent) {
        var possibleDuplicate = allTableModels.getEventTableModel().getTodoEventCrudService().findDuplicate(editedEvent);
        // Name, date or time changed - the result would be a duplicate
        if (possibleDuplicate.isPresent() && !possibleDuplicate.get().equals(originalEvent)) {
            throw new ValidationException("Event with given name for given date and time is already present.");
        }
    }
}
