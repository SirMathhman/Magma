package magma;

class MutableContext {
	boolean mutSeen = false;
	String mutVar = null;
}

public class Compiler {
	private static class StatementContext {
		StringBuilder output = new StringBuilder();
		MutableContext mutContext = new MutableContext();
		java.util.Map<String, String> varTypes = new java.util.HashMap<>();
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
			int beforeLen = ctx.output.length();
			processStatement(stmt, ctx);
			// Ensure space after each statement except the first, but avoid double spaces
			if (ctx.output.length() > beforeLen && ctx.output.length() > 0 && ctx.output.charAt(ctx.output.length() - 1) == ';') {
				// Only add space if not already present
				if (ctx.output.length() < 2 || ctx.output.charAt(ctx.output.length() - 2) != ' ') {
					ctx.output.append(' ');
				}
			}
		}
		return ctx.output.toString().trim();
	}

	private void processStatement(String stmt, StatementContext ctx) {
		if (stmt.startsWith("let mut ")) {
			String body = stmt.substring(8).trim();
			String result = handleLet(body, ctx);
			if (result != null) {
				ctx.output.append(result);
				ctx.mutContext.mutSeen = true;
				ctx.mutContext.mutVar = body.split("=")[0].trim();
			} else {
				throw new CompileException("Input is not supported");
			}
		} else if (stmt.startsWith("let ")) {
			String body = stmt.substring(4).trim();
			String result = handleLet(body, ctx);
			if (result != null) {
				ctx.output.append(result);
			} else {
				throw new CompileException("Input is not supported");
			}
	} else if (ctx.mutContext.mutSeen && ctx.mutContext.mutVar != null
		&& stmt.startsWith(ctx.mutContext.mutVar + " = ")) {
	    ctx.output.append(stmt).append(";");
		} else {
			throw new CompileException("Input is not supported");
		}
	}

	private String handleLet(String body, StatementContext ctx) {
		body = body.replace(";", "").trim();
		int colonIdx = body.indexOf(":");
		int eqIdx = body.indexOf("=");
		if (colonIdx != -1 && eqIdx != -1 && colonIdx < eqIdx) {
			return handleExplicitType(body, colonIdx, eqIdx, ctx);
		}
		if (eqIdx != -1) {
			return handleAnnotatedOrDefault(body, eqIdx, ctx);
		}
		return null;
	}

	private String handleExplicitType(String body, int colonIdx, int eqIdx, StatementContext ctx) {
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
		boolean isVar = ctx.varTypes.containsKey(value);
		if (valueType == null && isVar) {
			valueType = ctx.varTypes.get(value);
		}
		// If assigning from a variable, check its type against annotation
		if (isVar && ctx.varTypes.containsKey(value) && !type.equals(ctx.varTypes.get(value))) {
			throw new CompileException("Type annotation does not match value type");
		}
		// If assigning from a literal, check type suffix
		if (!isVar && valueType != null && !type.equals(valueType)) {
			throw new CompileException("Type annotation does not match value type");
		}
		String cType = magmaTypeToC(type);
		String emitValue;
		if (!isVar && valueType != null && value.endsWith(valueType)) {
			emitValue = value.substring(0, value.length() - valueType.length());
		} else {
			emitValue = value;
		}
		if (cType != null) {
			ctx.varTypes.put(var, type);
			return cType + " " + var + " = " + emitValue + ";";
		}
		return null;
	}

	private String handleAnnotatedOrDefault(String body, int eqIdx, StatementContext ctx) {
		String var = body.substring(0, eqIdx).trim();
		String value = body.substring(eqIdx + 1).trim();
		String type = null;
		String valueType = extractTypeFromValue(value);
		if (valueType != null) {
			type = valueType;
		} else if (ctx.varTypes.containsKey(value)) {
			type = ctx.varTypes.get(value);
		}
		String emitValue;
		String inferredType = type;
		boolean isVar = ctx.varTypes.containsKey(value);
		if (!isVar && valueType != null && value.endsWith(valueType)) {
			emitValue = value.substring(0, value.length() - valueType.length());
		} else {
			emitValue = value;
			if (type == null && isVar) {
				inferredType = ctx.varTypes.get(value);
			}
		}
		String finalType = inferredType != null ? inferredType : "I32";
		String cType = magmaTypeToC(finalType);
		if (cType == null) {
			cType = "int32_t";
			finalType = "I32";
		}
		ctx.varTypes.put(var, finalType);
		return cType + " " + var + " = " + emitValue + ";";
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
