package cz.muni.fi.pv168.project.business.service.validation;

import cz.muni.fi.pv168.project.model.TodoEvent;
import cz.muni.fi.pv168.project.ui.model.AllTableModels;

public class TodoEventValidator implements Validator<TodoEvent> {
    @Override
    public ValidationResult validate(TodoEvent entity) {
        ValidationResult result = new ValidationResult();

        ValidatorUtils.validateNonemptyString(result,"Event name", entity.getName());
        ValidatorUtils.validateCategoryList(result, entity.getCategories());

        return result;
    }

    @Override
    public ValidationResult validateAdd(AllTableModels allTableModels, TodoEvent newEntity) {
        ValidationResult result = validate(newEntity);

        if (allTableModels.getEventTableModel().getTodoEventCrudService().findDuplicate(newEntity).isPresent()) {
            result.add("Event with given name for given date and time is already present.");
        }

        return result;
    }

    @Override
    public ValidationResult validateEdit(AllTableModels allTableModels, TodoEvent originalEntity, TodoEvent editedEntity) {
        ValidationResult result = validate(editedEntity);

        var possibleDuplicate = allTableModels.getEventTableModel().getTodoEventCrudService().findDuplicate(editedEntity);
        if (possibleDuplicate.isPresent() && !possibleDuplicate.get().isDuplicate(originalEntity)) {
            result.add("Event with given name for given date and time is already present.");
        }

        return result;
    }

    @Override
    public ValidationResult validateDelete(AllTableModels allTableModels, TodoEvent entityToDelete) {
        return new ValidationResult();
    }
}
