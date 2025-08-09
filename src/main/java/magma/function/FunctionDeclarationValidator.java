package magma.function;

import magma.core.CompileException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validator for function declarations in Magma.
 * Validates the syntax of function declarations and processes them into their target language representation.
 */
public class FunctionDeclarationValidator {
	// Pattern to match function declarations with two formats:
	// 1. fn name() : ReturnType => { body }
	// 2. fn name() => { body } (implicit Void return type)
	private static final Pattern FUNCTION_PATTERN_WITH_RETURN_TYPE = Pattern.compile(
			"fn\\s+([a-zA-Z][a-zA-Z0-9_]*)\\s*\\(([^)]*)\\)" + "\\s*:\\s*([a-zA-Z][a-zA-Z0-9_]*)\\s*=>\\s*\\{(.*)\\}");
	private static final Pattern FUNCTION_PATTERN_WITHOUT_RETURN_TYPE =
			Pattern.compile("fn\\s+([a-zA-Z][a-zA-Z0-9_]*)\\s*\\(([^)]*)\\)\\s*=>\\s*\\{(.*)\\}");

	private final String statement;

	/**
	 * Constructs a new FunctionDeclarationValidator for the given statement.
	 *
	 * @param statement The function declaration statement to validate
	 */
	public FunctionDeclarationValidator(String statement) {
		this.statement = statement;
	}

	/**
	 * Checks if the statement is a function declaration.
	 *
	 * @return true if the statement is a function declaration, false otherwise
	 */
	public boolean isFunctionDeclaration() {
		return statement.trim().startsWith("fn ");
	}

	/**
	 * Parses and validates the function declaration.
	 *
	 * @return A FunctionDeclarationParams object containing the parsed components
	 * @throws CompileException if the function declaration is invalid
	 */
	public FunctionDeclarationParams parse() {
		if (!isFunctionDeclaration()) {
			throw new CompileException("Not a function declaration: " + statement);
		}

		String functionName;
		String parameters;
		String returnType = "Void"; // Default return type is Void
		String body;

		// Try to match function with explicit return type first
		Matcher matcherWithType = FUNCTION_PATTERN_WITH_RETURN_TYPE.matcher(statement.trim());
		if (matcherWithType.matches()) {
			functionName = matcherWithType.group(1);
			parameters = matcherWithType.group(2).trim();
			returnType = matcherWithType.group(3);
			body = matcherWithType.group(4).trim();
		} else {

			// Try to match function without return type
			Matcher matcherWithoutType = FUNCTION_PATTERN_WITHOUT_RETURN_TYPE.matcher(statement.trim());
			if (matcherWithoutType.matches()) {
				functionName = matcherWithoutType.group(1);
				parameters = matcherWithoutType.group(2).trim();
				body = matcherWithoutType.group(3).trim();
			} else {
				throw new CompileException("Invalid function declaration syntax: " + statement);
			}
		}

		// Validate function name (should be a valid identifier)
		if (!functionName.matches("[a-zA-Z][a-zA-Z0-9_]*")) {
			throw new CompileException("Invalid function name: " + functionName);
		}

		return new FunctionDeclarationParams(statement, functionName, parameters, returnType, body);
	}

	/**
	 * Processes a function declaration into its target language representation.
	 *
	 * @return The processed function declaration
	 * @throws CompileException if the function declaration is invalid
	 */
	public String process() {
		FunctionDeclarationParams params = parse();

		if (!params.isValid()) {
			throw new CompileException("Invalid function declaration: " + statement);
		}

		// Convert Magma return type to target language type
		String targetReturnType = convertType(params.returnType());

		// Build the output function declaration
		StringBuilder result = new StringBuilder();
		result.append(targetReturnType)
					.append(" ")
					.append(params.functionName())
					.append("(")
					.append(params.parameters())
					.append("){");

		// Preserve the function body exactly as it appears in the source, including return statements
		if (!params.body().isEmpty()) {
			result.append(params.body());
		}

		result.append("}");

		String output = result.toString();
		return output;
	}

	/**
	 * Converts a Magma type to its target language equivalent.
	 *
	 * @param magmaType The Magma type to convert
	 * @return The equivalent target language type
	 */
	private String convertType(String magmaType) {
		// Convert based on type category
		if (magmaType.equals("Void")) {
			return "void";
		} else if (magmaType.equals("Bool")) {
			return "bool";
		} else if (isIntegerType(magmaType)) {
			return convertIntegerType(magmaType);
		} else if (isFloatType(magmaType)) {
			return convertFloatType(magmaType);
		} else {
			return magmaType.toLowerCase();
		}
	}

	/**
	 * Checks if the type is an integer type.
	 *
	 * @param type The type to check
	 * @return true if it's an integer type, false otherwise
	 */
	private boolean isIntegerType(String type) {
		return type.startsWith("I") || type.startsWith("U");
	}

	/**
	 * Checks if the type is a floating-point type.
	 *
	 * @param type The type to check
	 * @return true if it's a floating-point type, false otherwise
	 */
	private boolean isFloatType(String type) {
		return type.startsWith("F");
	}

	/**
	 * Converts integer types to their C equivalents.
	 *
	 * @param type The Magma integer type
	 * @return The C integer type
	 */
	private String convertIntegerType(String type) {
		return switch (type) {
			case "I8" -> "int8_t";
			case "I16" -> "int16_t";
			case "I32" -> "int32_t";
			case "I64" -> "int64_t";
			case "U8" -> "uint8_t";
			case "U16" -> "uint16_t";
			case "U32" -> "uint32_t";
			case "U64" -> "uint64_t";
			default -> type.toLowerCase();
		};
	}

	/**
	 * Converts float types to their C equivalents.
	 *
	 * @param type The Magma float type
	 * @return The C float type
	 */
	private String convertFloatType(String type) {
		return switch (type) {
			case "F32" -> "float";
			case "F64" -> "double";
			default -> type.toLowerCase();
		};
	}
}