package magma;

public class Compiler {
	public static String compile(String input) throws CompileException {
		final String prelude = "intrinsic fn readInt() : I32; ";
		if (input == null) {
			throw new CompileException("Input is null");
		}

		boolean hasPrelude = input.contains(prelude);
		String rest = hasPrelude ? input.substring(input.indexOf(prelude) + prelude.length()).trim() : input.trim();
		// If source uses readInt() it must include the prelude
		if (!hasPrelude && rest.contains("readInt()")) {
			throw new CompileException("Missing prelude: source uses readInt() but does not include: '" + prelude + "'");
		}
		if (rest.startsWith("let ")) {
			return compileLet(rest, input);
		}
		if (rest.startsWith("fn ")) {
			return handleFunctions(rest, input);
		}
		return compileExpr(rest, input);
	}

	private static String handleFunctions(String rest, String input) throws CompileException {
		FunctionParser parser = new FunctionParser(rest);
		String newSource = parser.parse();
		return compileLet(newSource, input);
	}

	private static String compileExpr(String rest, String input) throws CompileException {
		ParseResult r = parseExprWithLets(rest, 0, null, null);
		return buildCWithLets(new java.util.ArrayList<>(), null, null, r.expr, r.varCount);
	}

	private static String compileLet(String rest, String input) throws CompileException {
		// Parse a sequence of let bindings: let name [: type]? = expr; ... finalExpr
		int varCount = 0;
		java.util.List<String> names = new java.util.ArrayList<>();
		java.util.List<String> initStmts = new java.util.ArrayList<>();
		java.util.Map<String, String> types = new java.util.HashMap<>();
		String cur = rest;
		// keep track of declared let names seen so far so later decl expressions can
		// reference earlier bindings (e.g. let x = ...; let y = &x;)
		java.util.Set<String> declaredSoFar = new java.util.HashSet<>();
		while (cur.startsWith("let ")) {
			int i = 4;
			if (cur.startsWith("mut ", i)) {
				i += 4;
			}
			StringBuilder ident = new StringBuilder();
			while (i < cur.length()) {
				char cc = cur.charAt(i);
				if (Character.isWhitespace(cc) || cc == ':')
					break;
				ident.append(cc);
				i++;
			}
			if (ident.length() == 0)
				throw new CompileException(
						"Invalid let declaration: expected identifier after 'let' in source: '" + input + "'");
			String name = ident.toString();
			int eq = cur.indexOf('=', i);
			if (eq == -1)
				throw new CompileException(
						"Invalid let declaration: missing '=' for binding '" + name + "' in source: '" + input + "'");
			int semi = cur.indexOf(';', eq);
			if (semi == -1)
				throw new CompileException("Invalid let declaration: missing terminating ';' after binding for '" + name
						+ "' in source: '" + input + "'");
			String declExpr = cur.substring(eq + 1, semi).trim();

			// detect optional type annotation between current index and '='
			String between = cur.substring(i, eq).trim();
			if (between.startsWith(":")) {
				String declType = between.substring(1).trim();
				if (!declType.isEmpty()) {
					types.put(name, declType);
				}
			}

			ParseResult pr = parseExprWithLets(declExpr, varCount, declaredSoFar, types);

			names.add(name);
			initStmts.add("    let_" + name + " = " + pr.expr + ";");
			varCount = pr.varCount;
			// add to declared set so subsequent decls can reference this name
			declaredSoFar.add(name);
			cur = cur.substring(semi + 1).trim();
		}
		// parse a sequence of statements (assignments) terminated by ';', finalExpr is
		// the last segment
		java.util.Set<String> letNames = new java.util.HashSet<>(names);
		java.util.List<String> initStmtsAfter = initStmts; // reuse name to collect ordered statements
	while (true) {
			int semi = cur.indexOf(';');
			if (semi == -1)
				break;
			String stmt = cur.substring(0, semi).trim();
			if (!stmt.isEmpty()) {
		varCount = handleStatement(stmt, names, initStmtsAfter, letNames, varCount, input, types);
			}
			cur = cur.substring(semi + 1).trim();
		}

		if (cur.isEmpty())
			throw new CompileException(
					"Invalid input: expected final expression after let bindings in source: '" + input + "'");

		// parse final expression allowing any of the let names
	ParseResult finalPr = parseExprWithLets(cur, varCount, letNames, types);

		return buildCWithLets(names, types, initStmtsAfter, finalPr.expr, finalPr.varCount);
	}

