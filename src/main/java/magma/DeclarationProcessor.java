package magma;

import java.util.Map;

/**
 * Handles processing of different types of variable declarations.
 * <p>
 * This class provides functionality for processing TypeScript and JavaScript
 * declarations into C-style declarations. It works with the TypeMapper and
 * ValueProcessor classes.
 */
public class DeclarationProcessor {
	private final TypeMapper typeMapper;
	private final ValueProcessor valueProcessor;
	private final Map<String, String> variableTypes;
	private final Map<String, Boolean> variableMutability;

	/**
	 * Creates a new DeclarationProcessor with the required dependencies.
	 *
	 * @param config the configuration containing all required dependencies
	 */
	public DeclarationProcessor(DeclarationConfig config) {
		this.typeMapper = config.typeMapper();
		this.valueProcessor = config.valueProcessor();
		this.variableTypes = config.variableTypes();
		this.variableMutability = config.variableMutability();
	}

	/**
	 * Creates a declaration context from an input string.
	 *
	 * @param input the input string containing the declaration
	 * @return a DeclarationContext object with extracted information
	 */
	public DeclarationContext createContext(String input) {
		// Check if the declaration contains the 'mut' keyword
		boolean isMutable = input.contains("let mut ");

		// Extract the variable name, considering the 'mut' keyword if present
		String variableNamePart;
		if (isMutable) {
			variableNamePart = input.substring(8, input.indexOf("=")).trim();
		} else {
			variableNamePart = input.substring(4, input.indexOf("=")).trim();
		}

		// If the variable name contains a type annotation, extract just the name part
		String variableName = variableNamePart;
		if (variableNamePart.contains(" : ")) {
			variableName = variableNamePart.substring(0, variableNamePart.indexOf(" : ")).trim();
		}

		String valueSection = input.substring(input.indexOf("="));
		String typeSuffix = typeMapper.detectTypeSuffix(valueSection);

		// Store the mutability information
		variableMutability.put(variableName, isMutable);

		// Create and return the context with mutability information
		return new DeclarationContext(input, variableName, valueSection, typeSuffix, isMutable);
	}

	/**
	 * Processes a TypeScript declaration with type annotation.
	 *
	 * @param context the declaration context
	 * @return the processed declaration as a C-style string
	 * @throws CompileException if there is a type incompatibility
	 */
	public String processTypeScriptDeclaration(DeclarationContext context) {
		String input = context.input();
		String typeSuffix = context.typeSuffix();
		String valueSection = context.valueSection();
		boolean isMutable = context.mutable();

		// Redefine variableName to extract only up to the type annotation, considering mutability
		String updatedVariableName;
		if (isMutable) {
			updatedVariableName = input.substring(8, input.indexOf(" : ")).trim();
		} else {
			updatedVariableName = input.substring(4, input.indexOf(" : ")).trim();
		}

		// Extract the type annotation
		String typeAnnotation = input.substring(input.indexOf(" : ") + 3, input.indexOf("=")).trim();

		// Check for type compatibility if a type suffix is present in the value
		if (typeSuffix != null && !typeAnnotation.equals(typeSuffix)) {
			throw new CompileException(
					"Type mismatch: Cannot assign " + typeSuffix + " value to " + typeAnnotation + " variable");
		}

		String type = typeMapper.mapTypeToC(typeAnnotation);

		// Remove type suffix from the value section if present
		String cleanValueSection = valueProcessor.cleanValueSection(valueSection, typeSuffix);

		VariableDeclaration declaration = new VariableDeclaration(updatedVariableName, cleanValueSection);
		return createTypeDeclaration(declaration, type);
	}

	/**
	 * Creates a type declaration using the provided variable declaration and C type.
	 *
	 * @param declaration the variable declaration containing name and value section
	 * @param type        the C type to use for the declaration
	 * @return a C-style type declaration
	 */
	public String createTypeDeclaration(VariableDeclaration declaration, String type) {
		String valueSection = declaration.valueSection();
		// Make sure the value section ends with a semicolon
		if (!valueSection.trim().endsWith(";")) {
			valueSection = valueSection.trim() + ";";
		}
		return type + " " + declaration.name() + " " + valueSection;
	}

