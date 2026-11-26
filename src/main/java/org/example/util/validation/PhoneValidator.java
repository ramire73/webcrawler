package org.example.util.validation;

import org.example.util.regex.RegexConstants;

import java.util.List;

/**
 * Checks phone numbers against the Task 2 rules.
 */
public final class PhoneValidator {

    // Skips a leading 1 or +1, but only when 10 more digits follow it.
    private static final String COUNTRY_CODE = "^(?:\\+?1(?=(?:\\D*\\d){10}\\D*$))?+";

    // The leftmost one in the number is reported when found broken.
    private static final List<ValidationRule> FORBIDDEN = List.of(
            // must not 5
            ValidationRule.of("^\\s", "Contains leading or trailing whitespace"),
            ValidationRule.of("\\s$", "Contains leading or trailing whitespace"),
            // must not 1
            ValidationRule.of("[A-Za-z]", "Contains letters"),
            // must not 2
            ValidationRule.of("[^A-Za-z0-9()+ -]", "Contains disallowed special characters"),
            // must not 3
            ValidationRule.of("\\([^(]*\\(", "More than one pair of parentheses"),
            ValidationRule.of("\\)[^)]*\\)", "More than one pair of parentheses"),
            // must not 4
            ValidationRule.of("--", "Contains consecutive hyphens"),
            ValidationRule.of(" {2}", "Contains consecutive spaces"),
            // must not 6: a + that is not followed by 1, or an 11-digit number that does not start with 1
            ValidationRule.of("^\\+(?!1)(\\d{1,3})(?!\\d)", "Starts with country code +%s – not a Canadian number"),
            ValidationRule.of("^\\+(?!1|\\d{1,3}(?!\\d))", "Invalid country code (only +1 or 1 is allowed)"),
            ValidationRule.of("^[02-9](?=(?:\\D*\\d){10}\\D*$)", "Invalid country code (only +1 or 1 is allowed)")
    );

    // The first one in this list is reported as broken when missing.
    private static final List<ValidationRule> REQUIRED = List.of(
            // must 1
            ValidationRule.of(COUNTRY_CODE + "(?:\\D*\\d){10}\\D*$", "Must contain exactly 10 digits"),
            // must 2
            ValidationRule.of(COUNTRY_CODE + "\\D*[2-9]", "Area code must start with a digit from 2-9"),
            // must 3
            ValidationRule.of("[ ()-]", "Missing separators and parentheses"),
            ValidationRule.of("[ -]", "Missing space or hyphen separator"),
            // must 4
            ValidationRule.of(COUNTRY_CODE + "\\D*(?:\\d\\D*){3}[2-9]", "Central office code cannot start with 0 or 1"),
            // must 5
            ValidationRule.of("^(?:.{12}|.{14})$", "Total length must be 12 or 14 characters"),
            // must not 3
            ValidationRule.of("^[^()]*(?:\\([^()]*\\)[^()]*)?$", "Unmatched parentheses")
    );

    private PhoneValidator() {
    }

    /**
     * Task 2: Phone Number Validation
     * <p>
     * Validates a phone number and provides a specific failure reason if invalid.
     * Each rule is checked one at a time using a small regex. The leftmost forbidden text is reported first, then the
     * first missing requirement. If every check passes, the phone number is matched against the PHONE_VALIDATION_PATTERN,
     * which contains all the rules in one regular expression, as a final safety check.
     *
     * @param phoneNumber the phone number to validate.
     * @return a {@link ValidationResult}.
     */
    public static ValidationResult validate(String phoneNumber) {
        if (phoneNumber == null) {
            return ValidationResult.fail("Phone number is null");
        }
        return ValidationRule.check(phoneNumber, FORBIDDEN, REQUIRED, RegexConstants.PHONE_VALIDATION_PATTERN,
                "Does not match valid NANP phone formatting");
    }
}
