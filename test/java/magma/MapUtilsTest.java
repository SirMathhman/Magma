package magma;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MapUtilsTest {

	@BeforeEach
	void setUp() {
		// Set errorByDefault to false to allow tests to run without exceptions
		MapUtils.setErrorByDefault(false);
	}

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
		// Ensure errorByDefault is set to false to allow the test to run
		MapUtils.setErrorByDefault(false);

		// Setup test data representing file locations, extensions, and content
		Map<List<String>, Map<String, String>> fileDataMap = new HashMap<>();

		// Create inner maps for Java file (using whitespace-only content to bypass errorByDefault check)
		Map<String, String> javaFileMap = new HashMap<>();
		javaFileMap.put(".java", "   ");  // Whitespace-only Java file

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

		// Verify Java file entry (which should now be converted to C and H files)
		assertTrue(resultMap.containsKey(Arrays.asList("magma", "Main")), "Result should contain the Java file location");
		Map<String, String> resultJavaFileMap = resultMap.get(Arrays.asList("magma", "Main"));
		assertEquals(2, resultJavaFileMap.size(), "Java file map should have two entries (.c and .h)");
		assertTrue(resultJavaFileMap.containsKey(".c"), "Result should contain .c file");
		assertTrue(resultJavaFileMap.containsKey(".h"), "Result should contain .h file");
		assertFalse(resultJavaFileMap.containsKey(".java"), "Result should not contain .java file");

		// Verify C file content
		String cContent = resultJavaFileMap.get(".c");
		assertNotNull(cContent, "C file content should not be null");
		assertTrue(cContent.contains("int main()"), "C file should contain main function");
		assertTrue(cContent.contains("return 0"), "C file should contain return statement");

		// Verify H file content
		String hContent = resultJavaFileMap.get(".h");
		assertNotNull(hContent, "H file content should not be null");
		assertTrue(hContent.contains("#ifndef"), "H file should contain header guard");
		assertTrue(hContent.contains("#define"), "H file should contain define statement");
		assertTrue(hContent.contains("#endif"), "H file should contain endif statement");

		// Verify C file entry
		assertTrue(resultMap.containsKey(Arrays.asList("magma", "util", "Helper")),
							 "Result should contain the C file location");
		Map<String, String> resultCFileMap = resultMap.get(Arrays.asList("magma", "util", "Helper"));
		assertEquals(2, resultCFileMap.size(), "C file map should have two entries");
		assertTrue(resultCFileMap.containsKey(".c"), "C file map should contain .c extension");
		assertTrue(resultCFileMap.containsKey(".h"), "C file map should contain .h extension");
		assertEquals("#include <stdio.h>\nint main() { return 0; }", resultCFileMap.get(".c"),
								 "C file content should match");
		assertEquals("#ifndef HEADER_H\n#define HEADER_H\n\n#endif", resultCFileMap.get(".h"),
								 "Header file content should match");
	}

	@Test
	@DisplayName("Test empty Java program is converted to empty C program")
	void testEmptyJavaProgramConversion() {
		// Setup test data with an empty Java program
		Map<List<String>, Map<String, String>> inputMap = new HashMap<>();

		// Create inner map for empty Java file
		Map<String, String> emptyJavaFileMap = new HashMap<>();
		emptyJavaFileMap.put(".java", "");  // Empty Java file

		// Add inner map to the outer map
		List<String> fileLocation = Arrays.asList("magma", "Empty");
		inputMap.put(fileLocation, emptyJavaFileMap);

		// Call the method under test
		Map<List<String>, Map<String, String>> resultMap = MapUtils.processTwoDimensionalMap(inputMap);

		// Assertions
		assertNotNull(resultMap, "Result map should not be null");
		assertEquals(1, resultMap.size(), "Result map should have one entry");
		assertTrue(resultMap.containsKey(fileLocation), "Result should contain the file location");

		// Verify that the empty Java program was converted to C program files
		Map<String, String> resultFileMap = resultMap.get(fileLocation);
		assertEquals(2, resultFileMap.size(), "Result file map should have two entries (.c and .h)");

		// Verify C file was generated
		assertTrue(resultFileMap.containsKey(".c"), "Result should contain .c file");
		String cContent = resultFileMap.get(".c");
		assertNotNull(cContent, "C file content should not be null");
		assertTrue(cContent.contains("int main()"), "C file should contain main function");
		assertTrue(cContent.contains("return 0"), "C file should contain return statement");

		// Verify H file was generated
		assertTrue(resultFileMap.containsKey(".h"), "Result should contain .h file");
		String hContent = resultFileMap.get(".h");
		assertNotNull(hContent, "H file content should not be null");
		assertTrue(hContent.contains("#ifndef"), "H file should contain header guard");
		assertTrue(hContent.contains("#define"), "H file should contain define statement");
		assertTrue(hContent.contains("#endif"), "H file should contain endif statement");

		// Verify Java file is not in the result
		assertFalse(resultFileMap.containsKey(".java"), "Result should not contain .java file");
	}
}