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
			if (containsMethod(classBody)) {
				StringBuilder result = new StringBuilder();
				result.append("struct ").append(className).append(" {};");

				// Extract and transform methods
				if (isMethod(classBody)) {
					result.append(" ").append(compileMethod(classBody, className));
				}

				return result.toString();
			} else {
				// Empty class
				return "struct " + className + " {};";
			}
		}
		return null;
	}

	private static boolean containsMethod(String classBody) {
		return classBody.contains("(") && classBody.contains(")") && 
			   (classBody.contains("void ") || classBody.contains("String "));
	}

	private static boolean isMethod(String classBody) {
		return (classBody.startsWith("void ") || classBody.startsWith("String ")) && 
			   classBody.contains("(") && classBody.contains(")") && classBody.contains("{");
	}

	private static String convertJavaTypesToC(String parameters) {
		return parameters.replace("String ", "const char* ");
	}

	private static String compileMethod(String classBody, String className) {
		// Parse method signature and body
		int paramStart = classBody.indexOf("(");
		int paramEnd = classBody.indexOf(")");
		int bodyStart = classBody.indexOf("{");
		int bodyEnd = classBody.lastIndexOf("}");
		
		// Extract return type and method name
		String signature = classBody.substring(0, paramStart).trim();
		String[] parts = signature.split("\\s+");
		String returnType = parts[0];
		String methodName = parts[1];
		
		// Extract parameters and body
		String parameters = classBody.substring(paramStart + 1, paramEnd);
		String methodBody = classBody.substring(bodyStart + 1, bodyEnd).trim();
		
		StringBuilder result = new StringBuilder();
		result.append(convertJavaTypesToC(returnType + " "))
			  .append(methodName)
			  .append("_")
			  .append(className)
			  .append("(void* _ref_");
		
		// Add parameters if any
		if (!parameters.trim().isEmpty()) {
			result.append(", ").append(convertJavaTypesToC(parameters));
		}
		
		result.append(") {struct ")
			  .append(className)
			  .append(" this = *(struct ")
			  .append(className)
			  .append("*) _ref_;");
		
		// Add method body if any
		if (!methodBody.isEmpty()) {
			result.append(" ").append(methodBody);
		}
		
		result.append("}");
		
		return result.toString();
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
