package cz.muni.fi.pv168.project.business.service.validation;

import cz.muni.fi.pv168.project.ui.model.AllTableModels;

/**
 * Validator interface for validating operations with entities.
 *
 * @param <E> Entity type
 */
public interface Validator<E> {
    ValidationResult validate(E entity);
    ValidationResult validateAdd(AllTableModels allTableModels, E newEntity);
    ValidationResult validateEdit(AllTableModels allTableModels, E originalEntity, E editedEntity);
    ValidationResult validateDelete(E entityToDelete);
}
