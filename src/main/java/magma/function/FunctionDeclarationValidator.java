package magma.function;

import magma.core.CompileException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validator for function declarations in Magma.
 * Validates the syntax of function declarations and processes them into their target language representation.
 */
public class FunctionDeclarationValidator {
    // Pattern to match function declarations: fn name() : ReturnType => { body }
    private static final Pattern FUNCTION_PATTERN = 
        Pattern.compile("fn\\s+([a-zA-Z][a-zA-Z0-9_]*)\\s*\\(([^)]*)\\)\\s*:\\s*([a-zA-Z][a-zA-Z0-9_]*)\\s*=>\\s*\\{([^}]*)\\}");

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

        Matcher matcher = FUNCTION_PATTERN.matcher(statement.trim());
        if (!matcher.matches()) {
            throw new CompileException("Invalid function declaration syntax: " + statement);
        }

        String functionName = matcher.group(1);
        String parameters = matcher.group(2).trim();
        String returnType = matcher.group(3);
        String body = matcher.group(4).trim();

        // Validate function name (should be a valid identifier)
        if (!functionName.matches("[a-zA-Z][a-zA-Z0-9_]*")) {
            throw new CompileException("Invalid function name: " + functionName);
        }

        // Validate return type
        if (returnType.isEmpty()) {
            throw new CompileException("Function must have a return type");
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
        result.append("function ")
              .append(params.functionName())
              .append("(")
              .append(params.parameters())
              .append(") : ")
              .append(targetReturnType)
              .append(" {");
              
        if (!params.body().isEmpty()) {
            result.append(" ").append(params.body()).append(" ");
        }
              
        result.append("}");
        
        return result.toString();
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
        return switch(type) {
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
        return switch(type) {
            case "F32" -> "float";
            case "F64" -> "double";
            default -> type.toLowerCase();
        };
    }
}