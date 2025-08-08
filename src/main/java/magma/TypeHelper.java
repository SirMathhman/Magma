package magma;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class that provides type mapping functionality.
 */
public class TypeHelper {
    // Keep track of defined struct types
    private static final Map<String, String> structTypes = new HashMap<>();
    
    /**
     * Maps Magma types to C types.
     * @param type The Magma type string
     * @return The corresponding C type string, or null if not found
     */
    public static String mapType(String type) {
        // Check built-in types first
        String builtInType = switch (type) {
            case "I8" -> "int8_t";
            case "I16" -> "int16_t";
            case "I32" -> "int32_t";
            case "I64" -> "int64_t";
            case "U8" -> "uint8_t";
            case "U16" -> "uint16_t";
            case "U32" -> "uint32_t";
            case "U64" -> "uint64_t";
            case "Bool" -> "bool";
            case "Void" -> "void";
            default -> null;
        };
        
        if (builtInType != null) {
            return builtInType;
        }
        
        // Check if it's a struct type
        return structTypes.get(type);
    }
    
    /**
     * Registers a struct type
     * @param name The struct name
     */
    public static void registerStructType(String name) {
        structTypes.put(name, name);
    }
    
    /**
     * Checks if a string is a valid identifier.
     * @param s The string to check
     * @return true if the string is a valid identifier, false otherwise
     */
    public static boolean isIdentifier(String s) {
        if (s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (i == 0) {
                if (!(Character.isLetter(c) || c == '_')) return false;
            } else {
                if (!(Character.isLetterOrDigit(c) || c == '_')) return false;
            }
        }
        return true;
    }
}