package org.example.util.validation;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Applies the validation rules using a pattern, and the reason to report when the rule is broken.
 * A forbidden rule is broken when its pattern is found in the input.
 * A required rule is broken when its pattern is not found.
 *
 * @param pattern the regex pattern.
 * @param reason  the failure reason.
 */
record ValidationRule(Pattern pattern, String reason) {

    static ValidationRule of(String regex, String reason) {
        return new ValidationRule(Pattern.compile(regex), reason);
    }

    /**
     * Reports one reason, chosen in this order:
     * 1. The forbidden rule that is broken first when reading from left to right.
     * 2. The first required rule that is broken, in list order.
     * 3. The full reason, if the whole input does not match the full pattern.
     *
     * @param input       the string input to check.
     * @param forbidden   the rules whose pattern must not be found.
     * @param required    the rules whose pattern must be found.
     * @param fullPattern the full pattern to use.
     * @param fullReason  the full failure reason.
     */
    static ValidationResult check(
            String input,
            List<ValidationRule> forbidden,
            List<ValidationRule> required,
            Pattern fullPattern,
            String fullReason
    ) {
        // The end of a match is where a left-to-right reader notices the problem.
        // On a tie, the rule listed first is kept.
        String leftmostReason = null;
        int leftmostEnd = Integer.MAX_VALUE;
        for (ValidationRule rule : forbidden) {
            Matcher matcher = rule.pattern().matcher(input);
            if (matcher.find() && matcher.end() < leftmostEnd) {
                // A %s in the reason is replaced by the text captured by group 1.
                leftmostReason = rule.reason().contains("%s")
                        ? rule.reason().replace("%s", matcher.group(1))
                        : rule.reason();
                leftmostEnd = matcher.end();
            }
        }
        if (leftmostReason != null) {
            return ValidationResult.fail(leftmostReason);
        }

        for (ValidationRule rule : required) {
            if (!rule.pattern().matcher(input).find()) {
                return ValidationResult.fail(rule.reason());
            }
        }

        return fullPattern.matcher(input).matches()
                ? ValidationResult.ok()
                : ValidationResult.fail(fullReason);
    }
}
