package magma.validation;

import magma.core.CompileException;
import magma.params.StructDeclarationParams;

/**
 * Validator for struct declarations.
 * 
 * This class validates the syntax of struct declarations and ensures they
 * follow the expected format: "struct Name { ... }" or "struct Name { field : Type }".
 * 
 * The validator performs the following checks:
 * - Validates overall struct syntax (struct keyword, name, braces)
 * - Validates individual field declarations:
 *   - Ensures field names and types are properly specified with a colon separator
 *   - Validates that multiple fields are properly separated by commas or semicolons
 *   - Checks for the validity of field types against the supported Magma types
 *   - Ensures field names are not empty and properly formatted
 * 
 * For structs with multiple fields, the validator ensures proper separation and formatting
 * of each field to prevent syntax errors and improve code clarity.
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

        // If this is an empty struct, return the simple C struct
        if (!params.hasFields()) {
            return String.format("struct %s {};", params.structName());
        }

        // Process struct with fields
        return processStructWithFields();
    }
    
    /**
     * Processes a struct declaration with fields.
     * 
     * @return the C equivalent of the struct declaration with fields
     * @throws CompileException if the field declaration is invalid
     */
    private String processStructWithFields() {
        String body = params.body();
        
        // Check if there are multiple fields (separated by commas or semicolons)
        if (body.contains(",") || body.contains(";")) {
            return processMultipleFields(body);
        }
        
        // Process a single field
        String[] parts = body.split(":");
        if (parts.length != 2) {
            throw new CompileException("Invalid field declaration in struct");
        }
        
        String fieldName = parts[0].trim();
        String fieldType = parts[1].trim();
        
        // Map Magma type to C type
        String cType = mapTypeToCType(fieldType);
        
        // Construct and return the C struct declaration with a single field
        return String.format("struct %s { %s %s; };", params.structName(), cType, fieldName);
    }
    
    /**
     * Processes a struct declaration with multiple fields.
     * 
     * @param body the body of the struct containing multiple field declarations
     * @return the C equivalent of the struct declaration with multiple fields
     * @throws CompileException if any field declaration is invalid
     */
    private String processMultipleFields(String body) {
        // Validate field separators
        validateFieldSeparators(body);
        
        // Split fields by commas or semicolons
        String[] fields = body.split("[,;]");
        StringBuilder fieldsBuilder = new StringBuilder();
        
        for (String field : fields) {
            field = field.trim();
            if (field.isEmpty()) {
                continue;
            }
            
            // Validate individual field
            validateField(field);
            
            String[] parts = field.split(":");
            // This check is now done in validateField()
            
            String fieldName = parts[0].trim();
            String fieldType = parts[1].trim();
            
            // Validate field name
            if (fieldName.isEmpty()) {
                throw new CompileException("Field name cannot be empty: " + field);
            }
            
            // Map Magma type to C type
            String cType = mapTypeToCType(fieldType);
            
            // Add the field to the builder
            fieldsBuilder.append(cType).append(" ").append(fieldName).append("; ");
        }
        
        // Construct and return the C struct declaration with multiple fields
        return String.format("struct %s { %s};", params.structName(), fieldsBuilder.toString());
    }
    
    /**
     * Validates that a field declaration has proper syntax.
     * 
     * @param field the field declaration to validate
     * @throws CompileException if the field declaration is invalid
     */
    private void validateField(String field) {
        // Check for presence of colon
        if (!field.contains(":")) {
            throw new CompileException("Missing colon in field declaration: " + field);
        }
        
        // Split by colon and validate parts
        String[] parts = field.split(":");
        if (parts.length != 2) {
            throw new CompileException("Invalid field declaration: " + field);
        }
        
        String fieldName = parts[0].trim();
        String fieldType = parts[1].trim();
        
        // Validate field name
        if (fieldName.isEmpty()) {
            throw new CompileException("Field name cannot be empty: " + field);
        }
        
        // Check for spaces in field name (possible missing separator)
        if (fieldName.contains(" ")) {
            throw new CompileException("Invalid field name (contains spaces): " + fieldName);
        }
        
        // Validate field type
        if (fieldType.isEmpty()) {
            throw new CompileException("Field type cannot be empty: " + field);
        }
        
        // Check for spaces in field type (possible missing separator)
        if (fieldType.contains(" ") && !fieldType.startsWith("struct ")) {
            throw new CompileException("Invalid field type (contains spaces): " + fieldType);
        }
    }
    
    /**
     * Validates that field separators (commas or semicolons) are used correctly.
     * 
     * @param body the body of the struct to validate
     * @throws CompileException if field separators are missing or used incorrectly
     */
    private void validateFieldSeparators(String body) {
        // Check for fields without separators
        if (body.contains(":")) {
            // Count the number of field declarations by counting colons
            int colonCount = 0;
            for (int i = 0; i < body.length(); i++) {
                if (body.charAt(i) == ':') {
                    colonCount++;
                }
            }
            
            // Count separators (commas and semicolons)
            int separatorCount = 0;
            for (int i = 0; i < body.length(); i++) {
                if (body.charAt(i) == ',' || body.charAt(i) == ';') {
                    separatorCount++;
                }
            }
            
            // If there are multiple fields but not enough separators
            if (colonCount > 1 && separatorCount < colonCount - 1) {
                throw new CompileException("Missing separator between fields: " + body);
            }
        }
    }
    
    /**
     * Maps a Magma type to its corresponding C type.
     * 
     * @param magmaType the Magma type name
     * @return the corresponding C type name
     * @throws CompileException if the type is unknown
     */
    private String mapTypeToCType(String magmaType) {
        return switch (magmaType) {
            case "I8" -> "int8_t";
            case "I16" -> "int16_t";
            case "I32" -> "int32_t";
            case "I64" -> "int64_t";
            case "U8" -> "uint8_t";
            case "U16" -> "uint16_t";
            case "U32" -> "uint32_t";
            case "U64" -> "uint64_t";
            case "F32" -> "float";
            case "F64" -> "double";
            case "Bool" -> "bool";
            default -> throw new CompileException("Unknown type: " + magmaType);
        };
    }
}