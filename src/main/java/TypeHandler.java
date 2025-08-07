import java.util.Arrays;
import java.util.Optional;

/**
 * Handler for type mapping and inference in Magma code.
 * This class provides functionality to handle type mapping and inference.
 */
public class TypeHandler {
    /**
     * Array of all supported type mappers.
     */
    private static final TypeMapper[] TYPE_MAPPERS = TypeMapper.values();

    /**
     * Checks if the Magma code contains any comparison operators (==, !=, <, >, <=, >=).
     *
     * @param magmaCode The Magma source code to analyze
     * @return True if the code contains any comparison operators
     */
    public static boolean containsComparisonOperators(String magmaCode) {
        return magmaCode.contains(" == ") || magmaCode.contains(" != ") || magmaCode.contains(" < ") ||
                magmaCode.contains(" > ") || magmaCode.contains(" <= ") || magmaCode.contains(" >= ");
    }

    /**
     * Checks if a value contains a comparison operator (==, !=, <, >, <=, >=).
     *
     * @param value The value to check
     * @return True if the value contains a comparison operator
     */
    public static boolean containsComparisonOperator(String value) {
        return value.contains(" == ") || value.contains(" != ") || value.contains(" < ") || value.contains(" > ") ||
                value.contains(" <= ") || value.contains(" >= ");
    }

