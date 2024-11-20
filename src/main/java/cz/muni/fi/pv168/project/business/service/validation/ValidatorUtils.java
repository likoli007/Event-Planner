package cz.muni.fi.pv168.project.business.service.validation;

import cz.muni.fi.pv168.project.model.*;

import java.util.List;

public class ValidatorUtils {
    static int DESCRIPTION_MAX_LENGTH = 36;
    static int NAME_MAX_LENGTH = 25;
    static int SHORTCUT_MAX_LENGTH = 4;

    /**
     * @deprecated use {@link #validateNonemptyString(ValidationResult, String, String)} instead.
     */
    public static void validateNonemptyStringOld(String fieldName, String input) {
        if (input.isEmpty()) {
            throw new ValidationException(fieldName + " must not be empty.");
        }
    }

    /**
     * @deprecated use {@link #validateCategoryList(ValidationResult, List)} instead.
     */
    public static void validateCategoryListOld(List<Category> categoryList) {
        if (categoryList.isEmpty()) {
            throw new ValidationException("No categories selected.");
        }
    }

    public static void validateNonNegativeInt(ValidationResult validationResult, String fieldName, int input) {
        if (input < 0) {
            validationResult.add(fieldName + " must not be negative.");
        }
    }

    public static void validateNonemptyString(ValidationResult validationResult, String fieldName, String input) {
        if (input.isEmpty()) {
            validationResult.add(fieldName + " must not be empty.");
        }
    }

    public static void validateStringLength(ValidationResult validationResult, String fieldName, String input,
                                            int maxLength, boolean required) {
        if (required) {
            validateNonemptyString(validationResult, fieldName, input);
        }

        if (input.length() > maxLength) {
            validationResult.add(fieldName + " must not be longer than " + maxLength + " characters.");
        }
    }

    public static void validateCategoryList(ValidationResult validationResult, List<Category> categoryList) {
        if (categoryList.isEmpty()) {
            validationResult.add("No categories selected.");
        }
    }
}
