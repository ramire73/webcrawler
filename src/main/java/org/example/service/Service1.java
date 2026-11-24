package org.example.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;
import java.util.TreeMap;

public class Service1 {
    private TreeMap<String, Integer> frequencyMap;
    private Queue<String> searchLog;
    private List<String> datasetRecords;

    public Service1(String csvFilePath) {
        frequencyMap = new TreeMap<>();
        searchLog = new LinkedList<>();
        datasetRecords = new ArrayList<>();
        loadDataset(csvFilePath);
    }

    /**
     * Loads program names and specializations from merged_programs.csv into memory.
     *
     * @param csvFilename the csv file name.
     */
    private void loadDataset(String csvFilename) {
        URL url = getClass().getClassLoader().getResource(csvFilename);
        if (url == null) {
            System.out.println("The csv source file doesn't exists");
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(url.getPath()))) {
            String line = br.readLine(); // Read header
            if (line == null) return;

            while ((line = br.readLine()) != null) {
                // Basic CSV split handling comma separation
                String[] parts = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                if (parts.length > 2) {
                    // Specialization at index 1, Program Name is at index 2
                    String specialization = parts[1].replaceAll("\"", "").trim();
                    String programName = parts[2].replaceAll("\"", "").trim();

                    if (!specialization.isEmpty()) datasetRecords.add(specialization);
                    if (!programName.isEmpty()) datasetRecords.add(programName);
                }
            }
            System.out.println("Loaded " + datasetRecords.size() + " searchable terms from " + csvFilename + ".\n");
        } catch (IOException e) {
            System.out.println("An error ocurred reading the CSV file: " + e.getMessage());
        }
    }

    /**
     * A. Implement a function to handle search queries from users.
     * B. Use a self-balancing search tree (e.g., AVL tree, red-black tree) to store each search query and its frequency.
     * <p>
     * A. Handle search queries, matching against the CSV dataset and updating frequency.
     * B. Maintain a chronological log of searches.
     *
     * @param query the query to search.
     */
    public void search(String query) {
        if (query == null || query.trim().isEmpty()) return;
        query = query.trim().toLowerCase();

        // Update frequency in Red-Black Tree (TreeMap)
        frequencyMap.put(query, frequencyMap.getOrDefault(query, 0) + 1);

        // Add to chronological log
        searchLog.offer(query);

        // Search matching records from CSV dataset
        System.out.println("\n--- Search Results for: \"" + query + "\" ---");
        int matchCount = 0;
        for (String record : datasetRecords) {
            if (record.toLowerCase().contains(query)) {
                System.out.println("  • " + record);
                matchCount++;
                if (matchCount >= 10) { // Limit display to top 10 matches per query
                    System.out.println("... (more matches found)");
                    break;
                }
            }
        }
        if (matchCount == 0) {
            System.out.println("No matching programs/specializations found in CSV.");
        }
        System.out.println("----------------------------------------------\n");
    }

    /**
     * Display top K searches using a Min-Priority Queue.
     *
     * @param k the top number of searches to display.
     */
    public List<QueryEntry> getTopSearches(int k) {
        PriorityQueue<QueryEntry> minPQ = new PriorityQueue<>(Comparator.comparingInt(e -> e.frequency()));

        for (String key : frequencyMap.keySet()) {
            minPQ.offer(new QueryEntry(key, frequencyMap.get(key)));
            if (minPQ.size() > k) {
                minPQ.poll();
            }
        }

        List<QueryEntry> topSearches = new ArrayList<>();
        while (!minPQ.isEmpty()) {
            topSearches.add(0, minPQ.poll()); // Descending order
        }
        return topSearches;
    }

    public Iterable<String> getSearchLog() {
        return searchLog;
    }

    public record QueryEntry(String query, int frequency) {

    }

    public static void main(String[] args) {
        String csvFile = "merged_programs.csv";
        Service1 tracker = new Service1(csvFile);

        Scanner scanner = new Scanner(System.in);
        System.out.println("=== Search Frequency Tracker  ===");
        System.out.println("Instructions:");
        System.out.println("- Type any keyword (e.g., 'Computer', 'Master', 'Biology', 'Data') to search CSV.");
        System.out.println("- Type 'top' to view top searches.");
        System.out.println("- Type 'log' to view search history.");
        System.out.println("- Type 'exit' to quit.\n");

        while (true) {
            System.out.print("Search > ");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("exit")) break;

            if (input.equalsIgnoreCase("top")) {
                List<QueryEntry> top = tracker.getTopSearches(5);
                System.out.println("\n--- Top Searches ---");
                for (int i = 0; i < top.size(); i++) {
                    System.out.printf("%d. \"%s\" (Searched %d times)\n", i + 1, top.get(i).query(), top.get(i).frequency());
                }
                System.out.println();
                continue;
            } else if (input.equalsIgnoreCase("log")) {
                System.out.println("\n--- Chronological Search Log ---");
                for (String q : tracker.getSearchLog()) {
                    System.out.println(" - " + q);
                }
                System.out.println();
                continue;
            }

            tracker.search(input);
        }
        scanner.close();
    }
}