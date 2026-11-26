package org.example.util.regex;

import java.util.regex.Pattern;

public abstract class RegexConstants {

    /**
     * Task 1: Email Validation
     * The email address must contain:
     * 1. Exactly one @ symbol.
     * 2. At least two uppercase letters anywhere in the address.
     * 3. At least one numeric digit in the local part (before @).
     * 4. At least one hyphen - in the domain part (after @), but the hyphen cannot be the first or last character of the domain.
     * 5. At least one underscore _ in the local part (before @).
     * 6. A domain suffix that contains exactly 3 lowercase alphabetic characters.
     * 7. A period . in the domain part, but not at the start, not immediately after @, and not at the end.
     * The email address must not contain:
     * 1. Consecutive periods .. anywhere in the address.
     * 2. Any whitespace characters.
     * 3. Any special characters other than “.”, “-”, “_” and “@”.
     * 4. Any digits in the domain part (after @).
     * 5. The local part starting or ending with a hyphen - or period “.”.
     * 6. The entire email exceeding 40 characters or being shorter than 5 characters.
     */
    public static final Pattern EMAIL_VALIDATION_PATTERN = Pattern.compile(
            // Start of the string.
            "^" +
            // Must 1: Exactly one @ symbol.
            "(?=[^@]*@[^@]*$)" +
            // Must 2: At least two uppercase letters anywhere.
            "(?=(?:[^A-Z]*[A-Z]){2})" +
            // Must 3: At least one digit in the local part (before @).
            "(?=[^@]*\\d[^@]*@)" +
            // Must 4: At least one hyphen - in the domain part (after @), but the hyphen cannot be the first or last character of the domain.
            "(?=[^@]*@(?!-)[A-Za-z-]+(?:\\.[A-Za-z-]+)*-[A-Za-z-]+(?:\\.[A-Za-z-]+)*(?<!-)\\.[a-z]{3}$)" +
            // Must 5: At least one underscore _ in the local part (before @).
            "(?=[^@]*_[^@]*@)" +
            // Must 6: A domain suffix that contains exactly 3 lowercase alphabetic characters.
            "(?=.*\\.[a-z]{3}$)" +
            // Must 7: A period . in the domain part, but not at the start, not immediately after @, and not at the end.
            "(?![^@]*@\\.)" +
            // Must not 1: Consecutive periods .. anywhere in the address.
            "(?!.*\\.\\.)" +
            // Must not 2: Any whitespace characters.
            "(?!.*\\s)" +
            // Must not 3: Any special characters other than “.”, “-”, “_” and “@”.
            "(?=[A-Za-z0-9._@-]+$)" +
            // Must not 4: Any digits in the domain part (after @).
            "(?![^@]*@.*\\d)" +
            // Must not 5: The local part starting or ending with a hyphen - or period “.”.
            "(?![.-])" +
            "(?![^@]*[.-]@)" +
            // Must not 6: The entire email exceeding 40 characters or being shorter than 5 characters.
            "(?=.{5,40}$)" +
            // All rules passed: Match the local part, @, the domain labels and the suffix.
            "[A-Za-z0-9._-]+" +
            "@" +
            "[A-Za-z-]+(?:\\.[A-Za-z-]+)*" +
            "\\.[a-z]{3}" +
            // End of the string.
            "$"
    );

    /**
     * Task 2: Phone Number Validation
     * The phone number must contain:
     * 1. Exactly 10 digits, optionally separated by spaces, hyphens (-), or enclosed in parentheses () in the area code.
     * 2. Area code (first 3 digits) must start with a digit between 2-9 (as per NANP Canadian rules).
     * 3. The phone number must contain at least one space or one hyphen separator.
     * 4. The central office code (second set of 3 digits) must not start with 0 or 1.
     * 5. The total length, including separators and parentheses, must be exactly 14 or 12 characters.
     * The phone number must not contain:
     * 1. Any alphabetic characters (letters).
     * 2. Any special characters except ( ), space , hyphen -.
     * 3. More than one pair of parentheses.
     * 4. Consecutive hyphens (--) or spaces.
     * 5. Any leading or trailing whitespace.
     * 6. Any phone number starting with a different country code (only +1 or 1 at the beginning are allowed).
     */
    public static final Pattern PHONE_VALIDATION_PATTERN = Pattern.compile(
            // Start of the string.
            "^" +
            // Must 1: Exactly 10 digits, optionally separated by spaces, hyphens (-), or enclosed in parentheses () in the area code.
            "(?=(?:\\+?1[ -])?(?:\\D*\\d){10}\\D*$)" +
            // Must 2: Area code (first 3 digits) must start with a digit between 2-9 (as per NANP Canadian rules).
            "(?=(?:\\+?1[ -])?\\(?[2-9]\\d{2})" +
            // Must 3: The phone number must contain at least one space or one hyphen separator.
            "(?=.*[ -])" +
            // Must 4: The central office code (second set of 3 digits) must not start with 0 or 1.
            "(?=(?:\\+?1[ -])?(?:\\(\\d{3}\\) |\\d{3}[ -])[2-9])" +
            // Must 5: The total length, including separators and parentheses, must be exactly 14 or 12 characters.
            "(?=(?:.{12}|.{14})$)" +
            // Must not 1: Any alphabetic characters (letters).
            "(?!.*[A-Za-z])" +
            // Must not 2: Any special characters except ( ), space , hyphen -.
            "(?=[0-9() +-]+$)" +
            // Must not 3: More than one pair of parentheses.
            "(?!.*\\(.*\\()" +
            "(?!.*\\).*\\))" +
            // Must not 4: Consecutive hyphens (--) or spaces.
            "(?!.*(?:-{2}| {2}))" +
            // Must not 5: Any leading or trailing whitespace.
            "(?!\\s)" +
            "(?!.*\\s$)" +
            // Must not 6: Any phone number starting with a different country code (only +1 or 1 at the beginning are allowed).
            "(?=(?:\\+?1[ -])?[(2-9])" +
            // All rules passed: Match the optional country code, area code, central office code, separator and line number.
            "(?:\\+?1[ -])?" +
            "(?:\\([2-9]\\d{2}\\) |[2-9]\\d{2}[ -])" +
            "[2-9]\\d{2}" +
            "[ -]" +
            "\\d{4}" +
            // End of the string.
            "$"
    );

