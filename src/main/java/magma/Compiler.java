package magma;

public class Compiler {
	/**
	 * Compiles the custom language to C code.
	 *
	 * @param input the input string
	 * @return an Ok Result holding the generated C code, or Err with error message
	 */
	public Result<String, String> compile(String input) {
		try {
			// Parse intrinsic declarations and expressions
			String trimmed = input.trim();

			// Split on the first semicolon to separate declaration from the rest
			int firstSemi = trimmed.indexOf(';');
			if (firstSemi == -1) {
				return Result.err("Expected both declaration and expression");
			}
			String declaration = trimmed.substring(0, firstSemi).trim();
			String expression = trimmed.substring(firstSemi + 1).trim();

			// Handle let-assignment cases via helper to keep compile() small
			if (expression.startsWith("let ") && expression.contains("=")) {
				var opt = tryHandleLets(expression);
				if (opt instanceof Option.Ok<Result<String, String>> okRes) {
					return okRes.value();
				}
			}

			// Support boolean literals true/false as top-level expressions
			if (expression.equals("true") || expression.equals("false")) {
				StringBuilder c = new StringBuilder();
				c.append("#include <stdlib.h>\n\n");
				c.append("int main(void) {\n");
				c.append("    exit(");
				c.append(expression.equals("true") ? "1" : "0");
				c.append(");\n");
				c.append("}\n");
				return Result.ok(c.toString());
			}

			// Check if it's a readInt intrinsic declaration
			if (declaration.startsWith("intrinsic fn readInt()")) {
				return compileReadIntExpression(expression);
			}

			return Result.err("Unsupported language construct: " + trimmed);
		} catch (Exception e) {
			return Result.err("Compilation error: " + e.getMessage());
		}
	}

	private Option<String> mapTokenToOp(String t) {
		if (t.equals("+"))
			return Option.ok(" + ");
		if (t.equals("-"))
			return Option.ok(" - ");
		if (t.equals("*"))
			return Option.ok(" * ");
		if (t.equals("=="))
			return Option.ok(" == ");
		return Option.err();
	}

	private Option<Result<String, String>> tryHandleLets(String expression) {
		LetInfo info = parseLetStatements(expression);
		// Composite RHS delegation (when final expression refers to the composite let
		// name)
		if (info.compositeLetRhs instanceof Option.Ok<String> compRhsOpt
				&& info.compositeLetName instanceof Option.Ok<String> compNameOpt
				&& info.finalExpr instanceof Option.Ok<String> finOpt) {
			String compRhs = compRhsOpt.value();
			String compName = compNameOpt.value();
			String fin = finOpt.value();
			if (fin.equals(compName)) {
				String cr = compRhs.trim();
				// If RHS is a readInt expression or contains readInt(), delegate
				if (cr.equals("readInt()") || cr.contains("readInt()")) {
					return Option.ok(compileReadIntExpression(compRhs));
				}
				// If RHS is a simple integer literal, emit C that exits with that value
				if (cr.matches("-?\\d+")) {
					StringBuilder c = new StringBuilder();
					c.append("#include <stdlib.h>\n\n");
					c.append("int main(void) {\n");
					c.append("    exit(").append(cr).append(");\n");
					c.append("}\n");
					return Option.ok(Result.ok(c.toString()));
				}
				// If RHS is boolean literal, emit C that exits with 1 for true, 0 for false
				if (cr.equals("true") || cr.equals("false")) {
					StringBuilder c = new StringBuilder();
					c.append("#include <stdlib.h>\n\n");
					c.append("int main(void) {\n");
					c.append("    exit(").append(cr.equals("true") ? "1" : "0").append(");\n");
					c.append("}\n");
					return Option.ok(Result.ok(c.toString()));
				}
				// Otherwise unsupported for now
				return Option.ok(Result.err("Unsupported composite RHS: " + compRhs));
			}
		}

		// If there's a composite let (constant or expression) but no final expression,
		// generate a simple C program that initialises the variable and returns 0.
		if (info.compositeLetName instanceof Option.Ok
				&& info.compositeLetRhs instanceof Option.Ok
				&& info.finalExpr instanceof Option.Err) {
			return Option.ok(Result.ok(buildCForComposite(info)));
		}

		// Build C for simple vars (readInt() variables)
		if (info.vars.isEmpty())
			return Option.ok(Result.err("No let variables found"));
		return Option.ok(Result.ok(buildCForVars(info)));
	}

	private String buildCForComposite(LetInfo info) {
		String name = "_x";
		String rhs = "0";
		if (info.compositeLetName instanceof Option.Ok<String> n)
			name = n.value();
		if (info.compositeLetRhs instanceof Option.Ok<String> r)
			rhs = r.value();

		StringBuilder c = new StringBuilder();
		c.append("#include <stdio.h>\n");
		c.append("#include <stdlib.h>\n\n");
		c.append("int main(void) {\n");
		c.append("    int " + name + " = " + rhs + ";\n");
		c.append("    (void)" + name + ";\n");
		c.append("    return 0;\n");
		c.append("}\n");
		return c.toString();
	}

	private static final class LetInfo {
		final java.util.List<String> vars = new java.util.ArrayList<>();
		Option<String> finalExpr = Option.err();
		Option<String> compositeLetName = Option.err();
		Option<String> compositeLetRhs = Option.err();
	}

