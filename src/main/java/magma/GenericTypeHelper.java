package magma;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class for handling generic type parameters and arguments.
 */
public class GenericTypeHelper {
    // Map of generic struct names to their type parameters
    private static final Map<String, List<String>> genericStructs = new HashMap<>();

    /**
     * Registers a generic struct with its type parameters.
     *
     * @param structName     The name of the struct
     * @param typeParameters The list of type parameters
     */
    public static void registerGenericStruct(String structName, List<String> typeParameters) {
        genericStructs.put(structName, new ArrayList<>(typeParameters));
    }

    /**
     * Checks if a struct is registered as generic.
     *
     * @param structName The name of the struct
     * @return true if the struct is generic, false otherwise
     */
    public static boolean isGenericStruct(String structName) {
        return genericStructs.containsKey(structName);
    }

    /**
     * Gets the type parameters for a generic struct.
     *
     * @param structName The name of the struct
     * @return The list of type parameters, or null if not a generic struct
     */
    public static List<String> getTypeParameters(String structName) {
        return genericStructs.get(structName);
    }

    /**
     * Parses type arguments from a generic type reference.
     * For example, "MyWrapper<I32>" would return ["I32"].
     *
     * @param typeReference The generic type reference
     * @return The list of type arguments
     * @throws CompileException If the type arguments are invalid
     */
    public static List<String> parseTypeArguments(String typeReference) throws CompileException {
        List<String> typeArguments = new ArrayList<>();
        
        // Extract struct name and type arguments
        int angleBracketPos = typeReference.indexOf('<');
        if (angleBracketPos <= 0) {
            throw new CompileException("Missing type arguments for generic struct", typeReference);
        }
        
        String structName = typeReference.substring(0, angleBracketPos).trim();
        
        // Check if this is a registered generic struct
        if (!isGenericStruct(structName)) {
            throw new CompileException("Unknown generic struct: " + structName, typeReference);
        }
        
        // Find the closing angle bracket
        int closingBracketPos = findMatchingAngleBracket(typeReference, angleBracketPos);
        if (closingBracketPos < 0) {
            throw new CompileException("Missing closing angle bracket in generic type", typeReference);
        }
        
        // Extract the type arguments string
        String typeArgsStr = typeReference.substring(angleBracketPos + 1, closingBracketPos).trim();
        
        // Split by commas, respecting nested angle brackets
        if (!typeArgsStr.isEmpty()) {
            String[] args = typeArgsStr.split(",");
            for (String arg : args) {
                typeArguments.add(arg.trim());
            }
        }
        
        // Validate the number of type arguments
        List<String> expectedParams = getTypeParameters(structName);
        if (typeArguments.size() != expectedParams.size()) {
            throw new CompileException(
                    "Wrong number of type arguments. Expected " + expectedParams.size() + 
                    ", got " + typeArguments.size(), typeReference);
        }
        
        return typeArguments;
    }
    
    /**
     * Finds the matching closing angle bracket for a given opening angle bracket.
     *
     * @param code The code string
     * @param openPos The position of the opening angle bracket
     * @return The position of the matching closing angle bracket, or -1 if not found
     */
    public static int findMatchingAngleBracket(String code, int openPos) {
        if (openPos >= code.length() || code.charAt(openPos) != '<') {
            return -1;
        }
        
        int depth = 1;
        for (int i = openPos + 1; i < code.length(); i++) {
            char c = code.charAt(i);
            if (c == '<') {
                depth++;
            } else if (c == '>') {
                depth--;
                if (depth == 0) {
                    return i;
                }
            }
        }
        
        return -1;
    }
    
    /**
     * Substitutes type parameters in a type with their concrete type arguments.
     *
     * @param type The type to process
     * @param typeParams The type parameters
     * @param typeArgs The type arguments
     * @return The type with parameters substituted
     */
    public static String substituteTypeParameters(String type, List<String> typeParams, List<String> typeArgs) {
        // If the type is a type parameter, substitute it
        for (int i = 0; i < typeParams.size(); i++) {
            if (type.equals(typeParams.get(i))) {
                return typeArgs.get(i);
            }
        }
        
        // Handle generic types
        Pattern genericPattern = Pattern.compile("([a-zA-Z_][a-zA-Z0-9_]*)\\s*<");
        Matcher matcher = genericPattern.matcher(type);
        if (matcher.find()) {
            // This is a nested generic type, needs special handling
            // This would be for future implementation
        }
        
        return type;
    }
    
    /**
     * Creates a concrete struct name with the given type arguments.
     *
     * @param structName The generic struct name
     * @param typeArgs The type arguments
     * @return The concrete struct name
     */
    public static String createConcreteStructName(String structName, List<String> typeArgs) {
        StringBuilder result = new StringBuilder(structName);
        for (String typeArg : typeArgs) {
            result.append("_").append(typeArg);
        }
        return result.toString();
    }
}