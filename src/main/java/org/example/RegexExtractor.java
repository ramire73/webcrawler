package org.example;

import org.example.service.DataExtractorService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.nio.file.Files.list;

public class RegexExtractor {

    public static void main(String[] args) {
        DataExtractorService service = new DataExtractorService();
        List<String> sampleEmails = List.of(
                "Ex_Ample9@my-domain.com",
                "TEst_User1@hyphen-site.net",
                "user..name@domain.com",
                "username@domain123.com",
                "Username9@-domain.com"
        );
        service.displayEmailValidation("Task 1: Email Validation", sampleEmails);
        System.out.println();
        List<String> samplePhones = List.of(
                "(416) 823-7784",
                "647-392-4482",
                "905 625 2324",
                "+93-647-333-2211",
                "289--453-2284",
                "4165557890"
        );
        service.displayPhoneValidation("Task 2: Phone Number Validation", samplePhones);

        List<String> validEmails = new ArrayList<>();
        List<String> invalidEmails = new ArrayList<>();
        List<String> validPhones = new ArrayList<>();
        List<String> invalidPhones = new ArrayList<>();
        List<String> dates = new ArrayList<>();
        List<String> urls = new ArrayList<>();
        List<String> prices = new ArrayList<>();

        // Folder with the text files produced by the web crawler.
        Path textFilesDir = Paths.get("./ua_all_text_pages");
        System.out.println("\nStarting regex extraction from text files...");

        try {
            if (!Files.exists(textFilesDir)) {
                System.err.println("The directory './ua_all_text_pages' does not exist.");
                return;
            }

            // Go through every regular .txt file in the folder and process it.
            try (Stream<Path> paths = list(textFilesDir)) {
                paths.filter(Files::isRegularFile)
                        .filter(path -> path.toString().toLowerCase().endsWith(".txt"))
                        .forEach(filePath -> service.processFile(
                                filePath,
                                validEmails,
                                invalidEmails,
                                validPhones,
                                invalidPhones,
                                dates,
                                urls,
                                prices
                        ));
            }

            service.displaySummary(
                    validEmails,
                    invalidEmails,
                    validPhones,
                    invalidPhones,
                    dates,
                    urls,
                    prices
            );

            System.out.println("\nExtraction Complete!");

        } catch (java.io.IOException e) {
            System.err.println("An error occurred reading the directory: " + e.getMessage());
        }
    }
}
