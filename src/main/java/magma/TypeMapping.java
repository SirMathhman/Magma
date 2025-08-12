package magma;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;

/**
 * Centralized type mapping between Magma types and C types.
 * This ensures consistency between the compiler and tests.
 */
public class TypeMapping {
  private static final Map<String, String> TYPE_MAP = new HashMap<>();

  static {
    // Integer types
    TYPE_MAP.put("I8", "int8_t");
    TYPE_MAP.put("I16", "int16_t");
    TYPE_MAP.put("I32", "int32_t");
    TYPE_MAP.put("I64", "int64_t");

    // Unsigned integer types
    TYPE_MAP.put("U8", "uint8_t");
    TYPE_MAP.put("U16", "uint16_t");
    TYPE_MAP.put("U32", "uint32_t");
    TYPE_MAP.put("U64", "uint64_t");

    // Other types
    TYPE_MAP.put("USize", "usize_t");
    TYPE_MAP.put("Bool", "bool");
    TYPE_MAP.put("Void", "void");
  }

  /**
   * Maps a Magma type to its corresponding C type.
   * 
   * @param magmaType The Magma type to map
   * @return The corresponding C type, or "struct " + magmaType for user-defined
   *         types
   */
  public static String mapType(String magmaType) {
    String mapped = TYPE_MAP.get(magmaType);
    if (mapped != null) {
      return mapped;
    }
    // For user-defined types (structs), prefix with "struct"
    return "struct " + magmaType;
  }

  /**
   * Gets all known integer types (both signed and unsigned).
   * 
   * @return Set of all integer type names
   */
  public static Set<String> getIntegerTypes() {
    return Set.of("I8", "I16", "I32", "I64", "U8", "U16", "U32", "U64");
  }

  /**
   * Gets all type mappings for testing purposes.
   * 
   * @return Map of Magma types to C types
   */
  public static Map<String, String> getAllMappings() {
    return new HashMap<>(TYPE_MAP);
  }

  /**
   * Checks if a given type is a known Magma type.
   * 
   * @param type The type to check
   * @return true if the type is known, false otherwise
   */
  public static boolean isKnownType(String type) {
    return TYPE_MAP.containsKey(type);
  }
}
