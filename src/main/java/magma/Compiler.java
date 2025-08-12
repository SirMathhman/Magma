package magma;

class MutableContext {
	boolean mutSeen = false;
	String mutVar = null;
}

public class Compiler {
	private static class StatementContext {
		StringBuilder output = new StringBuilder();
		MutableContext mutContext = new MutableContext();
	}

	// TODO: Implement compiler logic
	public String compile(String source) {
		if (source.isEmpty()) {
			return "";
		}
		String s = source.trim();

		// Support multiple statements separated by semicolons
		String[] statements = s.split(";");
		StatementContext ctx = new StatementContext();
		for (String stmt : statements) {
			stmt = stmt.trim();
			if (stmt.isEmpty()) {
				continue;
			}
			processStatement(stmt, ctx);
		}
		return ctx.output.toString().trim();
	}

	private void processStatement(String stmt, StatementContext ctx) {
		if (stmt.startsWith("let mut ")) {
			String body = stmt.substring(8).trim();
			String result = handleLet(body);
			if (result != null) {
				ctx.output.append(result);
				ctx.mutContext.mutSeen = true;
				ctx.mutContext.mutVar = body.split("=")[0].trim();
			} else {
				throw new CompileException("Input is not supported");
			}
		} else if (stmt.startsWith("let ")) {
			String body = stmt.substring(4).trim();
			String result = handleLet(body);
			if (result != null) {
				ctx.output.append(result);
			} else {
				throw new CompileException("Input is not supported");
			}
		} else if (ctx.mutContext.mutSeen && ctx.mutContext.mutVar != null
				&& stmt.startsWith(ctx.mutContext.mutVar + " = ")) {
			ctx.output.append(" ").append(stmt).append(";");
		} else {
			throw new CompileException("Input is not supported");
		}
	}

	private String handleLet(String body) {
		body = body.replace(";", "").trim();
		int colonIdx = body.indexOf(":");
		int eqIdx = body.indexOf("=");
		if (colonIdx != -1 && eqIdx != -1 && colonIdx < eqIdx) {
			return handleExplicitType(body, colonIdx, eqIdx);
		}
		if (eqIdx != -1) {
			return handleAnnotatedOrDefault(body, eqIdx);
		}
		return null;
	}

	private String handleExplicitType(String body, int colonIdx, int eqIdx) {
		// Refactor to use only 2 parameters for CheckStyle compliance
		String[] parts = body.split(":");
		if (parts.length != 2) {
			return null;
		}
		String var = parts[0].trim();
		String[] typeValue = parts[1].split("=");
		if (typeValue.length != 2) {
			return null;
		}
		String type = typeValue[0].trim();
		String value = typeValue[1].trim();
		String valueType = extractTypeFromValue(value);
		if (valueType != null && !type.equals(valueType)) {
			throw new CompileException("Type annotation does not match value type");
		}
		String cType = magmaTypeToC(type);
		if (cType != null) {
			return cType + " " + var + " = " + value + ";";
		}
		return null;
	}

	private String handleAnnotatedOrDefault(String body, int eqIdx) {
		String var = body.substring(0, eqIdx).trim();
		String valueType = body.substring(eqIdx + 1).trim();
		int typeStart = -1;
		for (int i = 0; i < valueType.length(); i++) {
			char ch = valueType.charAt(i);
			if (!Character.isDigit(ch)) {
				typeStart = i;
				break;
			}
		}
		if (typeStart != -1) {
			String value = valueType.substring(0, typeStart);
			String type = valueType.substring(typeStart);
			String cType = magmaTypeToC(type);
			if (cType != null) {
				return cType + " " + var + " = " + value + ";";
			}
		}
		// If no type, default to int32_t
		if (typeStart == -1) {
			String value = valueType;
			return "int32_t " + var + " = " + value + ";";
		}
		return null;
	}

	// Helper to extract type from value suffix (e.g., 0U64 -> U64)
	private String extractTypeFromValue(String value) {
		int idx = -1;
		for (int i = 0; i < value.length(); i++) {
			char ch = value.charAt(i);
			if (!Character.isDigit(ch)) {
				idx = i;
				break;
			}
		}
		if (idx != -1) {
			return value.substring(idx);
		}
		return null;
	}

	private String magmaTypeToC(String type) {
		switch (type) {
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
			default:
				return null;
		}
	}
}
