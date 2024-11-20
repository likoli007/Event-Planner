package cz.muni.fi.pv168.project.business.service.validation;

import cz.muni.fi.pv168.project.model.*;

import java.util.List;

public class ValidatorUtils {
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
}
