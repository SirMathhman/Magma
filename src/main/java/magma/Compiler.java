package magma;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Compiler {
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

	public static String compile(String input) throws CompileException {
		if (input.isEmpty()) return "";

		// Pattern to match "let x = 100;" or "let x : TYPE = 100;" or "let x = 100TYPE;" format
		// where TYPE can be U8, U16, U32, U64, I8, I16, I32, I64, F32, F64
		Pattern numericPattern = Pattern.compile(
				"let\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*(?:\\s*:\\s*(U8|U16|U32|U64|I8|I16|I32|I64|F32|F64)\\s*)?=\\s*(\\d+(?:\\.\\d+)?)(U8|U16|U32|U64|I8|I16|I32|I64|F32|F64)?\\s*;");
		Matcher numericMatcher = numericPattern.matcher(input);

		// Pattern to match "let x = true;" or "let x : Bool = false;" format
		Pattern boolPattern =
				Pattern.compile("let\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*(?:\\s*:\\s*(Bool)\\s*)?=\\s*(true|false)\\s*;");
		Matcher boolMatcher = boolPattern.matcher(input);

		// Pattern to match "let values : [U8; 3] = [1, 2, 3];" format
		Pattern arrayPattern = Pattern.compile(
				"let\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*:\\s*\\[(U8|U16|U32|U64|I8|I16|I32|I64|F32|F64)\\s*;\\s*(\\d+)]\\s*=\\s*\\[(\\d+(?:\\s*,\\s*\\d+)*)]\\s*;");
		Matcher arrayMatcher = arrayPattern.matcher(input);

		if (arrayMatcher.matches()) {
			String variableName = arrayMatcher.group(1);
			String elementType = arrayMatcher.group(2);
			String arraySize = arrayMatcher.group(3);
			String arrayValues = arrayMatcher.group(4);

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
			String variableName = numericMatcher.group(1);
			String typeAnnotation = numericMatcher.group(2);
			String value = numericMatcher.group(3);
			String typeSuffix = numericMatcher.group(4);

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
			String variableName = boolMatcher.group(1);
			String typeAnnotation = boolMatcher.group(2);
			String value = boolMatcher.group(3);

			// Bool type is always Bool
			String type = "Bool";

			// Validate type annotation if present
			if (typeAnnotation != null && !typeAnnotation.equals(type)) {
				throw new CompileException();
			}

			// Map Bool to C++ bool using the helper method
			String cppType = mapMagmaTypeToCpp(type);

			return cppType + " " + variableName + " = " + value + ";";
		}

		throw new CompileException();
	}
}
