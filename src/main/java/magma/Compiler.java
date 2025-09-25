package magma;

public class Compiler {
	private static final String C_ERR_EXIT_BLOCK = "    } else {\n        exit(1);\n    }\n";

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

			// Support simple if expressions: if (COND) THEN else ELSE
			if (expression.startsWith("if (") || expression.startsWith("if(")) {
				return compileIfExpression(expression);
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

	// removed duplicate helper: use extractRhsIfReadInt instead

	private String buildCForCompositeWithAssignment(String name) {
		StringBuilder c = new StringBuilder();
		c.append(buildCompositeHeader(name, "0").replace(" = 0;\n", ";\n"));
		c.append("    int __tmp;\n");
		c.append(emitScanfExitBlock(name, "="));
		c.append("    return 0;\n");
		c.append("}\n");
		return c.toString();
	}

	private Option<Result<String, String>> checkMutableThen(LetInfo info, String lhs,
			java.util.function.Supplier<Option<Result<String, String>>> onMutable) {
		if (!info.mutables.contains(lhs))
			return Option.ok(Result.err("Assignment to non-mutable variable: " + lhs));
		return onMutable.get();
	}

	private Option<Result<String, String>> handleCompositeRhs(String compRhs) {
		String cr = compRhs.trim();
		// If RHS is an if-expression, delegate to compileIfExpression
		if (cr.startsWith("if(") || cr.startsWith("if (")) {
			return Option.ok(compileIfExpression(compRhs));
		}
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

	private int findMatchingParen(String s, int openIndex) {
		int depth = 0;
		for (int i = openIndex; i < s.length(); i++) {
			char ch = s.charAt(i);
			if (ch == '(')
				depth++;
			else if (ch == ')') {
				depth--;
				if (depth == 0)
					return i;
			}
		}
		return -1;
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
		// Try composite-final handling
		var compRes = tryHandleCompositeCase(info);
		if (compRes instanceof Option.Ok)
			return compRes;
		// Try assignment-after-lets handling
		var asgRes = tryHandleAssignmentAfterLets(info);
		if (asgRes instanceof Option.Ok)
			return asgRes;
		// Build C for simple vars (readInt() variables)
		if (info.vars.isEmpty())
			return Option.ok(Result.err("No let variables found"));
		return Option.ok(Result.ok(buildCForVars(info)));
	}

	private Option<Result<String, String>> tryHandleCompositeCase(LetInfo info) {
		if (info.compositeLetRhs instanceof Option.Ok<String> compRhsOpt
				&& info.compositeLetName instanceof Option.Ok<String> compNameOpt
				&& info.finalExpr instanceof Option.Ok<String> finOpt) {
			return handleCompositeFinal(info, compRhsOpt.value(), compNameOpt.value(), finOpt.value());
		}
		if (info.compositeLetName instanceof Option.Ok
				&& info.compositeLetRhs instanceof Option.Ok
				&& info.finalExpr instanceof Option.Err) {
			return Option.ok(Result.ok(buildCForComposite(info)));
		}
		return Option.err();
	}

	private Option<Result<String, String>> tryHandleAssignmentAfterLets(LetInfo info) {
		if (info.assignments.isEmpty())
			return Option.err();
		// only support single assignment of form `x = readInt();` for now
		String asg = info.assignments.get(0).trim();
		var partsOpt = parseAssignmentWithOp(asg);
		if (partsOpt instanceof Option.Ok<String[]> p && info.compositeLetName instanceof Option.Ok<String> nameOpt) {
			String lhs = p.value()[0];
			String op = p.value()[1];
			if (lhs.equals(nameOpt.value())) {
				if (!info.mutables.contains(lhs))
					return Option.ok(Result.err("Assignment to non-mutable variable: " + lhs));
				if (op.equals("readInt()") || op.equals("=")) {
					return Option.ok(Result.ok(buildCForCompositeWithAssignment(nameOpt.value())));
				}
				if (op.equals("+=") || op.equals("-=") || op.equals("*=") || op.equals("/=")) {
					return Option.ok(Result.ok(buildCForCompositeWithCompoundAssignment(info, nameOpt.value(), op)));
				}
			}
		}
		return Option.err();
	}

	private String buildCForComposite(LetInfo info) {
		String name = "_x";
		String rhs = "0";
		if (info.compositeLetName instanceof Option.Ok<String> n)
			name = n.value();
		if (info.compositeLetRhs instanceof Option.Ok<String> r)
			rhs = r.value();
		StringBuilder c = new StringBuilder();
		c.append(buildCompositeHeader(name, rhs));
		c.append("    (void)" + name + ";\n");
		c.append("    return 0;\n");
		c.append("}\n");
		return c.toString();
	}

	private String buildCompositeHeader(String name, String rhs) {
		StringBuilder c = new StringBuilder();
		c.append("#include <stdio.h>\n");
		c.append("#include <stdlib.h>\n\n");
		c.append("int main(void) {\n");
		c.append("    int " + name + " = " + rhs + ";\n");
		return c.toString();
	}

	private Option<Result<String, String>> handleCompositeFinal(LetInfo info, String compRhs, String compName,
			String fin) {
		if (!fin.equals(compName))
			return Option.ok(Result.err("Final expression does not reference composite"));
		// If there is an assignment later that assigns to this composite name,
		// prefer emitting code that performs the assignment (e.g., x = readInt()).
		for (String asg : info.assignments) {
			var partsOpt = parseAssignmentWithOp(asg);
			if (partsOpt instanceof Option.Ok<String[]> p) {
				String lhs = p.value()[0];
				String op = p.value()[1].equals("readInt()") ? "=" : p.value()[1];
				// If RHS is readInt() and lhs is mutable
				if (op.equals("=") && lhs.equals(compName)) {
					return checkMutableThen(info, lhs, () -> Option.ok(Result.ok(buildCForCompositeWithAssignment(compName))));
				}
				// support compound ops like += where p.value()[1] == "+=" with RHS readInt()
				if ((op.equals("+=") || op.equals("-=") || op.equals("*=") || op.equals("/=")) && lhs.equals(compName)) {
					return checkMutableThen(info, lhs,
							() -> Option.ok(Result.ok(buildCForCompositeWithCompoundAssignment(info, compName, op))));
				}
			}
		}
		return handleCompositeRhs(compRhs);
	}

	private static final class LetInfo {
		final java.util.List<String> vars = new java.util.ArrayList<>();
		final java.util.List<String> assignments = new java.util.ArrayList<>();
		final java.util.Set<String> mutables = new java.util.HashSet<>();
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
				// support `let mut x = ...` by stripping leading mut and recording mutables
				boolean isMut = false;
				if (left.startsWith("mut ")) {
					isMut = true;
					left = left.substring(4).trim();
				}
				int colon = left.indexOf(':');
				String name = (colon == -1) ? left.trim() : left.substring(0, colon).trim();
				var readOpt = extractRhsIfReadInt(s);
				if (readOpt instanceof Option.Ok<String>) {
					info.vars.add(name);
					if (isMut)
						info.mutables.add(name);
				} else {
					var partsOpt = parseAssignment(s);
					if (partsOpt instanceof Option.Ok<String[]> parts) {
						String rhs = parts.value()[1];
						info.compositeLetName = Option.ok(name);
						info.compositeLetRhs = Option.ok(rhs);
						if (isMut)
							info.mutables.add(name);
					} else {
						// failed to parse assignment
						info.finalExpr = Option.ok("__PARSE_ERROR__");
						return info;
					}
				}
			} else {
				// Non-let statements: if they contain '=' treat them as assignments otherwise
				// final expression
				if (s.contains("=")) {
					// preserve the original assignment form (including compound ops like +=)
					info.assignments.add(s + ";");
				} else {
					info.finalExpr = Option.ok(s);
				}
			}
		}
		return info;
	}