    // Dates in YYYY-MM-DD format (with month 01-12 and day 01-31 bounds).
    public static final Pattern DATE_EXTRACTION_PATTERN = Pattern.compile(
            // Word boundary: Date must not be glued to other digits/letters.
            "\\b" +
            // YYYY: Exactly four digits for the year.
            "\\d{4}" +
            // Literal hyphen separator.
            "-" +
            // MM: 01-09 or 10-12 (rejects 00 and 13+).
            "(?:0[1-9]|1[0-2])" +
            // Literal hyphen separator.
            "-" +
            // DD: 01-09, 10-29 or 30-31 (rejects 00 and 32+).
            "(?:0[1-9]|[12]\\d|3[01])" +
            // Word boundary at the end.
            "\\b"
    );

    // URLs (HTTP/HTTPS).
    public static final Pattern URL_EXTRACTION_PATTERN = Pattern.compile(
            // Scheme: "http://" or "https://" (the s is optional).
            "\\bhttps?://" +
            // Zero or more sub-domain labels followed by a dot, e.g. "www." "cs.".
            "(?:[A-Za-z0-9-]+\\.)*" +
            // Last label of the host name (e.g. "com", "ca").
            "[A-Za-z0-9-]+" +
            // Optional port number, e.g. ":8080".
            "(?::\\d{1,5})?" +
            // Optional path/query/fragment: starts with / ? or #, then any characters except whitespace, < > and quotes.
            "(?:[/?#][^\\s<>\"']*)?" +
            // The URL must not END with sentence punctuation; this drops a trailing "." "," ")" etc. that belongs to the sentence.
            "(?<![.,;:!?)\\]'\"])"
    );

    // Phone numbers in text (common North American formats).
    public static final Pattern PHONE_EXTRACTION_PATTERN = Pattern.compile(
            // Must not be preceded by a letter/digit/. + - (so we do not grab the middle of a longer number or code).
            "(?<![\\w.+-])" +
            // Optional country code: "1" or "+1", optionally followed by - . or space.
            "(?:\\+?1[-. ]?)?" +
            // One of three formats:
            "(?:" +
            //   1) (416) 823-7784  - area code in parentheses.
            "\\(\\d{3}\\) ?\\d{3}[-. ]\\d{4}" +
            //   2) 647-392-4482 / 647.392.4482 / 647 392 4482;
            //      group 1 captures the separator and \\1 forces the
            //      SAME separator to be used both times.
            "|\\d{3}([-. ])\\d{3}\\1\\d{4}" +
            //   3) 4165557890 - ten digits with no separators.
            "|\\d{10}" +
            ")" +
            // Must not be followed by a letter/digit, or by . , - and a digit (prevents matching only part of a longer number).
            "(?!\\w|[.,-]\\d)"
    );

    // Email addresses in text
    public static final Pattern EMAIL_EXTRACTION_PATTERN = Pattern.compile(
            // Must not be preceded by a character that can be part of an email,
            //   so the match starts at the real beginning of the address
            "(?<![\\w.%+-])" +
            // Local part: letters, digits and . _ % + -
            "[A-Za-z0-9._%+-]+" +
            // Exactly one @ symbol.
            "@" +
            // One or more domain labels each followed by a dot, e.g. "uwindsor.".
            "(?:[A-Za-z0-9-]+\\.)+" +
            // Top-level domain: at least 2 letters, e.g. "ca", "com".
            "[A-Za-z]{2,}" +
            // Must not be followed by a letter/digit/hyphen (whole address only).
            "(?![\\w-])"
    );

    // Prices in text (e.g., $12.34, $1,234.56, $1234.56, 12.34 CAD, 1234.56 CAD)
    // The amount is either a number with thousands commas (1,234) or a plain
    // run of digits (1234). Trying the comma form first means "$1,234.56" is
    // matched whole, and the plain form stops "$1234.56" from being cut to "$123".
    public static final Pattern PRICE_EXTRACTION_PATTERN = Pattern.compile(
            // Case-insensitive, so "cad" and "CAD" are both accepted.
            "(?i)" +
            "(?:" +
            // Form A: currency symbol BEFORE the amount, e.g. "$12.34", "$ 1,234.56" currency symbol.
            "[$]" +
            // Optional spaces between symbol and amount.
            "\\s*" +
            // Amount: 1,234,567 (with commas) OR 1234567 (plain digits).
            "(?:\\d{1,3}(?:,\\d{3})+|\\d+)" +
            // Optional cents: a dot and 1-2 digits.
            "(?:\\.\\d{1,2})?" +
            "|" +
            // Form B: currency code AFTER the amount, e.g. "12.34 CAD", "1,234 CAD"
            // amount must start at a word boundary
            "\\b" +
            // Amount: with commas OR plain digits.
            "(?:\\d{1,3}(?:,\\d{3})+|\\d+)" +
            // Optional cents.
            "(?:\\.\\d{1,2})?" +
            // Optional spaces before the currency code.
            "\\s*" +
            // Supported currency codes.
            "(?:CAD)" +
            // Code must end at a word boundary (so "CADET" is not matched).
            "\\b" +
            ")"
    );
}