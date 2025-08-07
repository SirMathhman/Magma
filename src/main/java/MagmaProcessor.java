import java.util.List;
import java.util.stream.Collectors;

/**
 * Processor for Magma code.
 * This class provides functionality to process Magma code and generate C code.
 */
public class MagmaProcessor {
	/**
	 * Processes a line that may contain multiple declarations separated by semicolons.
	 * Handles semicolons that are part of array type declarations correctly.
	 *
	 * @param line The line to process
	 * @return The generated C code as a string
	 */
	public static String processLineWithMultipleDeclarations(String line) {
		// If the line doesn't contain any declarations, return empty string
		if (!line.contains("let ")) return "";

		// Split the line into declarations
		List<String> declarations = MagmaParser.splitLineIntoDeclarations(line);

		// Process each declaration and join the results
		return declarations.stream().map(MagmaProcessor::processDeclaration).collect(Collectors.joining());
	}

	/**
	 * Processes a single declaration.
	 *
	 * @param declaration The declaration to process
	 * @return The generated C code as a string
	 */
	public static String processDeclaration(String declaration) {
		// Process array declarations
		if (ArrayHandler.isArrayDeclaration(declaration)) return ArrayHandler.processArrayDeclaration(declaration);
		// Process variable declarations

		if (declaration.startsWith("let ")) return VariableHandler.processVariableDeclaration(declaration);

		return "";
	}

	/**
	 * Processes a single line of Magma code to extract assignments.
	 *
	 * @param line The line of Magma code to process
	 * @return The generated C code as a string
	 */
	public static String processAssignment(String line) {
		return VariableHandler.processAssignment(line);
	}
}