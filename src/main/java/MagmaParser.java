import java.util.ArrayList;
import java.util.List;

/**
 * Parser for Magma code.
 * This class provides functionality to parse Magma code and identify its components.
 */
public class MagmaParser {
	/**
	 * Checks if the Magma code contains any declarations (array or variable).
	 * Supports the following declaration formats:
	 * - Single-dimensional arrays: "let x : [Type; Size] = [val1, val2, ...];"
	 * - Multi-dimensional arrays: "let x : [Type; Size1, Size2, ...] = [[val1, val2], [val3, val4], ...];"
	 * - String declarations: "let x : [U8; Size] = "string";"
	 * - Typed variables: "let x : Type = value;"
	 * - Typeless variables: "let x = value;" (type is inferred)
	 * Supports all basic types (I8-I64, U8-U64, Bool, U8 for characters).
	 *
	 * @param magmaCode The Magma source code to check
	 * @return True if the code contains any declarations
	 */
	public static boolean containsDeclarations(String magmaCode) {
		if (!magmaCode.contains("let ")) return false;

		// Check for single-dimensional array declarations with semicolon syntax
		boolean hasSingleDimArrayDeclarations =
				magmaCode.matches("(?s).*let\\s+[a-zA-Z_][a-zA-Z0-9_]*\\s+:\\s+\\[[a-zA-Z0-9]+;\\s*[0-9]+]\\s+=\\s+\\[.*");

		// Check for multi-dimensional array declarations with semicolon syntax
		boolean hasMultiDimArrayDeclarations = magmaCode.matches(
				"(?s).*let\\s+[a-zA-Z_][a-zA-Z0-9_]*\\s+:\\s+\\[[a-zA-Z0-9]+;\\s*[0-9]+,\\s*[0-9]+.*]\\s+=\\s+\\[.*");

		// Check for string declarations
		boolean hasStringDeclarations =
				magmaCode.matches("(?s).*let\\s+[a-zA-Z_][a-zA-Z0-9_]*\\s+:\\s+\\[U8;\\s*[0-9]+]\\s+=\\s+\".*");

		// Check for variable declarations with explicit types
		boolean hasExplicitTypeDeclarations = false;
		for (TypeMapper typeMapper : TypeMapper.values())
			if (magmaCode.contains(typeMapper.typePattern())) {
				hasExplicitTypeDeclarations = true;
				break;
			}

		// Check for typeless declarations (let x = value;)
		boolean hasTypelessDeclarations = magmaCode.matches("(?s).*let\\s+[a-zA-Z_][a-zA-Z0-9_]*\\s+=\\s+.*");

		return hasSingleDimArrayDeclarations || hasMultiDimArrayDeclarations || hasStringDeclarations ||
					 hasExplicitTypeDeclarations || hasTypelessDeclarations;
	}

	/**
	 * Checks if the Magma code contains any assignments.
	 * Supports assignments in the format "variableName = value;".
	 * An assignment is identified by a line that doesn't start with "let " but contains an equals sign.
	 *
	 * @param magmaCode The Magma source code to check
	 * @return True if the code contains any assignments
	 */
	public static boolean containsAssignments(String magmaCode) {
		// Split the code into lines
		String[] lines = magmaCode.split("\n");

		// Check each line for assignments
		for (String line : lines) {
			String trimmedLine = line.trim();
			// If the line doesn't start with "let " but contains an equals sign, it's an assignment
			if (!trimmedLine.startsWith("let ") && trimmedLine.contains("=")) return true;
		}

		return false;
	}

	/**
	 * Processes a line that may contain multiple declarations separated by semicolons.
	 * Handles semicolons that are part of array type declarations correctly.
	 * Delegates to MagmaProcessor.processLineWithMultipleDeclarations.
	 *
	 * @param line The line to process
	 * @return The generated C code as a string
	 */
	public static String processLineWithMultipleDeclarations(String line) {
		return MagmaProcessor.processLineWithMultipleDeclarations(line);
	}

