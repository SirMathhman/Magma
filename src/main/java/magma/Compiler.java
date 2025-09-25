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

			// Handle simple let-assignment followed by usage, e.g.
			// "let result : I32 = readInt() + readInt() * readInt(); result"
			if (expression.startsWith("let ") && expression.contains("=")) {
				// Support multiple let statements separated by ';' followed by a final
				// expression.
				String[] stmts = expression.split(";");
				java.util.List<String> vars = new java.util.ArrayList<>();
				Option<String> finalExpr = Option.err();
				Option<String> compositeLetName = Option.err();
				Option<String> compositeLetRhs = Option.err();
				for (String s : stmts) {
					if (!(s instanceof String))
						continue;
					s = s.trim();
					if (s.isEmpty())
						continue;
					if (s.startsWith("let ")) {
						int eq = s.indexOf('=');
						if (eq == -1)
							return Result.err("Unsupported let statement: " + s);
						String left = s.substring(3, eq).trim(); // e.g. "x : I32"
						int colon = left.indexOf(':');
						String name = (colon == -1) ? left.trim() : left.substring(0, colon).trim();
						String rhs = s.substring(eq + 1).trim();
						// remove trailing semicolon if present
						if (rhs.endsWith(";"))
							rhs = rhs.substring(0, rhs.length() - 1).trim();
						if (rhs.equals("readInt()")) {
							vars.add(name);
						} else {
							// record composite RHS (e.g., arithmetic expression) to handle cases like
							// let result : I32 = readInt() + readInt() * readInt()
							compositeLetName = Option.ok(name);
							compositeLetRhs = Option.ok(rhs);
						}
					} else {
						// final expression after lets
						finalExpr = Option.ok(s);
					}
				}
				if (finalExpr instanceof Option.Err)
					return Result.err("No final expression after let statements");
				// If we found a composite RHS and the final expression is exactly the let name,
				// delegate to the readInt() expression compiler for the composite RHS.
				if (compositeLetRhs instanceof Option.Ok && compositeLetName instanceof Option.Ok
						&& finalExpr instanceof Option.Ok) {
					String compRhs = ((Option.Ok<String>) compositeLetRhs).value();
					String compName = ((Option.Ok<String>) compositeLetName).value();
					String fin = ((Option.Ok<String>) finalExpr).value();
					if (fin.equals(compName)) {
						return compileReadIntExpression(compRhs);
					}
				}
				// Generate full C program that declares the variables, scans them, and exits
				// with finalExpr
				if (vars.isEmpty())
					return Result.err("No let variables found");
				StringBuilder c = new StringBuilder();
				c.append("#include <stdio.h>\n");
				c.append("#include <stdlib.h>\n\n");
				c.append("int main(void) {\n");
				// Declare variables
				c.append("    int ");
				for (int i = 0; i < vars.size(); i++) {
					c.append(vars.get(i));
					if (i < vars.size() - 1)
						c.append(", ");
					else
						c.append(";\n");
				}
				// Build scanf condition
				c.append("    if (");
				for (int i = 0; i < vars.size(); i++) {
					if (i > 0)
						c.append(" && ");
					c.append("scanf(\"%d\", &" + vars.get(i) + ") == 1");
				}
				c.append(") {\n");
				// exit expression using finalExpr (assume variables and operators are
				// C-compatible)
				c.append("        exit(");
				String finVal = ((Option.Ok<String>) finalExpr).value();
				c.append(finVal);
				c.append(");\n");
				c.append("    } else {\n");
				c.append("        exit(1);\n");
				c.append("    }\n");
				c.append("    return 0;\n");
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
					if (t.equals("+"))
						ops[(i - 1) / 2] = " + ";
					else if (t.equals("-"))
						ops[(i - 1) / 2] = " - ";
					else if (t.equals("*"))
						ops[(i - 1) / 2] = " * ";
					else {
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
