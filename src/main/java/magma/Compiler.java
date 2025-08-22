package magma;

public class Compiler {
	public static String compile(String input) throws CompileException {
		final String prelude = "intrinsic fn readInt() : I32; ";
		if (input == null || !input.contains(prelude)) {
			throw new CompileException("Undefined symbol: " + input);
		}
		String rest = input.substring(input.indexOf(prelude) + prelude.length()).trim();
		if (rest.startsWith("let ")) {
			return compileLet(rest, input);
		}
		return compileExpr(rest, input);
	}

	private static String compileExpr(String rest, String input) throws CompileException {
		ParseResult r = parseExpr(rest, 0);
		if (r.varCount == 0)
			throw new CompileException("Undefined symbol: " + input);
		return buildC(r.expr, r.varCount);
	}

	private static String compileLet(String rest, String input) throws CompileException {
		// Parse a sequence of let bindings: let name [: type]? = expr; ... finalExpr
		int varCount = 0;
		java.util.List<String> names = new java.util.ArrayList<>();
		java.util.List<String> bindingExprs = new java.util.ArrayList<>();
		String cur = rest;
		while (cur.startsWith("let ")) {
			int i = 4;
			StringBuilder ident = new StringBuilder();
			while (i < cur.length()) {
				char cc = cur.charAt(i);
				if (Character.isWhitespace(cc) || cc == ':')
					break;
				ident.append(cc);
				i++;
			}
			if (ident.length() == 0)
				throw new CompileException("Undefined symbol: " + input);
			String name = ident.toString();
			int eq = cur.indexOf('=', i);
			if (eq == -1)
				throw new CompileException("Undefined symbol: " + input);
			int semi = cur.indexOf(';', eq);
			if (semi == -1)
				throw new CompileException("Undefined symbol: " + input);
			String declExpr = cur.substring(eq + 1, semi).trim();

			ParseResult pr = parseExpr(declExpr, varCount);

			names.add(name);
			bindingExprs.add(pr.expr);
			varCount = pr.varCount;

			cur = cur.substring(semi + 1).trim();
		}

		if (cur.isEmpty())
			throw new CompileException("Undefined symbol: " + input);

		// parse final expression allowing any of the let names
		java.util.Set<String> letNames = new java.util.HashSet<>(names);
		ParseResult finalPr = parseExprWithLets(cur, varCount, letNames);

		return buildCWithLets(names, bindingExprs, finalPr.expr, finalPr.varCount);
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
			if (c == '+' || c == '-' || c == '*') {
				out.append(c);
				idx++;
				continue;
			}
			throw new CompileException("Undefined symbol: " + s);
		}
		return new ParseResult(out.toString(), varCount);
	}

	private static ParseResult parseExprWithLets(String s, int startVar, java.util.Set<String> letNames)
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
			if (c == '+' || c == '-' || c == '*') {
				out.append(c);
				idx++;
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
			throw new CompileException("Undefined symbol: " + s);
		}
		return new ParseResult(out.toString(), varCount);
	}

	private static String buildC(String expr, int varCount) {
		StringBuilder sb = new StringBuilder();
		sb.append("#include <stdio.h>\n");
		sb.append("int main(void) {\n");
		for (int i = 0; i < varCount; i++)
			sb.append("    int _v").append(i).append(" = 0;\n");
		for (int i = 0; i < varCount; i++)
			sb.append("    if (scanf(\"%d\", &_v").append(i).append(") != 1) return 0;\n");
		sb.append("    return ").append(expr).append(";\n");
		sb.append("}\n");
		return sb.toString();
	}

	private static String buildCWithLets(java.util.List<String> names, java.util.List<String> bindingExprs,
			String finalExpr, int varCount) {
		StringBuilder sb = new StringBuilder();
		sb.append("#include <stdio.h>\n");
		sb.append("int main(void) {\n");
		for (int i = 0; i < varCount; i++)
			sb.append("    int _v").append(i).append(" = 0;\n");
		for (int i = 0; i < varCount; i++)
			sb.append("    if (scanf(\"%d\", &_v").append(i).append(") != 1) return 0;\n");
		for (int k = 0; k < names.size(); k++) {
			sb.append("    int let_").append(names.get(k)).append(" = ").append(bindingExprs.get(k)).append(";\n");
		}
		sb.append("    return ").append(finalExpr).append(";\n");
		sb.append("}\n");
		return sb.toString();
	}
}
