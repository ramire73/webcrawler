package org.example.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to read csv files.
 */
public abstract class CsvReaderUtils {

    /**
     * Loads different columns from merged_programs.csv into memory.
     *
     * @param csvFilename the csv file to use.
     * @return a list of strings with the data from the csv.
     */
    public static List<String> loadDataset(String csvFilename) {
        List<String> result = new ArrayList<>();
        InputStream in = CsvReaderUtils.class.getClassLoader().getResourceAsStream(csvFilename);
        if (in == null) {
            System.out.println("The csv source file " + csvFilename + " doesn't exist");
            return result;
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line = br.readLine(); // Skip header.
            if (line == null) return result;

            while ((line = br.readLine()) != null) {
                // Basic CSV split handling comma separation.
                String[] parts = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                if (parts.length > 6) {
                    // University at index 0, Specialization at index 1, Program Name is at index 2,
                    // Faculty at index 3, Academic Unit at index 5.
                    String university = parts[0].replaceAll("\"", "").trim();
                    String specialization = parts[1].replaceAll("\"", "").trim();
                    String programName = parts[2].replaceAll("\"", "").trim();
                    String faculty = parts[3].replaceAll("\"", "").trim();
                    String academicUnit = parts[5].replaceAll("\"", "").trim();

                    if (!university.isEmpty()) result.add(university);
                    if (!specialization.isEmpty()) result.add(specialization);
                    if (!programName.isEmpty()) result.add(programName);
                    if (!faculty.isEmpty()) result.add(faculty);
                    if (!academicUnit.isEmpty()) result.add(academicUnit);
                }
            }
            System.out.println("Loaded " + result.size() + " records from " + csvFilename + "... Done");
        } catch (IOException e) {
            System.out.println("An error occurred reading the CSV file: " + e.getMessage());
        }
        return result;
    }
}
