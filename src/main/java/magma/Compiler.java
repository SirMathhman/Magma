package magma;

public class Compiler {
	public static String compile(String input) throws CompileException {
		if (input.isEmpty()) return "";

		String result = tryCompilePackage(input);
		if (result != null) return result;

		result = tryCompileClass(input);
		if (result != null) return result;

		result = tryCompileMethodOrStatement(input);
		if (result != null) return result;

		throw new CompileException();
	}

	private static String tryCompilePackage(String input) {
		if (input.startsWith("package ") && input.endsWith(";")) {
			return "";
		}
		return null;
	}

	private static String tryCompileClass(String input) {
		if ((input.startsWith("class ") || input.startsWith("public class ")) && input.endsWith("}")) {
			int classKeywordStart = input.startsWith("public ") ? 13 : 6; // "public class " vs "class "
			String className = input.substring(classKeywordStart, input.indexOf(" {"));
			String classBody = input.substring(input.indexOf("{") + 1, input.lastIndexOf("}")).trim();

			// Check if class contains methods
			if (classBody.contains("void ") && classBody.contains("()")) {
				StringBuilder result = new StringBuilder();
				result.append("struct ").append(className).append(" {};");

				// Extract and transform methods
				if (classBody.startsWith("void ") && classBody.endsWith("{}")) {
					String methodName = classBody.substring(5, classBody.indexOf("()"));
					result.append(" void ")
								.append(methodName)
								.append("_")
								.append(className)
								.append("(void* _ref_) {struct ")
								.append(className)
								.append(" this = *(struct ")
								.append(className)
								.append("*) _ref_;}");
				}

				return result.toString();
			} else {
				// Empty class
				return "struct " + className + " {};";
			}
		}
		return null;
	}

	private static String tryCompileMethodOrStatement(String input) {
		if (input.startsWith("void ") && input.endsWith("{}")) {
			return input;
		}
		if (input.startsWith("if (") && input.endsWith("}")) {
			return input;
		}
		if (input.equals("return;")) {
			return input;
		}
		return null;
	}
}
