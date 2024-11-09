package cz.muni.fi.pv168.project.validation;

public class Validator {
    public static int parseInt(String fieldName, String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldName + "must be number, not \"" + input + "\".");
        }
    }
}
