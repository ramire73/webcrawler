package org.example.util.validation;

/**
 * Holds the result of a validation check, indicating whether the input is valid.
 * If not, a human-readable reason is displayed next to it.
 *
 * @param valid  True Valid / False Invalid
 * @param reason the failure reason.
 */
public record ValidationResult(boolean valid, String reason) {
    /**
     * Creates a result for an input that has passed every rule.
     */
    public static ValidationResult ok() {
        return new ValidationResult(true, "Valid");
    }

    /**
     * Creates a result for an input that broke a rule, and the reason explains which rule was broken.
     */
    public static ValidationResult fail(String reason) {
        return new ValidationResult(false, reason);
    }
}
