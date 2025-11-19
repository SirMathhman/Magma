package com.magma;

import java.math.BigInteger;
import java.util.Optional;

/**
 * Utility class for managing unit type definitions and limits.
 */
public final class UnitType {
    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private UnitType() {
        // Utility class
    }

    /**
     * Creates a BigInteger from a hexadecimal string.
     *
     * @param hexString The hexadecimal string
     * @return The BigInteger value
     */
    private static BigInteger bigIntFromHex(final String hexString) {
        return new BigInteger(hexString, 16);
    }

    /**
     * Gets the minimum value for a unit type.
     *
     * @param units The unit string (e.g., "U8", "I8", "U16")
     * @return The minimum value for the unit, or empty if unknown or unsigned
     */
    public static Optional<BigInteger> getMinValueForUnit(
            final String units) {
        return switch (units) {
            case "I8" -> Optional.of(BigInteger.valueOf(-0x80L));
            case "I16" -> Optional.of(BigInteger.valueOf(-0x8000L));
            case "I32" -> Optional.of(BigInteger.valueOf(-0x80000000L));
            case "I64" -> Optional.of(
                    BigInteger.valueOf(-0x8000000000000000L));
            default -> Optional.empty();
        };
    }

    /**
     * Gets the maximum value for a unit type.
     *
     * @param units The unit string (e.g., "U8", "I8", "U16")
     * @return The maximum value for the unit, or empty if unknown
     */
    public static Optional<BigInteger> getMaxValueForUnit(
            final String units) {
        final BigInteger u8Max = BigInteger.valueOf(0xFFL);
        final BigInteger u16Max = BigInteger.valueOf(0xFFFFL);
        final BigInteger u32Max = BigInteger.valueOf(0xFFFFFFFFL);
        final BigInteger u64Max = bigIntFromHex("FFFFFFFFFFFFFFFF");
        final BigInteger i8Max = BigInteger.valueOf(0x7FL);
        final BigInteger i16Max = BigInteger.valueOf(0x7FFFL);
        final BigInteger i32Max = BigInteger.valueOf(0x7FFFFFFFL);
        final BigInteger i64Max =
                BigInteger.valueOf(0x7FFFFFFFFFFFFFFFL);
        return switch (units) {
            case "U8" -> Optional.of(u8Max);
            case "U16" -> Optional.of(u16Max);
            case "U32" -> Optional.of(u32Max);
            case "U64" -> Optional.of(u64Max);
            case "I8" -> Optional.of(i8Max);
            case "I16" -> Optional.of(i16Max);
            case "I32" -> Optional.of(i32Max);
            case "I64" -> Optional.of(i64Max);
            default -> Optional.empty();
        };
    }
}

