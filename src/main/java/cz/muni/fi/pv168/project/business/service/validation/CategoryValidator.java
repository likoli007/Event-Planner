package cz.muni.fi.pv168.project.business.service.validation;

import cz.muni.fi.pv168.project.model.Category;
import cz.muni.fi.pv168.project.model.Template;
import cz.muni.fi.pv168.project.model.TodoEvent;
import cz.muni.fi.pv168.project.ui.model.AllTableModels;

public class CategoryValidator implements Validator<Category> {
    @Override
    public ValidationResult validate(Category entity) {
        ValidationResult result = new ValidationResult();

        ValidatorUtils.validateStringLength(result,"Category name", entity.getName(), ValidatorUtils.NAME_MAX_LENGTH, true);

        return result;
    }

    @Override
    public ValidationResult validateAdd(AllTableModels allTableModels, Category newEntity) {
        ValidationResult result = validate(newEntity);

        if (allTableModels.getCategoryTableModel().getCategoryCrudService().findDuplicate(newEntity).isPresent()) {
            result.add("Category with given name is already present.");
        }

        return result;
    }

    @Override
    public ValidationResult validateEdit(AllTableModels allTableModels, Category originalEntity, Category editedEntity) {
        ValidationResult result = validate(editedEntity);

        var possibleDuplicate = allTableModels.getCategoryTableModel().getCategoryCrudService().findDuplicate(editedEntity);
        if (possibleDuplicate.isPresent() && !possibleDuplicate.get().isDuplicate(originalEntity)) {
            result.add("Category with given name is already present.");
        }

        return result;
    }

    @Override
    public ValidationResult validateDelete(AllTableModels allTableModels, Category entityToDelete) {
        ValidationResult result = new ValidationResult();

        for (TodoEvent todoEvent : allTableModels.getEventTableModel().getTodoEventCrudService().findAll()) {
            for (Category todoEventCategory : todoEvent.getCategories()) {
                if (todoEventCategory.isDuplicate(entityToDelete)) {
                    result.add("Category with name \"" + entityToDelete.getName()
                            + "\" cannot be deleted, it is used in event \"" + todoEvent.getName() + "\".");
                }
            }
        }

        for (Template template : allTableModels.getTemplateTableModel().getTemplateCrudService().findAll()) {
            for (Category templateCategory : template.getCategories()) {
                if (templateCategory.isDuplicate(entityToDelete)) {
                    result.add("Category with name \"" + entityToDelete.getName()
                            + "\" cannot be deleted, it is used in template \"" + template.getName() + "\".");
                }
            }
        }

        return result;
    }
}
