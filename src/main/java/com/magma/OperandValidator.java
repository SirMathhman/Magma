package com.magma;

import java.math.BigInteger;
import java.util.Optional;

import com.magma.result.Err;
import com.magma.result.Ok;
import com.magma.result.Result;

/**
 * Utility class for validating operands and checking value ranges.
 */
public final class OperandValidator {
    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private OperandValidator() {
        // Utility class
    }

    /**
     * Checks if a value with units is within the allowed range.
     *
     * @param operand The operand string to check
     * @return Err if value is out of range, Ok otherwise
     */
    public static Result<String, String> checkValueRange(
            final String operand) {
        final String trimmed = operand.trim();
        if (StringParser.hasUnits(trimmed)) {
            final String numeric = StringParser.extractLeadingNumeric(trimmed);
            final String units = StringParser.extractUnits(trimmed);
            final Optional<BigInteger> minValue =
                    UnitType.getMinValueForUnit(units);
            final Optional<BigInteger> maxValue =
                    UnitType.getMaxValueForUnit(units);
            if (maxValue.isPresent()) {
                try {
                    final BigInteger value = new BigInteger(numeric);
                    if (minValue.isPresent()
                            && value.compareTo(minValue.get()) < 0) {
                        return new Err<>(
                                "Value below minimum for " + units);
                    }
                    if (value.compareTo(maxValue.get()) > 0) {
                        return new Err<>("Value exceeds maximum for " + units);
                    }
                } catch (final NumberFormatException e) {
                    // Invalid number format, will be caught elsewhere
                }
            }
        }
        return new Ok<>("");
    }

    /**
     * Checks if a result value exceeds the maximum for a given unit type.
     *
     * @param resultValue The result value as a string
     * @param units The unit type
     * @return Err if value exceeds maximum, Ok otherwise
     */
    public static Result<String, String> checkResultOverflow(
            final String resultValue, final String units) {
        final Optional<BigInteger> maxValue =
                UnitType.getMaxValueForUnit(units);
        if (maxValue.isPresent()) {
            try {
                final BigInteger value = new BigInteger(resultValue);
                if (value.compareTo(maxValue.get()) > 0) {
                    return new Err<>("Value exceeds maximum for " + units);
                }
            } catch (final NumberFormatException e) {
                // Invalid number format, will be caught elsewhere
            }
        }
        return new Ok<>("");
    }

    /**
     * Validates result overflow for operands with units.
     *
     * @param resultStr The result value as a string
     * @param validOperands Array of operand strings
     * @return Err if overflow detected, Ok otherwise
     */
    public static Result<String, String> validateResultOverflow(
            final String resultStr, final String[] validOperands) {
        if (validOperands.length > 0
                && StringParser.hasUnits(validOperands[0].trim())) {
            final String units = StringParser.extractUnits(
                    validOperands[0].trim());
            final Result<String, String> overflowCheck =
                    checkResultOverflow(resultStr, units);
            if (overflowCheck instanceof Err<String, String>) {
                return overflowCheck;
            }
        }
        return new Ok<>("");
    }

    /**
     * Validates result and returns Ok with result or Err if overflow or
     * negative for unsigned types.
     *
     * @param result The result value
     * @param validOperands Array of operand strings
     * @return Ok with result string or Err if overflow or negative for unsigned
     */
    public static Result<String, String> validateAndReturnResult(
            final int result, final String[] validOperands) {
        // Check for negative result with unsigned types
        if (result < 0 && validOperands.length > 0
                && StringParser.hasUnits(validOperands[0].trim())) {
            final String units = StringParser.extractUnits(
                    validOperands[0].trim());
            if (units.startsWith("U")) {
                return new Err<>("Negative values with units are not allowed");
            }
        }
        final String resultStr = String.valueOf(result);
        final Result<String, String> overflowCheck =
                validateResultOverflow(resultStr, validOperands);
        if (overflowCheck instanceof Err<String, String>) {
            return overflowCheck;
        }
        return new Ok<>(resultStr);
    }

    /**
     * Checks if all operands with units have matching units.
     * If any operand has units, all operands must have the same units.
     *
     * @param operands Array of operand strings
     * @return Err if units mismatch, Ok with empty string if units match
     */
    public static Result<String, String> checkUnitMismatches(
            final String[] operands) {
        // Check if any operand has units
        boolean hasAnyUnits = false;
        String firstUnits = "";
        for (final String operand : operands) {
            final String trimmed = operand.trim();
            if (StringParser.hasUnits(trimmed)) {
                hasAnyUnits = true;
                if (firstUnits.isEmpty()) {
                    firstUnits = StringParser.extractUnits(trimmed);
                }
                break;
            }
        }
        // If any operand has units, all must have the same units
        if (hasAnyUnits) {
            for (final String operand : operands) {
                final String trimmed = operand.trim();
                if (!StringParser.hasUnits(trimmed)) {
                    return new Err<>(
                            "Cannot operate on values with different units");
                }
                final String units = StringParser.extractUnits(trimmed);
                if (!firstUnits.equals(units)) {
                    return new Err<>(
                            "Cannot operate on values with different units");
                }
            }
        }
        return new Ok<>("");
    }

    /**
     * Validates operands and checks for unit mismatches and value ranges.
     *
     * @param operands Array of operand strings
     * @return Err if validation fails, Ok with operands if valid
     */
    public static Result<String[], String> validateOperands(
            final String[] operands) {
        for (final String operand : operands) {
            final Result<String, String> rangeCheck = checkValueRange(operand);
            if (rangeCheck instanceof Err<String, String> err) {
                return new Err<>(err.getError());
            }
        }
        return checkUnitMismatches(operands)
                .map(ignored -> operands);
    }
}

