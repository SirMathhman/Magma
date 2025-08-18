package magma;

public class Compiler {
	static String compile(String input) {
		// Evaluate simple integer arithmetic expressions containing +, -, *
		// Examples supported by tests: "", "5", "2 + 3", "5 - 3", "2 * 3"

		if (input == null || input.trim().isEmpty()) {
			return "int main(){return 0;}";
		}

		String trimmed = input.trim();

		// Support a minimal external readInt() function used by tests.
		// If the program declares `external fn readInt() : I32;` and uses `readInt()`
		// within an expression (e.g. `readInt() + 3`), generate a C program that
		// reads an integer into `v`, substitutes `readInt()` with `v` and returns
		// the evaluated expression.
		if (trimmed.contains("external fn readInt()") && trimmed.contains("readInt()")) {
			// split on the first semicolon to get the expression after the declaration
			int idx = trimmed.indexOf(';');
			String expr = "readInt()";
			StringBuilder functionDefs = new StringBuilder();
			if (idx >= 0 && idx + 1 < trimmed.length()) {
				expr = trimmed.substring(idx + 1).trim();
				if (expr.isEmpty())
					expr = "readInt()";
				// If the user wrapped the expression in braces (e.g. `{readInt()}`),
				// strip a single surrounding pair so we generate a valid C expression.
				if (expr.startsWith("{") && expr.endsWith("}")) {
					expr = expr.substring(1, expr.length() - 1).trim();
				}
				// Collect optional function declarations of form: fn name() => body;
				while (expr.startsWith("fn ")) {
					int semi = expr.indexOf(';');
					if (semi <= 0)
						break;
					String decl = expr.substring(0, semi).trim(); // fn name() => body
					int arrow = decl.indexOf("=>");
					if (arrow <= 0)
						break;
					String header = decl.substring(0, arrow).trim();
					String body = decl.substring(arrow + 2).trim();
					// parse name and optional single parameter from header: fn NAME() or fn
					// NAME(param : Type)
					int fnIdx = header.indexOf("fn");
					int paren = header.indexOf('(');
					int parenClose = header.indexOf(')');
					if (fnIdx < 0 || paren <= fnIdx || parenClose <= paren)
						break;
					String name = header.substring(fnIdx + 2, paren).trim();
					String params = header.substring(paren + 1, parenClose).trim();
					String paramDecl = "";
					if (!params.isEmpty()) {
						// support comma-separated parameters, each like `name : Type` (we ignore the
						// Type)
						String[] parts = params.split(",");
						java.util.StringJoiner sj = new java.util.StringJoiner(", ");
						for (String part : parts) {
							String p = part.trim();
							int colon = p.indexOf(':');
							String pName = colon > 0 ? p.substring(0, colon).trim() : p.split("\\s+")[0].trim();
							sj.add("int " + pName);
						}
						paramDecl = sj.toString();
					}
					functionDefs.append("int ").append(name).append("(").append(paramDecl).append("){return (").append(body)
							.append(");}\n");
					expr = expr.substring(semi + 1).trim();
				}
				// Support a simple `let` binding form used in tests: `let x = <expr>; <body>`
				// Translate to C by emitting `int x = <expr>;` and returning the body.
				if (expr.startsWith("let ")) {
					// Collect one or more leading `let` bindings and emit them as C declarations.
					StringBuilder decls = new StringBuilder();
					while (expr.startsWith("let ")) {
						int semi = expr.indexOf(';');
						if (semi <= 0)
							break;
						String binding = expr.substring(4, semi).trim(); // after "let " up to ';'
						int eq = binding.indexOf('=');
						if (eq <= 0)
							break;
						String name = binding.substring(0, eq).trim();
						String value = binding.substring(eq + 1).trim();
						String declType = value.startsWith("&") ? "int *" : "int";
						decls.append(declType).append(" ").append(name).append(" = ").append(value).append(";");
						expr = expr.substring(semi + 1).trim();
					}
					// expr now holds the final expression (body)
					if (!expr.isEmpty()) {
						return "#include <stdio.h>\nint readInt(){int v=0; if(scanf(\"%d\", &v)!=1) return 0; return v;}\n"
								+ functionDefs.toString()
								+ "int main(){" + decls.toString() + " return (" + expr + ");}";
					}
				}
			}
			// If the expression is an `if(cond){then}else{else}` form, try to translate it
			// to a C ternary
			String trimmedExpr = expr.trim();
			if (trimmedExpr.startsWith("if")) {
				// find condition between matching parentheses after 'if'
				int p = trimmedExpr.indexOf('(');
				if (p >= 0) {
					int depth = 0;
					int i = p;
					for (; i < trimmedExpr.length(); i++) {
						char c = trimmedExpr.charAt(i);
						if (c == '(')
							depth++;
						else if (c == ')') {
							depth--;
							if (depth == 0)
								break;
						}
					}
					if (i < trimmedExpr.length()) {
						String cond = trimmedExpr.substring(p + 1, i).trim();
						// then block
						int thenStart = trimmedExpr.indexOf('{', i + 1);
						if (thenStart >= 0) {
							int depthB = 0;
							int j = thenStart;
							for (; j < trimmedExpr.length(); j++) {
								char c = trimmedExpr.charAt(j);
								if (c == '{')
									depthB++;
								else if (c == '}') {
									depthB--;
									if (depthB == 0)
										break;
								}
							}
							if (j < trimmedExpr.length()) {
								String thenBody = trimmedExpr.substring(thenStart + 1, j).trim();
								// expect 'else' after then block
								int elseIdx = trimmedExpr.indexOf("else", j + 1);
								if (elseIdx >= 0) {
									int elseStart = trimmedExpr.indexOf('{', elseIdx + 4);
									if (elseStart >= 0) {
										int depthE = 0;
										int k = elseStart;
										for (; k < trimmedExpr.length(); k++) {
											char c = trimmedExpr.charAt(k);
											if (c == '{')
												depthE++;
											else if (c == '}') {
												depthE--;
												if (depthE == 0)
													break;
											}
										}
										if (k < trimmedExpr.length()) {
											String elseBody = trimmedExpr.substring(elseStart + 1, k).trim();
											String tern = "((" + cond + ") ? (" + thenBody + ") : (" + elseBody + "))";
											expr = tern;
										}
									}
								}
							}
						}
					}
				}
			}
			// Generate a C helper function so each readInt() call performs its own scanf
			return "#include <stdio.h>\nint readInt(){int v=0; if(scanf(\"%d\", &v)!=1) return 0; return v;}\n"
					+ functionDefs.toString()
					+ "int main(){return (" + expr + ");}";
		}
		int value = 0;

		// Try plain integer first
		try {
			value = Integer.parseInt(trimmed);
		} catch (NumberFormatException e) {
			// tokenize numbers and operators (+, -, *)
			java.util.List<String> tokens = new java.util.ArrayList<>();
			java.util.regex.Matcher m = java.util.regex.Pattern.compile("(-?\\d+)|[+\\-*]").matcher(trimmed);
			while (m.find()) {
				tokens.add(m.group());
			}
			try {
				// if no tokens, fall back to 0
				if (tokens.isEmpty()) {
					value = 0;
				} else {
					// first handle multiplication (higher precedence)
					java.util.List<String> reduced = new java.util.ArrayList<>();
					for (int i = 0; i < tokens.size(); i++) {
						String t = tokens.get(i);
						if ("*".equals(t) && !reduced.isEmpty() && i + 1 < tokens.size()) {
							int left = Integer.parseInt(reduced.remove(reduced.size() - 1));
							int right = Integer.parseInt(tokens.get(++i));
							reduced.add(Integer.toString(left * right));
						} else {
							reduced.add(t);
						}
					}
					// now handle + and - left-to-right
					int acc = Integer.parseInt(reduced.get(0));
					for (int i = 1; i < reduced.size(); i += 2) {
						String op = reduced.get(i);
						int rhs = Integer.parseInt(reduced.get(i + 1));
						if ("+".equals(op))
							acc += rhs;
						else if ("-".equals(op))
							acc -= rhs;
						else
							throw new NumberFormatException("Unknown operator: " + op);
					}
					value = acc;
				}
			} catch (Exception ex) {
				// On any parse/eval error return 0
				value = 0;
			}
		}

		return "int main(){return " + value + ";}";
	}
}
