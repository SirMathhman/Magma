package com.magma;

import java.math.BigInteger;
import java.util.Optional;
import java.util.function.BinaryOperator;

import com.magma.result.Err;
import com.magma.result.Ok;
import com.magma.result.Result;

/**
 * Main entry point for the Magma application.
 */
public final class Main {
    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private Main() {
        // Utility class
    }

    /**
     * Extracts the leading numeric part from a string.
     *
     * @param str The string to extract from
     * @return The leading numeric part (including negative sign if present)
     */
    private static String extractLeadingNumeric(final String str) {
        final StringBuilder result = new StringBuilder();
        int startIndex = 0;
        // Check for negative sign at the start
        if (str.length() > 0 && str.charAt(0) == '-') {
            result.append('-');
            startIndex = 1;
        }
        for (int i = startIndex; i < str.length(); i++) {
            final char c = str.charAt(i);
            if (Character.isDigit(c)) {
                result.append(c);
            } else {
                break;
            }
        }
        return result.toString();
    }

    /**
     * Checks if a string has units (non-numeric characters after the leading
     * digits).
     *
     * @param str The string to check
     * @return true if the string has units, false otherwise
     */
    private static boolean hasUnits(final String str) {
        final String numeric = extractLeadingNumeric(str);
        return numeric.length() < str.length();
    }