	private Option<String[]> parseAssignment(String s) {
		// legacy: return [lhs, rhs]
		int eq = s.indexOf('=');
		if (eq == -1)
			return Option.err();
		String left = s.substring(0, eq).trim();
		String right = trimSemicolon(s.substring(eq + 1).trim());
		return Option.ok(new String[] { left, right });
	}

	private Option<String[]> parseAssignmentWithOp(String s) {
		// returns [lhs, op, rhs] where op is one of =, +=, -=, *=, /=
		for (String op : new String[] { "+=", "-=", "*=", "/=" }) {
			int idx = s.indexOf(op);
			if (idx != -1) {
				String left = s.substring(0, idx).trim();
				String right = trimSemicolon(s.substring(idx + 2).trim());
				return Option.ok(new String[] { left, op, right });
			}
		}
		// fallback to simple assignment
		var simple = parseAssignment(s);
		if (simple instanceof Option.Ok<String[]> p) {
			return Option.ok(new String[] { p.value()[0], "=", p.value()[1] });
		}
		return Option.err();
	}

	private String buildCForCompositeWithCompoundAssignment(LetInfo info, String name, String op) {
		String init = "0";
		if (info.compositeLetRhs instanceof Option.Ok<String> r)
			init = r.value();
		StringBuilder c = new StringBuilder();
		c.append(buildCompositeHeader(name, init));
		c.append("    int __tmp;\n");
		c.append(emitScanfExitBlock(name, op));
		c.append("    return 0;\n");
		c.append("}\n");
		return c.toString();
	}

