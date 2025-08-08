package magma.node;

import java.util.List;
import java.util.ArrayList;

/**
 * Record to hold a parsed struct identifier and its position in the code.
 * Also includes type parameters for generic structs.
 */
public record StructIdentifier(String name, int position, List<String> typeParameters) {
    /**
     * Constructor that initializes with empty type parameters for non-generic structs.
     * 
     * @param name The name of the struct
     * @param position The position in the code
     */
    public StructIdentifier(String name, int position) {
        this(name, position, new ArrayList<>());
    }
    
    /**
     * Generates a name for a concrete struct instance with specific type arguments.
     * For example, "MyWrapper<I32>" becomes "MyWrapper_I32".
     * 
     * @param typeArguments The concrete type arguments
     * @return The concrete struct name
     */
    public String getConcreteStructName(List<String> typeArguments) {
        if (typeParameters.isEmpty() || typeArguments.isEmpty()) {
            return name;
        }
        
        StringBuilder result = new StringBuilder(name);
        for (String typeArg : typeArguments) {
            result.append("_").append(typeArg);
        }
        return result.toString();
    }
    
    /**
     * Checks if this struct has type parameters (is generic).
     * 
     * @return true if the struct has type parameters, false otherwise
     */
    public boolean isGeneric() {
        return !typeParameters.isEmpty();
    }
}