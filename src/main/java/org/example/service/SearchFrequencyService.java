package org.example.service;

import org.example.util.RedBlackTree;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class SearchFrequencyService {
    private RedBlackTree rbTree;
    private Map<Long, String> queryMap;
    private Map<Long, Integer> frequencyMap;
    private Queue<String> searchLog;
    private List<String> datasetRecords;

    /**
     * Constructor.
     *
     * @param csvFilename the csv file name.
     */
    public SearchFrequencyService(String csvFilename) {
        rbTree = new RedBlackTree();
        queryMap = new HashMap<>();
        frequencyMap = new HashMap<>();
        searchLog = new LinkedList<>();
        datasetRecords = new ArrayList<>();

        loadDataset(csvFilename);
    }

    /**
     * Loads program names and specializations from merged_programs.csv into memory.
     *
     * @param csvFilename the csv file name.
     */
    private void loadDataset(String csvFilename) {
        URL url = getClass().getClassLoader().getResource(csvFilename);
        if (url == null) {
            System.out.println("The csv source file " + csvFilename + " doesn't exists");
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(url.getPath()))) {
            String line = br.readLine(); // Skip header.
            if (line == null) return;

            while ((line = br.readLine()) != null) {
                // Basic CSV split handling comma separation.
                String[] parts = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                if (parts.length > 2) {
                    // Specialization at index 1, Program Name is at index 2.
                    String specialization = parts[1].replaceAll("\"", "").trim();
                    String programName = parts[2].replaceAll("\"", "").trim();

                    if (!specialization.isEmpty()) datasetRecords.add(specialization);
                    if (!programName.isEmpty()) datasetRecords.add(programName);
                }
            }
            System.out.println("Loaded " + datasetRecords.size() + " records from " + csvFilename + "... Done\n");
        } catch (IOException e) {
            System.out.println("An error occurred reading the CSV file: " + e.getMessage());
        }
    }

    /**
     * A. Implement a function to handle search queries from users.
     * B. Use a self-balancing search tree (e.g., AVL tree, red-black tree) to store each search query and its frequency.
     *
     * @param query the query to search.
     */
    public void search(String query) {
        if (query == null || query.trim().isEmpty()) return;
        query = query.trim().toLowerCase();
        // Map string query to a long hash key for RedBlackTree.
        long key = query.hashCode();
        // Insert into the red-black tree if the element is not already present.
        if (!rbTree.search(key)) {
            rbTree.insert(key);
        }
        // Track query string and frequency.
        queryMap.put(key, query);
        frequencyMap.put(key, frequencyMap.getOrDefault(key, 0) + 1);
        // Maintain chronological search log.
        searchLog.offer(query);
        // Perform CSV search matching.
        System.out.println("\n[--- Search Results for: \"" + query + "\" (Frequency: " + frequencyMap.get(key) + ") ---]");
        int matchCount = 0;
        for (String record : datasetRecords) {
            if (record.toLowerCase().contains(query)) {
                System.out.println("  * " + record);
                matchCount++;
                if (matchCount >= 10) {
                    System.out.println("  ... (more matches found)");
                    break;
                }
            }
        }
        if (matchCount == 0) {
            System.out.println("No matching specializations/programs found in CSV.");
        }
        System.out.println("-----------------------------------------------------------------\n");
    }

    /**
     * Display top searches ranked by frequency.
     *
     * @param k the top number of searches to display.
     */
    public void displayTopSearches(int k) {
        PriorityQueue<QueryEntry> minPQ = new PriorityQueue<>(Comparator.comparingInt(e -> e.frequency()));

        // Build the Min-Priority Queue.
        for (Map.Entry<Long, Integer> entry : frequencyMap.entrySet()) {
            String queryStr = queryMap.get(entry.getKey());
            int frequency = entry.getValue();
            minPQ.offer(new QueryEntry(queryStr, frequency));
            if (minPQ.size() > k) {
                minPQ.poll();
            }
        }

        // Extract items from Min-PQ into a list and sort in descending order.
        List<QueryEntry> topList = new ArrayList<>();
        while (!minPQ.isEmpty()) {
            topList.add(0, minPQ.poll());
        }

        System.out.println("\n[--- Top " + k + " Searches ---]");
        int rank = 1;
        for (QueryEntry entry : topList) {
            System.out.printf("%d. \"%s\" (Searched %d times)\n", rank++, entry.query(), entry.frequency());
        }
        System.out.println("----------------------------\n");
    }

    public Iterable<String> getSearchLog() {
        return searchLog;
    }

    // Helper record to store the query and frequency.
    public record QueryEntry(String query, int frequency) {

    }
}