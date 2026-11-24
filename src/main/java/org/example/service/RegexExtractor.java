package org.example.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class RegexExtractor {

    // =========================================================================
    // Extraction Patterns (Tasks 3 - 7)
    // =========================================================================

    // Task 3: Dates in YYYY-MM-DD format (with month 01-12 and day 01-31 bounds)
    private static final Pattern DATE_PATTERN = Pattern.compile(
            "\\b\\d{4}-(?:0[1-9]|1[0-2])-(?:0[1-9]|[12]\\d|3[01])\\b"
    );

    // Task 4: URLs (HTTP/HTTPS)
    private static final Pattern URL_PATTERN = Pattern.compile(
            "\\bhttps?://(?:[A-Za-z0-9-]+\\.)*[A-Za-z0-9-]+(?::\\d{1,5})?(?:[/?#][^\\s<>\"']*)?(?<![.,;:!?)\\]'\"])"
    );

    // Task 5: Phone numbers in text (common North American formats)
    private static final Pattern PHONE_EXTRACTION_PATTERN = Pattern.compile(
            "(?<![\\w.+-])(?:\\+?1[-. ]?)?(?:\\(\\d{3}\\) ?\\d{3}[-. ]\\d{4}|\\d{3}([-. ])\\d{3}\\1\\d{4}|\\d{10})(?!\\w|[.,-]\\d)"
    );

    // Task 6: Email addresses in text
    private static final Pattern EMAIL_EXTRACTION_PATTERN = Pattern.compile(
            "(?<![\\w.%+-])[A-Za-z0-9._%+-]+@(?:[A-Za-z0-9-]+\\.)+[A-Za-z]{2,}(?![\\w-])"
    );

    // Task 7: Prices in text (e.g., $12.34, $1,234.56, 12.34 CAD, USD 9.99)
    private static final Pattern PRICE_PATTERN = Pattern.compile(
            "(?i)(?:[$€£¥]\\s*\\d{1,3}(?:,\\d{3})*(?:\\.\\d{1,2})?|\\b\\d{1,3}(?:,\\d{3})*(?:\\.\\d{1,2})?\\s*(?:CAD|USD|EUR|GBP)\\b)"
    );

    // =========================================================================
    // Strict Validation Patterns (Tasks 1 - 2)
    // =========================================================================

    // Task 1: Strict email validation pattern
    // - Total length 5..40, no whitespace, no consecutive dots
    // - At least 2 uppercase letters anywhere
    // - Local part: >= 1 digit, >= 1 '_', cannot start/end with '.' or '-'
    // - Domain part: no digits, >= 1 '-', no leading/trailing '.' or '-', and 3 lowercase TLD
    private static final Pattern STRICT_EMAIL_VALIDATION_PATTERN = Pattern.compile(
            "^(?=.{5,40}$)" +
                    "(?!.*\\s)" +
                    "(?!.*\\.\\.)" +
                    "(?=(?:[^A-Z]*[A-Z]){2})" +
                    "(?=[A-Za-z0-9._-]*\\d[^@]*@)" +
                    "(?=[A-Za-z0-9._-]*_[^@]*@)" +
                    "(?![.-])" +
                    "[A-Za-z0-9._-]+(?<![.-])" +
                    "@" +
                    "(?=[A-Za-z-]+(?:\\.[A-Za-z-]+)*-[A-Za-z-]+(?:\\.[A-Za-z-]+)*\\.[a-z]{3}$)" +
                    "(?!\\.)" +
                    "(?!-)" +
                    "[A-Za-z-]+(?:\\.[A-Za-z-]+)*(?<!-)" +
                    "\\.[a-z]{3}$"
    );

    // Task 2: Strict NANP Canadian phone number validation pattern
    // - Total length 12 or 14 characters (including separators and parentheses)
    // - Optional leading +1 or 1 with space/hyphen separator, no '--' or '  ', no leading/trailing space/hyphen
    // - At least one space or hyphen separator; area code and central office code start with 2-9
    private static final Pattern STRICT_PHONE_VALIDATION_PATTERN = Pattern.compile(
            "^(?=(?:.{12}|.{14})$)" +
                    "(?!.*--)" +
                    "(?!.*  )" +
                    "(?![\\s-])(?![\\s-]$)" +
                    "(?=.*[ -])" +
                    "(?:\\+?1[ -])?" +
                    "(?:\\([2-9]\\d{2}\\) |[2-9]\\d{2}[ -])" +
                    "[2-9]\\d{2}[ -]\\d{4}$"
    );

    public record ValidationResult(boolean valid, String reason) {
        public static ValidationResult ok() {
            return new ValidationResult(true, "Valid");
        }

        public static ValidationResult fail(String reason) {
            return new ValidationResult(false, reason);
        }
    }

    public static void main(String[] args) {
        // Sample checks for Task 1 & Task 2
        List<String> sampleEmails = List.of(
                "Ex_Ample9@my-domain.com",
                "TEst_User1@hyphen-site.net",
                "user..name@domain.com",
                "username@domain123.com",
                "bad_email@domain.org"
        );
        printEmailValidationReport("Task 1: Sample Email Validation", sampleEmails);

        System.out.println();

        List<String> samplePhones = List.of(
                "(416) 823-7784",
                "647-392-4482",
                "905 625 2324",
                "+93-647-333-2211",
                "289--453-2284",
                "4165557890"
        );
        printPhoneValidationReport("Task 2: Sample Phone Validation", samplePhones);

        // Overall accumulators across all files in ./ua_all_text_pages
        List<String> allValidEmails = new ArrayList<>();
        List<String> allInvalidEmails = new ArrayList<>();
        List<String> allValidPhones = new ArrayList<>();
        List<String> allInvalidPhones = new ArrayList<>();
        List<String> allDates = new ArrayList<>();
        List<String> allUrls = new ArrayList<>();
        List<String> allPrices = new ArrayList<>();

        Path textFilesDir = Paths.get("./ua_all_text_pages");
        System.out.println("\nStarting regex extraction from text files...");

        try {
            if (!Files.exists(textFilesDir)) {
                System.err.println("The directory './ua_all_text_pages' does not exist.");
                return;
            }

            try (Stream<Path> paths = Files.list(textFilesDir)) {
                paths.filter(Files::isRegularFile)
                        .filter(path -> path.toString().toLowerCase().endsWith(".txt"))
                        .forEach(filePath -> processFile(
                                filePath,
                                allValidEmails,
                                allInvalidEmails,
                                allValidPhones,
                                allInvalidPhones,
                                allDates,
                                allUrls,
                                allPrices
                        ));
            }

            printOverallSummary(
                    allValidEmails,
                    allInvalidEmails,
                    allValidPhones,
                    allInvalidPhones,
                    allDates,
                    allUrls,
                    allPrices
            );

            System.out.println("\nExtraction Complete!");

        } catch (IOException e) {
            System.err.println("An error occurred reading the directory: " + e.getMessage());
        }
    }

    // =========================================================================
    // Validation Methods (Tasks 1 - 2)
    // =========================================================================

    /**
     * Task 1: Validates an email address and provides a specific failure reason if invalid.
     */
    public static ValidationResult validateEmail(String email) {
        if (email == null) {
            return ValidationResult.fail("Email is null");
        }
        if (email.length() < 5 || email.length() > 40) {
            return ValidationResult.fail("Length must be between 5 and 40 characters");
        }
        if (email.matches(".*\\s.*")) {
            return ValidationResult.fail("Contains whitespace characters");
        }
        if (email.contains("..")) {
            return ValidationResult.fail("Contains consecutive periods");
        }
        if (!email.matches("^[A-Za-z0-9._@-]+$")) {
            return ValidationResult.fail("Contains disallowed special characters");
        }

        int atIndex = email.indexOf('@');
        if (atIndex == -1 || atIndex != email.lastIndexOf('@')) {
            return ValidationResult.fail("Must contain exactly one '@' symbol");
        }

        String localPart = email.substring(0, atIndex);
        String domainPart = email.substring(atIndex + 1);

        if (localPart.isEmpty() || domainPart.isEmpty()) {
            return ValidationResult.fail("Local part or domain part is empty");
        }
        if (localPart.startsWith(".") || localPart.startsWith("-")
                || localPart.endsWith(".") || localPart.endsWith("-")) {
            return ValidationResult.fail("Local part cannot start or end with '.' or '-'");
        }
        if (!localPart.matches(".*\\d.*")) {
            return ValidationResult.fail("Local part must contain at least one digit");
        }
        if (!localPart.contains("_")) {
            return ValidationResult.fail("Local part must contain at least one underscore '_'");
        }

        long uppercaseCount = email.chars().filter(Character::isUpperCase).count();
        if (uppercaseCount < 2) {
            return ValidationResult.fail("Must contain at least two uppercase letters");
        }

        if (domainPart.matches(".*\\d.*")) {
            return ValidationResult.fail("Digits in domain");
        }
        if (domainPart.startsWith(".") || domainPart.endsWith(".") || domainPart.startsWith("-")) {
            return ValidationResult.fail("Domain cannot start/end with '.' or start with '-'");
        }
        if (domainPart.endsWith("-")) {
            return ValidationResult.fail("Domain cannot end with '-'");
        }
        if (!domainPart.contains(".")) {
            return ValidationResult.fail("Domain must contain a period '.'");
        }
        if (!domainPart.contains("-")) {
            return ValidationResult.fail("Domain must contain at least one hyphen '-'");
        }
        if (!domainPart.matches(".*\\.[a-z]{3}$")) {
            return ValidationResult.fail("Domain suffix must be exactly 3 lowercase letters");
        }
        if (!STRICT_EMAIL_VALIDATION_PATTERN.matcher(email).matches()) {
            return ValidationResult.fail("Does not match required email structure");
        }

        return ValidationResult.ok();
    }

    /**
     * Task 2: Validates a phone number and provides a specific failure reason if invalid.
     */
    public static ValidationResult validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return ValidationResult.fail("Phone number is null");
        }
        if (phoneNumber.startsWith(" ") || phoneNumber.endsWith(" ")) {
            return ValidationResult.fail("Contains leading or trailing whitespace");
        }
        if (!phoneNumber.matches("^[0-9()+\\- ]+$")) {
            return ValidationResult.fail("Contains letters or disallowed special characters");
        }
        if (phoneNumber.contains("--")) {
            return ValidationResult.fail("Contains consecutive hyphens");
        }
        if (phoneNumber.contains("  ")) {
            return ValidationResult.fail("Contains consecutive spaces");
        }

        long openParens = phoneNumber.chars().filter(ch -> ch == '(').count();
        long closeParens = phoneNumber.chars().filter(ch -> ch == ')').count();
        if (openParens > 1 || closeParens > 1) {
            return ValidationResult.fail("More than one pair of parentheses");
        }
        if (openParens != closeParens) {
            return ValidationResult.fail("Unmatched parentheses");
        }

        String digitsOnly = phoneNumber.replaceAll("\\D", "");
        if (phoneNumber.startsWith("+")) {
            if (!phoneNumber.startsWith("+1 ") && !phoneNumber.startsWith("+1-")) {
                return ValidationResult.fail("Invalid country code (only +1 or 1 is allowed)");
            }
        } else if (digitsOnly.length() == 11 && !digitsOnly.startsWith("1")) {
            return ValidationResult.fail("Invalid country code (only +1 or 1 is allowed)");
        }

        if (digitsOnly.length() != 10) {
            return ValidationResult.fail("Must contain exactly 10 digits (found " + digitsOnly.length() + ")");
        }
        if (!phoneNumber.contains(" ") && !phoneNumber.contains("-")) {
            return ValidationResult.fail("Missing space or hyphen separator");
        }
        if (phoneNumber.length() != 12 && phoneNumber.length() != 14) {
            return ValidationResult.fail("Total length must be 12 or 14 characters (found " + phoneNumber.length() + ")");
        }

        char areaCodeFirstDigit = digitsOnly.charAt(0);
        if (areaCodeFirstDigit < '2' || areaCodeFirstDigit > '9') {
            return ValidationResult.fail("Area code must start with a digit from 2-9");
        }

        char centralOfficeFirstDigit = digitsOnly.charAt(3);
        if (centralOfficeFirstDigit == '0' || centralOfficeFirstDigit == '1') {
            return ValidationResult.fail("Central office code cannot start with 0 or 1");
        }

        if (!STRICT_PHONE_VALIDATION_PATTERN.matcher(phoneNumber).matches()) {
            return ValidationResult.fail("Does not match valid NANP phone formatting");
        }

        return ValidationResult.ok();
    }

    // =========================================================================
    // Extraction Methods (Tasks 3 - 7)
    // =========================================================================

    /** Task 3: Extracts dates in YYYY-MM-DD format. */
    public static List<String> extractDates(String text) {
        return extractAllMatches(DATE_PATTERN, text);
    }

    /** Task 4: Extracts URLs from text. */
    public static List<String> extractUrls(String text) {
        return extractAllMatches(URL_PATTERN, text);
    }

    /** Task 5: Extracts phone numbers from text. */
    public static List<String> extractPhoneNumbers(String text) {
        return extractAllMatches(PHONE_EXTRACTION_PATTERN, text);
    }

    /** Task 6: Extracts email addresses from text. */
    public static List<String> extractEmails(String text) {
        return extractAllMatches(EMAIL_EXTRACTION_PATTERN, text);
    }

    /** Task 7: Extracts prices from text (e.g., $12.34 or 12.34 CAD). */
    public static List<String> extractPrices(String text) {
        return extractAllMatches(PRICE_PATTERN, text);
    }

    private static List<String> extractAllMatches(Pattern pattern, String text) {
        List<String> matches = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return matches;
        }
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            matches.add(matcher.group());
        }
        return matches;
    }

    // =========================================================================
    // File Processing & Reporting Helpers
    // =========================================================================

    private static void processFile(
            Path filePath,
            List<String> allValidEmails,
            List<String> allInvalidEmails,
            List<String> allValidPhones,
            List<String> allInvalidPhones,
            List<String> allDates,
            List<String> allUrls,
            List<String> allPrices
    ) {
        System.out.println("==================================================");
        System.out.println("File: " + filePath.getFileName());
        System.out.println("==================================================");

        try {
            String content = Files.readString(filePath);

            List<String> dates = extractDates(content);
            List<String> phones = extractPhoneNumbers(content);
            List<String> emails = extractEmails(content);
            List<String> urls = extractUrls(content);
            List<String> prices = extractPrices(content);

            allDates.addAll(dates);
            allUrls.addAll(urls);
            allPrices.addAll(prices);

            printResults("[Dates (YYYY-MM-DD)]", dates, Integer.MAX_VALUE);
            printResults("[Phone Numbers]", phones, Integer.MAX_VALUE);
            validateAndCollectPhones(phones, allValidPhones, allInvalidPhones);
            printResults("[Email Addresses]", emails, Integer.MAX_VALUE);
            validateAndCollectEmails(emails, allValidEmails, allInvalidEmails);
            printResults("[URLs]", urls, 5);
            printResults("[Prices]", prices, Integer.MAX_VALUE);

        } catch (IOException e) {
            System.err.println("Could not read file " + filePath.getFileName() + ": " + e.getMessage());
        }
    }

    private static void validateAndCollectEmails(
            List<String> emails,
            List<String> globalValid,
            List<String> globalInvalid
    ) {
        List<String> validList = new ArrayList<>();
        List<String> invalidList = new ArrayList<>();

        for (String email : emails) {
            ValidationResult result = validateEmail(email);
            if (result.valid()) {
                validList.add(email);
                globalValid.add(email);
            } else {
                String formatted = email + " (Reason: " + result.reason() + ")";
                invalidList.add(formatted);
                globalInvalid.add(formatted);
            }
        }

        System.out.println("[Validated Emails - Valid]:");
        printListOrEmpty(validList, "  No valid emails found.");

        System.out.println("[Validated Emails - Invalid]:");
        printListOrEmpty(invalidList, "  No invalid emails found.");
    }

    private static void validateAndCollectPhones(
            List<String> phones,
            List<String> globalValid,
            List<String> globalInvalid
    ) {
        List<String> validList = new ArrayList<>();
        List<String> invalidList = new ArrayList<>();

        for (String phone : phones) {
            ValidationResult result = validatePhoneNumber(phone);
            if (result.valid()) {
                validList.add(phone);
                globalValid.add(phone);
            } else {
                String formatted = phone + " (Reason: " + result.reason() + ")";
                invalidList.add(formatted);
                globalInvalid.add(formatted);
            }
        }

        System.out.println("[Validated Phones - Valid]:");
        printListOrEmpty(validList, "  No valid phone numbers found.");

        System.out.println("[Validated Phones - Invalid]:");
        printListOrEmpty(invalidList, "  No invalid phone numbers found.");
    }

    private static void printEmailValidationReport(String title, List<String> emails) {
        List<String> validList = new ArrayList<>();
        List<String> invalidList = new ArrayList<>();

        for (String email : emails) {
            ValidationResult result = validateEmail(email);
            if (result.valid()) {
                validList.add(email);
            } else {
                invalidList.add(email + " (Reason: " + result.reason() + ")");
            }
        }

        System.out.println("=== " + title + " ===");
        System.out.println("Valid Emails:");
        printListOrEmpty(validList, "  (None)");
        System.out.println("Invalid Emails:");
        printListOrEmpty(invalidList, "  (None)");
    }

    private static void printPhoneValidationReport(String title, List<String> phones) {
        List<String> validList = new ArrayList<>();
        List<String> invalidList = new ArrayList<>();

        for (String phone : phones) {
            ValidationResult result = validatePhoneNumber(phone);
            if (result.valid()) {
                validList.add(phone);
            } else {
                invalidList.add(phone + " (Reason: " + result.reason() + ")");
            }
        }

        System.out.println("=== " + title + " ===");
        System.out.println("Valid Phone Numbers:");
        printListOrEmpty(validList, "  (None)");
        System.out.println("Invalid Phone Numbers:");
        printListOrEmpty(invalidList, "  (None)");
    }

    private static void printOverallSummary(
            List<String> allValidEmails,
            List<String> allInvalidEmails,
            List<String> allValidPhones,
            List<String> allInvalidPhones,
            List<String> allDates,
            List<String> allUrls,
            List<String> allPrices
    ) {
        System.out.println("\n==================================================");
        System.out.println("OVERALL SUMMARY ACROSS ALL FILES");
        System.out.println("==================================================");

        System.out.println("[All Valid Emails (" + allValidEmails.size() + ")]:");
        printListOrEmpty(allValidEmails, "  No valid emails found.");

        System.out.println("[All Invalid Emails (" + allInvalidEmails.size() + ")]:");
        printListOrEmpty(allInvalidEmails, "  No invalid emails found.");

        System.out.println("[All Valid Phone Numbers (" + allValidPhones.size() + ")]:");
        printListOrEmpty(allValidPhones, "  No valid phone numbers found.");

        System.out.println("[All Invalid Phone Numbers (" + allInvalidPhones.size() + ")]:");
        printListOrEmpty(allInvalidPhones, "  No invalid phone numbers found.");

        System.out.println("[All Extracted Dates (" + allDates.size() + ")]:");
        printListOrEmpty(allDates, "  No dates found.");

        System.out.println("[All Extracted URLs (" + allUrls.size() + ")]:");
        printListOrEmpty(allUrls, "  No URLs found.");

        System.out.println("[All Extracted Prices (" + allPrices.size() + ")]:");
        printListOrEmpty(allPrices, "  No prices found.");
    }

    private static void printResults(String header, List<String> results, int limit) {
        System.out.println(header + ":");
        if (results.isEmpty()) {
            System.out.println("No results found.");
            return;
        }
        int max = Math.min(results.size(), limit);
        for (int i = 0; i < max; i++) {
            System.out.println("* " + results.get(i));
        }
    }

    private static void printListOrEmpty(List<String> items, String emptyMessage) {
        if (items.isEmpty()) {
            System.out.println(emptyMessage);
        } else {
            items.forEach(item -> System.out.println("  * " + item));
        }
    }
}