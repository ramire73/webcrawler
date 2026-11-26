package org.example.service;

import org.example.util.regex.RegexConstants;
import org.example.util.validation.EmailValidator;
import org.example.util.validation.PhoneValidator;
import org.example.util.validation.ValidationResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataExtractorService {

    public static final int LIMIT = 15;

    /**
     * Validates a list of sample emails (Task 1), and displays them split into valid and invalid.
     *
     * @param title  the title to display.
     * @param emails a list of emails.
     */
    public void displayEmailValidation(String title, List<String> emails) {
        List<String> validList = new ArrayList<>();
        List<String> invalidList = new ArrayList<>();

        for (String email : emails) {
            ValidationResult result = EmailValidator.validate(email);
            if (result.valid()) {
                validList.add(email);
            } else {
                invalidList.add(email + " (Reason: " + result.reason() + ")");
            }
        }

        System.out.println("=== " + title + " ===");
        System.out.println("Valid Emails:");
        displayListOrEmpty(validList, "  (None)");
        System.out.println("Invalid Emails:");
        displayListOrEmpty(invalidList, "  (None)");
    }

    /**
     * Validates a list of sample phone numbers (Task 2), and displays them split into valid and invalid.
     *
     * @param title  the title to display.
     * @param phones a list of phones.
     */
    public void displayPhoneValidation(String title, List<String> phones) {
        List<String> validList = new ArrayList<>();
        List<String> invalidList = new ArrayList<>();

        for (String phone : phones) {
            ValidationResult result = PhoneValidator.validate(phone);
            if (result.valid()) {
                validList.add(phone);
            } else {
                invalidList.add(phone + " (Reason: " + result.reason() + ")");
            }
        }

        System.out.println("=== " + title + " ===");
        System.out.println("Valid Phone Numbers:");
        displayListOrEmpty(validList, "  (None)");
        System.out.println("Invalid Phone Numbers:");
        displayListOrEmpty(invalidList, "  (None)");
    }

    /**
     * Runs Task 1 validation on every email extracted from one file, displays
     * the valid and invalid lists for that file, and adds them to the overall lists.
     *
     * @param emails        a list of emails.
     * @param validEmails   a list of valid emails.
     * @param invalidEmails a list of invalid emails.
     */
    private void validateAndCollectEmails(List<String> emails, List<String> validEmails, List<String> invalidEmails) {
        List<String> validList = new ArrayList<>();
        List<String> invalidList = new ArrayList<>();

        for (String email : emails) {
            ValidationResult result = EmailValidator.validate(email);
            if (result.valid()) {
                validList.add(email);
                validEmails.add(email);
            } else {
                String formatted = email + " (Reason: " + result.reason() + ")";
                invalidList.add(formatted);
                invalidEmails.add(formatted);
            }
        }

        System.out.println("[Validated Emails - Valid]:");
        displayListOrEmpty(validList, "  No valid emails found.");

        System.out.println("[Validated Emails - Invalid]:");
        displayListOrEmpty(invalidList, "  No invalid emails found.");
    }

    /**
     * Runs Task 2 validation on every phone number extracted from one file,
     * displays the valid and invalid lists for that file, and adds them to the overall lists.
     *
     * @param phones        a list of phones.
     * @param validPhones   a list of valid phones.
     * @param invalidPhones a list of invalid phones.
     */
    private void validateAndCollectPhones(List<String> phones, List<String> validPhones, List<String> invalidPhones) {
        List<String> validList = new ArrayList<>();
        List<String> invalidList = new ArrayList<>();

        for (String phone : phones) {
            ValidationResult result = PhoneValidator.validate(phone);
            if (result.valid()) {
                validList.add(phone);
                validPhones.add(phone);
            } else {
                String formatted = phone + " (Reason: " + result.reason() + ")";
                invalidList.add(formatted);
                invalidPhones.add(formatted);
            }
        }

        System.out.println("[Validated Phones - Valid]:");
        displayListOrEmpty(validList, "  No valid phone numbers found.");

        System.out.println("[Validated Phones - Invalid]:");
        displayListOrEmpty(invalidList, "  No invalid phone numbers found.");
    }


    /**
     * Task 3: Finding Dates in Text.
     *
     * @param text the text to process.
     * @return a list of extracted results.
     */
    private List<String> extractDates(String text) {
        return extractAllMatches(RegexConstants.DATE_EXTRACTION_PATTERN, text);
    }

    /**
     * Task 4: Finding URLs in Text.
     *
     * @param text the text to process.
     * @return a list of extracted results.
     */
    public List<String> extractUrls(String text) {
        return extractAllMatches(RegexConstants.URL_EXTRACTION_PATTERN, text);
    }

    /**
     * Task 5: Extracting Phone Numbers from Text.
     *
     * @param text the text to process.
     * @return a list of extracted results.
     */
    public List<String> extractPhoneNumbers(String text) {
        return extractAllMatches(RegexConstants.PHONE_EXTRACTION_PATTERN, text);
    }

    /**
     * Task 6: Extracting Email Addresses from Text.
     *
     * @param text the text to process.
     * @return a list of extracted results.
     */
    public List<String> extractEmails(String text) {
        return extractAllMatches(RegexConstants.EMAIL_EXTRACTION_PATTERN, text);
    }

    /**
     * Task 7: Extracting Prices from Text.
     *
     * @param text the text to process.
     * @return a list of extracted results.
     */
    public List<String> extractPrices(String text) {
        return extractAllMatches(RegexConstants.PRICE_EXTRACTION_PATTERN, text);
    }

    /**
     * Shared helper for Tasks 3-7: runs the given pattern over the text with
     * Matcher.find() and collects every match, in the order it appears.
     *
     * @param pattern the pattern to use.
     * @param text    the text to apply the pattern.
     * @return an empty list for null or empty text.
     *
     */
    private List<String> extractAllMatches(Pattern pattern, String text) {
        List<String> result = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return result;
        }
        Matcher matcher = pattern.matcher(text);
        // find() moves to the next match each time it is called.
        while (matcher.find()) {
            result.add(matcher.group());
        }
        return result;
    }

    /**
     * Reads one text file, runs all five extraction tasks (3-7) on it, displays
     * the results, validates the extracted phones and emails with tasks 1-2,
     * and adds everything to the overall lists used for the final summary.
     *
     * @param validEmails   a list of valid emails.
     * @param invalidEmails a list of invalid emails.
     * @param validPhones   a list of valid phones.
     * @param invalidPhones a list of invalid phones.
     * @param allDates      a list of dates.
     * @param allUrls       a list of urls.
     * @param allPrices     a list of prices.
     */
    public void processFile(
            Path filePath,
            List<String> validEmails,
            List<String> invalidEmails,
            List<String> validPhones,
            List<String> invalidPhones,
            List<String> allDates,
            List<String> allUrls,
            List<String> allPrices
    ) {
        System.out.println("==================================================");
        System.out.println("File: " + filePath.getFileName());
        System.out.println("==================================================");

        try {
            // Read the whole file into one string.
            String content = Files.readString(filePath);

            // Run the five extraction tasks on the file content.
            List<String> emails = extractEmails(content);
            List<String> phones = extractPhoneNumbers(content);
            List<String> dates = extractDates(content);
            List<String> urls = extractUrls(content);
            List<String> prices = extractPrices(content);

            // Add this file's results to the overall lists.
            allDates.addAll(dates);
            allUrls.addAll(urls);
            allPrices.addAll(prices);

            displayResults("[Email Addresses]", emails, LIMIT);
            validateAndCollectEmails(emails, validEmails, invalidEmails);
            displayResults("[Phone Numbers]", phones, LIMIT);
            validateAndCollectPhones(phones, validPhones, invalidPhones);
            displayResults("[Dates (YYYY-MM-DD)]", dates, LIMIT);
            displayResults("[URLs]", urls, 5); // Just for display a few urls.
            displayResults("[Prices]", prices, LIMIT);
        } catch (IOException e) {
            System.err.println("Could not read file " + filePath.getFileName() + ": " + e.getMessage());
        }
    }

    /**
     * Displays the combined results of all tasks across every processed file, with counts.
     *
     * @param validEmails   a list of valid emails.
     * @param invalidEmails a list of invalid emails.
     * @param validPhones   a list of valid phones.
     * @param invalidPhones a list of invalid phones.
     * @param dates         a list of dates.
     * @param urls          a list of URLs.
     * @param prices        a list of prices.
     */
    public void displaySummary(
            List<String> validEmails,
            List<String> invalidEmails,
            List<String> validPhones,
            List<String> invalidPhones,
            List<String> dates,
            List<String> urls,
            List<String> prices
    ) {
        System.out.println("\n==================================================");
        System.out.println("OVERALL SUMMARY ACROSS ALL FILES");
        System.out.println("Note: The results are limited to display only " + LIMIT + " per category.");
        System.out.println("==================================================");


        System.out.println("[All Valid Emails (" + validEmails.size() + ")]:");
        displayListOrEmpty(validEmails, "  No valid emails found.");

        System.out.println("[All Invalid Emails (" + invalidEmails.size() + ")]:");
        displayListOrEmpty(invalidEmails, "  No invalid emails found.");

        System.out.println("[All Valid Phone Numbers (" + validPhones.size() + ")]:");
        displayListOrEmpty(validPhones, "  No valid phone numbers found.");

        System.out.println("[All Invalid Phone Numbers (" + invalidPhones.size() + ")]:");
        displayListOrEmpty(invalidPhones, "  No invalid phone numbers found.");

        System.out.println("[All Extracted Dates (" + dates.size() + ")]:");
        displayListOrEmpty(dates, "  No dates found.");

        System.out.println("[All Extracted URLs (" + urls.size() + ")]:");
        displayListOrEmpty(urls, "  No URLs found.");

        System.out.println("[All Extracted Prices (" + prices.size() + ")]:");
        displayListOrEmpty(prices, "  No prices found.");
    }

    /**
     * Displays a header and up to "limit" results, or "No results found." if the list is empty.
     *
     * @param header  the header.
     * @param results a list of {@link String}.
     * @param limit   the limit of results to display.
     */
    private void displayResults(String header, List<String> results, int limit) {
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

    /**
     * Display each item on its own line, or the given message if the list is empty.
     *
     * @param items        a list of {@link String}.
     * @param emptyMessage an empty message.
     */
    private void displayListOrEmpty(List<String> items, String emptyMessage) {
        if (items.isEmpty()) {
            System.out.println(emptyMessage);
        } else {
            if (items.size() > LIMIT) {
                items.subList(0, LIMIT).forEach(item -> System.out.println("  * " + item));
            } else {
                items.forEach(item -> System.out.println("  * " + item));
            }
        }
    }
}