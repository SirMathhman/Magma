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

		// Simple expression parsing - handle readInt() calls and addition
		if (expression.equals("readInt()")) {
			// Single readInt() call
			c.append("    int value;\n");
			c.append("    if (scanf(\"%d\", &value) == 1) {\n");
			c.append("        exit(value);\n");
			c.append("    } else {\n");
			c.append("        exit(1);\n");
			c.append("    }\n");
		} else if (expression.contains(" + ")) {
			// Handle addition expressions
			String[] additionParts = expression.split(" \\+ ");
			if (additionParts.length == 2 &&
					additionParts[0].trim().equals("readInt()") &&
					additionParts[1].trim().equals("readInt()")) {
				// readInt() + readInt()
				c.append("    int value1, value2;\n");
				c.append("    if (scanf(\"%d\", &value1) == 1 && scanf(\"%d\", &value2) == 1) {\n");
				c.append("        exit(value1 + value2);\n");
				c.append("    } else {\n");
				c.append("        exit(1);\n");
				c.append("    }\n");
			} else {
				return Result.err("Unsupported addition expression: " + expression);
			}
		} else {
			return Result.err("Unsupported expression: " + expression);
		}

		c.append("    return 0;\n");
		c.append("}\n");

		return Result.ok(c.toString());
	}
}
