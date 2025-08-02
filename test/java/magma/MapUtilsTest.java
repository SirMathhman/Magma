package magma;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class MapUtilsTest {

    @Test
    @DisplayName("Test processTwoDimensionalMap returns the same map")
    void testProcessTwoDimensionalMap() {
        // Setup test data
        Map<List<String>, Map<String, String>> testMap = new HashMap<>();
        
        // Create inner maps
        Map<String, String> innerMap1 = new HashMap<>();
        innerMap1.put("key1", "value1");
        innerMap1.put("key2", "value2");
        
        Map<String, String> innerMap2 = new HashMap<>();
        innerMap2.put("keyA", "valueA");
        innerMap2.put("keyB", "valueB");
        
        // Add inner maps to the outer map with List<String> keys
        testMap.put(Arrays.asList("category1", "subcategory1"), innerMap1);
        testMap.put(Arrays.asList("category2", "subcategory2"), innerMap2);
        
        // Call the method under test
        Map<List<String>, Map<String, String>> resultMap = MapUtils.processTwoDimensionalMap(testMap);
        
        // Assertions
        assertNotNull(resultMap, "Result map should not be null");
        assertEquals(testMap.size(), resultMap.size(), "Result map should have the same size as input map");
        
        // Check that the maps are identical (since the current implementation just returns the input)
        assertEquals(testMap, resultMap, "Result map should be equal to input map");
        
        // Verify specific entries
        assertTrue(resultMap.containsKey(Arrays.asList("category1", "subcategory1")), 
                "Result should contain the first key");
        assertTrue(resultMap.containsKey(Arrays.asList("category2", "subcategory2")), 
                "Result should contain the second key");
        
        Map<String, String> resultInnerMap1 = resultMap.get(Arrays.asList("category1", "subcategory1"));
        assertEquals("value1", resultInnerMap1.get("key1"), "Inner value should match");
        assertEquals("value2", resultInnerMap1.get("key2"), "Inner value should match");
        
        Map<String, String> resultInnerMap2 = resultMap.get(Arrays.asList("category2", "subcategory2"));
        assertEquals("valueA", resultInnerMap2.get("keyA"), "Inner value should match");
        assertEquals("valueB", resultInnerMap2.get("keyB"), "Inner value should match");
    }
    
    @Test
    @DisplayName("Test processTwoDimensionalMap with empty map")
    void testProcessTwoDimensionalMapWithEmptyMap() {
        Map<List<String>, Map<String, String>> emptyMap = new HashMap<>();
        Map<List<String>, Map<String, String>> resultMap = MapUtils.processTwoDimensionalMap(emptyMap);
        
        assertNotNull(resultMap, "Result map should not be null even with empty input");
        assertTrue(resultMap.isEmpty(), "Result map should be empty when input is empty");
    }
    
    @Test
    @DisplayName("Test processTwoDimensionalMap with file locations, extensions, and content")
    void testProcessTwoDimensionalMapWithFileData() {
        // Setup test data representing file locations, extensions, and content
        Map<List<String>, Map<String, String>> fileDataMap = new HashMap<>();
        
        // Create inner maps for Java file
        Map<String, String> javaFileMap = new HashMap<>();
        javaFileMap.put(".java", "public class Main { public static void main(String[] args) { } }");
        
        // Create inner maps for C files
        Map<String, String> cFileMap = new HashMap<>();
        cFileMap.put(".c", "#include <stdio.h>\nint main() { return 0; }");
        cFileMap.put(".h", "#ifndef HEADER_H\n#define HEADER_H\n\n#endif");
        
        // Add inner maps to the outer map with file location keys
        fileDataMap.put(Arrays.asList("magma", "Main"), javaFileMap);
        fileDataMap.put(Arrays.asList("magma", "util", "Helper"), cFileMap);
        
        // Call the method under test
        Map<List<String>, Map<String, String>> resultMap = MapUtils.processTwoDimensionalMap(fileDataMap);
        
        // Assertions
        assertNotNull(resultMap, "Result map should not be null");
        assertEquals(fileDataMap.size(), resultMap.size(), "Result map should have the same size as input map");
        
        // Verify Java file entry
        assertTrue(resultMap.containsKey(Arrays.asList("magma", "Main")), 
                "Result should contain the Java file location");
        Map<String, String> resultJavaFileMap = resultMap.get(Arrays.asList("magma", "Main"));
        assertEquals(1, resultJavaFileMap.size(), "Java file map should have one entry");
        assertTrue(resultJavaFileMap.containsKey(".java"), "Java file map should contain .java extension");
        assertEquals("public class Main { public static void main(String[] args) { } }", 
                resultJavaFileMap.get(".java"), "Java file content should match");
        
        // Verify C file entry
        assertTrue(resultMap.containsKey(Arrays.asList("magma", "util", "Helper")), 
                "Result should contain the C file location");
        Map<String, String> resultCFileMap = resultMap.get(Arrays.asList("magma", "util", "Helper"));
        assertEquals(2, resultCFileMap.size(), "C file map should have two entries");
        assertTrue(resultCFileMap.containsKey(".c"), "C file map should contain .c extension");
        assertTrue(resultCFileMap.containsKey(".h"), "C file map should contain .h extension");
        assertEquals("#include <stdio.h>\nint main() { return 0; }", 
                resultCFileMap.get(".c"), "C file content should match");
        assertEquals("#ifndef HEADER_H\n#define HEADER_H\n\n#endif", 
                resultCFileMap.get(".h"), "Header file content should match");
    }
}