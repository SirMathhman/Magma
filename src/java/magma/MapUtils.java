package magma;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for handling compiler input and output data structures.
 * <p>
 * This class provides functions for working with two-dimensional maps that represent
 * the file structure used by the Magma Java-to-C compiler. In the current implementation,
 * these functions are stubs that will be expanded in future versions.
 * <p>
 * Following Kent Beck's rules of simple design:
 * 1. Passes all tests
 * 2. Reveals intention through clear code and documentation
 * 3. No duplication
 * 4. Fewest elements needed
 */
public class MapUtils {

	/**
	 * Processes a two-dimensional map representing compiler input and output.
	 *
	 * @param inputMap A two-dimensional map where:
	 *                 - First level keys (List<String>) represent file locations (e.g., [magma, Main] for magma/Main.java)
	 *                 - Second level keys (String) represent file extensions (e.g., .java, .c, .h)
	 *                 - Values (String) represent file content
	 * @return A processed two-dimensional map of the same type as the input
	 */
	public static Map<List<String>, Map<String, String>> processTwoDimensionalMap(Map<List<String>, Map<String, String>> inputMap) {
		// Create a new map to store the processed results
		Map<List<String>, Map<String, String>> resultMap = new HashMap<>();

		// Process each entry in the input map
		for (Map.Entry<List<String>, Map<String, String>> entry : inputMap.entrySet()) {
			List<String> fileLocation = entry.getKey();
			Map<String, String> extensionContentMap = entry.getValue();

			// Create a new inner map for this file location
			final Map<String, String> processedExtensionContentMap = getStringStringMap(extensionContentMap);

			// Add the processed inner map to the result map
			resultMap.put(fileLocation, processedExtensionContentMap);
		}

		return resultMap;
	}

	/**
	 * Helper method that processes a map of file extensions to content.
	 * <p>
	 * In future implementations, this method will apply different processing
	 * logic based on file extensions:
	 * - For Java files (.java): Parse and compile the Java source code
	 * - For C files (.c, .h): Generate or modify C source and header files
	 *
	 * @param extensionContentMap Map of file extensions to file content
	 * @return Processed map of file extensions to file content
	 */
	private static Map<String, String> getStringStringMap(Map<String, String> extensionContentMap) {
		Map<String, String> processedExtensionContentMap = new HashMap<>();

		// Process each file extension and content pair
		for (Map.Entry<String, String> innerEntry : extensionContentMap.entrySet()) {
			String fileExtension = innerEntry.getKey();

			// Process based on file extension
			// For Java files, extension will always be .java
			// For C files, extension could be .c or .h
			String processedContent = innerEntry.getValue();

			// Add the processed content to the inner map
			processedExtensionContentMap.put(fileExtension, processedContent);
		}
		return processedExtensionContentMap;
	}
}