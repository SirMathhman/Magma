package magma;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Compiler {
	private static final Pattern LET_PATTERN = Pattern.compile(
			"let\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*(?:\\s*:\\s*(I8|I16|I32|I64|U8|U16|U32|U64|Bool))?\\s*=\\s*(?:([0-9]+)(I8|I16|I32|I64|U8|U16|U32|U64)?|(true|false|True|False|TRUE|FALSE));");

	public static String compile(String input) throws CompileException {
		if (input.isEmpty()) return "";

		Matcher matcher = LET_PATTERN.matcher(input);
		if (matcher.matches()) {
			return processLetStatement(new LetStatement(matcher));
		}

		throw new CompileException();
	}

	private static String processLetStatement(LetStatement stmt) throws CompileException {
		if (stmt.getBooleanValue() != null) {
			String normalizedBoolValue = stmt.getBooleanValue().toLowerCase();
			String type = (stmt.getTypeAnnotation() != null) ? mapTypeToC(stmt.getTypeAnnotation()) : "bool";
			return stmt.formatDeclaration(type, normalizedBoolValue);
		}

		return processNumericLiteral(stmt);
	}

	private static String processNumericLiteral(LetStatement stmt) throws CompileException {
		// If this is a numeric value but has a Bool type annotation, throw an exception
		if (stmt.getTypeAnnotation() != null && stmt.getTypeAnnotation().equals("Bool")) {
			throw new CompileException();
		}

		// Check type compatibility if both annotation and suffix are present
		if (stmt.getTypeAnnotation() != null && stmt.getSuffix() != null) {
			validateTypeCompatibility(stmt.getTypeAnnotation(), stmt.getSuffix());
		}

		String type = determineType(stmt);
		return stmt.formatDeclaration(type, stmt.getNumericValue());
	}

	private static String determineType(LetStatement stmt) {
		if (stmt.getSuffix() != null) {
			return mapTypeToC(stmt.getSuffix());
		} else if (stmt.getTypeAnnotation() != null) {
			return mapTypeToC(stmt.getTypeAnnotation());
		} else {
			return "int32_t"; // Default type
		}
	}

	private static String mapTypeToC(String type) {
		if ("Bool".equals(type)) {
			return "bool";
		}

		return mapNumericTypeToC(type);
	}

	private static String mapNumericTypeToC(String type) {
		if (type.startsWith("I")) {
			return mapSignedTypeToC(type);
		} else if (type.startsWith("U")) {
			return mapUnsignedTypeToC(type);
		}
		return "int32_t";
	}

	private static String mapSignedTypeToC(String type) {
		switch (type) {
			case "I8":
				return "int8_t";
			case "I16":
				return "int16_t";
			case "I32":
				return "int32_t";
			case "I64":
				return "int64_t";
			default:
				return "int32_t";
		}
	}

	private static String mapUnsignedTypeToC(String type) {
		switch (type) {
			case "U8":
				return "uint8_t";
			case "U16":
				return "uint16_t";
			case "U32":
				return "uint32_t";
			case "U64":
				return "uint64_t";
			default:
				return "int32_t";
		}
	}

	private static void validateTypeCompatibility(String typeAnnotation, String suffix) throws CompileException {
		boolean isSignedAnnotation = typeAnnotation != null && typeAnnotation.startsWith("I");
		boolean isUnsignedSuffix = suffix != null && suffix.startsWith("U");

		if (isSignedAnnotation && isUnsignedSuffix) {
			throw new CompileException();
		}
	}
}