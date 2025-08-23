package magma;

public class Compiler {
	public static String compile(String input) throws CompileException {
		String rest = extractRest(input);
		return dispatch(rest, input);
	}

	private static String dispatch(String rest, String input) throws CompileException {
		if (rest.startsWith("let ")) {
			return compileLet(rest, input);
		} else if (rest.startsWith("fn ")) {
			return handleFunctions(rest, input);
		} else if (rest.startsWith("struct ")) {
			return handleStructs(rest, input);
		} else {
			ParseResult r = parseExprWithLets(rest, 0, null, null, null);
			return buildCWithLets(new java.util.ArrayList<>(), null, null, r.expr, r.varCount);
		}
	}

	private static String extractRest(String input) throws CompileException {
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
		return rest;
	}

	private static String handleFunctions(String rest, String input) throws CompileException {
		FunctionParser parser = new FunctionParser(rest);
		String newSource = parser.parse();
		return compileLet(newSource, input);
	}

	private static String handleStructs(String rest, String input) throws CompileException {
		StructParser parser = new StructParser(rest);
		String newSource = parser.parse();
		return compileLet(newSource, input);
	}

	private static String compileLet(String rest, String input) throws CompileException {
		// Parse a sequence of let bindings: let name [: type]? = expr; ... finalExpr
		int varCount = 0;
		java.util.List<String> names = new java.util.ArrayList<>();
		java.util.List<String> initStmts = new java.util.ArrayList<>();
		java.util.Map<String, String> types = new java.util.HashMap<>();
		java.util.Map<String, String> funcAliases = new java.util.HashMap<>();
		String cur = rest;
		// keep track of declared let names seen so far so later decl expressions can
		// reference earlier bindings (e.g. let x = ...; let y = &x;)
		java.util.Set<String> declaredSoFar = new java.util.HashSet<>();
		while (cur.startsWith("let ")) {
			int i = 4;
			if (cur.startsWith("mut ", i))
				i += 4;
			ExprUtils.IdentResult identRes = ExprUtils.collectIdentifierResult(cur, i);
			if (identRes.ident.isEmpty())
				throw new CompileException(
						"Invalid let declaration: expected identifier after 'let' in source: '" + input + "'");
			int iAfter = identRes.idx;
			String name = identRes.ident;
			int eq = ExprUtils.findAssignmentIndex(cur, iAfter);
			int semi = cur.indexOf(';', eq);
			if (semi == -1)
				throw new CompileException("Invalid let declaration: missing terminating ';' after binding for '" + name
						+ "' in source: '" + input + "'");
			String declExpr = cur.substring(eq + 1, semi).trim();
			String between = cur.substring(iAfter, eq).trim();
			if (between.startsWith(":")) {
				String declType = between.substring(1).trim();
				if (!declType.isEmpty())
					types.put(name, declType);
			}

			// special-case: let assigned a function identifier with a function type, e.g.
			// let func : () => I32 = readInt; -> treat func() as readInt()
			String typeForName = types.get(name);
			if (!ExprUtils.tryHandleFunctionAlias(name, declExpr, typeForName, funcAliases)) {
				ParseResult pr = parseExprWithLets(declExpr, varCount, declaredSoFar, types, funcAliases);
				names.add(name);
				initStmts.add("    let_" + name + " = " + pr.expr + ";");
				varCount = pr.varCount;
			} else {
				names.add(name);
			}
			// add to declared set so subsequent decls can reference this name
			declaredSoFar.add(name);
			cur = cur.substring(semi + 1).trim();
		}
		java.util.List<String> initStmtsAfter = initStmts; // reuse name to collect ordered statements
		ParseResult finalPr = processStatementsAndFinal(cur, names, initStmtsAfter, types, funcAliases, varCount, input);

		return buildCWithLets(names, types, initStmtsAfter, finalPr.expr, finalPr.varCount);
	}