	private static final class ParseResult {
		final String expr;
		final int varCount;

		ParseResult(String expr, int varCount) {
			this.expr = expr;
			this.varCount = varCount;
		}
	}

	private static ParseResult parseExpr(String s, int startVar) throws CompileException {
		StringBuilder out = new StringBuilder();
		int idx = 0;
		int len = s.length();
		int varCount = startVar;
		while (idx < len) {
			char c = s.charAt(idx);
			if (Character.isWhitespace(c)) {
				idx++;
				continue;
			}
			String token = "readInt()";
			if (idx + token.length() <= len && s.startsWith(token, idx)) {
				out.append("_v").append(varCount);
				varCount++;
				idx += token.length();
				continue;
			}
			// allow plain literals and identifiers (no lets context)
			int consumed = tryAppendLiteral(s, idx, out);
			if (consumed > 0) {
				idx += consumed;
				continue;
			}
			if (c == '+' || c == '-' || c == '*') {
				out.append(c);
				idx++;
				continue;
			}
			throw new CompileException("Unexpected token '" + c + "' at index " + idx + " in expression: '" + s + "'");
		}
		return new ParseResult(out.toString(), varCount);
	}

    

	private static ParseResult parseExprWithLets(String s, int startVar, java.util.Set<String> letNames,
			java.util.Map<String, String> types)
			throws CompileException {
		StringBuilder out = new StringBuilder();
		int idx = 0;
		int len = s.length();
		int varCount = startVar;
		while (idx < len) {
			char c = s.charAt(idx);
			if (Character.isWhitespace(c)) {
				idx++;
				continue;
			}
			String token = "readInt()";
			if (idx + token.length() <= len && s.startsWith(token, idx)) {
				out.append("_v").append(varCount);
				varCount++;
				idx += token.length();
				continue;
			}
			// handle unary operators via helpers
			if (c == '&') {
				idx = ExprUtils.handleAmpersand(s, idx, out, letNames);
				continue;
			}
			if (c == '*') {
				idx = ExprUtils.handleAsterisk(s, idx, out, letNames, types);
				continue;
			}
			int consumedInt2 = tryAppendLiteral(s, idx, out);
			if (consumedInt2 > 0) {
				idx += consumedInt2;
				continue;
			}
			if (letNames != null && Character.isJavaIdentifierStart(c)) {
				StringBuilder id = new StringBuilder();
				while (idx < len && Character.isJavaIdentifierPart(s.charAt(idx))) {
					id.append(s.charAt(idx));
					idx++;
				}
				if (letNames.contains(id.toString())) {
					out.append("let_").append(id.toString());
					continue;
				}
			}
			if (c == '+' || c == '-') {
				out.append(c);
				idx++;
				continue;
			}
			throw new CompileException("Unexpected token '" + c + "' at index " + idx + " in expression: '" + s + "'");
		}
		return new ParseResult(out.toString(), varCount);
	}

	// buildC removed; buildCWithLets is used for all emission paths
	// Try to append a literal (boolean or integer) at s[idx] into out. Returns
	// number of
	// characters consumed (0 if none).
	private static int tryAppendLiteral(String s, int idx, StringBuilder out) {
		String tTrue = "true";
		if (idx + tTrue.length() <= s.length() && s.startsWith(tTrue, idx)) {
			out.append("1");
			return tTrue.length();
		}
		String tFalse = "false";
		if (idx + tFalse.length() <= s.length() && s.startsWith(tFalse, idx)) {
			out.append("0");
			return tFalse.length();
		}
		int len = s.length();
		if (idx >= len)
			return 0;
		char c = s.charAt(idx);
		if (!Character.isDigit(c))
			return 0;
		int start = idx;
		while (idx < len && Character.isDigit(s.charAt(idx)))
			idx++;
		out.append(s.substring(start, idx));
		return idx - start;
	}