    /**
     * Extracts the units (non-numeric part) from a string.
     *
     * @param str The string to extract units from
     * @return The units part, or empty string if no units
     */
    private static String extractUnits(final String str) {
        final String numeric = extractLeadingNumeric(str);
        if (numeric.length() < str.length()) {
            return str.substring(numeric.length());
        }
        return "";
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
    private static Optional<BigInteger> getMinValueForUnit(
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
    private static Optional<BigInteger> getMaxValueForUnit(
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

    /**
     * Checks if a value with units is within the allowed range.
     *
     * @param operand The operand string to check
     * @return Err if value is out of range, Ok otherwise
     */
    private static Result<String, String> checkValueRange(
            final String operand) {
        final String trimmed = operand.trim();
        if (hasUnits(trimmed)) {
            final String numeric = extractLeadingNumeric(trimmed);
            final String units = extractUnits(trimmed);
            final Optional<BigInteger> minValue = getMinValueForUnit(units);
            final Optional<BigInteger> maxValue = getMaxValueForUnit(units);
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
    private static Result<String, String> checkResultOverflow(
            final String resultValue, final String units) {
        final Optional<BigInteger> maxValue = getMaxValueForUnit(units);
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
    private static Result<String, String> validateResultOverflow(
            final String resultStr, final String[] validOperands) {
        if (validOperands.length > 0
                && hasUnits(validOperands[0].trim())) {
            final String units = extractUnits(validOperands[0].trim());
            final Result<String, String> overflowCheck =
                    checkResultOverflow(resultStr, units);
            if (overflowCheck instanceof Err<String, String>) {
                return overflowCheck;
            }
        }
        return new Ok<>("");
    }

    /**
     * Validates result and returns Ok with result or Err if overflow.
     *
     * @param result The result value
     * @param validOperands Array of operand strings
     * @return Ok with result string or Err if overflow
     */
    private static Result<String, String> validateAndReturnResult(
            final int result, final String[] validOperands) {
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
     *
     * @param operands Array of operand strings
     * @return Err if units mismatch, Ok with empty string if units match
     */
    private static Result<String, String> checkUnitMismatches(
            final String[] operands) {
        for (int i = 0; i < operands.length; i++) {
            final String operandI = operands[i].trim();
            if (hasUnits(operandI)) {
                final String unitsI = extractUnits(operandI);
                for (int j = i + 1; j < operands.length; j++) {
                    final String operandJ = operands[j].trim();
                    if (hasUnits(operandJ)) {
                        final String unitsJ = extractUnits(operandJ);
                        if (!unitsI.equals(unitsJ)) {
                            return new Err<>(
                                    "Cannot operate on values with different "
                                    + "units");
                        }
                    }
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
    private static Result<String[], String> validateOperands(
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

    /**
     * Processes operands using the given configuration.
     *
     * @param operands Array of validated operand strings
     * @param config The configuration for processing
     * @return Result with the computed value
     */
    private static Result<String, String> processOperands(
            final String[] operands,
            final OperandConfig config) {
        int result = config.initialValue();
        for (int i = config.startIndex(); i < operands.length; i++) {
            final String numeric = extractLeadingNumeric(
                    operands[i].trim());
            result = config.operator().apply(result,
                    Integer.parseInt(numeric));
        }
        final String resultStr = String.valueOf(result);
        final Result<String, String> overflowCheck =
                validateResultOverflow(resultStr, operands);
        if (overflowCheck instanceof Err<String, String>) {
            return overflowCheck;
        }
        return new Ok<>(resultStr);
    }

    /**
     * Splits and validates operands from input.
     *
     * @param input The input string containing the expression
     * @param operatorPattern The operator pattern to split on
     * @return Err if validation fails, Ok with validated operands otherwise
     */
    private static Result<String[], String> splitAndValidate(
            final String input,
            final String operatorPattern) {
        final String[] operands = input.split(operatorPattern);
        return validateOperands(operands);
    }

    /**
     * Splits expression into parts and extracts operands and operators.
     *
     * @param input The input string containing the expression
     * @param operatorPattern The regex pattern to split on
     * @return Tuple of operands array and operators array, or Err if invalid
     */
    private static Result<Tuple<String[], String[]>, String> splitExpression(
            final String input, final String operatorPattern) {
        final String[] parts = input.split(operatorPattern);
        if (parts.length < 2) {
            return new Err<>("Invalid expression");
        }
        final String[] operands = new String[(parts.length + 1) / 2];
        final String[] operators = new String[parts.length / 2];
        for (int i = 0; i < parts.length; i += 2) {
            operands[i / 2] = parts[i];
        }
        for (int i = 1; i < parts.length; i += 2) {
            operators[i / 2] = parts[i].trim();
        }
        return new Ok<>(new Tuple<>(operands, operators));
    }

    /**
     * Processes an arithmetic expression with the given operator.
     *
     * @param input The input string containing the expression
     * @param operatorPattern The operator pattern to split on
     * @param config The configuration for processing operands
     * @return Result with the evaluated expression
     */
    private static Result<String, String> processArithmetic(
            final String input,
            final String operatorPattern,
            final OperandConfig config) {
        return splitAndValidate(input, operatorPattern)
                .flatMap(operands -> processOperands(operands, config));
    }

    /**
     * Processes multiplication operations, returning values and operators for
     * addition/subtraction.
     *
     * @param validOperands Array of validated operand strings
     * @param operators Array of operator strings
     * @return Tuple of values list and addition/subtraction operators list
     */
    private static Tuple<java.util.List<Integer>, java.util.List<String>>
            processMultiplications(final String[] validOperands,
                                  final String[] operators) {
        final java.util.List<Integer> values =
                new java.util.ArrayList<>();
        final java.util.List<String> addSubOps =
                new java.util.ArrayList<>();
        int current = Integer.parseInt(
                extractLeadingNumeric(validOperands[0].trim()));
        for (int i = 0; i < operators.length; i++) {
            final String op = operators[i];
            final int nextValue = Integer.parseInt(
                    extractLeadingNumeric(validOperands[i + 1].trim()));
            if ("*".equals(op)) {
                current *= nextValue;
            } else {
                values.add(current);
                addSubOps.add(op);
                current = nextValue;
            }
        }
        values.add(current);
        return new Tuple<>(values, addSubOps);
    }

    /**
     * Processes addition and subtraction operations.
     *
     * @param values List of values to operate on
     * @param addSubOps List of addition/subtraction operators
     * @return The final result
     */
    private static int processAdditionsAndSubtractions(
            final java.util.List<Integer> values,
            final java.util.List<String> addSubOps) {
        int result = values.get(0);
        for (int i = 0; i < addSubOps.size(); i++) {
            final String op = addSubOps.get(i);
            final int value = values.get(i + 1);
            if ("-".equals(op)) {
                result -= value;
            } else {
                result += value;
            }
        }
        return result;
    }

    /**
     * Splits and validates an expression, then processes it with the given
     * function.
     *
     * @param input The input string containing the expression
     * @param operatorPattern The regex pattern to split on
     * @param processor Function to process validated operands and operators
     * @return Result with the evaluated expression
     */
    private static Result<String, String> splitValidateAndProcess(
            final String input,
            final String operatorPattern,
            final java.util.function.Function<Tuple<String[], String[]>,
                    Result<String, String>> processor) {
        return splitExpression(input, operatorPattern)
                .flatMap(tuple -> {
                    final String[] operands = tuple.first();
                    final String[] operators = tuple.second();
                    return validateOperands(operands)
                            .flatMap(validOperands -> processor.apply(
                                    new Tuple<>(validOperands, operators)));
                });
    }

    /**
     * Processes an expression with operator precedence (multiplication before
     * addition/subtraction).
     *
     * @param input The input string containing the expression
     * @return Result with the evaluated expression
     */
    private static Result<String, String> processWithPrecedence(
            final String input) {
        return splitValidateAndProcess(input, " (?=[+\\-*])|(?<=[+\\-*]) ",
                tuple -> {
                    final String[] validOperands = tuple.first();
                    final String[] operators = tuple.second();
                    final var multResult =
                            processMultiplications(validOperands, operators);
                    final int result =
                            processAdditionsAndSubtractions(
                                    multResult.first(),
                                    multResult.second());
                    return validateAndReturnResult(result, validOperands);
                });
    }

    /**
     * Processes a mixed arithmetic expression with both addition and
     * subtraction.
     *
     * @param input The input string containing the expression
     * @return Result with the evaluated expression
     */
    private static Result<String, String> processMixedArithmetic(
            final String input) {
        return splitValidateAndProcess(input, " (?=[+-])|(?<=[+-]) ",
                tuple -> {
                    final String[] validOperands = tuple.first();
                    final String[] operators = tuple.second();
                    // Process left-to-right
                    int result = Integer.parseInt(
                            extractLeadingNumeric(validOperands[0].trim()));
                    for (int i = 0; i < operators.length; i++) {
                        final int value = Integer.parseInt(
                                extractLeadingNumeric(
                                        validOperands[i + 1].trim()));
                        if ("-".equals(operators[i])) {
                            result -= value;
                        } else {
                            result += value;
                        }
                    }
                    return validateAndReturnResult(result, validOperands);
                });
    }

    /**
     * Interprets a string and evaluates arithmetic expressions or extracts
     * the leading numeric part.
     *
     * @param input The input string to interpret (assumed to be present
     *              and non-empty)
     * @return A Result containing the evaluated result or leading numeric
     *         part of the string wrapped in Ok
     */
    public static Result<String, String> interpret(final String input) {
        // Check if input contains any arithmetic operator
        if (input.contains(" + ") || input.contains(" - ")
                || input.contains(" * ")) {
            // Check if it contains multiplication with addition/subtraction
            if (input.contains(" * ")
                    && (input.contains(" + ") || input.contains(" - "))) {
                return processWithPrecedence(input);
            }
            // Check if it contains both addition and subtraction
            if (input.contains(" + ") && input.contains(" - ")) {
                return processMixedArithmetic(input);
            }
            // Single operator type
            if (input.contains(" + ")) {
                return processArithmetic(input, " \\+ ",
                        new OperandConfig(0, Integer::sum, 0));
            }
            if (input.contains(" - ")) {
                return splitAndValidate(input, " - ")
                        .flatMap(operands -> {
                            final int firstValue = Integer.parseInt(
                                    extractLeadingNumeric(
                                            operands[0].trim()));
                            return processOperands(operands,
                                    new OperandConfig(firstValue,
                                            (a, b) -> a - b, 1));
                        });
            }
            if (input.contains(" * ")) {
                return processArithmetic(input, " \\* ",
                        new OperandConfig(1, (a, b) -> a * b, 0));
            }
        }
        // Fall back to extracting leading numeric part
        // Check value range for single values with units
        final Result<String, String> rangeCheck = checkValueRange(input);
        if (rangeCheck instanceof Err<String, String>) {
            return rangeCheck;
        }
        return new Ok<>(extractLeadingNumeric(input));
    }

    /**
     * A tuple containing two values.
     *
     * @param <A> The type of the first value
     * @param <B> The type of the second value
     * @param first The first value
     * @param second The second value
     */
    private record Tuple<A, B>(A first, B second) {
    }

    /**
     * Configuration for processing operands.
     *
     * @param initialValue The initial value to start with
     * @param operator The binary operator to apply
     * @param startIndex The index to start processing from
     */
    private record OperandConfig(int initialValue,
                                 BinaryOperator<Integer> operator,
                                 int startIndex) {
    }
}

