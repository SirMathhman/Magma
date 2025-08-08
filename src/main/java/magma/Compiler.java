package magma;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Compiler {
	private record ParsedIdentifier(String name, String rest) {}

	private record Declaration(String cType, String value) {}

	public static String compile(String input) throws CompileException {
		if (input.isEmpty()) return "";
		String trimmed = input.trim();

		// Support multiple semicolon-terminated statements
		Map<String, String> env = new HashMap<>(); // varName -> cType
		List<String> outputs = new ArrayList<>();

		int start = 0;
		for (int i = 0; i < trimmed.length(); i++) {
			char ch = trimmed.charAt(i);
			if (ch == ';') {
				String stmt = trimmed.substring(start, i + 1).trim();
				start = i + 1;
				if (stmt.isEmpty()) continue;
				String out = compileStatement(stmt, env);
				outputs.add(out);
			}
		}
		// If no semicolon-terminated statement was found, try as single statement
		if (outputs.isEmpty()) {
			String candidate = trimmed;
			if (!candidate.endsWith(";")) {
				candidate = candidate + ";";
			}
			String out = compileStatement(candidate, env);
			if (out != null) return out;
		}

		if (!outputs.isEmpty()) {
			return String.join(" ", outputs);
		}

		throw new CompileException("Invalid input", input);
	}

	private static String compileStatement(String stmt, Map<String, String> env) throws CompileException {
		String s = stmt.trim();
		if (s.isEmpty()) return null;
		if (!s.endsWith(";")) throw new CompileException("Statement must end with a semicolon", s);
		s = s.substring(0, s.length() - 1).trim(); // drop trailing ';'

		if (!s.startsWith("let ")) throw new CompileException("Statement must start with 'let' keyword", stmt);
		s = s.substring("let ".length());

		// Parse identifier
		ParsedIdentifier parsed = parseIdentifierOrThrow(s, stmt);
		String name = parsed.name();
		String rest = parsed.rest();

		Declaration decl = parseDeclaration(rest, env, stmt);

		env.put(name, decl.cType());
		return emitDecl(name, decl.cType(), decl.value());
	}

	private static ParsedIdentifier parseIdentifierOrThrow(String s, String stmt) throws CompileException {
		int pos = 0;
		while (pos < s.length() && isIdentChar(s.charAt(pos), pos == 0)) pos++;
		if (pos == 0) throw new CompileException("Invalid identifier - must start with letter or underscore", stmt);
		String name = s.substring(0, pos);
		String rest = s.substring(pos).trim();
		return new ParsedIdentifier(name, rest);
	}

	private static Declaration parseDeclaration(String rest, Map<String, String> env, String stmt)
			throws CompileException {
		if (rest.startsWith(":")) {
			return handleTypedDeclaration(rest.substring(1).trim(), stmt);
		}
		if (rest.startsWith("=")) {
			return handleUntypedDeclaration(rest.substring(1).trim(), env, stmt);
		}
		throw new CompileException("Invalid input", stmt);
	}

	private static Declaration handleTypedDeclaration(String s, String stmt) throws CompileException {
		int eqIdx = s.indexOf("=");
		if (eqIdx < 0) throw new CompileException("Invalid input", stmt);
		String typeStr = s.substring(0, eqIdx).trim();
		String cType = mapType(typeStr);
		if (cType == null) throw new CompileException("Invalid type: " + typeStr, stmt);
		String valuePart = s.substring(eqIdx + 1).trim();
		String value;
		if ("true".equals(valuePart) || "false".equals(valuePart)) {
			if (!"bool".equals(cType)) throw new CompileException("Invalid input", stmt);
			value = valuePart;
		} else if (valuePart.equals("0")) {
			value = "0";
		} else {
			throw new CompileException("Invalid input", stmt);
		}
		return new Declaration(cType, value);
	}

	private static Declaration handleUntypedDeclaration(String s, Map<String, String> env, String stmt)
			throws CompileException {
		String cType;
		String value;
		if (s.equals("true") || s.equals("false")) {
			cType = mapType("Bool");
			value = s;
		} else if (s.matches("\\d+")) {
			cType = mapType("I32");
			value = s;
		} else if (s.startsWith("0")) {
			String suffix = s.substring(1);
			cType = mapType(suffix);
			if (cType == null) throw new CompileException("Invalid input", stmt);
			value = "0";
		} else if (isIdentifier(s)) {
			String srcType = env.get(s);
			if (srcType == null) throw new CompileException("Undefined variable: " + s, stmt);
			cType = srcType;
			value = s;
		} else {
			throw new CompileException("Invalid input", stmt);
		}
		return new Declaration(cType, value);
	}

	private static boolean isIdentifier(String s) {
		if (s.isEmpty()) return false;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (!isIdentChar(c, i == 0)) return false;
		}
		return true;
	}

	private static boolean isIdentChar(char c, boolean first) {
		if (first) return Character.isLetter(c) || c == '_';
		return Character.isLetterOrDigit(c) || c == '_';
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
			case "Bool" -> "bool";
			default -> null;
		};
	}

	private static String emitDecl(String name, String cType, String value) {
		return cType + " " + name + " = " + value + ";";
	}
}
