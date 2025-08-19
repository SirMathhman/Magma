package com.example;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class StringSetListGeneratorTest {

    @Test
    void parseGroups_demoInput_parsesCorrectly() {
        String input = "a,b,c; d e; f, g; ;h";
        List<Set<String>> parsed = StringSetListGenerator.parseGroups(input);

        assertEquals(4, parsed.size(), "should parse 4 non-empty groups");

        Set<String> expected0 = new LinkedHashSet<>(Arrays.asList("a","b","c"));
        Set<String> expected1 = new LinkedHashSet<>(Arrays.asList("d","e"));
        Set<String> expected2 = new LinkedHashSet<>(Arrays.asList("f","g"));
        Set<String> expected3 = new LinkedHashSet<>(Collections.singletonList("h"));

        assertEquals(expected0, parsed.get(0));
        assertEquals(expected1, parsed.get(1));
        assertEquals(expected2, parsed.get(2));
        assertEquals(expected3, parsed.get(3));
    }

    @Test
    void parseGroups_nullOrEmpty_returnsEmptyList() {
        assertTrue(StringSetListGenerator.parseGroups(null).isEmpty(), "null should return empty list");
        assertTrue(StringSetListGenerator.parseGroups("").isEmpty(), "empty string should return empty list");
        assertTrue(StringSetListGenerator.parseGroups("   ").isEmpty(), "blank string should return empty list");
    }
}
