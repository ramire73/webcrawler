package org.example;

import org.example.service.UAlbertaService;

import java.util.Scanner;

public class UAlbertaCrawler {

    public static void main(String[] args) {
        UAlbertaService service = new UAlbertaService();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n===========================");
            System.out.println("       WEB SCRAPING MENU     ");
            System.out.println("===========================");
            System.out.println("1. Task 1 & 2");
            System.out.println("2. Task 3");
            System.out.println("3. Exit");
            System.out.print("Type your choice (1-3): ");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> {
                    service.task1();
                    System.out.println("-> Returning to menu...");
                }
                case "2" -> {
                    service.task3();
                    System.out.println("-> Returning to menu...");
                }
                case "3" -> {
                    System.out.println("\nExiting program. Goodbye!");
                    running = false;
                }
                default -> System.out.println("\n[Error] Invalid choice! Please select a valid option (1-3).");
            }
        }
        scanner.close();
    }
}