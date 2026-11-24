package org.example;

import org.example.service.WordCompletionService;
import org.example.util.CsvReaderUtils;

import java.util.List;
import java.util.Scanner;

public class WordCompletion {

    public static void main(String[] args) {
        String csvFile = "merged_programs.csv";
        List<String> dataset = CsvReaderUtils.loadDataset(csvFile);
        WordCompletionService service = new WordCompletionService();
        service.load(dataset);
        System.out.println("The size of the TST is:" + service.size());

        Scanner scanner = new Scanner(System.in, "UTF-8");
        boolean running = true;

        System.out.println("\n===========================");
        System.out.println("      WORD COMPLETION      ");
        System.out.println("===========================");
        System.out.println("Instructions:");
        System.out.println(" - Type a keyword (e.g., 'computer', 'master', 'science') to search CSV.");
        System.out.println(" - Type 'exit' to quit.\n");
        while (running) {
            System.out.print("Search > ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "exit" -> {
                    System.out.println("\nExiting program. Goodbye!");
                    running = false;
                }
                default -> service.display(input);
            }
        }
        scanner.close();
    }
}