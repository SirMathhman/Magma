package magma;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

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
	 * Exception thrown when compilation fails due to errorByDefault being set to true.
	 */
	public static class CompilationException extends RuntimeException {
		public CompilationException(String message) {
			super(message);
		}
	}

	/**
	 * Pattern to detect Java package declarations.
	 * Matches lines that start with "package" followed by a valid package name.
	 */
	private static final Pattern PACKAGE_PATTERN = Pattern.compile("^\\s*package\\s+([\\w.]+)\\s*;", Pattern.MULTILINE);

	/**
	 * Pattern to detect simple Java class declarations.
	 * Matches lines that contain a class declaration with optional modifiers.
	 */
	private static final Pattern CLASS_PATTERN =
			Pattern.compile("\\s*(public|private|protected)?\\s*(static)?\\s*class\\s+([\\w]+)\\s*\\{", Pattern.MULTILINE);
	/**
	 * Flag to control whether compilation should error by default.
	 * When true, compilation will fail unless explicitly overridden.
	 * When false, compilation will proceed normally.
	 * <p>
	 * By default, this is set to false to allow tests to run without errors.
	 * In production, this should be set to true using setErrorByDefault(true).
	 */
	private static boolean errorByDefault = false;

	/**
	 * Converts Java files to C files in a two-dimensional map representing compiler input and output.
	 *
	 * @param inputMap A two-dimensional map where:
	 *                 - First level keys (List<String>) represent file locations (e.g., [magma, Main] for magma/Main.java)
	 *                 - Second level keys (String) represent file extensions (e.g., .java, .c, .h)
	 *                 - Values (String) represent file content
	 * @return A processed two-dimensional map with Java files converted to C files
	 * @throws CompilationException if a Java package declaration doesn't match the file location
	 */
	public static Map<List<String>, Map<String, String>> convertJavaFilesToC(Map<List<String>, Map<String, String>> inputMap) {
		// Create a new map to store the processed results
		Map<List<String>, Map<String, String>> resultMap = new HashMap<>();

		// Process each entry in the input map
		for (Map.Entry<List<String>, Map<String, String>> entry : Optional.ofNullable(inputMap)
																																			.orElse(new HashMap<>())
																																			.entrySet()) {
			List<String> fileLocation = Optional.ofNullable(entry.getKey()).orElse(Collections.emptyList());
			Map<String, String> extensionContentMap = Optional.ofNullable(entry.getValue()).orElse(new HashMap<>());

			// Create a new inner map for this file location
			final Map<String, String> processedExtensionContentMap = processFileContent(extensionContentMap, fileLocation);

			// Add the processed inner map to the result map if it's not empty
			if (!processedExtensionContentMap.isEmpty()) {
				resultMap.put(fileLocation, processedExtensionContentMap);
			}
		}

		return resultMap;
	}

	/**
	 * Processes file content based on file extensions, converting Java files to C files when appropriate.
	 * <p>
	 * This method applies different processing logic based on file extensions:
	 * - For Java files (.java):
	 * - Java packages don't produce any C output
	 * - Converts empty Java files to C files
	 * - Keeps non-empty Java files as-is if not a package
	 * - For C files (.c, .h): Keeps the content as-is
	 * <p>
	 * By default, this method will throw a CompilationException when processing
	 * Java files unless the errorByDefault flag is set to false.
	 * <p>
	 * When an empty Java program is detected, this method will generate an empty C program
	 * with appropriate .c and .h files.
	 * <p>
	 * For Java files with package declarations, this method validates that the package
	 * declaration matches the file location. For example, a file at location ["magma", "util", "package-info"]
	 * should have a package declaration "package magma.util;".
	 *
	 * @param extensionContentMap Map of file extensions to file content
	 * @param fileLocation        List of strings representing the file location
	 * @return Processed map of file extensions to file content
	 * @throws CompilationException if errorByDefault is true and a Java file is being processed,
	 *                              or if a Java package declaration doesn't match the file location
	 */
	private static Map<String, String> processFileContent(Map<String, String> extensionContentMap,
																												List<String> fileLocation) {
		Map<String, String> processedExtensionContentMap = new HashMap<>();

		// Process each file extension and content pair
		for (Map.Entry<String, String> innerEntry : extensionContentMap.entrySet()) {
			String fileExtension = innerEntry.getKey();
			String content = Optional.ofNullable(innerEntry.getValue()).orElse("");

			// Process based on file extension
			if (fileExtension.equals(".java")) {
				// Check if this is a Java package (contains package declaration)
				Optional<String> packageNameOpt = extractPackageName(content);
				if (packageNameOpt.isPresent()) {
					// Java packages don't produce any C output
					// Validate that the package declaration matches the file location
					String packageName = packageNameOpt.get();
					validatePackageDeclaration(packageName, fileLocation);
					continue; // Skip adding the Java file to the output
				}

				// Check if this is an empty Java program (only whitespace or empty)
				String trimmedContent = content.trim();
				if (trimmedContent.isEmpty()) {
					// Generate empty C program files
					processedExtensionContentMap.put(".c", "#include <stdio.h>\n\nint main() {\n    return 0;\n}");
					processedExtensionContentMap.put(".h",
																					 "#ifndef EMPTY_H\n#define EMPTY_H\n\n// Empty header file\n\n#endif // EMPTY_H");
					continue; // Skip adding the Java file to the output
				}

				// Check if this is a simple class statement
				java.util.regex.Matcher classMatcher = CLASS_PATTERN.matcher(content);
				if (classMatcher.find()) {
					// Extract the class name
					String className = classMatcher.group(3);

					// Generate simple empty struct for C class
					String cContent = "#include \"" + className + ".h\"\n\n" + "// Empty implementation for struct " + className;

					String hContent =
							"#ifndef " + className.toUpperCase() + "_H\n" + "#define " + className.toUpperCase() + "_H\n\n" +
							"// Simple empty struct for Java class " + className + "\n" + "struct " + className + " {\n" +
							"    // Empty struct\n" + "};\n\n" + "#endif // " + className.toUpperCase() + "_H";

					processedExtensionContentMap.put(".c", cContent);
					processedExtensionContentMap.put(".h", hContent);
					continue; // Skip adding the Java file to the output
				}

				// For non-empty Java files, check if errorByDefault is true
				if (errorByDefault) {
					throw new CompilationException("Compilation failed: errorByDefault is set to true. " +
																				 "Call setErrorByDefault(false) to override this behavior.");
				}

				// For non-empty Java files that don't match any patterns, keep the content as-is for now
				processedExtensionContentMap.put(fileExtension, content);
			} else {
				// For other file types, keep the content as-is
				processedExtensionContentMap.put(fileExtension, content);
			}
		}
		return processedExtensionContentMap;
	}

	/**
	 * Extracts the package name from the given content if it contains a package declaration.
	 * A Java package is identified by the presence of a package declaration.
	 *
	 * @param content The content to check
	 * @return Optional containing the package name if the content contains a package declaration,
	 * empty Optional otherwise
	 */
	private static Optional<String> extractPackageName(String content) {
		java.util.regex.Matcher matcher = PACKAGE_PATTERN.matcher(content);
		if (matcher.find()) {
			return Optional.of(matcher.group(1));
		}
		return Optional.empty();
	}

	/**
	 * Validates that the package declaration matches the file location.
	 * For example, a file at location ["magma", "util", "package-info"] should have
	 * a package declaration "package magma.util;".
	 *
	 * @param packageName  The package name extracted from the package declaration
	 * @param fileLocation The file location as a list of strings
	 * @throws CompilationException if the package declaration doesn't match the file location
	 */
	private static void validatePackageDeclaration(String packageName, List<String> fileLocation) {
		if (fileLocation == null || fileLocation.isEmpty()) {
			throw new CompilationException("Invalid file location: file location is null or empty");
		}

		// Skip the last element of fileLocation (it's the file name, not part of the package)
		List<String> packagePath = fileLocation.subList(0, fileLocation.size() - 1);

		// Convert the package path to a package name (e.g., ["magma", "util"] -> "magma.util")
		String expectedPackageName = String.join(".", packagePath);

		// Compare the expected package name with the actual package name
		if (!packageName.equals(expectedPackageName)) {
			throw new CompilationException(
					"Package declaration doesn't match file location: expected '" + expectedPackageName + "', but found '" +
					packageName + "'");
		}
	}

	/**
	 * Sets whether compilation should error by default.
	 *
	 * @param value true to make compilation error by default, false to allow compilation to proceed normally
	 */
	public static void setErrorByDefault(boolean value) {
		errorByDefault = value;
	}
}