	private LetInfo parseLetStatements(String expression) {
		LetInfo info = new LetInfo();
		String[] stmts = expression.split(";");
		for (String s : stmts) {
			if (!(s instanceof String))
				continue;
			s = s.trim();
			if (s.isEmpty())
				continue;
			if (s.startsWith("let ")) {
				int eq = s.indexOf('=');
				if (eq == -1) {
					// record an error in finalExpr to be handled by caller
					info.finalExpr = Option.ok("__PARSE_ERROR__");
					return info;
				}
				String left = s.substring(3, eq).trim();
				int colon = left.indexOf(':');
				String name = (colon == -1) ? left.trim() : left.substring(0, colon).trim();
				String rhs = s.substring(eq + 1).trim();
				if (rhs.endsWith(";"))
					rhs = rhs.substring(0, rhs.length() - 1).trim();
				if (rhs.equals("readInt()")) {
					info.vars.add(name);
				} else {
					info.compositeLetName = Option.ok(name);
					info.compositeLetRhs = Option.ok(rhs);
				}
			} else {
				info.finalExpr = Option.ok(s);
			}
		}
		return info;
	}

	private String buildCForVars(LetInfo info) {
		StringBuilder c = new StringBuilder();
		c.append("#include <stdio.h>\n");
		c.append("#include <stdlib.h>\n\n");
		c.append("int main(void) {\n");
		// Declare variables
		c.append("    int ");
		for (int i = 0; i < info.vars.size(); i++) {
			c.append(info.vars.get(i));
			if (i < info.vars.size() - 1)
				c.append(", ");
			else
				c.append(";\n");
		}
		// Build scanf condition
		c.append("    if (");
		for (int i = 0; i < info.vars.size(); i++) {
			if (i > 0)
				c.append(" && ");
			c.append("scanf(\"%d\", &" + info.vars.get(i) + ") == 1");
		}
		c.append(") {\n");
		// exit expression using finalExpr (assume variables and operators are
		// C-compatible)
		c.append("        exit(");
		String finVal = (info.finalExpr instanceof Option.Ok<String> fv) ? fv.value() : "0";
		c.append(finVal);
		c.append(");\n");
		c.append("    } else {\n");
		c.append("        exit(1);\n");
		c.append("    }\n");
		c.append("    return 0;\n");
		c.append("}\n");
		return c.toString();
	}

	private Result<String, String> compileReadIntExpression(String expression) {
		StringBuilder c = new StringBuilder();
		c.append("#include <stdio.h>\n");
		c.append("#include <stdlib.h>\n");
		c.append("\n");
		c.append("int main(void) {\n");

		// Simple expression parsing - handle readInt() calls, addition and subtraction
		if (expression.equals("readInt()")) {
			// Single readInt() call
			appendReadIntLogic(c, 1, new String[0]);
		} else {
			// Tokenize by spaces to allow mixed operators like: readInt() + readInt() -
			// readInt()
			String[] tokens = expression.split(" ");
			// tokens should be odd length: readInt(), op, readInt(), op, readInt(), ...
			if (tokens.length < 3 || tokens.length % 2 == 0) {
				return Result.err("Unsupported expression: " + expression);
			}
			int count = (tokens.length + 1) / 2;
			String[] ops = new String[count - 1];
			boolean ok = true;
			for (int i = 0; i < tokens.length; i++) {
				if (i % 2 == 0) {
					if (!tokens[i].trim().equals("readInt()")) {
						ok = false;
						break;
					}
				} else {
					String t = tokens[i].trim();
					var mappedOpt = mapTokenToOp(t);
					if (mappedOpt instanceof Option.Ok<String> mo) {
						ops[(i - 1) / 2] = mo.value();
					} else {
						ok = false;
						break;
					}
				}
			}
			if (!ok)
				return Result.err("Unsupported expression: " + expression);
			appendReadIntLogic(c, count, ops);
		}

		c.append("    return 0;\n");
		c.append("}\n");

		return Result.ok(c.toString());
	}

	private void appendReadIntLogic(StringBuilder c, int count, String[] ops) {
		if (count <= 0) {
			c.append("    exit(1);\n");
			return;
		}

		// Declare variables: value or value1, value2, ...
		if (count == 1) {
			c.append("    int value;\n");
		} else {
			c.append("    int ");
			for (int i = 1; i <= count; i++) {
				c.append("value" + i);
				if (i < count)
					c.append(", ");
				else
					c.append(";\n");
			}
		}

		// Build scanf condition
		c.append("    if (");
		for (int i = 1; i <= count; i++) {
			if (i > 1)
				c.append(" && ");
			String varName = (count == 1) ? "value" : ("value" + i);
			c.append("scanf(\"%d\", &" + varName + ") == 1");
		}
		c.append(") {\n");

		// Build exit expression
		c.append("        exit(");
		if (count == 1) {
			c.append("value");
		} else {
			for (int i = 1; i <= count; i++) {
				c.append("value" + i);
				if (i < count) {
					String op = (ops instanceof String[] && ops.length >= i) ? ops[i - 1] : " + ";
					c.append(op);
				}
			}
		}
		c.append(");\n");
		c.append("    } else {\n");
		c.append("        exit(1);\n");
		c.append("    }\n");
	}
}
