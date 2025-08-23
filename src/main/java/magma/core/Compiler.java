package magma.core;

import magma.parse.FunctionParser;
import magma.parse.StructParser;
import magma.parse.ExpressionParser;
import magma.parse.StatementUtils;
import magma.util.ExprUtils;

public class Compiler {
	public static String compile(String input) throws CompileException {
		String rest = extractRest(input);
		return dispatch(rest, input);
	}

	// if-expression handling moved to IfUtils to keep Compiler small

	private static String dispatch(String rest, String input) throws CompileException {
		if (rest.startsWith("let ")) {
			return compileLet(rest, input);
		} else if (rest.startsWith("fn ")) {
			FunctionParser parser = new FunctionParser(rest);
			String newSource = parser.parse();
			return compileLet(newSource, input);
		} else if (rest.startsWith("struct ")) {
			StructParser parser = new StructParser(rest);
			String newSource = parser.parse();
			return compileLet(newSource, input);
		} else {
			ExpressionParser.ParseResult r = ExpressionParser.parseExprWithLets(rest, 0, null, null, null);
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
				ExpressionParser.ParseResult pr = ExpressionParser.parseExprWithLets(declExpr, varCount, declaredSoFar, types,
						funcAliases);
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
		ExpressionParser.ParseResult finalPr = StatementUtils.processStatementsAndFinal(cur, names, initStmtsAfter, types,
				funcAliases, varCount, input);

		return buildCWithLets(names, types, initStmtsAfter, finalPr.expr, finalPr.varCount);
	}

	// parseExpr was removed to lower method count; ExpressionParser now handles
	// expressions

	// buildC removed; buildCWithLets is used for all emission paths

	// ...existing code...

	private static String buildCWithLets(java.util.List<String> names, java.util.Map<String, String> types,
			java.util.List<String> initStmts, String finalExpr, int varCount) {
		StringBuilder sb = new StringBuilder();
		sb.append("#include <stdio.h>\n");
		sb.append("static int read_input(void) { int v=0; if (scanf(\"%d\", &v) != 1) return 0; return v; }\n");
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