    /**
     * Gets the default TypeMapper (I32).
     *
     * @return The default TypeMapper
     */
    public static TypeMapper getDefaultTypeMapper() {
        return Arrays.stream(TYPE_MAPPERS)
                .filter(mapper -> mapper.javaType().equals("I32"))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("I32 type mapper not found"));
    }

    /**
     * Finds the TypeMapper that matches the given line.
     * Uses a more flexible approach to handle different whitespace patterns.
     *
     * @param line The line to check
     * @return Optional containing the matching TypeMapper, or empty if none match
     */
    public static Optional<TypeMapper> findMatchingTypeMapper(String line) {
        // Find the position of the colon and equals sign
        int colonPos = line.indexOf(":");
        if (colonPos == -1) return Optional.empty(); // No colon found
        
        int equalsPos = line.indexOf("=", colonPos);
        if (equalsPos == -1) return Optional.empty(); // No equals sign found after colon
        
        // Extract the type, trimming any whitespace
        String type = line.substring(colonPos + 1, equalsPos).trim();
        
        // Check if this type matches any of the known type mappers
        return Arrays.stream(TYPE_MAPPERS)
                .filter(mapper -> type.equals(mapper.javaType()))
                .findFirst();
    }

    /**
     * Infers the type from a value with a type suffix, from boolean literals, from char literals,
     * or from expressions with comparison operators.
     * For example:
     * - "100U64" would infer the U64 type
     * - "true" or "false" would infer the Bool type
     * - "'a'" (char in single quotes) would infer the U8 type
     * - "a == b", "a != b", "a < b", "a > b", "a <= b", "a >= b" would infer the Bool type
     *
     * @param value The value to infer the type from
     * @return Optional containing the inferred TypeMapper, or empty if no type can be inferred
     */
    public static Optional<TypeMapper> inferTypeFromValue(String value) {
        // Check for boolean literals
        if ("true".equals(value) || "false".equals(value)) return findTypeMapperByJavaType("Bool");

        // Check for char literals (values in single quotes)
        if (value.length() >= 3 && value.startsWith("'") && value.endsWith("'")) return findTypeMapperByJavaType("U8");

        // Check for comparison operators (==, !=, <, >, <=, >=)
        if (containsComparisonOperator(value)) return findTypeMapperByJavaType("Bool");

        // Check for type suffixes (e.g., 100I8, 200U16)
        return Arrays.stream(TYPE_MAPPERS)
                .map(mapper -> getTypeMapper(value, mapper))
                .flatMap(Optional::stream)
                .findFirst();
    }

    /**
     * Gets a TypeMapper for a value with a type suffix.
     *
     * @param value  The value to check
     * @param mapper The TypeMapper to check against
     * @return Optional containing the TypeMapper if the value has the corresponding type suffix, or empty otherwise
     */
    public static Optional<TypeMapper> getTypeMapper(String value, TypeMapper mapper) {
        String suffix = mapper.javaType();
        if (value.endsWith(suffix) && value.length() > suffix.length()) {
            // Check if the characters before the suffix are numeric
            String numPart = value.substring(0, value.length() - suffix.length());
            if (numPart.matches("-?\\d+")) return Optional.of(mapper);
        }
        return Optional.empty();
    }

    /**
     * Finds a TypeMapper by its Java type name.
     *
     * @param javaType The Java type name to find
     * @return Optional containing the TypeMapper, or empty if not found
     */
    public static Optional<TypeMapper> findTypeMapperByJavaType(String javaType) {
        return Arrays.stream(TYPE_MAPPERS).filter(mapper -> mapper.javaType().equals(javaType)).findFirst();
    }

    /**
     * Removes the type suffix from a value.
     * For example:
     * - "100U64" would become "100"
     * - "'a'" would remain "'a'" (char literals keep their single quotes)
     * - "true" and "false" would remain unchanged (boolean literals)
     * - "a == b", "a != b", "a < b", "a > b", "a <= b", "a >= b" would remain unchanged (comparison operators)
     *
     * @param value The value with a potential type suffix
     * @return The value without the type suffix
     */
    public static String removeTypeSuffix(String value) {
        // Find the type suffix if present
        Optional<TypeMapper> typeMapper = inferTypeFromValue(value);

        // No type suffix found, return the original value
        if (typeMapper.isEmpty()) return value;

        // For char literals, keep the single quotes
        if (typeMapper.get().javaType().equals("U8") && value.startsWith("'") && value.endsWith("'")) return value;

        // For boolean literals, keep the original value
        if (typeMapper.get().javaType().equals("Bool") && ("true".equals(value) || "false".equals(value))) return value;

        // For comparison operators, keep the original value
        if (typeMapper.get().javaType().equals("Bool") && containsComparisonOperator(value)) return value;

        // For other types, remove the type suffix
        String suffix = typeMapper.get().javaType();
        return value.substring(0, value.length() - suffix.length());
    }

    /**
     * Validates that a numeric value is within the valid range for its type.
     *
     * @param type  The Java type (I8, I16, I32, I64, U8, U16, U32, U64)
     * @param value The value to validate
     * @param line  The original line for error reporting
     * @throws IllegalArgumentException if the value is outside the valid range for the type
     */
    public static void validateValueRange(String type, String value, String line) {
        // Skip validation for character literals and non-numeric values
        if (value.startsWith("'") || value.equals("true") || value.equals("false")) return;

        // Remove type suffix if present
        String numericValue = value;
        for (TypeMapper mapper : TYPE_MAPPERS)
            if (value.endsWith(mapper.javaType())) {
                numericValue = value.substring(0, value.length() - mapper.javaType().length());
                break;
            }

        try {
            // Parse the value and check against type bounds
            long longValue = Long.parseLong(numericValue);
            validateNumericRange(type, value, line, longValue);
        } catch (NumberFormatException e) {
            // Not a numeric value, skip validation
        }
    }

    /**
     * Validates that a numeric value is within the valid range for its type.
     *
     * @param type      The Java type (I8, I16, I32, I64, U8, U16, U32, U64)
     * @param value     The original value string for error reporting
     * @param line      The original line for error reporting
     * @param longValue The parsed numeric value to validate
     * @throws IllegalArgumentException if the value is outside the valid range for the type
     */
    public static void validateNumericRange(String type, String value, String line, long longValue) {
        switch (type) {
            case "I8":
                if (longValue < Byte.MIN_VALUE || longValue > Byte.MAX_VALUE) throw new IllegalArgumentException(
                        "Value out of range: " + value + " is outside the valid range for I8 (" + Byte.MIN_VALUE + " to " +
                        Byte.MAX_VALUE + "). Line: " + line);
                break;
            case "I16":
                if (longValue < Short.MIN_VALUE || longValue > Short.MAX_VALUE) throw new IllegalArgumentException(
                        "Value out of range: " + value + " is outside the valid range for I16 (" + Short.MIN_VALUE + " to " +
                        Short.MAX_VALUE + "). Line: " + line);
                break;
            case "I32":
                if (longValue < Integer.MIN_VALUE || longValue > Integer.MAX_VALUE) throw new IllegalArgumentException(
                        "Value out of range: " + value + " is outside the valid range for I32 (" + Integer.MIN_VALUE + " to " +
                        Integer.MAX_VALUE + "). Line: " + line);
                break;
            case "I64":
                // Already a long, no need to check
                break;
            case "U8":
                if (longValue < 0 || longValue > 255) throw new IllegalArgumentException(
                        "Value out of range: " + value + " is outside the valid range for U8 (0 to 255). Line: " + line);
                break;
            case "U16":
                if (longValue < 0 || longValue > 65535) throw new IllegalArgumentException(
                        "Value out of range: " + value + " is outside the valid range for U16 (0 to 65535). Line: " + line);
                break;
            case "U32":
                if (longValue < 0 || longValue > 4294967295L) throw new IllegalArgumentException(
                        "Value out of range: " + value + " is outside the valid range for U32 (0 to 4294967295). Line: " + line);
                break;
            case "U64":
                if (longValue < 0) throw new IllegalArgumentException(
                        "Value out of range: " + value + " is outside the valid range for U64 (0 to 18446744073709551615). Line: " +
                        line);
                break;
        }
    }
}