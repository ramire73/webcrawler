package org.example.service;

import org.example.util.textprocessing.TST;

import java.util.ArrayList;
import java.util.List;

public class WordCompletionService {

    private final TST<Integer> trie = new TST<>();

    /**
     * Adds the dataset to the trie.
     *
     * @param dataset the list of strings.
     */
    public void load(List<String> dataset) {
        for (String data : dataset) {
            addText(data);
        }
    }

    /**
     * Splits a piece of text into words (letters only) and adds them.
     *
     * @param text the text to split into tokens.
     */
    private void addText(String text) {
        if (text == null) return;
        // Split the text into tokens using regex. \p{L} = any letter, incl. é, ô...
        for (String token : text.split("[^\\p{L}]+")) {
            addWord(token);
        }
    }

    /**
     * Adds one word to the Trie. TST.put(word) sets the count to 1 or adds 1.
     *
     * @param word the word to add.
     */
    private void addWord(String word) {
        if (word == null) return;
        word = word.trim().toLowerCase();
        // skip empty tokens (TST rejects "") and 1-letter fragments.
        if (word.length() < 2) return;

        trie.put(word);
    }

    /**
     * Search every word in the trie that starts with the given prefix.
     *
     * @param prefix the prefix to search.
     * @return a list of strings with the prefix matches.
     */
    private List<String> search(String prefix) {
        List<String> words = new ArrayList<>();
        if (prefix == null) return words;
        prefix = prefix.trim().toLowerCase();
        // TST does not accept an empty key.
        if (prefix.isEmpty()) return words;

        for (String word : trie.prefixMatch(prefix)) {
            words.add(word);
        }
        return words;
    }

    /**
     * How many times a word occurred in the data (0 if absent).
     *
     * @param word the word to count.
     * @return the count.
     */
    public int frequency(String word) {
        Integer count = trie.get(word);
        return count == null ? 0 : count;
    }

    /**
     * @return the size of the trie.
     */
    public int size() {
        return trie.size();
    }

    /**
     * Display a list of words that start with the given prefix.
     *
     * @param prefix the prefix to search.
     */
    public void display(String prefix) {
        List<String> words = search(prefix);
        System.out.println("======================================");
        System.out.println("Prefix: \"" + prefix + "\"");
        if (words.isEmpty()) {
            System.out.println("No words found starting with \"" + prefix + "\".");
        } else {
            System.out.println("Words found (" + words.size() + "): " + words);

            StringBuilder sb = new StringBuilder("Frequency in data: ");
            for (int i = 0; i < words.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(words.get(i)).append("=").append(frequency(words.get(i)));
            }
            System.out.println(sb);
        }
        System.out.println("======================================");
    }
}