package magma;

public class Compiler {
	public static Result<String, CompileException> compile(String input) {
		if (input.isEmpty()) return new Ok<>("");

		String result = tryCompilePackage(input);
		if (result != null) return new Ok<>(result);

		result = tryCompileClass(input);
		if (result != null) return new Ok<>(result);

		result = tryCompileSealedInterface(input);
		if (result != null) return new Ok<>(result);

		result = tryCompileMethodOrStatement(input);
		if (result != null) return new Ok<>(result);

		return new Err<>(new CompileException());
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

	private static String compileInterfaceWithMultipleImplementations(String[] declarations) {
		// Parse interface
		String interfaceDecl = declarations[0].trim();
		String interfaceName = interfaceDecl.substring(17, interfaceDecl.indexOf(" {"));

		// Parse implementing classes
		StringBuilder result = new StringBuilder();
		StringBuilder enumVariants = new StringBuilder();
		StringBuilder unionFields = new StringBuilder();

		for (int i = 1; i < declarations.length; i++) {
			String classDecl = declarations[i].trim();
			String classKeywordStart = classDecl.startsWith("public ") ? "public class " : "class ";
			int classStart = classDecl.indexOf(classKeywordStart) + classKeywordStart.length();
			String className = classDecl.substring(classStart, classDecl.indexOf(" implements"));

			// Generate struct for each implementing class
			result.append("struct ").append(className).append(" {}; ");

			// Add to enum variants
			if (!enumVariants.isEmpty()) enumVariants.append(", ");
			enumVariants.append(className).append("Type");

			// Add to union fields
			if (!unionFields.isEmpty()) unionFields.append("; ");
			unionFields.append(className).append(" ").append(className.toLowerCase());
		}

		// Generate enum, union, and final struct
		result.append("enum ").append(interfaceName).append("Type {").append(enumVariants).append("}; ");
		result.append("union ").append(interfaceName).append("Value {").append(unionFields).append(";}; ");
		result.append("struct ")
					.append(interfaceName)
					.append(" {")
					.append(interfaceName)
					.append("Type _type_; ")
					.append(interfaceName)
					.append("Value _value_;};");

		return result.toString();
	}

	private static String tryCompileSealedInterface(String input) {
		if (!input.startsWith("sealed interface ")) return null;
		
		// Handle sealed interface with implementing classes (contains "; " followed by class declarations)
		if (input.contains("; ")) {
			String[] declarations = input.split("; ");
			if (declarations.length >= 2 && isValidSealedInterfaceWithImplementations(declarations)) {
				return compileInterfaceWithMultipleImplementations(declarations);
			}
		}
		
		// Handle single sealed interface (with or without methods)
		if (input.endsWith("}")) {
			return compileSingleSealedInterface(input);
		}
		
		return null;
	}
	
	private static boolean isValidSealedInterfaceWithImplementations(String[] declarations) {
		String firstDecl = declarations[0].trim();
		
		// Check if first declaration is a sealed interface
		if (!firstDecl.startsWith("sealed interface ")) return false;
		
		// Check if remaining declarations are implementing classes (must start with "class" and contain "implements")
		for (int i = 1; i < declarations.length; i++) {
			String decl = declarations[i].trim();
			if (!decl.startsWith("class ") && !decl.startsWith("public class ")) {
				return false;
			}
			if (!decl.contains(" implements ")) {
				return false;
			}
		}
		
		return true;
	}

	private static String compileSingleSealedInterface(String input) {
		int interfaceStart = 17; // after "sealed interface "
		int braceIndex = input.indexOf(" {");
		if (braceIndex == -1) return null; // No space before brace found
		
		String interfaceName = input.substring(interfaceStart, braceIndex);
		String interfaceBody = input.substring(input.indexOf("{") + 1, input.lastIndexOf("}")).trim();
		
		StringBuilder result = new StringBuilder();
		
		// Generate basic sealed interface structure
		result.append("enum ").append(interfaceName).append("Type {}; ");
		result.append("union ").append(interfaceName).append("Value {}; ");
		result.append("struct ").append(interfaceName).append(" {")
			  .append(interfaceName).append("Type _type_; ")
			  .append(interfaceName).append("Value _value_;};");
		
		// Handle method declarations in the interface
		if (!interfaceBody.isEmpty()) {
			String methodResult = tryCompileInterfaceMethod(interfaceBody, interfaceName);
			if (methodResult != null) {
				result.append(" ").append(methodResult);
			}
		}
		
		return result.toString();
	}

	private static String tryCompileInterfaceMethod(String methodDecl, String interfaceName) {
		// Handle method declarations like "void method();"
		if (methodDecl.contains("(") && methodDecl.contains(")") && methodDecl.endsWith(";")) {
			// Remove the semicolon and add empty body for compilation
			String methodWithBody = methodDecl.substring(0, methodDecl.length() - 1) + "{}";
			String compiledMethod = compileMethod(methodWithBody, interfaceName);
			// Remove space before opening brace for interface methods to match expected format
			return compiledMethod.replace(") {", "){");
		}
		return null;
	}

	private static boolean containsMethod(String classBody) {
		return classBody.contains("(") && classBody.contains(")") &&
					 (classBody.contains("void ") || classBody.contains("String "));
	}

	private static boolean isMethod(String classBody) {
		return (classBody.startsWith("void ") || classBody.startsWith("String ")) && classBody.contains("(") &&
					 classBody.contains(")") && classBody.contains("{");
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

		result.append(") {struct ").append(className).append(" this = *(struct ").append(className).append("*) _ref_;");

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
