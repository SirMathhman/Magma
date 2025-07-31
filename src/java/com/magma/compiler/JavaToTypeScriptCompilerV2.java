package com.magma.compiler;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A compiler that translates Java source code to TypeScript.
 * This is a self-hosted compiler, meaning it's written in Java and can compile itself to TypeScript.
 * <p>
 * This is a simplified version that uses regex for parsing instead of a full parser library.
 * Version 2 adds support for interfaces, inheritance, and constructors.
 */
public class JavaToTypeScriptCompilerV2 {

	// Pattern to match a class declaration with inheritance and interface implementation
	private static final Pattern CLASS_PATTERN =
			Pattern.compile("public\\s+class\\s+(\\w+)(?:\\s+extends\\s+(\\w+))?(?:\\s+implements\\s+([\\w,\\s]+))?\\s*\\{",
											Pattern.DOTALL);

	// Pattern to match an interface declaration
	private static final Pattern INTERFACE_PATTERN =
			Pattern.compile("public\\s+interface\\s+(\\w+)\\s*\\{", Pattern.DOTALL);

	// Pattern to match field declarations
	private static final Pattern FIELD_PATTERN =
			Pattern.compile("(private|public|protected)\\s+(\\w+)\\s+(\\w+)\\s*;", Pattern.MULTILINE);

	// Pattern to match method declarations
	private static final Pattern METHOD_PATTERN =
			Pattern.compile("(private|public|protected)\\s+(\\w+)\\s+(\\w+)\\s*\\(([^)]*)\\)\\s*\\{", Pattern.DOTALL);

	// Pattern to match interface method declarations (no method body)
	private static final Pattern INTERFACE_METHOD_PATTERN =
			Pattern.compile("(\\w+)\\s+(\\w+)\\s*\\(([^)]*)\\)\\s*;", Pattern.MULTILINE);

	// Pattern to match constructor declarations
	private static final Pattern CONSTRUCTOR_PATTERN =
			Pattern.compile("public\\s+(\\w+)\\s*\\(([^)]*)\\)\\s*\\{", Pattern.DOTALL);

	// Pattern to match method parameters
	private static final Pattern PARAM_PATTERN = Pattern.compile("(\\w+)\\s+(\\w+)");

	// Pattern to match super constructor calls
	private static final Pattern SUPER_CALL_PATTERN = Pattern.compile("super\\s*\\(([^)]*)\\)\\s*;", Pattern.MULTILINE);

	// Map of Java types to TypeScript types
	private static final Map<String, String> TYPE_MAPPING = new HashMap<>();

	static {
		// Initialize type mappings
		JavaToTypeScriptCompilerV2.TYPE_MAPPING.put("String", "string");
		JavaToTypeScriptCompilerV2.TYPE_MAPPING.put("int", "number");
		JavaToTypeScriptCompilerV2.TYPE_MAPPING.put("long", "number");
		JavaToTypeScriptCompilerV2.TYPE_MAPPING.put("double", "number");
		JavaToTypeScriptCompilerV2.TYPE_MAPPING.put("float", "number");
		JavaToTypeScriptCompilerV2.TYPE_MAPPING.put("boolean", "boolean");
		JavaToTypeScriptCompilerV2.TYPE_MAPPING.put("char", "string");
		JavaToTypeScriptCompilerV2.TYPE_MAPPING.put("byte", "number");
		JavaToTypeScriptCompilerV2.TYPE_MAPPING.put("short", "number");
		JavaToTypeScriptCompilerV2.TYPE_MAPPING.put("Integer", "number");
		JavaToTypeScriptCompilerV2.TYPE_MAPPING.put("Long", "number");
		JavaToTypeScriptCompilerV2.TYPE_MAPPING.put("Double", "number");
		JavaToTypeScriptCompilerV2.TYPE_MAPPING.put("Float", "number");
		JavaToTypeScriptCompilerV2.TYPE_MAPPING.put("Boolean", "boolean");
		JavaToTypeScriptCompilerV2.TYPE_MAPPING.put("Character", "string");
		JavaToTypeScriptCompilerV2.TYPE_MAPPING.put("Byte", "number");
		JavaToTypeScriptCompilerV2.TYPE_MAPPING.put("Short", "number");
		JavaToTypeScriptCompilerV2.TYPE_MAPPING.put("Object", "any");
		JavaToTypeScriptCompilerV2.TYPE_MAPPING.put("void", "void");
	}

	/**
	 * Creates a new instance of the Java to TypeScript compiler.
	 */
	public JavaToTypeScriptCompilerV2() {
		// No initialization needed for the simplified version
	}

