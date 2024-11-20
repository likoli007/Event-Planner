package cz.muni.fi.pv168.project.business.service.validation;

import cz.muni.fi.pv168.project.model.Template;
import cz.muni.fi.pv168.project.ui.model.AllTableModels;

public class TemplateValidator implements Validator<Template> {
    @Override
    public ValidationResult validate(Template entity) {
        ValidationResult result = new ValidationResult();

        ValidatorUtils.validateNonNegativeInt(result, "Interval length", entity.getInterval().getAmount());
        ValidatorUtils.validateStringLength(result, "Template name", entity.getName(), ValidatorUtils.NAME_MAX_LENGTH, true);
        ValidatorUtils.validateStringLength(result, "Template description", entity.getName(), ValidatorUtils.DESCRIPTION_MAX_LENGTH, false);
        ValidatorUtils.validateCategoryList(result, entity.getCategories());

        return result;
    }

    @Override
    public ValidationResult validateAdd(AllTableModels allTableModels, Template newEntity) {
        ValidationResult result = validate(newEntity);

        if (allTableModels.getTemplateTableModel().getTemplateCrudService().findDuplicate(newEntity).isPresent()) {
            result.add("Template with given name is already present.");
        }

        return result;
    }

    @Override
    public ValidationResult validateEdit(AllTableModels allTableModels, Template originalEntity, Template editedEntity) {
        ValidationResult result = validate(editedEntity);

        var possibleDuplicate = allTableModels.getTemplateTableModel().getTemplateCrudService().findDuplicate(editedEntity);
        if (possibleDuplicate.isPresent() && !possibleDuplicate.get().isDuplicate(originalEntity)) {
            result.add("Template with given name is already present.");
        }

        return result;
    }

    @Override
    public ValidationResult validateDelete(AllTableModels allTableModels, Template entityToDelete) {
        return new ValidationResult();
    }
}