	/**
	 * Splits a line into individual declarations.
	 * Handles semicolons that are part of array type declarations correctly.
	 *
	 * @param line The line to split
	 * @return A list of individual declarations
	 */
	public static List<String> splitLineIntoDeclarations(String line) {
		List<String> declarations = new ArrayList<>();

		// If the line doesn't contain any semicolons, return the whole line
		if (!line.contains(";")) {
			declarations.add(line);
			return declarations;
		}

		// Find all declaration boundaries
		List<Integer> splitPoints = findDeclarationSplitPoints(line);

		// Extract declarations using the split points
		return extractDeclarationsFromSplitPoints(line, splitPoints);
	}

	/**
	 * Finds the points where declarations should be split.
	 * These are the indices of "let " that follow a semicolon that's not inside an array.
	 *
	 * @param line The line to analyze
	 * @return A list of indices where declarations should be split
	 */
	public static List<Integer> findDeclarationSplitPoints(String line) {
		List<Integer> splitPoints = new ArrayList<>();
		splitPoints.add(0); // Always include the start of the line

		ParsingState state = new ParsingState(false, false);

		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);

			// Update tracking state
			state = updateTrackingState(c, state);

			// Skip if not a semicolon or if inside an array
			if (c != ';' || state.insideArrayType() || state.insideArrayValue()) continue;

			// Check if there's another declaration after this semicolon
			int nextLetIndex = line.indexOf("let ", i + 1);
			if (nextLetIndex == -1) continue;

			// Found a valid split point
			splitPoints.add(nextLetIndex);
			i = nextLetIndex - 1; // Skip to the next declaration
		}

		return splitPoints;
	}

	/**
	 * Updates the tracking state based on the current character.
	 *
	 * @param c            The current character
	 * @param currentState The current parsing state
	 * @return The updated parsing state
	 */
	public static ParsingState updateTrackingState(char c, ParsingState currentState) {
		boolean insideArrayType = currentState.insideArrayType();
		boolean insideArrayValue = currentState.insideArrayValue();

		// Track if we're inside an array type declaration [Type; Size]
		if (c == '[') insideArrayType = true;
		else if (c == ']') insideArrayType = false;

		// Track if we're inside an array value [val1, val2, ...]
		if (c == ']' && insideArrayValue) insideArrayValue = false;

		// Only create a new state object if something changed
		if (insideArrayType != currentState.insideArrayType() || insideArrayValue != currentState.insideArrayValue())
			return new ParsingState(insideArrayType, insideArrayValue);

		return currentState;
	}

	/**
	 * Extracts declarations from a line using the split points.
	 *
	 * @param line        The line to extract declarations from
	 * @param splitPoints The indices where declarations start
	 * @return A list of extracted declarations
	 */
	public static List<String> extractDeclarationsFromSplitPoints(String line, List<Integer> splitPoints) {
		List<String> declarations = new ArrayList<>();

		// Process each split point
		for (int i = 0; i < splitPoints.size(); i++) {
			int startIndex = splitPoints.get(i);
			int endIndex =
					(i < splitPoints.size() - 1) ? findDeclarationEnd(line, startIndex, splitPoints.get(i + 1)) : line.length();

			String declaration = line.substring(startIndex, endIndex).trim();
			if (!declaration.isEmpty()) declarations.add(declaration);
		}

		return declarations;
	}

	/**
	 * Finds the end of a declaration.
	 *
	 * @param line           The line containing the declaration
	 * @param startIndex     The start index of the declaration
	 * @param nextStartIndex The start index of the next declaration
	 * @return The end index of the declaration
	 */
	public static int findDeclarationEnd(String line, int startIndex, int nextStartIndex) {
		// Look for a semicolon before the next declaration
		int semicolonIndex = line.lastIndexOf(";", nextStartIndex - 1);

		// If found and it's after the start of this declaration, use it
		if (semicolonIndex > startIndex) return semicolonIndex;

		// Otherwise, use the start of the next declaration
		return nextStartIndex;
	}
}