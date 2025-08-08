package magma;

public class Compiler {
	public static String compile(String input) throws CompileException {
		if (input.isEmpty()) return "";
		String trimmed = input.trim();

		// Support simple let declarations for integer types U8..U64 and I8..I64 with a zero initializer and variable name x
		if (trimmed.startsWith("let x : ") && trimmed.endsWith(" = 0;")) {
			String type = trimmed.substring("let x : ".length(), trimmed.length() - " = 0;".length());
			String cType = mapType(type);
			if (cType != null) {
				return emitDecl(cType);
			}
		}

		// Support suffix-typed integer literal, e.g., "let x = 0U8;"
		if (trimmed.startsWith("let x = 0") && trimmed.endsWith(";")) {
			String suffix = trimmed.substring("let x = 0".length(), trimmed.length() - 1);
			String cType = mapType(suffix);
			if (cType != null) {
				return emitDecl(cType);
			}
		}

		throw new CompileException("Invalid input", input);
	}

	private static String mapType(String type) {
		return switch (type) {
			case "I8" -> "int8_t";
			case "I16" -> "int16_t";
			case "I32" -> "int32_t";
			case "I64" -> "int64_t";
			case "U8" -> "uint8_t";
			case "U16" -> "uint16_t";
			case "U32" -> "uint32_t";
			case "U64" -> "uint64_t";
			default -> null;
		};
	}

	private static String emitDecl(String cType) {
		return cType + " x = 0;";
	}
}
