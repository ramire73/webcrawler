package org.example;

import org.example.service.SearchFrequencyService;

import java.util.Scanner;

public class SearchFrequencyTracker {

    public static void main(String[] args) {
        String csvFile = "merged_programs.csv";
        SearchFrequencyService service = new SearchFrequencyService(csvFile);
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("===========================");
        System.out.println(" SEARCH FREQUENCY TRACKER     ");
        System.out.println("===========================");
        System.out.println("Instructions:");
        System.out.println(" - Type a keyword (e.g., 'computer', 'master', 'science') to search CSV.");
        System.out.println(" - Type 'top' to view top searches.");
        System.out.println(" - Type 'log' to view search history.");
        System.out.println(" - Type 'exit' to quit.\n");

        while (running) {
            System.out.print("Search > ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "top" -> {
                    service.displayTopSearches(10);
                }
                case "log" -> {
                    System.out.println("\n[--- Search Log ---]");
                    for (String log : service.getSearchLog()) {
                        System.out.println(" * " + log);
                    }
                    System.out.println();
                }
                case "exit" -> {
                    System.out.println("\nExiting program. Goodbye!");
                    running = false;
                }
                default -> service.search(input);
            }
        }
        scanner.close();
    }
}
