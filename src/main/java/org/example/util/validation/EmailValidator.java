package org.example.util.validation;

import org.example.util.regex.RegexConstants;

import java.util.List;

/**
 * Checks email addresses against the Task 1 rules.
 */
public final class EmailValidator {

    // The leftmost one in the address is reported as broken when found.
    private static final List<ValidationRule> FORBIDDEN = List.of(
            // must 1
            ValidationRule.of("@[^@]*@", "Must contain exactly one '@' symbol"),
            // must 4
            ValidationRule.of("@-", "Domain starts with hyphen"),
            ValidationRule.of("@[^@]*-$", "Domain ends with hyphen"),
            // must 7
            ValidationRule.of("@\\.", "Domain starts with period"),
            ValidationRule.of("@[^@]*\\.$", "Domain ends with period"),
            // must not 1
            ValidationRule.of("\\.\\.", "Contains consecutive periods"),
            // must not 2
            ValidationRule.of("\\s", "Contains whitespace characters"),
            // must not 3
            ValidationRule.of("[^A-Za-z0-9._@-]", "Contains disallowed special characters"),
            // must not 4
            ValidationRule.of("@[^@]*?\\d", "Digits in domain"),
            // must not 5
            ValidationRule.of("^[.-]", "Local part cannot start or end with '.' or '-'"),
            ValidationRule.of("[.-]@", "Local part cannot start or end with '.' or '-'"),
            // must not 6
            ValidationRule.of("^.{41}", "Length must be between 5 and 40 characters")
    );

    // The first one in this list is reported as broken when missing.
    private static final List<ValidationRule> REQUIRED = List.of(
            // must not 6
            ValidationRule.of("^.{5}", "Length must be between 5 and 40 characters"),
            // must 1
            ValidationRule.of("@", "Must contain exactly one '@' symbol"),
            // must 2
            ValidationRule.of("^(?:[^A-Z]*[A-Z]){2}", "Must contain at least two uppercase letters"),
            // must 3
            ValidationRule.of("\\d[^@]*@", "Local part must contain at least one digit"),
            // must 4
            ValidationRule.of("@[^@]*-", "Domain must contain at least one hyphen '-'"),
            // must 5
            ValidationRule.of("_[^@]*@", "Local part must contain at least one underscore '_'"),
            // must 6
            ValidationRule.of("\\.[a-z]{3}$", "Domain suffix must be exactly 3 lowercase letters"),
            // must 7
            ValidationRule.of("@[^@]*\\.", "Domain must contain a period '.'")
    );

    private EmailValidator() {
    }

    /**
     * Task 1: Email Validation
     * <p>
     * Validates an email address and provides a specific failure reason if invalid.
     * Each rule is checked one at a time using a small regex. The leftmost forbidden text is reported first, then the
     * first missing requirement. If every check passes, the email is matched against the EMAIL_VALIDATION_PATTERN,
     * which contains all the rules in one regular expression, as a final safety check.
     *
     * @param email the email to validate.
     * @return a {@link ValidationResult}.
     */
    public static ValidationResult validate(String email) {
        if (email == null) {
            return ValidationResult.fail("Email is null");
        }
        return ValidationRule.check(email, FORBIDDEN, REQUIRED, RegexConstants.EMAIL_VALIDATION_PATTERN,
                "Does not match required email structure");
    }
}
