package com.magma;

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
     * @return The leading numeric part
     */
    private static String extractLeadingNumeric(final String str) {
        final StringBuilder result = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
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
     * Checks if any operand with units is negative.
     *
     * @param operands Array of operand strings
     * @return Err if a negative value with units is found, Ok otherwise
     */
    private static Result<String, String> checkNegativeWithUnits(
            final String[] operands) {
        for (final String operand : operands) {
            final String trimmed = operand.trim();
            if (trimmed.startsWith("-") && hasUnits(trimmed)) {
                return new Err<>("Negative values with units are not allowed");
            }
        }
        return new Ok<>("");
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
     * Validates operands and checks for unit mismatches and negative values
     * with units.
     *
     * @param operands Array of operand strings
     * @return Err if validation fails, Ok with operands if valid
     */
    private static Result<String[], String> validateOperands(
            final String[] operands) {
        return checkNegativeWithUnits(operands)
                .flatMap(ignored -> checkUnitMismatches(operands))
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
        return new Ok<>(String.valueOf(result));
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
     * Processes a mixed arithmetic expression with both addition and
     * subtraction.
     *
     * @param input The input string containing the expression
     * @return Result with the evaluated expression
     */
    private static Result<String, String> processMixedArithmetic(
            final String input) {
        // Split by both operators, keeping track of operators
        final String[] parts = input.split(" (?=[+-])|(?<=[+-]) ");
        if (parts.length < 2) {
            return new Err<>("Invalid expression");
        }
        // Extract operands (every other element starting from 0)
        final String[] operands = new String[(parts.length + 1) / 2];
        for (int i = 0; i < parts.length; i += 2) {
            operands[i / 2] = parts[i];
        }
        // Validate operands
        final Result<String[], String> validated =
                validateOperands(operands);
        return validated.flatMap(validOperands -> {
            // Process left-to-right
            int result = Integer.parseInt(
                    extractLeadingNumeric(validOperands[0].trim()));
            for (int i = 1; i < validOperands.length; i++) {
                final int value = Integer.parseInt(
                        extractLeadingNumeric(validOperands[i].trim()));
                // Find the operator between operands[i-1] and operands[i]
                final int opIndex = 2 * i - 1;
                if (opIndex < parts.length) {
                    final String operator = parts[opIndex].trim();
                    if ("-".equals(operator)) {
                        result -= value;
                    } else {
                        result += value;
                    }
                }
            }
            return new Ok<>(String.valueOf(result));
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
        // Check for negative value with units (single value case)
        final String trimmed = input.trim();
        if (trimmed.startsWith("-") && hasUnits(trimmed)) {
            return new Err<>("Negative values with units are not allowed");
        }
        // Check if input contains any arithmetic operator
        if (input.contains(" + ") || input.contains(" - ")) {
            // Check if it contains both operators
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
        }
        // Fall back to extracting leading numeric part
        return new Ok<>(extractLeadingNumeric(input));
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

