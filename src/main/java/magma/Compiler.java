package magma;

public class Compiler {
	public static String compile(String input) throws CompileException {
		// Recognize the test prelude declaration for the intrinsic readInt and a single
		// call
		final String prelude = "intrinsic fn readInt() : I32; ";
		if (input != null && input.contains(prelude)) {
			String rest = input.substring(input.indexOf(prelude) + prelude.length()).trim();
			// Parse a simple expression composed of `readInt()` tokens separated by '+' and
			// whitespace.
			int idx = 0;
			int len = rest.length();
			int varCount = 0;
			StringBuilder expr = new StringBuilder();
			while (idx < len) {
				// skip whitespace
				char c = rest.charAt(idx);
				if (Character.isWhitespace(c)) {
					idx++;
					continue;
				}
				// token: readInt()
				String token = "readInt()";
				if (idx + token.length() <= len && rest.startsWith(token, idx)) {
					expr.append("_v").append(varCount);
					varCount++;
					idx += token.length();
					continue;
				}
				// token: operator (+, -, *)
				char ch = rest.charAt(idx);
				if (ch == '+' || ch == '-' || ch == '*') {
					expr.append(ch);
					idx++;
					continue;
				}
				// anything else is invalid for our minimal compiler
				throw new CompileException("Undefined symbol: " + input);
			}

			if (varCount == 0) {
				throw new CompileException("Undefined symbol: " + input);
			}

			// Build C program: declare variables, read each with scanf, then return the
			// expression
			StringBuilder sb = new StringBuilder();
			sb.append("#include <stdio.h>\n");
			sb.append("int main(void) {\n");
			// declare variables (one per line to keep generated C simple)
			for (int i = 0; i < varCount; i++) {
				sb.append("    int _v").append(i).append(" = 0;\n");
			}
			// read each value
			for (int i = 0; i < varCount; i++) {
				sb.append("    if (scanf(\"%d\", &_v").append(i).append(") != 1) return 0;\n");
			}
			// return expression
			sb.append("    return ").append(expr.toString()).append(";\n");
			sb.append("}\n");
			return sb.toString();
		}
		throw new CompileException("Undefined symbol: " + input);
	}
}
