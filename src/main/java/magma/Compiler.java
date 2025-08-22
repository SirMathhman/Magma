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
		StringBuilder expr = new StringBuilder();
		int varCount = parseExpr(rest, 0, expr);
		if (varCount == 0)
			throw new CompileException("Undefined symbol: " + input);
		return buildC(expr.toString(), varCount);
	}

	private static String compileLet(String rest, String input) throws CompileException {
		// rest starts with "let "
		int i = 4;
		StringBuilder ident = new StringBuilder();
		while (i < rest.length()) {
			char cc = rest.charAt(i);
			if (Character.isWhitespace(cc) || cc == ':')
				break;
			ident.append(cc);
			i++;
		}
		if (ident.length() == 0)
			throw new CompileException("Undefined symbol: " + input);
		String name = ident.toString();
		int eq = rest.indexOf('=', i);
		if (eq == -1)
			throw new CompileException("Undefined symbol: " + input);
		int semi = rest.indexOf(';', eq);
		if (semi == -1)
			throw new CompileException("Undefined symbol: " + input);
		String declExpr = rest.substring(eq + 1, semi).trim();
		String afterExpr = rest.substring(semi + 1).trim();

		StringBuilder expr1 = new StringBuilder();
		int varCount = parseExpr(declExpr, 0, expr1);

		StringBuilder expr2 = new StringBuilder();
		int varCount2 = parseExpr(afterExpr, varCount, expr2, name);

		return buildCWithLet(name, expr1.toString(), expr2.toString(), varCount2);
	}

	// parseExpr starting from startVar; for the simple expression grammar and
	// without let identifier
	private static int parseExpr(String s, int startVar, StringBuilder out) throws CompileException {
		return parseExpr(s, startVar, out, null);
	}

	// parseExpr with optional letName which is allowed as an identifier in the
	// expression
	private static int parseExpr(String s, int startVar, StringBuilder out, String letName) throws CompileException {
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
			if (letName != null && Character.isJavaIdentifierStart(c)) {
				StringBuilder id = new StringBuilder();
				while (idx < len && Character.isJavaIdentifierPart(s.charAt(idx))) {
					id.append(s.charAt(idx));
					idx++;
				}
				if (id.toString().equals(letName)) {
					out.append("let_").append(letName);
					continue;
				}
			}
			throw new CompileException("Undefined symbol: " + s);
		}
		return varCount;
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

	private static String buildCWithLet(String name, String expr1, String expr2, int varCount) {
		StringBuilder sb = new StringBuilder();
		sb.append("#include <stdio.h>\n");
		sb.append("int main(void) {\n");
		for (int i = 0; i < varCount; i++)
			sb.append("    int _v").append(i).append(" = 0;\n");
		for (int i = 0; i < varCount; i++)
			sb.append("    if (scanf(\"%d\", &_v").append(i).append(") != 1) return 0;\n");
		sb.append("    int let_").append(name).append(" = ").append(expr1).append(";\n");
		sb.append("    return ").append(expr2).append(";\n");
		sb.append("}\n");
		return sb.toString();
	}
}