	private static ParseResult processStatementsAndFinal(String cur, java.util.List<String> names,
			java.util.List<String> initStmtsAfter, java.util.Map<String, String> types,
			java.util.Map<String, String> funcAliases, int varCount, String input) throws CompileException {
		java.util.Set<String> letNames = new java.util.HashSet<>(names);
		while (true) {
			int semi = cur.indexOf(';');
			if (semi == -1)
				break;
			String stmt = cur.substring(0, semi).trim();
			if (!stmt.isEmpty()) {
				varCount = handleStatement(stmt, names, initStmtsAfter, letNames, varCount, input, types, funcAliases);
			}
			cur = cur.substring(semi + 1).trim();
		}

		if (cur.isEmpty())
			throw new CompileException(
					"Invalid input: expected final expression after let bindings in source: '" + input + "'");

		// parse final expression allowing any of the let names
		return parseExprWithLets(cur, varCount, letNames, types, funcAliases);
	}

	private static final class ParseResult {
		final String expr;
		final int varCount;

		ParseResult(String expr, int varCount) {
			this.expr = expr;
			this.varCount = varCount;
		}
	}

	// parseExpr was removed to lower method count; readInt detection moved to
	// ExprUtils

	private static ParseResult parseExprWithLets(String s, int startVar, java.util.Set<String> letNames,
			java.util.Map<String, String> types, java.util.Map<String, String> funcAliases)
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
			int consumedRead = ExprUtils.readIntConsumed(s, idx);
			if (consumedRead > 0) {
				out.append("_v").append(varCount);
				varCount++;
				idx += consumedRead;
				continue;
			}
			// allow bare function aliases like func() where func was bound to readInt
			int consumedAlias = ExprUtils.aliasCallConsumed(s, idx, funcAliases);
			if (consumedAlias > 0) {
				out.append("_v").append(varCount);
				varCount++;
				idx += consumedAlias;
				continue;
			}
			// handle unary operators via helpers
			int opIdx = OperatorUtils.tryHandleLogical(s, idx, out);
			if (opIdx != -1) {
				idx = opIdx;
				continue;
			}
			if (c == '&') {
				ExprUtils.OpResult amp = ExprUtils.handleAmpersandResult(s, idx, letNames);
				out.append(amp.out);
				idx = amp.idx;
				continue;
			}
			if (c == '*') {
				ExprUtils.OpResult ast = ExprUtils.handleAsteriskResult(s, idx, letNames, types);
				out.append(ast.out);
				idx = ast.idx;
				continue;
			}
			int consumedInt2 = LiteralUtils.tryAppendLiteral(s, idx, out);
			if (consumedInt2 > 0) {
				idx += consumedInt2;
				continue;
			}
			ExprUtils.OpResult idRes = ExprUtils.handleIdentifierWithLetsResult(s, idx, letNames);
			if (idRes != null) {
				out.append(idRes.out);
				idx = idRes.idx;
				continue;
			}
			// Check for field access (e.g., "var.field")
			int fieldConsumed = StructUtils.handleFieldAccess(s, idx, out, letNames);
			if (fieldConsumed != -1) {
				idx = fieldConsumed;
				continue;
			}
			if (c == '+' || c == '-') {
				out.append(c);
				idx++;
				continue;
			}
			if (c == '.') {
				out.append(c);
				idx++;
				continue;
			}
			throw new CompileException("Unexpected token '" + c + "' at index " + idx + " in expression: '" + s + "'");
		}
		return new ParseResult(out.toString(), varCount);
	}

	// buildC removed; buildCWithLets is used for all emission paths

	// ...existing code...

	// Handle a statement (either nested let or assignment) and return updated
	// varCount.
	private static int handleStatement(String stmt, java.util.List<String> names, java.util.List<String> initStmtsAfter,
			java.util.Set<String> letNames, int varCount, String input, java.util.Map<String, String> types,
			java.util.Map<String, String> funcAliases)
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
			ParseResult pr2 = parseExprWithLets(rhs, varCount, letNames, types, funcAliases);
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
		ParseResult pr = parseExprWithLets(right, varCount, letNames, types, funcAliases);
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
