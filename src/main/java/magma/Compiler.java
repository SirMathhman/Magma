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
				i = processCodeBlock(code, env, out, i);
			} else {
				i = processStatement(code, env, out, i);
			}
		}
		return out.toString();
	}
	
	private static int processCodeBlock(String code, Map<String, VarInfo> env, StringBuilder out, int i) throws CompileException {
		// Compile code block with isolated scope
		int close = findMatchingBrace(code, i);
		String inner = code.substring(i + 1, close).trim();
		Map<String, VarInfo> childEnv = new HashMap<>(env);
		String innerOut = compileSequence(inner, childEnv);
		out.append('{').append(innerOut).append('}');
		return close + 1;
	}
	
	private static int processStatement(String code, Map<String, VarInfo> env, StringBuilder out, int i) throws CompileException {
		int n = code.length();
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
		return semi + 1;
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
			return compileLetDeclaration(s, env, stmt);
		}

		// Otherwise handle simple assignment: <ident> = <expr>
		return compileAssignment(s, env, stmt);
	}
	
	private static String compileLetDeclaration(String s, Map<String, VarInfo> env, String stmt) throws CompileException {
		s = s.substring("let ".length());
		boolean isMut = false;
		if (s.startsWith("mut ")) {
			isMut = true;
			s = s.substring("mut ".length());
		}
		
		// Extract identifier and the rest starting with ':' or '='
		int idxColon = s.indexOf(':');
		int idxEq = s.indexOf('=');
		
		// Determine which character comes first, colon or equals
		int split;
		if (idxColon >= 0) {
			if (idxEq < 0 || idxColon < idxEq) {
				split = idxColon;
			} else {
				split = idxEq;
			}
		} else {
			split = idxEq;
		}
		
		if (split < 0) throw new CompileException("Invalid input", stmt);
		String name = s.substring(0, split).trim();
		if (!TypeHelper.isIdentifier(name)) throw new CompileException("Invalid identifier - must start with letter or underscore", stmt);
		String rest = s.substring(split).trim();

		Declaration decl = DeclarationHelper.parseDeclaration(rest, env, stmt);

		env.put(name, new VarInfo(decl.cType(), isMut));
		return decl.cType() + " " + name + " = " + decl.value() + ";";
	}
	
	private static String compileAssignment(String s, Map<String, VarInfo> env, String stmt) throws CompileException {
		int eqIdx = s.indexOf('=');
		if (eqIdx < 0) throw new CompileException("Invalid input", stmt);
		String left = s.substring(0, eqIdx).trim();
		String right = s.substring(eqIdx + 1).trim();
		VarInfo info = env.get(left);
		if (info == null) throw new CompileException("Undefined variable: " + left, stmt);
		if (!info.mutable()) throw new CompileException("Cannot assign to immutable variable: " + left, stmt);
		
		// For assignment, allow comparison or simple values, but types must match
		Declaration rhs = ValueResolver.tryParseComparison(right, env, stmt);
		if (rhs == null) rhs = ValueResolver.resolveSimpleValue(right, env, stmt);
		if (!info.cType().equals(rhs.cType())) throw new CompileException("Type mismatch in assignment", stmt);
		return left + " = " + rhs.value() + ";";
	}


	// These methods have been moved to DeclarationHelper class


	// These methods have been moved to helper classes
	// TypeHelper: isIdentifier, mapType
	// ValueResolver: resolveSimpleValue, resolveBooleanLiteral, resolveIdentifier,
	//                resolveNumericLiteral, resolveTypeSuffixedNumericLiteral,
	//                findTypeSuffixPosition, tryParseComparison
}