	// ...existing code...

	// Handle a statement (either nested let or assignment) and return updated
	// varCount.
	private static int handleStatement(String stmt, java.util.List<String> names, java.util.List<String> initStmtsAfter,
			java.util.Set<String> letNames, int varCount, String input, java.util.Map<String, String> types)
			throws CompileException {
		if (stmt.startsWith("let ")) {
			int i2 = 4;
			if (stmt.startsWith("mut ", i2)) {
				i2 += 4;
			}
			StringBuilder ident2 = new StringBuilder();
			while (i2 < stmt.length()) {
				char cc = stmt.charAt(i2);
				if (Character.isWhitespace(cc) || cc == ':')
					break;
				ident2.append(cc);
				i2++;
			}
			if (ident2.length() == 0)
				throw new CompileException(
						"Invalid let declaration inside statements: '" + stmt + "' in source: '" + input + "'");
			String name2 = ident2.toString();
			int eq2 = stmt.indexOf('=', i2);
			if (eq2 == -1)
				throw new CompileException(
						"Invalid let declaration: missing '=' for binding '" + name2 + "' in source: '" + input + "'");
			String rhs = stmt.substring(eq2 + 1).trim();
			ParseResult pr2 = parseExprWithLets(rhs, varCount, letNames, types);
			names.add(name2);
			initStmtsAfter.add("    let_" + name2 + " = " + pr2.expr + ";");
			varCount = pr2.varCount;
			letNames.add(name2);
			return varCount;
		}
		int eq = stmt.indexOf('=');
		if (eq == -1) {
			throw new CompileException(
					"Invalid statement before final expression: '" + stmt + "' in source: '" + input + "'");
		}
		String left = stmt.substring(0, eq).trim();
		String right = stmt.substring(eq + 1).trim();
		boolean isDeref = false;
		String target = left;
		if (left.startsWith("*")) {
			isDeref = true;
			target = left.substring(1).trim();
		}
		if (!letNames.contains(target)) {
			throw new CompileException("Invalid assignment to unknown name '" + target + "' in source: '" + input + "'");
		}
	ParseResult pr = parseExprWithLets(right, varCount, letNames, types);
		varCount = pr.varCount;
		if (isDeref) {
			// assign to pointer dereference: let_target should be treated as pointer
			initStmtsAfter.add("    *let_" + target + " = " + pr.expr + ";");
		} else {
			initStmtsAfter.add("    let_" + target + " = " + pr.expr + ";");
		}
		return varCount;
	}

	private static String buildCWithLets(java.util.List<String> names, java.util.Map<String, String> types,
			java.util.List<String> initStmts, String finalExpr, int varCount) {
		StringBuilder sb = new StringBuilder();
		sb.append("#include <stdio.h>\n");
		sb.append("int main(void) {\n");
		for (int i = 0; i < varCount; i++)
			sb.append("    int _v").append(i).append(" = 0;\n");
		for (int i = 0; i < varCount; i++)
			sb.append("    if (scanf(\"%d\", &_v").append(i).append(") != 1) return 0;\n");
		// declare let_ variables initialized to 0; actual initialization happens in
		// initStmts
		for (int k = 0; k < names.size(); k++) {
			String name = names.get(k);
			String t = types != null ? types.get(name) : null;
			if (t != null && (t.startsWith("*") || t.startsWith("mut *") || t.startsWith("*"))) {
				// pointer: declare as int* and initialize to NULL
				sb.append("    int *let_").append(name).append(" = NULL;\n");
			} else {
				sb.append("    int let_").append(name).append(" = 0;\n");
			}
		}
		// emit any initialization/assignment statements in source order
		if (initStmts != null) {
			for (String s : initStmts) {
				sb.append(s).append("\n");
			}
		}
		sb.append("    return ").append(finalExpr).append(";\n");
		sb.append("}\n");
		return sb.toString();
	}
}
