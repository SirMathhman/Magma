package magma;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Compiler {
	// Map to track variable mutability
	private static final Map<String, Boolean> variableMutability = new HashMap<>();

	// Helper method to map Magma types to C++ types
	private static String mapMagmaTypeToCpp(String magmaType) throws CompileException {
		switch (magmaType) {
			case "U8":
				return "uint8_t";
			case "U16":
				return "uint16_t";
			case "U32":
				return "uint32_t";
			case "U64":
				return "uint64_t";
			case "I8":
				return "int8_t";
			case "I16":
				return "int16_t";
			case "I32":
				return "int32_t";
			case "I64":
				return "int64_t";
			case "F32":
				return "float";
			case "F64":
				return "double";
			case "Bool":
				return "bool";
			default:
				throw new CompileException();
		}
	}

	/**
	 * Compiles Magma code to C++ code.
	 * Supports both single and multiple variable declarations and statements.
	 * 
	 * @param input The Magma code to compile
	 * @return The compiled C++ code
	 * @throws CompileException If the input is invalid
	 */
	public static String compile(String input) throws CompileException {
		// Note: We don't clear the variableMutability map here to allow tracking variables across statements

		if (input.isEmpty()) return "";

		// Support for multiple variable declarations and statements
		// Check if the input contains multiple statements by looking for patterns like "let ... ; let" or "let ... ; x ="
		if (input.matches("(?s).*let\\s+.*?;\\s*(?:let\\s+|[a-zA-Z_][a-zA-Z0-9_]*\\s*=).*")) {
			// Input contains multiple statements
			StringBuilder result = new StringBuilder();
			
			// Use a robust approach to identify and process each statement
			// This handles complex cases like array declarations with semicolons in the size specification
			// and ensures we don't split inside string literals or nested brackets
			int startPos = 0;
			int endPos;
			int depth = 0; // Track nesting level of brackets
			boolean inString = false; // Track if we're inside a string literal
			
			for (int i = 0; i < input.length(); i++) {
				char c = input.charAt(i);
				
				// Handle string literals (accounting for escaped quotes)
				if (c == '"' && (i == 0 || input.charAt(i - 1) != '\\')) {
					inString = !inString;
				}
				
				// Skip processing if we're inside a string literal
				if (inString) {
					continue;
				}
				
				// Track nesting level of brackets
				if (c == '[' || c == '{') {
					depth++;
				} else if (c == ']' || c == '}') {
					depth--;
				}
				
				// If we find a semicolon at depth 0, we've found the end of a statement
				if (c == ';' && depth == 0) {
					endPos = i + 1; // Include the semicolon
					String statement = input.substring(startPos, endPos).trim();
					
					if (!statement.isEmpty()) {
						// Process the statement using the original compile logic
						String processedStatement = compileOriginal(statement);
						if (!processedStatement.isEmpty()) {
							if (result.length() > 0) {
								result.append("\n");
							}
							result.append(processedStatement);
						}
					}
					
					startPos = endPos;
				}
			}
			
			// Process any remaining part of the input
			if (startPos < input.length()) {
				String statement = input.substring(startPos).trim();
				if (!statement.isEmpty()) {
					String processedStatement = compileOriginal(statement);
					if (!processedStatement.isEmpty()) {
						if (result.length() > 0) {
							result.append("\n");
						}
						result.append(processedStatement);
					}
				}
			}
			
			return result.toString();
		}
		
		// If not multiple statements, process as a single statement using the original logic
		return compileOriginal(input);
	}
	
	/**
	 * Original compile method logic for processing a single statement.
	 * This preserves the existing functionality for single declarations.
	 * 
	 * @param input The single Magma statement to compile
	 * @return The compiled C++ code
	 * @throws CompileException If the input is invalid
	 */
	private static String compileOriginal(String input) throws CompileException {
		if (input.isEmpty()) return "";

		// Pattern to match "let x = 100;" or "let mut x = 100;" or "let x : TYPE = 100;" or "let mut x : TYPE = 100;" or "let x = 100TYPE;" format
		// where TYPE can be U8, U16, U32, U64, I8, I16, I32, I64, F32, F64
		Pattern numericPattern = Pattern.compile(
				"let\\s+(mut\\s+)?([a-zA-Z_][a-zA-Z0-9_]*)\\s*(?:\\s*:\\s*(U8|U16|U32|U64|I8|I16|I32|I64|F32|F64)\\s*)?=\\s*(\\d+(?:\\.\\d+)?)(U8|U16|U32|U64|I8|I16|I32|I64|F32|F64)?\\s*;");
		Matcher numericMatcher = numericPattern.matcher(input);

		// Pattern to match "let x = true;" or "let mut x = true;" or "let x : Bool = false;" or "let mut x : Bool = false;" format
		Pattern boolPattern =
				Pattern.compile("let\\s+(mut\\s+)?([a-zA-Z_][a-zA-Z0-9_]*)\\s*(?:\\s*:\\s*(Bool)\\s*)?=\\s*(true|false)\\s*;");
		Matcher boolMatcher = boolPattern.matcher(input);

		// Pattern to match "let values : *[U8; 3] = [1, 2, 3];" or "let mut values : *[U8; 3] = [1, 2, 3];" format
		Pattern arrayPattern = Pattern.compile(
				"let\\s+(mut\\s+)?([a-zA-Z_][a-zA-Z0-9_]*)\\s*:\\s*\\*\\[(U8|U16|U32|U64|I8|I16|I32|I64|F32|F64)\\s*;\\s*(\\d+)]\\s*=\\s*\\[(\\d+(?:\\s*,\\s*\\d+)*)]\\s*;");
		Matcher arrayMatcher = arrayPattern.matcher(input);

		// Pattern to match "let array : *[U8; 2, 2] = [[1, 2], [3, 4]];" or "let mut array : *[U8; 2, 2] = [[1, 2], [3, 4]];" format (2D array)
		Pattern array2DPattern = Pattern.compile(
				"let\\s+(mut\\s+)?([a-zA-Z_][a-zA-Z0-9_]*)\\s*:\\s*\\*\\[(U8|U16|U32|U64|I8|I16|I32|I64|F32|F64)\\s*;\\s*(\\d+)\\s*,\\s*(\\d+)]\\s*=\\s*\\[(\\[\\d+(?:\\s*,\\s*\\d+)*](?:\\s*,\\s*\\[\\d+(?:\\s*,\\s*\\d+)*])*)]\\s*;");
		Matcher array2DMatcher = array2DPattern.matcher(input);

		// Pattern to match "let string : *[U8; 5] = "Hello";" or "let mut string : *[U8; 5] = "Hello";" format
		Pattern stringArrayPattern = Pattern.compile(
				"let\\s+(mut\\s+)?([a-zA-Z_][a-zA-Z0-9_]*)\\s*:\\s*\\*\\[(U8|U16|U32|U64|I8|I16|I32|I64|F32|F64)\\s*;\\s*(\\d+)]\\s*=\\s*\"([^\"]*)\"\\s*;");
		Matcher stringArrayMatcher = stringArrayPattern.matcher(input);

		// Pattern to match "let y : *I32 = &x;" or "let mut y : *I32 = &x;" format (pointer declaration)
		Pattern pointerPattern = Pattern.compile(
				"let\\s+(mut\\s+)?([a-zA-Z_][a-zA-Z0-9_]*)\\s*:\\s*\\*(U8|U16|U32|U64|I8|I16|I32|I64|F32|F64|Bool)\\s*=\\s*&([a-zA-Z_][a-zA-Z0-9_]*)\\s*;");
		Matcher pointerMatcher = pointerPattern.matcher(input);

		// Pattern to match "let z : I32 = *y;" or "let mut z : I32 = *y;" format (pointer dereferencing)
		Pattern dereferencePattern = Pattern.compile(
				"let\\s+(mut\\s+)?([a-zA-Z_][a-zA-Z0-9_]*)\\s*:\\s*(U8|U16|U32|U64|I8|I16|I32|I64|F32|F64|Bool)\\s*=\\s*\\*([a-zA-Z_][a-zA-Z0-9_]*)\\s*;");
		Matcher dereferenceMatcher = dereferencePattern.matcher(input);

		// Pattern to match "let y = x;" or "let mut y = x;" format (variable reference)
		// Exclude TRUE and FALSE as valid variable names on the right-hand side
		Pattern variableReferencePattern = Pattern.compile(
				"let\\s+(mut\\s+)?([a-zA-Z_][a-zA-Z0-9_]*)\\s*(?:\\s*:\\s*(U8|U16|U32|U64|I8|I16|I32|I64|F32|F64|Bool)\\s*)?=\\s*(?!(TRUE|FALSE)\\b)([a-zA-Z_][a-zA-Z0-9_]*)\\s*;");
		Matcher variableReferenceMatcher = variableReferencePattern.matcher(input);

		// Pattern to match "x = 100;" format (variable reassignment)
		Pattern reassignmentPattern = Pattern.compile(
				"([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*(\\d+(?:\\.\\d+)?)(U8|U16|U32|U64|I8|I16|I32|I64|F32|F64)?\\s*;");
		Matcher reassignmentMatcher = reassignmentPattern.matcher(input);

		// Pattern to match "x = true;" or "x = false;" format (boolean reassignment)
		Pattern boolReassignmentPattern = Pattern.compile("([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*(true|false)\\s*;");
		Matcher boolReassignmentMatcher = boolReassignmentPattern.matcher(input);

		// Pattern to match "x = y;" format (variable reference reassignment)
		// Exclude TRUE and FALSE as valid variable names on the right-hand side
		Pattern variableReferenceReassignmentPattern =
				Pattern.compile("([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*(?!(TRUE|FALSE)\\b)([a-zA-Z_][a-zA-Z0-9_]*)\\s*;");
		Matcher variableReferenceReassignmentMatcher = variableReferenceReassignmentPattern.matcher(input);

		if (stringArrayMatcher.matches()) {
			String mutKeyword = stringArrayMatcher.group(1);
			String variableName = stringArrayMatcher.group(2);
			String elementType = stringArrayMatcher.group(3);
			String arraySize = stringArrayMatcher.group(4);
			String stringLiteral = stringArrayMatcher.group(5);

			// Track variable mutability
			boolean isMutable = mutKeyword != null;
			variableMutability.put(variableName, isMutable);

			// Validate that the string length matches the declared size
			if (stringLiteral.length() != Integer.parseInt(arraySize)) {
				throw new CompileException();
			}

			// Map Magma array element type to C++ type
			String cppType = mapMagmaTypeToCpp(elementType);

			// Convert each character to its numeric value and build the array initialization string
			StringBuilder cppArrayValues = new StringBuilder();
			for (int i = 0; i < stringLiteral.length(); i++) {
				if (i > 0) {
					cppArrayValues.append(", ");
				}
				cppArrayValues.append((int) stringLiteral.charAt(i));
			}

			// Generate C++ code for array initialization
			return cppType + " " + variableName + "[" + arraySize + "] = {" + cppArrayValues + "};";
		} else if (array2DMatcher.matches()) {
			String mutKeyword = array2DMatcher.group(1);
			String variableName = array2DMatcher.group(2);
			String elementType = array2DMatcher.group(3);
			String rowSize = array2DMatcher.group(4);
			String colSize = array2DMatcher.group(5);
			String arrayValues = array2DMatcher.group(6);

			// Track variable mutability
			boolean isMutable = mutKeyword != null;
			variableMutability.put(variableName, isMutable);

			// Parse the 2D array values
			String[] rows = arrayValues.split("\\s*,\\s*(?=\\[)");
			int rowCount = rows.length;

			// Validate that the number of rows matches the declared row size
			if (rowCount != Integer.parseInt(rowSize)) {
				throw new CompileException();
			}

			// Validate that each row has the correct number of columns
			for (String row : rows) {
				// Remove the square brackets and split by comma
				String rowContent = row.substring(1, row.length() - 1);
				String[] columns = rowContent.split("\\s*,\\s*");

				// Validate that the number of columns matches the declared column size
				if (columns.length != Integer.parseInt(colSize)) {
					throw new CompileException();
				}
			}

			// Map Magma array element type to C++ type
			String cppType = mapMagmaTypeToCpp(elementType);

			// Format the 2D array values for C++ initialization
			// Replace outer square brackets with curly braces
			String cppArrayValues = "{" + arrayValues.replaceAll("\\[", "{").replaceAll("]", "}") + "}";

			// Generate C++ code for 2D array initialization
			return cppType + " " + variableName + "[" + rowSize + "][" + colSize + "] = " + cppArrayValues + ";";
		} else if (arrayMatcher.matches()) {
			String mutKeyword = arrayMatcher.group(1);
			String variableName = arrayMatcher.group(2);
			String elementType = arrayMatcher.group(3);
			String arraySize = arrayMatcher.group(4);
			String arrayValues = arrayMatcher.group(5);

			// Track variable mutability
			boolean isMutable = mutKeyword != null;
			variableMutability.put(variableName, isMutable);

			// Count the number of elements in the array
			String[] elements = arrayValues.split("\\s*,\\s*");
			int elementCount = elements.length;

			// Validate that the number of elements matches the declared size
			if (elementCount != Integer.parseInt(arraySize)) {
				throw new CompileException();
			}

			// Map Magma array element type to C++ type
			String cppType = mapMagmaTypeToCpp(elementType);

			// Format the array values for C++ initialization
			String cppArrayValues = arrayValues.replaceAll("\\s*,\\s*", ", ");

			// Generate C++ code for array initialization
			return cppType + " " + variableName + "[" + arraySize + "] = {" + cppArrayValues + "};";
		} else if (numericMatcher.matches()) {
			String mutKeyword = numericMatcher.group(1);
			String variableName = numericMatcher.group(2);
			String typeAnnotation = numericMatcher.group(3);
			String value = numericMatcher.group(4);
			String typeSuffix = numericMatcher.group(5);

			// Track variable mutability
			boolean isMutable = mutKeyword != null;
			variableMutability.put(variableName, isMutable);

			// Use type suffix if present, otherwise use type annotation, or default to I32
			String type;
			// The numeric value is already captured without the suffix
			type = Objects.requireNonNullElseGet(typeSuffix, () -> Objects.requireNonNullElse(typeAnnotation, "I32"));

			// Validate type consistency when both type annotation and type suffix are present
			if (typeAnnotation != null && typeSuffix != null && !typeAnnotation.equals(typeSuffix)) {
				throw new CompileException();
			}

			// Map Magma types to C++ types
			String cppType = mapMagmaTypeToCpp(type);

			return cppType + " " + variableName + " = " + value + ";";
		} else if (boolMatcher.matches()) {
			String mutKeyword = boolMatcher.group(1);
			String variableName = boolMatcher.group(2);
			String typeAnnotation = boolMatcher.group(3);
			String value = boolMatcher.group(4);

			// Track variable mutability
			boolean isMutable = mutKeyword != null;
			variableMutability.put(variableName, isMutable);

			// Bool type is always Bool
			String type = "Bool";

			// Validate type annotation if present
			if (typeAnnotation != null && !typeAnnotation.equals(type)) {
				throw new CompileException();
			}

			// Map Bool to C++ bool using the helper method
			String cppType = mapMagmaTypeToCpp(type);

			return cppType + " " + variableName + " = " + value + ";";
		} else if (pointerMatcher.matches()) {
			String mutKeyword = pointerMatcher.group(1);
			String variableName = pointerMatcher.group(2);
			String type = pointerMatcher.group(3);
			String referencedVariable = pointerMatcher.group(4);

			// Track variable mutability
			boolean isMutable = mutKeyword != null;
			variableMutability.put(variableName, isMutable);

			// Map Magma type to C++ type
			String cppType = mapMagmaTypeToCpp(type);

			// Generate C++ code for pointer declaration
			return cppType + "* " + variableName + " = &" + referencedVariable + ";";
		} else if (dereferenceMatcher.matches()) {
			String mutKeyword = dereferenceMatcher.group(1);
			String variableName = dereferenceMatcher.group(2);
			String type = dereferenceMatcher.group(3);
			String pointerVariable = dereferenceMatcher.group(4);

			// Track variable mutability
			boolean isMutable = mutKeyword != null;
			variableMutability.put(variableName, isMutable);

			// Map Magma type to C++ type
			String cppType = mapMagmaTypeToCpp(type);

			// Generate C++ code for pointer dereferencing
			return cppType + " " + variableName + " = *" + pointerVariable + ";";
		} else if (variableReferenceMatcher.matches()) {
			String mutKeyword = variableReferenceMatcher.group(1);
			String variableName = variableReferenceMatcher.group(2);
			String typeAnnotation = variableReferenceMatcher.group(3);
			String referencedVariable = variableReferenceMatcher.group(5); // Group index changed due to negative lookahead

			// Track variable mutability
			boolean isMutable = mutKeyword != null;
			variableMutability.put(variableName, isMutable);

			// Check if the referenced variable is TRUE or FALSE (case-insensitive)
			if (referencedVariable.equalsIgnoreCase("TRUE") || referencedVariable.equalsIgnoreCase("FALSE")) {
				throw new CompileException();
			}

			// Use type annotation if present, otherwise use auto
			String cppType = typeAnnotation != null ? mapMagmaTypeToCpp(typeAnnotation) : "auto";

			// Generate C++ code for variable reference
			return cppType + " " + variableName + " = " + referencedVariable + ";";
		} else if (reassignmentMatcher.matches()) {
			String variableName = reassignmentMatcher.group(1);
			String value = reassignmentMatcher.group(2);
			String typeSuffix = reassignmentMatcher.group(3);

			// Check if the variable exists and is mutable
			Boolean isMutable = variableMutability.get(variableName);
			if (isMutable == null) {
				// Variable doesn't exist
				throw new CompileException();
			}
			if (!isMutable) {
				// Variable is not mutable
				throw new CompileException();
			}

			// Generate C++ code for variable reassignment
			return variableName + " = " + value + ";";
		} else if (boolReassignmentMatcher.matches()) {
			String variableName = boolReassignmentMatcher.group(1);
			String value = boolReassignmentMatcher.group(2);

			// Check if the variable exists and is mutable
			Boolean isMutable = variableMutability.get(variableName);
			if (isMutable == null) {
				// Variable doesn't exist
				throw new CompileException();
			}
			if (!isMutable) {
				// Variable is not mutable
				throw new CompileException();
			}

			// Generate C++ code for boolean reassignment
			return variableName + " = " + value + ";";
		} else if (variableReferenceReassignmentMatcher.matches()) {
			String variableName = variableReferenceReassignmentMatcher.group(1);
			String referencedVariable =
					variableReferenceReassignmentMatcher.group(3); // Group index changed due to negative lookahead

			// Check if the variable exists and is mutable
			Boolean isMutable = variableMutability.get(variableName);
			if (isMutable == null) {
				// Variable doesn't exist
				throw new CompileException();
			}
			if (!isMutable) {
				// Variable is not mutable
				throw new CompileException();
			}

			// Check if the referenced variable is TRUE or FALSE (case-insensitive)
			if (referencedVariable.equalsIgnoreCase("TRUE") || referencedVariable.equalsIgnoreCase("FALSE")) {
				throw new CompileException();
			}

			// Generate C++ code for variable reference reassignment
			return variableName + " = " + referencedVariable + ";";
		}

		throw new CompileException();
	}
}
