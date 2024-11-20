package cz.muni.fi.pv168.project.business.service.validation;

import cz.muni.fi.pv168.project.model.Template;
import cz.muni.fi.pv168.project.model.TimeUnit;
import cz.muni.fi.pv168.project.model.TodoEvent;
import cz.muni.fi.pv168.project.ui.model.AllTableModels;

public class TimeUnitValidator implements Validator<TimeUnit> {
    @Override
    public ValidationResult validate(TimeUnit entity) {
        ValidationResult result = new ValidationResult();

        ValidatorUtils.validateNonNegativeInt(result, "Time unit length", entity.getMinutes());
        ValidatorUtils.validateNonemptyString(result, "Time unit name", entity.getName());
        ValidatorUtils.validateNonemptyString(result, "Time unit shortcut", entity.getShortcut());

        return result;
    }

    @Override
    public ValidationResult validateAdd(AllTableModels allTableModels, TimeUnit newEntity) {
        ValidationResult result = validate(newEntity);

        if (allTableModels.getTimeUnitTableModel().getTimeUnitCrudService().findDuplicate(newEntity).isPresent()) {
            result.add("Time unit with given name or shortcut is already present.");
        }

        return result;
    }

    @Override
    public ValidationResult validateEdit(AllTableModels allTableModels, TimeUnit originalEntity, TimeUnit editedEntity) {
        ValidationResult result = validate(editedEntity);

        var possibleDuplicate = allTableModels.getTimeUnitTableModel().getTimeUnitCrudService().findDuplicate(editedEntity);
        if (possibleDuplicate.isPresent() && !possibleDuplicate.get().isDuplicate(originalEntity)) {
            result.add("Time unit with given name or shortcut is already present.");
        }

        return result;
    }

    @Override
    public ValidationResult validateDelete(AllTableModels allTableModels, TimeUnit entityToDelete) {
        ValidationResult result = new ValidationResult();

        for (TodoEvent todoEvent : allTableModels.getEventTableModel().getTodoEventCrudService().findAll()) {
            if (todoEvent.getInterval().getTimeUnit().isDuplicate(entityToDelete)) {
                result.add("Time unit with name \"" + entityToDelete.getName()
                        + "\" cannot be deleted, it is used in event \"" + todoEvent.getName() + "\".");
            }
        }

        for (Template template : allTableModels.getTemplateTableModel().getTemplateCrudService().findAll()) {
            if (template.getInterval().getTimeUnit().isDuplicate(entityToDelete)) {
                result.add("Time unit with name \"" + entityToDelete.getName()
                        + "\" cannot be deleted, it is used in template \"" + template.getName() + "\".");
            }
        }

        return result;
    }
}
