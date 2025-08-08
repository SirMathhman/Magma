package magma;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Compiler {
	public static String compile(String input) throws CompileException {
		if (input == null || input.isEmpty()) return "";
		String trimmed = input.trim();
		if (trimmed.isEmpty()) return "";

		Map<String, VarInfo> env = new HashMap<>();
		String result = compileSequence(trimmed, env);
		return result;
	}

	private static String compileSequence(String code, Map<String, VarInfo> env) throws CompileException {
		if (code.isEmpty()) return "";
		
		StringBuilder out = new StringBuilder();
		int i = 0;
		int n = code.length();
		
		while (i < n) {
			// Skip whitespace
			while (i < n && Character.isWhitespace(code.charAt(i))) i++;
			if (i >= n) break;
			
			char ch = code.charAt(i);
			if (ch == '{') {
				// Compile code block with isolated scope
				int close = findMatchingBrace(code, i);
				String inner = code.substring(i + 1, close).trim();
				Map<String, VarInfo> childEnv = new HashMap<>(env);
				String innerOut = compileSequence(inner, childEnv);
				out.append('{').append(innerOut).append('}');
				i = close + 1;
			} else {
				// Process a single statement ending with semicolon
				int semi = code.indexOf(';', i);
				if (semi < 0) {
					throw new CompileException("Statement must end with a semicolon", code.substring(i));
				}
				
				// Check for code block starting before semicolon (invalid)
				int bracePos = code.indexOf('{', i);
				if (bracePos >= 0 && bracePos < semi) {
					throw new CompileException("Statement must end with a semicolon", 
							code.substring(i, bracePos));
				}
				
				// Process statement
				String stmt = code.substring(i, semi + 1).trim();
				if (!stmt.isEmpty()) {
					if (!stmt.endsWith(";")) {
						throw new CompileException("Statement must end with a semicolon", stmt);
					}
					
					String compiled = compileStatement(stmt, env);
					if (compiled != null && !compiled.isEmpty()) {
						if (out.length() > 0) out.append(' ');
						out.append(compiled);
					}
				}
				i = semi + 1;
			}
		}
		return out.toString();
	}

	private static int findMatchingBrace(String code, int openIdx) throws CompileException {
		int depth = 0;
		for (int j = openIdx; j < code.length(); j++) {
			char cj = code.charAt(j);
			if (cj == '{') depth++;
			else if (cj == '}') {
				depth--;
				if (depth == 0) return j;
			}
		}
		throw new CompileException("Unmatched '{'", code);
	}

	private static String compileStatement(String stmt, Map<String, VarInfo> env) throws CompileException {
		String s = stmt.trim();
		// Drop trailing ';' (guaranteed by caller)
		s = s.substring(0, s.length() - 1).trim();

		// Handle let declarations (with optional 'mut')
		if (s.startsWith("let ")) {
			s = s.substring("let ".length());
			boolean isMut = false;
			if (s.startsWith("mut ")) {
				isMut = true;
				s = s.substring("mut ".length());
			}
			// Extract identifier and the rest starting with ':' or '='
			int idxColon = s.indexOf(':');
			int idxEq = s.indexOf('=');
			int split = (idxColon >= 0 && (idxEq < 0 || idxColon < idxEq)) ? idxColon : idxEq;
			if (split < 0) throw new CompileException("Invalid input", stmt);
			String name = s.substring(0, split).trim();
			if (!isIdentifier(name)) throw new CompileException("Invalid identifier - must start with letter or underscore", stmt);
			String rest = s.substring(split).trim();

			Declaration decl = parseDeclaration(rest, env, stmt);

			env.put(name, new VarInfo(decl.cType(), isMut));
			return decl.cType() + " " + name + " = " + decl.value() + ";";
		}

		// Otherwise handle simple assignment: <ident> = <expr>
		int eqIdx = s.indexOf('=');
		if (eqIdx < 0) throw new CompileException("Invalid input", stmt);
		String left = s.substring(0, eqIdx).trim();
		String right = s.substring(eqIdx + 1).trim();
		VarInfo info = env.get(left);
		if (info == null) throw new CompileException("Undefined variable: " + left, stmt);
		if (!info.mutable()) throw new CompileException("Cannot assign to immutable variable: " + left, stmt);
		// For assignment, allow comparison or simple values, but types must match
		Declaration rhs = tryParseComparison(right, env, stmt);
		if (rhs == null) rhs = resolveSimpleValue(right, env, stmt);
		if (!info.cType().equals(rhs.cType())) throw new CompileException("Type mismatch in assignment", stmt);
		return left + " = " + rhs.value() + ";";
	}


 private static Declaration parseDeclaration(String rest, Map<String, VarInfo> env, String stmt)
			throws CompileException {
		if (rest.startsWith(":")) {
			return handleTypedDeclaration(rest.substring(1).trim(), env, stmt);
		}
		if (rest.startsWith("=")) {
			String expr = rest.substring(1).trim();
			Declaration cmp = tryParseComparison(expr, env, stmt);
			if (cmp != null) return cmp;
			return resolveSimpleValue(expr, env, stmt);
		}
		throw new CompileException("Invalid input", stmt);
}

	private static Declaration handleTypedDeclaration(String s, Map<String, VarInfo> env, String stmt)
			throws CompileException {
		int eqIdx = s.indexOf("=");
		if (eqIdx < 0) throw new CompileException("Invalid input", stmt);
		
		// Extract and validate type
		String typeStr = s.substring(0, eqIdx).trim();
		String cType = mapType(typeStr);
		if (cType == null) throw new CompileException("Invalid type: " + typeStr, stmt);
		String valuePart = s.substring(eqIdx + 1).trim();

		// Boolean type with possible comparison
		if ("bool".equals(cType)) {
			// Try comparison expression first
			Declaration cmp = tryParseComparison(valuePart, env, stmt);
			if (cmp != null) {
				return cmp; // already has bool type
			}
			
			// Check for boolean literals
			if ("true".equals(valuePart) || "false".equals(valuePart)) {
				return new Declaration(cType, valuePart);
			}
		}
		
		// Numeric types
		if (cType.contains("int") || cType.contains("uint")) {
			// Check for numeric literals
			if (valuePart.matches("\\d+")) {
				return new Declaration(cType, valuePart);
			}
			
			// Check for identifier with matching type
			if (isIdentifier(valuePart)) {
				VarInfo var = env.get(valuePart);
				if (var != null && var.cType().equals(cType)) {
					return new Declaration(cType, valuePart);
				}
			}
		}
		
		throw new CompileException("Invalid value for type " + typeStr, stmt);
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
		// Boolean literals
		if (s.equals("true") || s.equals("false")) {
			return new Declaration(mapType("Bool"), s);
		}
		
		// Identifiers (variables)
		if (isIdentifier(s)) {
			VarInfo var = env.get(s);
			if (var == null) throw new CompileException("Undefined variable: " + s, stmt);
			return new Declaration(var.cType(), s);
		}
		
		// Numeric literals
		if (s.matches("\\d+")) {
			// Regular number defaults to I32
			return new Declaration(mapType("I32"), s);
		} 
		
		// Type-suffixed number literals (e.g., 0U8)
		if (s.matches("\\d+U\\d+") || s.matches("\\d+I\\d+")) {
			int letterPos = -1;
			for (int i = 0; i < s.length(); i++) {
				char c = s.charAt(i);
				if (c == 'U' || c == 'I') {
					letterPos = i;
					break;
				}
			}
			
			if (letterPos > 0) {
				String digits = s.substring(0, letterPos);
				String suffix = s.substring(letterPos);
				String cType = mapType(suffix);
				if (cType == null) throw new CompileException("Invalid type suffix: " + suffix, stmt);
				return new Declaration(cType, digits);
			}
		}
		
		throw new CompileException("Invalid input", stmt);
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