	private String trimSemicolon(String s) {
		if (s.endsWith(";"))
			return s.substring(0, s.length() - 1).trim();
		return s;
	}

	private String emitScanfExitBlock(String name, String op) {
		StringBuilder c = new StringBuilder();
		c.append("    if (scanf(\"%d\", &__tmp) == 1) {\n");
		c.append(buildOpLine(name, op));
		c.append("        exit(" + name + ");\n");
		c.append(C_ERR_EXIT_BLOCK);
		return c.toString();
	}

	private String buildOpLine(String name, String op) {
		if ("+=".equals(op))
			return "        " + name + " += __tmp;\n";
		if ("-=".equals(op))
			return "        " + name + " -= __tmp;\n";
		if ("*=".equals(op))
			return "        " + name + " *= __tmp;\n";
		if ("/=".equals(op))
			return "        " + name + " /= __tmp;\n";
		return "        " + name + " = __tmp;\n";
	}

	private Option<String> extractRhsIfReadInt(String asg) {
		var parts = parseAssignment(asg);
		if (parts instanceof Option.Ok<String[]> p) {
			String lhs = p.value()[0];
			String rhs = p.value()[1];
			if (rhs.equals("readInt()"))
				return Option.ok(lhs);
		}
		return Option.err();
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
		c.append(C_ERR_EXIT_BLOCK);
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

	private Result<String, String> compileIfExpression(String expression) {
		// Very small and strict parser for patterns like: if (A == B) X else Y
		String s = expression.trim();
		// Remove leading if and surrounding parentheses
		int startCond = s.indexOf('(');
		if (startCond == -1)
			return Result.err("Unsupported if expression: " + expression);
		int endCond = findMatchingParen(s, startCond);
		if (endCond == -1)
			return Result.err("Unsupported if expression: missing closing ) for " + expression);
		String cond = s.substring(startCond + 1, endCond).trim();
		String rest = s.substring(endCond + 1).trim();
		// Expect pattern: THEN else ELSE
		int elseIdx = rest.indexOf("else");
		if (elseIdx == -1)
			return Result.err("Unsupported if expression (no else): " + expression);
		String thenPart = rest.substring(0, elseIdx).trim();
		String elsePart = rest.substring(elseIdx + 4).trim();

		// For now support cond: readInt() == readInt()
		if (!cond.equals("readInt() == readInt()"))
			return Result.err("Unsupported condition: " + cond);

		// thenPart and elsePart should be integer literals (or simple expressions)
		String thenVal = thenPart;
		String elseVal = elsePart;

		// Build C that reads two ints and branches
		StringBuilder c = new StringBuilder();
		c.append("#include <stdio.h>\n");
		c.append("#include <stdlib.h>\n\n");
		c.append("int main(void) {\n");
		c.append("    int a, b;\n");
		c.append("    if (scanf(\"%d%d\", &a, &b) == 2) {\n");
		c.append("        if (a == b) exit(").append(thenVal).append(");\n");
		c.append("        else exit(").append(elseVal).append(");\n");
		c.append(C_ERR_EXIT_BLOCK);
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
		c.append(C_ERR_EXIT_BLOCK);
	}
}
