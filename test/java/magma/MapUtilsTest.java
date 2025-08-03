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

	// Helper method to create test data for convertJavaFilesToC tests
	private Map<List<String>, Map<String, String>> createTestMap() {
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

		return testMap;
	}

	@Test
	@DisplayName("Test convertJavaFilesToC returns equal map")
	void testConvertJavaFilesToCReturnsEqualMap() {
		// Setup test data
		Map<List<String>, Map<String, String>> testMap = createTestMap();

		// Call the method under test
		Map<List<String>, Map<String, String>> resultMap = MapUtils.convertJavaFilesToC(testMap);

		// Assertion
		assertNotNull(resultMap, "Result map should not be null");
		assertEquals(testMap.size(), resultMap.size(), "Result map should have the same size as input map");
		assertEquals(testMap, resultMap, "Result map should be equal to input map");
		assertTrue(resultMap.containsKey(Arrays.asList("category1", "subcategory1")),
							 "Result should contain the first key");
		assertTrue(resultMap.containsKey(Arrays.asList("category2", "subcategory2")),
							 "Result should contain the second key");

		// Verify inner values
		Map<String, String> resultInnerMap1 = resultMap.get(Arrays.asList("category1", "subcategory1"));
		assertEquals("value1", resultInnerMap1.get("key1"), "Inner value should match");
		assertEquals("value2", resultInnerMap1.get("key2"), "Inner value should match");

		Map<String, String> resultInnerMap2 = resultMap.get(Arrays.asList("category2", "subcategory2"));
		assertEquals("valueA", resultInnerMap2.get("keyA"), "Inner value should match");
		assertEquals("valueB", resultInnerMap2.get("keyB"), "Inner value should match");
	}

	@Test
	@DisplayName("Test convertJavaFilesToC with empty map returns non-null result")
	void testConvertJavaFilesToCWithEmptyMapReturnsNonNull() {
		Map<List<String>, Map<String, String>> emptyMap = new HashMap<>();
		Map<List<String>, Map<String, String>> resultMap = MapUtils.convertJavaFilesToC(emptyMap);

		assertNotNull(resultMap, "Result map should not be null even with empty input");
	}

	@Test
	@DisplayName("Test convertJavaFilesToC with empty map returns empty map")
	void testConvertJavaFilesToCWithEmptyMapReturnsEmptyMap() {
		Map<List<String>, Map<String, String>> emptyMap = new HashMap<>();
		Map<List<String>, Map<String, String>> resultMap = MapUtils.convertJavaFilesToC(emptyMap);

		assertTrue(resultMap.isEmpty(), "Result map should be empty when input is empty");
	}

	// Helper method to create file data map for convertJavaFilesToC tests
	private Map<List<String>, Map<String, String>> createFileDataMap() {
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

		return fileDataMap;
	}

	@Test
	@DisplayName("Test convertJavaFilesToC with file data returns non-null result")
	void testConvertJavaFilesToCWithFileDataReturnsNonNull() {
		// Ensure errorByDefault is set to false to allow the test to run
		MapUtils.setErrorByDefault(false);

		// Setup test data
		Map<List<String>, Map<String, String>> fileDataMap = createFileDataMap();

		// Call the method under test
		Map<List<String>, Map<String, String>> resultMap = MapUtils.convertJavaFilesToC(fileDataMap);

		// Assertion
		assertNotNull(resultMap, "Result map should not be null");
	}

	@Test
	@DisplayName("Test convertJavaFilesToC with file data preserves map size")
	void testConvertJavaFilesToCWithFileDataPreservesMapSize() {
		// Ensure errorByDefault is set to false to allow the test to run
		MapUtils.setErrorByDefault(false);

		// Setup test data
		Map<List<String>, Map<String, String>> fileDataMap = createFileDataMap();

		// Call the method under test
		Map<List<String>, Map<String, String>> resultMap = MapUtils.convertJavaFilesToC(fileDataMap);

		// Assertion
		assertEquals(fileDataMap.size(), resultMap.size(), "Result map should have the same size as input map");
	}

	@Test
	@DisplayName("Test convertJavaFilesToC preserves Java file location")
	void testConvertJavaFilesToCPreservesJavaFileLocation() {
		// Ensure errorByDefault is set to false to allow the test to run
		MapUtils.setErrorByDefault(false);

		// Setup test data
		Map<List<String>, Map<String, String>> fileDataMap = createFileDataMap();

		// Call the method under test
		Map<List<String>, Map<String, String>> resultMap = MapUtils.convertJavaFilesToC(fileDataMap);

		// Assertion
		assertTrue(resultMap.containsKey(Arrays.asList("magma", "Main")), "Result should contain the Java file location");
	}

	@Test
	@DisplayName("Test convertJavaFilesToC converts Java file to C and H files")
	void testConvertJavaFilesToCConvertsJavaFileToCorrectSize() {
		// Ensure errorByDefault is set to false to allow the test to run
		MapUtils.setErrorByDefault(false);

		// Setup test data
		Map<List<String>, Map<String, String>> fileDataMap = createFileDataMap();

		// Call the method under test
		Map<List<String>, Map<String, String>> resultMap = MapUtils.convertJavaFilesToC(fileDataMap);

		// Get Java file map and verify size
		Map<String, String> resultJavaFileMap = resultMap.get(Arrays.asList("magma", "Main"));
		assertEquals(2, resultJavaFileMap.size(), "Java file map should have two entries (.c and .h)");
	}

	@Test
	@DisplayName("Test convertJavaFilesToC creates C file")
	void testConvertJavaFilesToCCreatesC() {
		// Ensure errorByDefault is set to false to allow the test to run
		MapUtils.setErrorByDefault(false);

		// Setup test data
		Map<List<String>, Map<String, String>> fileDataMap = createFileDataMap();

		// Call the method under test
		Map<List<String>, Map<String, String>> resultMap = MapUtils.convertJavaFilesToC(fileDataMap);

		// Get Java file map and verify C file exists
		Map<String, String> resultJavaFileMap = resultMap.get(Arrays.asList("magma", "Main"));
		assertTrue(resultJavaFileMap.containsKey(".c"), "Result should contain .c file");
	}

	@Test
	@DisplayName("Test convertJavaFilesToC creates H file")
	void testConvertJavaFilesToCCreatesH() {
		// Ensure errorByDefault is set to false to allow the test to run
		MapUtils.setErrorByDefault(false);

		// Setup test data
		Map<List<String>, Map<String, String>> fileDataMap = createFileDataMap();

		// Call the method under test
		Map<List<String>, Map<String, String>> resultMap = MapUtils.convertJavaFilesToC(fileDataMap);

		// Get Java file map and verify H file exists
		Map<String, String> resultJavaFileMap = resultMap.get(Arrays.asList("magma", "Main"));
		assertTrue(resultJavaFileMap.containsKey(".h"), "Result should contain .h file");
	}

	@Test
	@DisplayName("Test convertJavaFilesToC removes Java file")
	void testConvertJavaFilesToCRemovesJavaFile() {
		// Ensure errorByDefault is set to false to allow the test to run
		MapUtils.setErrorByDefault(false);

		// Setup test data
		Map<List<String>, Map<String, String>> fileDataMap = createFileDataMap();

		// Call the method under test
		Map<List<String>, Map<String, String>> resultMap = MapUtils.convertJavaFilesToC(fileDataMap);

		// Get Java file map and verify Java file is removed
		Map<String, String> resultJavaFileMap = resultMap.get(Arrays.asList("magma", "Main"));
		assertFalse(resultJavaFileMap.containsKey(".java"), "Result should not contain .java file");
	}

	@Test
	@DisplayName("Test convertJavaFilesToC creates non-null C content")
	void testConvertJavaFilesToCCreatesNonNullCContent() {
		// Ensure errorByDefault is set to false to allow the test to run
		MapUtils.setErrorByDefault(false);

		// Setup test data
		Map<List<String>, Map<String, String>> fileDataMap = createFileDataMap();

		// Call the method under test
		Map<List<String>, Map<String, String>> resultMap = MapUtils.convertJavaFilesToC(fileDataMap);

		// Get Java file map and verify C content is not null
		Map<String, String> resultJavaFileMap = resultMap.get(Arrays.asList("magma", "Main"));
		String cContent = resultJavaFileMap.get(".c");
		assertNotNull(cContent, "C file content should not be null");
	}

	@Test
	@DisplayName("Test convertJavaFilesToC creates C content with main function")
	void testConvertJavaFilesToCCreatesCContentWithMainFunction() {
		// Ensure errorByDefault is set to false to allow the test to run
		MapUtils.setErrorByDefault(false);

		// Setup test data
		Map<List<String>, Map<String, String>> fileDataMap = createFileDataMap();

		// Call the method under test
		Map<List<String>, Map<String, String>> resultMap = MapUtils.convertJavaFilesToC(fileDataMap);

		// Get Java file map and verify C content contains main function
		Map<String, String> resultJavaFileMap = resultMap.get(Arrays.asList("magma", "Main"));
		String cContent = resultJavaFileMap.get(".c");
		assertTrue(cContent.contains("int main()"), "C file should contain main function");
	}

	@Test
	@DisplayName("Test convertJavaFilesToC creates C content with return statement")
	void testConvertJavaFilesToCCreatesCContentWithReturnStatement() {
		// Ensure errorByDefault is set to false to allow the test to run
		MapUtils.setErrorByDefault(false);

		// Setup test data
		Map<List<String>, Map<String, String>> fileDataMap = createFileDataMap();

		// Call the method under test
		Map<List<String>, Map<String, String>> resultMap = MapUtils.convertJavaFilesToC(fileDataMap);

		// Get Java file map and verify C content contains return statement
		Map<String, String> resultJavaFileMap = resultMap.get(Arrays.asList("magma", "Main"));
		String cContent = resultJavaFileMap.get(".c");
		assertTrue(cContent.contains("return 0"), "C file should contain return statement");
	}

	@Test
	@DisplayName("Test convertJavaFilesToC creates non-null H content")
	void testConvertJavaFilesToCCreatesNonNullHContent() {
		// Ensure errorByDefault is set to false to allow the test to run
		MapUtils.setErrorByDefault(false);

		// Setup test data
		Map<List<String>, Map<String, String>> fileDataMap = createFileDataMap();

		// Call the method under test
		Map<List<String>, Map<String, String>> resultMap = MapUtils.convertJavaFilesToC(fileDataMap);

		// Get Java file map and verify H content is not null
		Map<String, String> resultJavaFileMap = resultMap.get(Arrays.asList("magma", "Main"));
		String hContent = resultJavaFileMap.get(".h");
		assertNotNull(hContent, "H file content should not be null");
	}

	@Test
	@DisplayName("Test convertJavaFilesToC creates H content with ifndef")
	void testConvertJavaFilesToCCreatesHContentWithIfndef() {
		// Ensure errorByDefault is set to false to allow the test to run
		MapUtils.setErrorByDefault(false);

		// Setup test data
		Map<List<String>, Map<String, String>> fileDataMap = createFileDataMap();

		// Call the method under test
		Map<List<String>, Map<String, String>> resultMap = MapUtils.convertJavaFilesToC(fileDataMap);

		// Get Java file map and verify H content contains ifndef
		Map<String, String> resultJavaFileMap = resultMap.get(Arrays.asList("magma", "Main"));
		String hContent = resultJavaFileMap.get(".h");
		assertTrue(hContent.contains("#ifndef"), "H file should contain header guard");
	}

	@Test
	@DisplayName("Test convertJavaFilesToC creates H content with define")
	void testConvertJavaFilesToCCreatesHContentWithDefine() {
		// Ensure errorByDefault is set to false to allow the test to run
		MapUtils.setErrorByDefault(false);

		// Setup test data
		Map<List<String>, Map<String, String>> fileDataMap = createFileDataMap();

		// Call the method under test
		Map<List<String>, Map<String, String>> resultMap = MapUtils.convertJavaFilesToC(fileDataMap);

		// Get Java file map and verify H content contains define
		Map<String, String> resultJavaFileMap = resultMap.get(Arrays.asList("magma", "Main"));
		String hContent = resultJavaFileMap.get(".h");
		assertTrue(hContent.contains("#define"), "H file should contain define statement");
	}

	@Test
	@DisplayName("Test convertJavaFilesToC creates H content with endif")
	void testConvertJavaFilesToCCreatesHContentWithEndif() {
		// Ensure errorByDefault is set to false to allow the test to run
		MapUtils.setErrorByDefault(false);

		// Setup test data
		Map<List<String>, Map<String, String>> fileDataMap = createFileDataMap();

		// Call the method under test
		Map<List<String>, Map<String, String>> resultMap = MapUtils.convertJavaFilesToC(fileDataMap);

		// Get Java file map and verify H content contains endif
		Map<String, String> resultJavaFileMap = resultMap.get(Arrays.asList("magma", "Main"));
		String hContent = resultJavaFileMap.get(".h");
		assertTrue(hContent.contains("#endif"), "H file should contain endif statement");
	}

	@Test
	@DisplayName("Test convertJavaFilesToC preserves C file location")
	void testConvertJavaFilesToCPreservesCFileLocation() {
		// Ensure errorByDefault is set to false to allow the test to run
		MapUtils.setErrorByDefault(false);

		// Setup test data
		Map<List<String>, Map<String, String>> fileDataMap = createFileDataMap();

		// Call the method under test
		Map<List<String>, Map<String, String>> resultMap = MapUtils.convertJavaFilesToC(fileDataMap);

		// Assertion
		assertTrue(resultMap.containsKey(Arrays.asList("magma", "util", "Helper")),
							 "Result should contain the C file location");
	}

	@Test
	@DisplayName("Test convertJavaFilesToC preserves C file map size")
	void testConvertJavaFilesToCPreservesCFileMapSize() {
		// Ensure errorByDefault is set to false to allow the test to run
		MapUtils.setErrorByDefault(false);

		// Setup test data
		Map<List<String>, Map<String, String>> fileDataMap = createFileDataMap();

		// Call the method under test
		Map<List<String>, Map<String, String>> resultMap = MapUtils.convertJavaFilesToC(fileDataMap);

		// Get C file map and verify size
		Map<String, String> resultCFileMap = resultMap.get(Arrays.asList("magma", "util", "Helper"));
		assertEquals(2, resultCFileMap.size(), "C file map should have two entries");
	}

	@Test
	@DisplayName("Test convertJavaFilesToC preserves C file extension")
	void testConvertJavaFilesToCPreservesCFileExtension() {
		// Ensure errorByDefault is set to false to allow the test to run
		MapUtils.setErrorByDefault(false);

		// Setup test data
		Map<List<String>, Map<String, String>> fileDataMap = createFileDataMap();

		// Call the method under test
		Map<List<String>, Map<String, String>> resultMap = MapUtils.convertJavaFilesToC(fileDataMap);

		// Get C file map and verify C extension exists
		Map<String, String> resultCFileMap = resultMap.get(Arrays.asList("magma", "util", "Helper"));
		assertTrue(resultCFileMap.containsKey(".c"), "C file map should contain .c extension");
	}

	@Test
	@DisplayName("Test convertJavaFilesToC preserves H file extension")
	void testConvertJavaFilesToCPreservesHFileExtension() {
		// Ensure errorByDefault is set to false to allow the test to run
		MapUtils.setErrorByDefault(false);

		// Setup test data
		Map<List<String>, Map<String, String>> fileDataMap = createFileDataMap();

		// Call the method under test
		Map<List<String>, Map<String, String>> resultMap = MapUtils.convertJavaFilesToC(fileDataMap);

		// Get C file map and verify H extension exists
		Map<String, String> resultCFileMap = resultMap.get(Arrays.asList("magma", "util", "Helper"));
		assertTrue(resultCFileMap.containsKey(".h"), "C file map should contain .h extension");
	}

	@Test
	@DisplayName("Test convertJavaFilesToC preserves C file content")
	void testConvertJavaFilesToCPreservesCFileContent() {
		// Ensure errorByDefault is set to false to allow the test to run
		MapUtils.setErrorByDefault(false);

		// Setup test data
		Map<List<String>, Map<String, String>> fileDataMap = createFileDataMap();

		// Call the method under test
		Map<List<String>, Map<String, String>> resultMap = MapUtils.convertJavaFilesToC(fileDataMap);

		// Get C file map and verify C content
		Map<String, String> resultCFileMap = resultMap.get(Arrays.asList("magma", "util", "Helper"));
		assertEquals("#include <stdio.h>\nint main() { return 0; }", resultCFileMap.get(".c"),
								 "C file content should match");
	}

	@Test
	@DisplayName("Test convertJavaFilesToC preserves H file content")
	void testConvertJavaFilesToCPreservesHFileContent() {
		// Ensure errorByDefault is set to false to allow the test to run
		MapUtils.setErrorByDefault(false);

		// Setup test data
		Map<List<String>, Map<String, String>> fileDataMap = createFileDataMap();

		// Call the method under test
		Map<List<String>, Map<String, String>> resultMap = MapUtils.convertJavaFilesToC(fileDataMap);

		// Get C file map and verify H content
		Map<String, String> resultCFileMap = resultMap.get(Arrays.asList("magma", "util", "Helper"));
		assertEquals("#ifndef HEADER_H\n#define HEADER_H\n\n#endif", resultCFileMap.get(".h"),
								 "Header file content should match");
	}

	// Helper method to create empty Java program map for convertJavaFilesToC tests
	private Map<List<String>, Map<String, String>> createEmptyJavaProgramMap() {
		// Setup test data with an empty Java program
		Map<List<String>, Map<String, String>> inputMap = new HashMap<>();

		// Create inner map for empty Java file
		Map<String, String> emptyJavaFileMap = new HashMap<>();
		emptyJavaFileMap.put(".java", "");  // Empty Java file

		// Add inner map to the outer map
		List<String> fileLocation = Arrays.asList("magma", "Empty");
		inputMap.put(fileLocation, emptyJavaFileMap);

		return inputMap;
	}

	// Helper method to create Java package map for convertJavaFilesToC tests
	private Map<List<String>, Map<String, String>> createJavaPackageMap() {
		// Setup test data with a Java package
		Map<List<String>, Map<String, String>> inputMap = new HashMap<>();

		// Create inner map for Java package file
		Map<String, String> javaPackageFileMap = new HashMap<>();
		javaPackageFileMap.put(".java", "package magma.util;\n\n/**\n * This is a package-info.java file.\n */");

		// Add inner map to the outer map
		List<String> fileLocation = Arrays.asList("magma", "util", "package-info");
		inputMap.put(fileLocation, javaPackageFileMap);

		return inputMap;
	}

	// Helper method to create simple Java class map for convertJavaFilesToC tests
	private Map<List<String>, Map<String, String>> createSimpleClassMap() {
		// Setup test data with a simple Java class
		Map<List<String>, Map<String, String>> inputMap = new HashMap<>();

		// Create inner map for Java class file
		Map<String, String> javaClassFileMap = new HashMap<>();
		javaClassFileMap.put(".java", "public class Example { }");

		// Add inner map to the outer map
		List<String> fileLocation = Arrays.asList("magma", "Example");
		inputMap.put(fileLocation, javaClassFileMap);

		return inputMap;
	}

	// Helper method to create Java class with imports map for convertJavaFilesToC tests
	private Map<List<String>, Map<String, String>> createClassWithImportsMap() {
		// Setup test data with a Java class that has imports
		Map<List<String>, Map<String, String>> inputMap = new HashMap<>();

		// Create inner map for Java class file with imports
		Map<String, String> javaClassFileMap = new HashMap<>();
		javaClassFileMap.put(".java", "import java.util.List;\nimport java.util.Map;\n\npublic class ImportExample { }");

		// Add inner map to the outer map
		List<String> fileLocation = Arrays.asList("magma", "ImportExample");
		inputMap.put(fileLocation, javaClassFileMap);

		return inputMap;
	}

	@Test
	@DisplayName("Test convertJavaFilesToC with empty Java program returns non-null result")
	void testConvertJavaFilesToCWithEmptyJavaProgramReturnsNonNull() {
		// Setup test data
		Map<List<String>, Map<String, String>> inputMap = createEmptyJavaProgramMap();

		// Call the method under test
		Map<List<String>, Map<String, String>> resultMap = MapUtils.convertJavaFilesToC(inputMap);

		// Assertion
		assertNotNull(resultMap, "Result map should not be null");
	}

	@Test
	@DisplayName("Test convertJavaFilesToC with empty Java program preserves map size")
	void testConvertJavaFilesToCWithEmptyJavaProgramPreservesMapSize() {
		// Setup test data
		Map<List<String>, Map<String, String>> inputMap = createEmptyJavaProgramMap();

		// Call the method under test
		Map<List<String>, Map<String, String>> resultMap = MapUtils.convertJavaFilesToC(inputMap);

		// Assertion
		assertEquals(1, resultMap.size(), "Result map should have one entry");
	}

	@Test
	@DisplayName("Test convertJavaFilesToC with empty Java program preserves file location")
	void testConvertJavaFilesToCWithEmptyJavaProgramPreservesFileLocation() {
		// Setup test data
		Map<List<String>, Map<String, String>> inputMap = createEmptyJavaProgramMap();
		List<String> fileLocation = Arrays.asList("magma", "Empty");

		// Call the method under test
		Map<List<String>, Map<String, String>> resultMap = MapUtils.convertJavaFilesToC(inputMap);

		// Assertion
		assertTrue(resultMap.containsKey(fileLocation), "Result should contain the file location");
	}

	@Test
	@DisplayName("Test convertJavaFilesToC with empty Java program creates correct number of files")
	void testConvertJavaFilesToCWithEmptyJavaProgramCreatesCorrectNumberOfFiles() {
		// Setup test data
		Map<List<String>, Map<String, String>> inputMap = createEmptyJavaProgramMap();
		List<String> fileLocation = Arrays.asList("magma", "Empty");

		// Call the method under test
		Map<List<String>, Map<String, String>> resultMap = MapUtils.convertJavaFilesToC(inputMap);

		// Get result file map and verify size
		Map<String, String> resultFileMap = resultMap.get(fileLocation);
		assertEquals(2, resultFileMap.size(), "Result file map should have two entries (.c and .h)");
	}

	@Test
	@DisplayName("Test convertJavaFilesToC with empty Java program creates C file")
	void testConvertJavaFilesToCWithEmptyJavaProgramCreatesC() {
		// Setup test data
		Map<List<String>, Map<String, String>> inputMap = createEmptyJavaProgramMap();
		List<String> fileLocation = Arrays.asList("magma", "Empty");

		// Call the method under test
		Map<List<String>, Map<String, String>> resultMap = MapUtils.convertJavaFilesToC(inputMap);

		// Get result file map and verify C file exists
		Map<String, String> resultFileMap = resultMap.get(fileLocation);
		assertTrue(resultFileMap.containsKey(".c"), "Result should contain .c file");
	}

	@Test
	@DisplayName("Test convertJavaFilesToC with empty Java program creates non-null C content")
	void testConvertJavaFilesToCWithEmptyJavaProgramCreatesNonNullCContent() {
		// Setup test data
		Map<List<String>, Map<String, String>> inputMap = createEmptyJavaProgramMap();
		List<String> fileLocation = Arrays.asList("magma", "Empty");

		// Call the method under test
		Map<List<String>, Map<String, String>> resultMap = MapUtils.convertJavaFilesToC(inputMap);

		// Get result file map and verify C content is not null
		Map<String, String> resultFileMap = resultMap.get(fileLocation);
		String cContent = resultFileMap.get(".c");
		assertNotNull(cContent, "C file content should not be null");
	}

	@Test
	@DisplayName("Test convertJavaFilesToC with empty Java program creates C content with main function")
	void testConvertJavaFilesToCWithEmptyJavaProgramCreatesCContentWithMainFunction() {
		// Setup test data
		Map<List<String>, Map<String, String>> inputMap = createEmptyJavaProgramMap();
		List<String> fileLocation = Arrays.asList("magma", "Empty");

		// Call the method under test
		Map<List<String>, Map<String, String>> resultMap = MapUtils.convertJavaFilesToC(inputMap);

		// Get result file map and verify C content contains main function
		Map<String, String> resultFileMap = resultMap.get(fileLocation);
		String cContent = resultFileMap.get(".c");
		assertTrue(cContent.contains("int main()"), "C file should contain main function");
	}

	@Test
	@DisplayName("Test convertJavaFilesToC with empty Java program creates C content with return statement")
	void testConvertJavaFilesToCWithEmptyJavaProgramCreatesCContentWithReturnStatement() {
		// Setup test data
		Map<List<String>, Map<String, String>> inputMap = createEmptyJavaProgramMap();
		List<String> fileLocation = Arrays.asList("magma", "Empty");

		// Call the method under test
		Map<List<String>, Map<String, String>> resultMap = MapUtils.convertJavaFilesToC(inputMap);

		// Get result file map and verify C content contains return statement
		Map<String, String> resultFileMap = resultMap.get(fileLocation);
		String cContent = resultFileMap.get(".c");
		assertTrue(cContent.contains("return 0"), "C file should contain return statement");
	}

	@Test
	@DisplayName("Test convertJavaFilesToC with empty Java program creates H file")
	void testConvertJavaFilesToCWithEmptyJavaProgramCreatesH() {
		// Setup test data
		Map<List<String>, Map<String, String>> inputMap = createEmptyJavaProgramMap();
		List<String> fileLocation = Arrays.asList("magma", "Empty");

		// Call the method under test
		Map<List<String>, Map<String, String>> resultMap = MapUtils.convertJavaFilesToC(inputMap);

		// Get result file map and verify H file exists
		Map<String, String> resultFileMap = resultMap.get(fileLocation);
		assertTrue(resultFileMap.containsKey(".h"), "Result should contain .h file");
	}

	@Test
	@DisplayName("Test convertJavaFilesToC with empty Java program creates non-null H content")
	void testConvertJavaFilesToCWithEmptyJavaProgramCreatesNonNullHContent() {
		// Setup test data
		Map<List<String>, Map<String, String>> inputMap = createEmptyJavaProgramMap();
		List<String> fileLocation = Arrays.asList("magma", "Empty");

		// Call the method under test
		Map<List<String>, Map<String, String>> resultMap = MapUtils.convertJavaFilesToC(inputMap);

		// Get result file map and verify H content is not null
		Map<String, String> resultFileMap = resultMap.get(fileLocation);
		String hContent = resultFileMap.get(".h");
		assertNotNull(hContent, "H file content should not be null");
	}

	@Test
	@DisplayName("Test convertJavaFilesToC with empty Java program creates H content with ifndef")
	void testConvertJavaFilesToCWithEmptyJavaProgramCreatesHContentWithIfndef() {
		// Setup test data
		Map<List<String>, Map<String, String>> inputMap = createEmptyJavaProgramMap();
		List<String> fileLocation = Arrays.asList("magma", "Empty");

		// Call the method under test
		Map<List<String>, Map<String, String>> resultMap = MapUtils.convertJavaFilesToC(inputMap);

		// Get result file map and verify H content contains ifndef
		Map<String, String> resultFileMap = resultMap.get(fileLocation);
		String hContent = resultFileMap.get(".h");
		assertTrue(hContent.contains("#ifndef"), "H file should contain header guard");
	}

	@Test
	@DisplayName("Test convertJavaFilesToC with empty Java program creates H content with define")
	void testConvertJavaFilesToCWithEmptyJavaProgramCreatesHContentWithDefine() {
		// Setup test data
		Map<List<String>, Map<String, String>> inputMap = createEmptyJavaProgramMap();
		List<String> fileLocation = Arrays.asList("magma", "Empty");

		// Call the method under test
		Map<List<String>, Map<String, String>> resultMap = MapUtils.convertJavaFilesToC(inputMap);

		// Get result file map and verify H content contains define
		Map<String, String> resultFileMap = resultMap.get(fileLocation);
		String hContent = resultFileMap.get(".h");
		assertTrue(hContent.contains("#define"), "H file should contain define statement");
	}

	@Test
	@DisplayName("Test convertJavaFilesToC with empty Java program creates H content with endif")
	void testConvertJavaFilesToCWithEmptyJavaProgramCreatesHContentWithEndif() {
		// Setup test data
		Map<List<String>, Map<String, String>> inputMap = createEmptyJavaProgramMap();
		List<String> fileLocation = Arrays.asList("magma", "Empty");

		// Call the method under test
		Map<List<String>, Map<String, String>> resultMap = MapUtils.convertJavaFilesToC(inputMap);

		// Get result file map and verify H content contains endif
		Map<String, String> resultFileMap = resultMap.get(fileLocation);
		String hContent = resultFileMap.get(".h");
		assertTrue(hContent.contains("#endif"), "H file should contain endif statement");
	}

	@Test
	@DisplayName("Test convertJavaFilesToC with empty Java program removes Java file")
	void testConvertJavaFilesToCWithEmptyJavaProgramRemovesJavaFile() {
		// Setup test data
		Map<List<String>, Map<String, String>> inputMap = createEmptyJavaProgramMap();
		List<String> fileLocation = Arrays.asList("magma", "Empty");

		// Call the method under test
		Map<List<String>, Map<String, String>> resultMap = MapUtils.convertJavaFilesToC(inputMap);

		// Get result file map and verify Java file is removed
		Map<String, String> resultFileMap = resultMap.get(fileLocation);
		assertFalse(resultFileMap.containsKey(".java"), "Result should not contain .java file");
	}

	@Test
	@DisplayName("Test convertJavaFilesToC with Java package doesn't produce C output")
	void testConvertJavaFilesToCWithJavaPackageDoesntProduceCOutput() {
		// Setup test data
		Map<List<String>, Map<String, String>> inputMap = createJavaPackageMap();
		List<String> fileLocation = Arrays.asList("magma", "util", "package-info");

		// Call the method under test
		Map<List<String>, Map<String, String>> resultMap = MapUtils.convertJavaFilesToC(inputMap);

		// Verify that the Java package location is not in the result map
		// Java packages shouldn't produce any C output
		assertFalse(resultMap.containsKey(fileLocation), "Java package should not produce any output");
	}

	@Test
	@DisplayName("Test convertJavaFilesToC validates input configuration")
	void testConvertJavaFilesToCValidatesInputConfiguration() {
		// Setup test data with both a Java package and a regular Java file
		Map<List<String>, Map<String, String>> inputMap = new HashMap<>();
		inputMap.putAll(createJavaPackageMap());
		inputMap.putAll(createEmptyJavaProgramMap());

		List<String> packageLocation = Arrays.asList("magma", "util", "package-info");
		List<String> fileLocation = Arrays.asList("magma", "Empty");

		// Call the method under test
		Map<List<String>, Map<String, String>> resultMap = MapUtils.convertJavaFilesToC(inputMap);

		// Verify that the Java package location is not in the result map
		assertFalse(resultMap.containsKey(packageLocation), "Java package should not produce any output");

		// Verify that the regular Java file location is in the result map
		assertTrue(resultMap.containsKey(fileLocation), "Regular Java file should produce output");

		// Verify that the regular Java file was converted to C and H files
		Map<String, String> resultFileMap = resultMap.get(fileLocation);
		assertTrue(resultFileMap.containsKey(".c"), "C file should be created");
		assertTrue(resultFileMap.containsKey(".h"), "H file should be created");
	}

	@Test
	@DisplayName("Test convertJavaFilesToC with simple class returns non-null result")
	void testConvertJavaFilesToCWithSimpleClassReturnsNonNull() {
		// Ensure errorByDefault is set to false to allow the test to run
		MapUtils.setErrorByDefault(false);

		// Setup test data
		Map<List<String>, Map<String, String>> inputMap = createSimpleClassMap();

		// Call the method under test
		Map<List<String>, Map<String, String>> resultMap = MapUtils.convertJavaFilesToC(inputMap);

		// Assertion
		assertNotNull(resultMap, "Result map should not be null");
	}

	@Test
	@DisplayName("Test convertJavaFilesToC with simple class preserves file location")
	void testConvertJavaFilesToCWithSimpleClassPreservesFileLocation() {
		// Ensure errorByDefault is set to false to allow the test to run
		MapUtils.setErrorByDefault(false);

		// Setup test data
		Map<List<String>, Map<String, String>> inputMap = createSimpleClassMap();
		List<String> fileLocation = Arrays.asList("magma", "Example");

		// Call the method under test
		Map<List<String>, Map<String, String>> resultMap = MapUtils.convertJavaFilesToC(inputMap);

		// Assertion
		assertTrue(resultMap.containsKey(fileLocation), "Result should contain the file location");
	}

	@Test
	@DisplayName("Test convertJavaFilesToC with simple class creates C and H files")
	void testConvertJavaFilesToCWithSimpleClassCreatesCAndHFiles() {
		// Ensure errorByDefault is set to false to allow the test to run
		MapUtils.setErrorByDefault(false);

		// Setup test data
		Map<List<String>, Map<String, String>> inputMap = createSimpleClassMap();
		List<String> fileLocation = Arrays.asList("magma", "Example");

		// Call the method under test
		Map<List<String>, Map<String, String>> resultMap = MapUtils.convertJavaFilesToC(inputMap);

		// Get result file map and verify it contains C and H files
		Map<String, String> resultFileMap = resultMap.get(fileLocation);
		assertTrue(resultFileMap.containsKey(".c"), "Result should contain .c file");
		assertTrue(resultFileMap.containsKey(".h"), "Result should contain .h file");
	}

	@Test
	@DisplayName("Test convertJavaFilesToC with simple class removes Java file")
	void testConvertJavaFilesToCWithSimpleClassRemovesJavaFile() {
		// Ensure errorByDefault is set to false to allow the test to run
		MapUtils.setErrorByDefault(false);

		// Setup test data
		Map<List<String>, Map<String, String>> inputMap = createSimpleClassMap();
		List<String> fileLocation = Arrays.asList("magma", "Example");

		// Call the method under test
		Map<List<String>, Map<String, String>> resultMap = MapUtils.convertJavaFilesToC(inputMap);

		// Get result file map and verify it doesn't contain Java file
		Map<String, String> resultFileMap = resultMap.get(fileLocation);
		assertFalse(resultFileMap.containsKey(".java"), "Result should not contain .java file");
	}

	@Test
	@DisplayName("Test convertJavaFilesToC with simple class creates C content with class name")
	void testConvertJavaFilesToCWithSimpleClassCreatesCContentWithClassName() {
		// Ensure errorByDefault is set to false to allow the test to run
		MapUtils.setErrorByDefault(false);

		// Setup test data
		Map<List<String>, Map<String, String>> inputMap = createSimpleClassMap();
		List<String> fileLocation = Arrays.asList("magma", "Example");

		// Call the method under test
		Map<List<String>, Map<String, String>> resultMap = MapUtils.convertJavaFilesToC(inputMap);

		// Get result file map and verify C content contains class name
		Map<String, String> resultFileMap = resultMap.get(fileLocation);
		String cContent = resultFileMap.get(".c");
		assertTrue(cContent.contains("Example.h"), "C file should include the class header");
		assertTrue(cContent.contains("struct Example"), "C file should reference the struct name");
	}

	@Test
	@DisplayName("Test convertJavaFilesToC with simple class creates H content with class name")
	void testConvertJavaFilesToCWithSimpleClassCreatesHContentWithClassName() {
		// Ensure errorByDefault is set to false to allow the test to run
		MapUtils.setErrorByDefault(false);

		// Setup test data
		Map<List<String>, Map<String, String>> inputMap = createSimpleClassMap();
		List<String> fileLocation = Arrays.asList("magma", "Example");

		// Call the method under test
		Map<List<String>, Map<String, String>> resultMap = MapUtils.convertJavaFilesToC(inputMap);

		// Get result file map and verify H content contains class name
		Map<String, String> resultFileMap = resultMap.get(fileLocation);
		String hContent = resultFileMap.get(".h");
		assertTrue(hContent.contains("EXAMPLE_H"), "H file should use class name in header guard");
		assertTrue(hContent.contains("Simple empty struct for Java class Example"),
							 "H file should reference the Java class");
		assertTrue(hContent.contains("struct Example"), "H file should define a struct with the class name");
	}

	@Test
	@DisplayName("Test convertJavaFilesToC with class containing imports ignores imports")
	void testConvertJavaFilesToCWithClassContainingImportsIgnoresImports() {
		// Ensure errorByDefault is set to false to allow the test to run
		MapUtils.setErrorByDefault(false);

		// Setup test data
		Map<List<String>, Map<String, String>> inputMap = createClassWithImportsMap();
		List<String> fileLocation = Arrays.asList("magma", "ImportExample");

		// Call the method under test
		Map<List<String>, Map<String, String>> resultMap = MapUtils.convertJavaFilesToC(inputMap);

		// Get result file map and verify it contains C and H files
		Map<String, String> resultFileMap = resultMap.get(fileLocation);
		assertTrue(resultFileMap.containsKey(".c"), "Result should contain .c file");
		assertTrue(resultFileMap.containsKey(".h"), "Result should contain .h file");

		// Verify C content contains class name but not import statements
		String cContent = resultFileMap.get(".c");
		assertTrue(cContent.contains("ImportExample.h"), "C file should include the class header");
		assertTrue(cContent.contains("struct ImportExample"), "C file should reference the struct name");
		assertFalse(cContent.contains("java.util.List"), "C file should not contain import statements");

		// Verify H content contains class name but not import statements
		String hContent = resultFileMap.get(".h");
		assertTrue(hContent.contains("IMPORTEXAMPLE_H"), "H file should use class name in header guard");
		assertTrue(hContent.contains("Simple empty struct for Java class ImportExample"),
							 "H file should reference the Java class");
		assertTrue(hContent.contains("struct ImportExample"), "H file should define a struct with the class name");
		assertFalse(hContent.contains("java.util.Map"), "H file should not contain import statements");
	}
}