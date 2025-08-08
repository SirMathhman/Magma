package magma;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Compiler {
	public static String compile(String input) throws CompileException {
		if (input.isEmpty()) return "";
		String trimmed = input.trim();

		// Support multiple semicolon-terminated statements
		Map<String, VarInfo> env = new HashMap<>(); // varName -> info
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

	private static String compileStatement(String stmt, Map<String, VarInfo> env) throws CompileException {
		String s = stmt.trim();
		if (s.isEmpty()) return null;
		if (!s.endsWith(";")) throw new CompileException("Statement must end with a semicolon", s);
		s = s.substring(0, s.length() - 1).trim(); // drop trailing ';'

		// Handle let declarations (with optional 'mut')
		if (s.startsWith("let ")) {
			s = s.substring("let ".length());
			boolean isMut = false;
			if (s.startsWith("mut ")) {
				isMut = true;
				s = s.substring("mut ".length());
			}
			// Parse identifier
			ParsedIdentifier parsed = parseIdentifierOrThrow(s, stmt);
			String name = parsed.name();
			String rest = parsed.rest();

			Declaration decl = parseDeclaration(rest, env, stmt);

			env.put(name, new VarInfo(decl.cType(), isMut));
			return decl.cType() + " " + name + " = " + decl.value() + ";";
		}

		// Otherwise handle simple assignment: <ident> = <expr>
		int eqIdx = s.indexOf('=');
		if (eqIdx < 0) throw new CompileException("Invalid input", stmt);
		String left = s.substring(0, eqIdx).trim();
		String right = s.substring(eqIdx + 1).trim();
		if (!isIdentifier(left)) throw new CompileException("Invalid identifier", stmt);
		VarInfo info = env.get(left);
		if (info == null) throw new CompileException("Undefined variable: " + left, stmt);
		if (!info.mutable()) throw new CompileException("Cannot assign to immutable variable: " + left, stmt);
		Declaration rhs = handleUntypedDeclaration(right, env, stmt);
		if (!info.cType().equals(rhs.cType())) throw new CompileException("Type mismatch in assignment", stmt);
		return left + " = " + rhs.value() + ";";
	}

	private static ParsedIdentifier parseIdentifierOrThrow(String s, String stmt) throws CompileException {
		int pos = 0;
		while (pos < s.length()) {
			char c = s.charAt(pos);
			if (pos == 0) {
				if (!(Character.isLetter(c) || c == '_')) break;
			} else {
				if (!(Character.isLetterOrDigit(c) || c == '_')) break;
			}
			pos++;
		}
		if (pos == 0) throw new CompileException("Invalid identifier - must start with letter or underscore", stmt);
		String name = s.substring(0, pos);
		String rest = s.substring(pos).trim();
		return new ParsedIdentifier(name, rest);
	}

	private static Declaration parseDeclaration(String rest, Map<String, VarInfo> env, String stmt)
			throws CompileException {
		if (rest.startsWith(":")) {
			return handleTypedDeclaration(rest.substring(1).trim(), env, stmt);
		}
		if (rest.startsWith("=")) {
			return handleUntypedDeclaration(rest.substring(1).trim(), env, stmt);
		}
		throw new CompileException("Invalid input", stmt);
	}

	private static Declaration handleTypedDeclaration(String s, Map<String, VarInfo> env, String stmt)
			throws CompileException {
		int eqIdx = s.indexOf("=");
		if (eqIdx < 0) throw new CompileException("Invalid input", stmt);
		String typeStr = s.substring(0, eqIdx).trim();
		String cType = mapType(typeStr);
		if (cType == null) throw new CompileException("Invalid type: " + typeStr, stmt);
		String valuePart = s.substring(eqIdx + 1).trim();

		// Try comparison expression first
		Declaration cmp = tryParseComparison(valuePart, env, stmt);
		if (cmp != null) {
			if (!"bool".equals(cType)) throw new CompileException("Invalid input", stmt);
			return cmp; // already bool type with formatted expression
		}

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

	private static Declaration handleUntypedDeclaration(String s, Map<String, VarInfo> env, String stmt)
			throws CompileException {
		// Try comparison expression first
		Declaration cmp = tryParseComparison(s, env, stmt);
		if (cmp != null) return cmp;

		return resolveSimpleValue(s, env, stmt);
	}

	private static boolean isIdentifier(String s) {
		if (s.isEmpty()) return false;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (i == 0) {
				if (!(Character.isLetter(c) || c == '_')) return false;
			} else {
				if (!(Character.isLetterOrDigit(c) || c == '_')) return false;
			}
		}
		return true;
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


	private static Declaration resolveSimpleValue(String s, Map<String, VarInfo> env, String stmt)
			throws CompileException {
		String cType;
		String value;
		if (s.equals("true") || s.equals("false")) {
			cType = mapType("Bool");
			value = s;
		} else if (s.matches("\\d+")) {
			cType = mapType("I32");
			value = s;
		} else if (s.startsWith("0") && s.length() > 1) {
			String suffix = s.substring(1);
			cType = mapType(suffix);
			if (cType == null) throw new CompileException("Invalid input", stmt);
			value = "0";
		} else if (isIdentifier(s)) {
			VarInfo var = env.get(s);
			if (var == null) throw new CompileException("Undefined variable: " + s, stmt);
			cType = var.cType();
			value = s;
		} else {
			throw new CompileException("Invalid input", stmt);
		}
		return new Declaration(cType, value);
	}

	private static Declaration tryParseComparison(String s, Map<String, VarInfo> env, String stmt)
			throws CompileException {
		String[] ops = {"<=", ">=", "==", "!=", "<", ">"};
		for (String op : ops) {
			int idx = s.indexOf(op);
			if (idx >= 0) {
				String left = s.substring(0, idx).trim();
				String right = s.substring(idx + op.length()).trim();
				if (left.isEmpty() || right.isEmpty()) throw new CompileException("Invalid input", stmt);
				Declaration l = resolveSimpleValue(left, env, stmt);
				Declaration r = resolveSimpleValue(right, env, stmt);
				String expr = l.value() + " " + op + " " + r.value();
				return new Declaration("bool", expr);
			}
		}
		return null;
	}
}
