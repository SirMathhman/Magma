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

			// Split on semicolon to separate declaration from expression
			String[] parts = trimmed.split(";");
			if (parts.length < 2) {
				return Result.err("Expected both declaration and expression");
			}

			// Parse the intrinsic declaration
			String declaration = parts[0].trim();
			String expression = parts[1].trim();

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
			appendReadIntLogic(c, 1, "");
		} else {
			// detect operator
			String op = null;
			if (expression.contains(" + ")) op = " + ";
			else if (expression.contains(" - ")) op = " - ";

			if (op != null) {
				String[] partsOp = expression.split(java.util.regex.Pattern.quote(op));
				if (partsOp.length == 2 && partsOp[0].trim().equals("readInt()") && partsOp[1].trim().equals("readInt()")) {
					appendReadIntLogic(c, 2, op);
				} else {
					return Result.err("Unsupported expression: " + expression);
				}
			} else {
				return Result.err("Unsupported expression: " + expression);
			}
		}

		c.append("    return 0;\n");
		c.append("}\n");

		return Result.ok(c.toString());
	}

	private void appendReadIntLogic(StringBuilder c, int count, String op) {
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
				if (i < count) c.append(", ");
				else c.append(";\n");
			}
		}

		// Build scanf condition
		c.append("    if (");
		for (int i = 1; i <= count; i++) {
			if (i > 1) c.append(" && ");
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
				if (i < count) c.append(op);
			}
		}
		c.append(");\n");
		c.append("    } else {\n");
		c.append("        exit(1);\n");
		c.append("    }\n");
	}
}