	/**
	 * Compiles Java source code to TypeScript.
	 *
	 * @param javaCode The Java source code to compile
	 * @return The equivalent TypeScript code
	 */
	public String compile(final String javaCode) {
		final StringBuilder typeScriptCode = new StringBuilder();

		// Process interfaces first
		this.processInterfaces(javaCode, typeScriptCode);

		// Process classes
		this.processClasses(javaCode, typeScriptCode);

		return typeScriptCode.toString();
	}

	/**
	 * Process interfaces in the Java code and add them to the TypeScript code.
	 */
	private void processInterfaces(final String javaCode, final StringBuilder typeScriptCode) {
		// Find interface declarations using regex
		final Matcher interfaceMatcher = JavaToTypeScriptCompilerV2.INTERFACE_PATTERN.matcher(javaCode);

		while (interfaceMatcher.find()) {
			final String interfaceName = interfaceMatcher.group(1);
			typeScriptCode.append("export interface ").append(interfaceName).append(" {\n");

			// Find method declarations within the interface
			final String interfaceBody = javaCode.substring(interfaceMatcher.end());
			final Matcher methodMatcher = JavaToTypeScriptCompilerV2.INTERFACE_METHOD_PATTERN.matcher(interfaceBody);

			while (methodMatcher.find()) {
				final String returnType = methodMatcher.group(1);
				final String methodName = methodMatcher.group(2);
				final String paramList = methodMatcher.group(3);

				// Map Java return type to TypeScript return type
				final String tsReturnType = JavaToTypeScriptCompilerV2.TYPE_MAPPING.getOrDefault(returnType, "any");

				// Process method parameters
				final StringBuilder tsParams = new StringBuilder();
				if (null != paramList && !paramList.trim().isEmpty()) {
					final String[] params = paramList.split(",");
					for (int i = 0; i < params.length; i++) {
						final Matcher paramMatcher = JavaToTypeScriptCompilerV2.PARAM_PATTERN.matcher(params[i].trim());
						if (paramMatcher.find()) {
							final String paramType = paramMatcher.group(1);
							final String paramName = paramMatcher.group(2);

							// Map Java parameter type to TypeScript parameter type
							final String tsParamType = JavaToTypeScriptCompilerV2.TYPE_MAPPING.getOrDefault(paramType, "any");

							if (0 < i) tsParams.append(", ");
							tsParams.append(paramName).append(": ").append(tsParamType);
						}
					}
				}

				// Add method declaration to TypeScript interface
				typeScriptCode.append("    ")
											.append(methodName)
											.append("(")
											.append(tsParams)
											.append("): ")
											.append(tsReturnType)
											.append(";\n");
			}

			typeScriptCode.append("}\n\n");
		}
	}