	/**
	 * Processes a variable declaration with a type suffix.
	 *
	 * @param context the declaration context
	 * @return the processed declaration as a C-style string
	 */
	public String processTypeSuffixDeclaration(DeclarationContext context) {
		String variableName = context.variableName();
		String valueSection = context.valueSection();
		String typeSuffix = context.typeSuffix();
		boolean isMutable = context.mutable();

		// Store mutability information
		variableMutability.put(variableName, isMutable);

		// Remove type suffix from the value section
		String cleanValueSection = valueProcessor.cleanValueSection(valueSection, typeSuffix);
		String type = typeMapper.mapTypeToC(typeSuffix);
		VariableDeclaration declaration = new VariableDeclaration(variableName, cleanValueSection);
		return createTypeDeclaration(declaration, type);
	}

	/**
	 * Processes a TypeScript declaration with type annotation.
	 *
	 * @param params the parameters for processing
	 * @return the processed declaration
	 */
	public String processTypeScriptAnnotation(TypeScriptAnnotationParams params) {
		// Extract the type annotation
		String typeAnnotation =
				params.statement().substring(params.statement().indexOf(" : ") + 3, params.statement().indexOf("=")).trim();

		// Check if the declaration contains the 'mut' keyword
		boolean isMutable = params.statement().contains("let mut ");

		// Extract just the variable name for error messages (before the type annotation)
		String cleanVariableName;
		if (isMutable) {
			cleanVariableName = params.statement().substring(8, params.statement().indexOf(" : ")).trim();
		} else {
			cleanVariableName = params.statement().substring(4, params.statement().indexOf(" : ")).trim();
		}

		// Check if the value is a variable reference
		if (valueProcessor.isVariableReference(params.rawValue())) {
			// For error reporting, use just the variable name without type annotation
			checkVariableTypeCompatibility(new TypeCheckParams(params.rawValue(), typeAnnotation, cleanVariableName));
		}

		// Store the variable type and mutability
		variableTypes.put(params.variableName(), typeAnnotation);
		variableMutability.put(params.variableName(), isMutable);

		return processTypeScriptDeclaration(params.context());
	}

	/**
	 * Checks if a variable reference is compatible with the target type.
	 *
	 * @param params the type check parameters containing variable name, target type, and target name
	 * @throws CompileException if the variable reference is incompatible with the target type
	 */
	public void checkVariableTypeCompatibility(TypeCheckParams params) {
		if (variableTypes.containsKey(params.variableName())) {
			String variableType = variableTypes.get(params.variableName());
			if (!variableType.equals(params.targetType())) {
				throw new CompileException(
						"Type mismatch: Cannot assign " + variableType + " variable '" + params.variableName() + "' to " +
						params.targetType() + " variable '" + params.targetName() + "'");
			}
		}
	}

	/**
	 * Processes a standard JavaScript declaration.
	 *
	 * @param statement    the statement to process
	 * @param variableName the variable name
	 * @return the processed declaration
	 */
	public String processStandardDeclaration(String statement, String variableName) {
		// Assume I32 type for standard declarations
		variableTypes.put(variableName, "I32");

		// Check if the declaration contains the 'mut' keyword
		boolean isMutable = statement.contains("let mut ");

		// Store mutability information
		variableMutability.put(variableName, isMutable);

		// Make sure the statement ends with a semicolon
		String result;
		if (isMutable) {
			// Remove the 'mut' keyword when converting to C
			result = statement.replaceFirst("let mut", "int32_t");
		} else {
			result = statement.replaceFirst("let", "int32_t");
		}

		if (!result.trim().endsWith(";")) {
			result = result.trim() + ";";
		}
		return result;
	}
}