package magma;

public class Compiler {
	public static Result<String, CompileException> compile(String input) {
		if (input.isEmpty()) return new Ok<>("");

		String result = tryCompilePackage(input);
		if (result != null) return new Ok<>(addHeaders(input, result));

		result = tryCompileClass(input);
		if (result != null) return new Ok<>(addHeaders(input, result));

		result = tryCompileSealedInterface(input);
		if (result != null) return new Ok<>(addHeaders(input, result));

		result = tryCompileMethodOrStatement(input);
		if (result != null) return new Ok<>(addHeaders(input, result));

		return new Err<>(new CompileException());
	}

	private static String addHeaders(String input, String compiledResult) {
		StringBuilder headers = new StringBuilder();
		
		// Add stdbool.h header if boolean type is used
		if (input.contains("boolean ")) {
			headers.append("#include <stdbool.h>\n");
		}
		
		if (headers.length() > 0) {
			return headers.toString() + compiledResult;
		}
		
		return compiledResult;
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


	private static String generateImplementationMethod(String interfaceBody, String className, String classBody) {
		// Parse method signature from interface body (e.g., "void method();")
		if (interfaceBody.contains("(") && interfaceBody.contains(")")) {
			// Convert interface method declaration to implementation
			String methodDecl = interfaceBody.trim();
			if (methodDecl.endsWith(";")) {
				// Remove semicolon and add empty body
				String methodWithBody = methodDecl.substring(0, methodDecl.length() - 1) + "{}";
				String compiledMethod = compileMethod(methodWithBody, className);
				// Remove space before opening brace for interface methods to match expected format
				return compiledMethod.replace(") {", "){");
			}
		}
		return null;
	}

	private static String generateDispatcherMethod(String interfaceBody, String interfaceName, String[] declarations) {
		// Parse method signature from interface body
		if (interfaceBody.contains("(") && interfaceBody.contains(")")) {
			String methodDecl = interfaceBody.trim();
			if (methodDecl.endsWith(";")) {
				// Parse method signature
				int paramStart = methodDecl.indexOf("(");
				int paramEnd = methodDecl.indexOf(")");
				
				String signature = methodDecl.substring(0, paramStart).trim();
				String[] parts = signature.split("\\s+");
				String returnType = parts[0];
				String methodName = parts[1];
				String parameters = methodDecl.substring(paramStart + 1, paramEnd);
				
				StringBuilder result = new StringBuilder();
				result.append(convertJavaTypesToC(returnType + " "))
							.append(methodName)
							.append("_")
							.append(interfaceName)
							.append("(void* _ref_");
				
				// Add parameters if any
				if (!parameters.trim().isEmpty()) {
					result.append(", ").append(convertJavaTypesToC(parameters));
				}
				
				result.append("){struct ").append(interfaceName).append(" this = *(struct ").append(interfaceName).append("*) _ref_; ");
				
				// Generate if-else chain for dispatching
				for (int i = 1; i < declarations.length; i++) {
					String classDecl = declarations[i].trim();
					String classKeywordStart = classDecl.startsWith("public ") ? "public class " : "class ";
					int classStart = classDecl.indexOf(classKeywordStart) + classKeywordStart.length();
					String className = classDecl.substring(classStart, classDecl.indexOf(" implements"));
					
					if (i == 1) {
						result.append("if(this._type_ == ").append(interfaceName).append("Type.").append(className).append("Type) ");
					} else {
						result.append(" else if(this._type_ == ").append(interfaceName).append("Type.").append(className).append("Type) ");
					}
					result.append(methodName).append("_").append(className).append("(&(this._value_))");
					if (i == declarations.length - 1) {
						result.append(";");
					}
				}
				
				result.append("}");
				return result.toString();
			}
		}
		return null;
	}

	private static String tryCompileSealedInterface(String input) {
		if (!input.startsWith("sealed interface ")) return null;
		
		// Handle sealed interface with implementing classes (contains "; " followed by class declarations)
		if (input.contains("; ")) {
			String[] declarations = splitDeclarations(input);
			if (declarations.length >= 2 && isValidSealedInterfaceWithImplementations(declarations)) {
				return compileSealedInterface(declarations);
			}
		}
		
		// Handle single sealed interface (with or without methods)
		if (input.endsWith("}")) {
			return compileSealedInterface(new String[]{input});
		}
		
		return null;
	}
	
	private static String compileSealedInterface(String[] declarations) {
		String interfaceDecl = declarations[0].trim();
		String interfaceName = interfaceDecl.substring(17, interfaceDecl.indexOf(" {"));
		String interfaceBody = interfaceDecl.substring(interfaceDecl.indexOf("{") + 1, interfaceDecl.lastIndexOf("}")).trim();
		
		StringBuilder result = new StringBuilder();
		
		// Generate implementations and union types
		String[] enumAndUnion = generateImplementationStructs(declarations, result);
		
		// Generate basic sealed interface structure
		generateSealedInterfaceStructure(interfaceName, enumAndUnion[0], enumAndUnion[1], result);
		
		// Handle methods if interface has them
		if (!interfaceBody.isEmpty()) {
			generateMethods(declarations, interfaceBody, interfaceName, result);
		}
		
		return result.toString();
	}
	
	private static String[] generateImplementationStructs(String[] declarations, StringBuilder result) {
		StringBuilder enumVariants = new StringBuilder();
		StringBuilder unionFields = new StringBuilder();
		
		if (declarations.length > 1) {
			for (int i = 1; i < declarations.length; i++) {
				String classDecl = declarations[i].trim();
				String className = extractClassName(classDecl);

				result.append("struct ").append(className).append(" {}; ");

				if (!enumVariants.isEmpty()) enumVariants.append(", ");
				enumVariants.append(className).append("Type");

				if (!unionFields.isEmpty()) unionFields.append("; ");
				unionFields.append(className).append(" ").append(className.toLowerCase());
			}
		}
		
		return new String[]{enumVariants.toString(), unionFields.toString()};
	}
	
	private static String extractClassName(String classDecl) {
		String classKeywordStart = classDecl.startsWith("public ") ? "public class " : "class ";
		int classStart = classDecl.indexOf(classKeywordStart) + classKeywordStart.length();
		return classDecl.substring(classStart, classDecl.indexOf(" implements"));
	}
	
	private static void generateSealedInterfaceStructure(String interfaceName, String enumVariants, String unionFields, StringBuilder result) {
		result.append("enum ").append(interfaceName).append("Type {").append(enumVariants).append("}; ");
		result.append("union ").append(interfaceName).append("Value {").append(unionFields);
		if (!unionFields.isEmpty()) {
			result.append(";");
		}
		result.append("}; ");
		result.append("struct ")
					.append(interfaceName)
					.append(" {")
					.append(interfaceName)
					.append("Type _type_; ")
					.append(interfaceName)
					.append("Value _value_;};");
	}
	
	private static void generateMethods(String[] declarations, String interfaceBody, String interfaceName, StringBuilder result) {
		if (declarations.length > 1) {
			// Generate implementation methods for each class
			for (int i = 1; i < declarations.length; i++) {
				String classDecl = declarations[i].trim();
				String className = extractClassName(classDecl);
				String classBody = classDecl.substring(classDecl.indexOf("{") + 1, classDecl.lastIndexOf("}")).trim();
				
				String methodResult = generateImplementationMethod(interfaceBody, className, classBody);
				if (methodResult != null) {
					result.append(" ").append(methodResult);
				}
			}
			
			// Generate dispatcher method for the interface
			String dispatcherMethod = generateDispatcherMethod(interfaceBody, interfaceName, declarations);
			if (dispatcherMethod != null) {
				result.append(" ").append(dispatcherMethod);
			}
		} else {
			String methodResult = tryCompileInterfaceMethod(interfaceBody, interfaceName);
			if (methodResult != null) {
				result.append(" ").append(methodResult);
			}
		}
	}
	
	private static String[] splitDeclarations(String input) {
		// Split on "; " but only when it's at the top level (not inside braces)
		StringBuilder current = new StringBuilder();
		java.util.List<String> declarations = new java.util.ArrayList<>();
		int braceLevel = 0;
		
		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			current.append(c);
			
			if (c == '{') {
				braceLevel++;
			} else if (c == '}') {
				braceLevel--;
				// When we close a top-level brace, check if next characters are "; "
				if (braceLevel == 0 && i + 2 < input.length() && 
					input.charAt(i + 1) == ';' && input.charAt(i + 2) == ' ') {
					// End current declaration
					declarations.add(current.toString());
					current = new StringBuilder();
					i += 2; // Skip the "; " 
				}
			}
		}
		
		// Add the last declaration if there's any remaining content
		if (!current.toString().trim().isEmpty()) {
			declarations.add(current.toString());
		}
		
		return declarations.toArray(new String[0]);
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
					 (classBody.contains("void ") || classBody.contains("String ") || classBody.contains("boolean "));
	}

	private static boolean isMethod(String classBody) {
		return (classBody.startsWith("void ") || classBody.startsWith("String ") || classBody.startsWith("boolean ")) && classBody.contains("(") &&
					 classBody.contains(")") && classBody.contains("{");
	}

	private static String convertJavaTypesToC(String parameters) {
		return parameters.replace("String ", "const char* ")
						 .replace("boolean ", "bool ");
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