	/**
	 * Process classes in the Java code and add them to the TypeScript code.
	 */
	private void processClasses(final String javaCode, final StringBuilder typeScriptCode) {
		// Find class declarations using regex
		final Matcher classMatcher = JavaToTypeScriptCompilerV2.CLASS_PATTERN.matcher(javaCode);

		while (classMatcher.find()) {
			final String className = classMatcher.group(1);
			final String parentClass = classMatcher.group(2); // May be null
			final String interfaces = classMatcher.group(3);  // May be null

			// Start class declaration
			typeScriptCode.append("export class ").append(className);

			// Add inheritance if present
			if (null != parentClass) typeScriptCode.append(" extends ").append(parentClass);

			// Add interface implementation if present
			if (null != interfaces) {
				typeScriptCode.append(" implements ");
				final String[] interfaceList = interfaces.split(",");
				for (int i = 0; i < interfaceList.length; i++) {
					if (0 < i) typeScriptCode.append(", ");
					typeScriptCode.append(interfaceList[i].trim());
				}
			}

			typeScriptCode.append(" {\n");

			// Extract class body for further processing
			final int classStart = classMatcher.start();
			final int classEnd = this.findMatchingBrace(javaCode, classMatcher.end() - 1);
			final String classBody = javaCode.substring(classStart, classEnd + 1);

			// Find field declarations within the class
			final Matcher fieldMatcher = JavaToTypeScriptCompilerV2.FIELD_PATTERN.matcher(classBody);
			while (fieldMatcher.find()) {
				final String visibility = fieldMatcher.group(1);
				final String type = fieldMatcher.group(2);
				final String name = fieldMatcher.group(3);

				// Map Java type to TypeScript type
				final String tsType = JavaToTypeScriptCompilerV2.TYPE_MAPPING.getOrDefault(type, "any");

				// Add field declaration to TypeScript code
				typeScriptCode.append("    ")
											.append(visibility)
											.append(" ")
											.append(name)
											.append(": ")
											.append(tsType)
											.append(";\n");
			}

			// Find constructor declarations within the class
			final Matcher constructorMatcher = JavaToTypeScriptCompilerV2.CONSTRUCTOR_PATTERN.matcher(classBody);
			while (constructorMatcher.find() && constructorMatcher.group(1).equals(className)) {
				final String paramList = constructorMatcher.group(2);

				// Process constructor parameters
				final StringBuilder tsParams = new StringBuilder();
				if (null != paramList && !paramList.trim().isEmpty()) {
					final String[] params = paramList.split(",");
					for (int i = 0; i < params.length; i++) {
						final Matcher paramMatcher = JavaToTypeScriptCompilerV2.PARAM_PATTERN.matcher(params[i].trim());
						if (paramMatcher.find()) {
							final String paramType = paramMatcher.group(1);
							final String paramName = paramMatcher.group(2);

							// Map Java parameter type to TypeScript parameter type
							final String tsParamType = JavaToTypeScriptCompilerV2.TYPE_MAPPING.getOrDefault(paramType, "any");

							if (0 < i) tsParams.append(", ");
							tsParams.append(paramName).append(": ").append(tsParamType);
						}
					}
				}

				// Add constructor declaration to TypeScript code
				typeScriptCode.append("    ").append("constructor(").append(tsParams).append(") {\n");

				// Check for super constructor call
				final int constructorBodyStart = constructorMatcher.end();
				final int constructorBodyEnd = this.findMatchingBrace(classBody, constructorBodyStart - 1);
				final String constructorBody = classBody.substring(constructorBodyStart, constructorBodyEnd);

				final Matcher superCallMatcher = JavaToTypeScriptCompilerV2.SUPER_CALL_PATTERN.matcher(constructorBody);
				if (superCallMatcher.find()) {
					final String superArgs = superCallMatcher.group(1);
					typeScriptCode.append("        super(").append(superArgs).append(");\n");
				}

				// Add placeholder for constructor body
				typeScriptCode.append("        // Constructor implementation\n");
				typeScriptCode.append("    }\n\n");
			}

			// Find method declarations within the class
			final Matcher methodMatcher = JavaToTypeScriptCompilerV2.METHOD_PATTERN.matcher(classBody);
			while (methodMatcher.find()) {
				final String visibility = methodMatcher.group(1);
				final String returnType = methodMatcher.group(2);
				final String methodName = methodMatcher.group(3);
				final String paramList = methodMatcher.group(4);

				// Skip constructors as they're handled separately
				if (methodName.equals(className)) continue;

				// Map Java return type to TypeScript return type
				final String tsReturnType = JavaToTypeScriptCompilerV2.TYPE_MAPPING.getOrDefault(returnType, "any");

				// Process method parameters
				final StringBuilder tsParams = new StringBuilder();
				if (null != paramList && !paramList.trim().isEmpty()) {
					final String[] params = paramList.split(",");
					for (int i = 0; i < params.length; i++) {
						final Matcher paramMatcher = JavaToTypeScriptCompilerV2.PARAM_PATTERN.matcher(params[i].trim());
						if (paramMatcher.find()) {
							final String paramType = paramMatcher.group(1);
							final String paramName = paramMatcher.group(2);

							// Map Java parameter type to TypeScript parameter type
							final String tsParamType = JavaToTypeScriptCompilerV2.TYPE_MAPPING.getOrDefault(paramType, "any");

							if (0 < i) tsParams.append(", ");
							tsParams.append(paramName).append(": ").append(tsParamType);
						}
					}
				}

				// Add method declaration to TypeScript code
				typeScriptCode.append("    ")
											.append(visibility)
											.append(" ")
											.append(methodName)
											.append("(")
											.append(tsParams)
											.append("): ")
											.append(tsReturnType)
											.append(" {\n");

				// Add a placeholder for method body
				if ("void".equals(tsReturnType)) typeScriptCode.append("        // Method implementation\n");
				else
					typeScriptCode.append("        // Method implementation\n").append("        return null; // Placeholder\n");

				typeScriptCode.append("    }\n\n");
			}

			typeScriptCode.append("}\n\n");
		}
	}

	/**
	 * Find the position of the matching closing brace for an opening brace.
	 *
	 * @param text         The text to search in
	 * @param openBracePos The position of the opening brace
	 * @return The position of the matching closing brace
	 */
	private int findMatchingBrace(final String text, final int openBracePos) {
		int count = 1;
		for (int i = openBracePos + 1; i < text.length(); i++) {
			final char c = text.charAt(i);
			if ('{' == c) count++;
			else if ('}' == c) {
				count--;
				if (0 == count) return i;
			}
		}
		return text.length() - 1; // Fallback if no matching brace is found
	}
}