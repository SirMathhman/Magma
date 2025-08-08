package magma;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Compiler {
	public static String compile(String input) throws CompileException {
		if (input.isEmpty()) return "";

		// Pattern to match "let x = 100;" or "let x : TYPE = 100;" or "let x = 100TYPE;" format
		// where TYPE can be U8, U16, U32, U64, I8, I16, I32, I64
		Pattern numericPattern = Pattern.compile(
				"let\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*(?:\\s*:\\s*(U8|U16|U32|U64|I8|I16|I32|I64)\\s*)?=\\s*(\\d+)(U8|U16|U32|U64|I8|I16|I32|I64)?\\s*;");
		Matcher numericMatcher = numericPattern.matcher(input);

		// Pattern to match "let x = true;" or "let x : Bool = false;" format
		Pattern boolPattern = Pattern.compile(
				"let\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*(?:\\s*:\\s*(Bool)\\s*)?=\\s*(true|false)\\s*;");
		Matcher boolMatcher = boolPattern.matcher(input);

		if (numericMatcher.matches()) {
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
			String cppType;
			switch (type) {
				case "U8":
					cppType = "uint8_t";
					break;
				case "U16":
					cppType = "uint16_t";
					break;
				case "U32":
					cppType = "uint32_t";
					break;
				case "U64":
					cppType = "uint64_t";
					break;
				case "I8":
					cppType = "int8_t";
					break;
				case "I16":
					cppType = "int16_t";
					break;
				case "I32":
					cppType = "int32_t";
					break;
				case "I64":
					cppType = "int64_t";
					break;
				default:
					throw new CompileException();
			}

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

			// Map Bool to C++ bool
			String cppType = "bool";

			return cppType + " " + variableName + " = " + value + ";";
		}

		throw new CompileException();
	}
}
