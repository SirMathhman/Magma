package magma;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main entry point for the Magma Java-to-C compiler.
 * <p>
 * This class demonstrates the basic structure of compiler input and output
 * using the MapUtils class. In future implementations, this will be replaced
 * with actual compiler functionality.
 * <p>
 * Following Kent Beck's rules of simple design:
 * 1. Passes all tests
 * 2. Reveals intention through clear code and documentation
 * 3. No duplication
 * 4. Fewest elements needed
 */
public class Main {
	public static void main(String[] args) {
		// Example usage of the two-dimensional map function that will be used for compiler I/O
		Map<List<String>, Map<String, String>> exampleMap = new HashMap<>();

		// Create inner maps representing file content by extension
		Map<String, String> javaFileMap = new HashMap<>();
		javaFileMap.put(".java", "public class Example { }");  // Java file that will trigger the error by default

		// Create an empty Java file map
		Map<String, String> emptyJavaFileMap = new HashMap<>();
		emptyJavaFileMap.put(".java", "");  // Empty Java file that will be converted to C program

		Map<String, String> cFileMap = new HashMap<>();
		cFileMap.put(".c", "#include <stdio.h>\nint main() { return 0; }");
		cFileMap.put(".h", "#ifndef HEADER_H\n#define HEADER_H\n\n#endif");

		// Add inner maps to the outer map with List<String> keys representing file paths
		exampleMap.put(Arrays.asList("magma", "Example"), javaFileMap);
		exampleMap.put(Arrays.asList("magma", "EmptyExample"), emptyJavaFileMap);
		exampleMap.put(Arrays.asList("magma", "util", "Helper"), cFileMap);

		try {
			// This will throw a CompilationException because errorByDefault is true by default
			System.out.println("Attempting to process map with Java files (should fail)...");
			System.out.println("This line should not be reached if errorByDefault is true.");
		} catch (MapUtils.CompilationException e) {
			System.out.println("Expected error occurred: " + e.getMessage());

			// Now demonstrate how to override the default behavior
			System.out.println("\nOverriding errorByDefault to allow compilation...");
			MapUtils.setErrorByDefault(false);

			try {
				// This should now succeed because we've set errorByDefault to false
				Map<List<String>, Map<String, String>> resultMap = MapUtils.processTwoDimensionalMap(exampleMap);
				System.out.println("Successfully processed map after overriding errorByDefault.");
				System.out.println("Result map: " + resultMap);
			} catch (Exception e2) {
				System.out.println("Unexpected error after overriding errorByDefault: " + e2.getMessage());
			}
		}
	}
}
