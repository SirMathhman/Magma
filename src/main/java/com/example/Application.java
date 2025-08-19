package com.example;

import java.util.*;

/**
 * Utility that parses an input string into a List of Sets of Strings.
 *
 * Format (assumed):
 * - Groups are separated by semicolons (';').
 * - Inside a group, items may be separated by commas and/or whitespace.
 *
 * Examples:
 * "a,b,c; d e; f, g" => [{a,b,c}, {d,e}, {f,g}]
 */
public class Application {

    /**
     * Parses the provided input into a List of Sets of Strings.
     *
     * Behavior and assumptions:
     * - If input is null or empty => returns an empty list.
     * - Groups are split on ';'. Empty groups are ignored.
     * - Within groups, tokens are split on commas or any whitespace.
     * - Tokens are trimmed; empty tokens are ignored.
     * - Order of items in each set is preserved (insertion order) by using LinkedHashSet.
     *
     * @param input the input string
     * @return a List of Sets parsed from input
     */
    public static List<Set<String>> run(String input) {
        List<Set<String>> result = new ArrayList<>();
        if (input == null || input.trim().isEmpty()) return result;

        String[] groups = input.split(";");
        for (String g : groups) {
            if (g == null) continue;
            String trimmedGroup = g.trim();
            if (trimmedGroup.isEmpty()) continue;

            Set<String> set = new LinkedHashSet<>();
            // split on commas or any whitespace
            String[] tokens = trimmedGroup.split("[,\\s]+");
            for (String t : tokens) {
                if (t == null) continue;
                String s = t.trim();
                if (!s.isEmpty()) set.add(s);
            }
            if (!set.isEmpty()) result.add(set);
        }
        return result;
    }

    // Small demo runner
    public static void main(String[] args) {
        String input;
        if (args.length == 0) {
            input = "a,b,c; d e; f, g; ;h";
            System.out.println("No args provided. Using demo input: " + input);
        } else {
            input = String.join(" ", args);
        }

        List<Set<String>> parsed = run(input);
        System.out.println("Parsed result:");
        for (int i = 0; i < parsed.size(); i++) {
            System.out.println("Group " + i + ": " + parsed.get(i));
        }
    }
}
