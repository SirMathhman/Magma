package magma.validation;

import magma.core.CompileException;
import magma.params.StructDeclarationParams;

/**
 * Validator for struct declarations.
 * 
 * This class validates the syntax of struct declarations and ensures they
 * follow the expected format: "struct Name { ... }".
 */
public class StructDeclarationValidator {
    private final StructDeclarationParams params;

    /**
     * Creates a new StructDeclarationValidator.
     *
     * @param params the parameters for struct declaration validation
     */
    public StructDeclarationValidator(StructDeclarationParams params) {
        this.params = params;
    }

    /**
     * Validates a struct declaration and returns the C equivalent.
     *
     * @return the C equivalent of the struct declaration
     * @throws CompileException if the struct declaration is invalid
     */
    public String validateStructDeclaration() {
        // Check if we have a valid statement
        if (params.statement() == null || params.statement().trim().isEmpty()) {
            throw new CompileException("Empty struct declaration");
        }

        // Check if statement starts with "struct "
        if (!params.statement().trim().startsWith("struct ")) {
            throw new CompileException("Struct declaration must start with 'struct'");
        }

        // Check if struct has a name
        if (params.structName() == null || params.structName().trim().isEmpty()) {
            throw new CompileException("Struct must have a name");
        }

        // Check if we have an opening brace
        if (!params.statement().contains("{")) {
            throw new CompileException("Missing opening brace in struct declaration");
        }

        // Check if we have a closing brace
        if (!params.statement().contains("}")) {
            throw new CompileException("Missing closing brace in struct declaration");
        }

        // Construct and return the C struct declaration
        return String.format("struct %s {};", params.structName());
    }
}