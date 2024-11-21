package cz.muni.fi.pv168.project.business.service.validation;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {
    private final List<String> validationErrors;

    public ValidationResult() {
        this.validationErrors = new ArrayList<>();
    }

    public void add(String message) {
        validationErrors.add(message);
    }

    public boolean isValid() {
        return validationErrors.isEmpty();
    }

    @Override
    public String toString() {
        if (isValid()) {
            return "Validation passed";
        }
        return "Validation has failed:\n" + String.join("\n", validationErrors);
    }

}
