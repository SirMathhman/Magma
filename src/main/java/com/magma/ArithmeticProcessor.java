package com.magma;

import java.util.function.BinaryOperator;

import com.magma.result.Result;

/**
 * Utility class for processing arithmetic operations.
 */
public final class ArithmeticProcessor {
    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private ArithmeticProcessor() {
        // Utility class
    }

    /**
     * Processes operands using the given configuration.
     *
     * @param operands Array of validated operand strings
     * @param config The configuration for processing
     * @return Result with the computed value
     */
    public static Result<String, String> processOperands(
            final String[] operands,
            final OperandConfig config) {
        int result = config.initialValue();
        for (int i = config.startIndex(); i < operands.length; i++) {
            final String numeric = StringParser.extractLeadingNumeric(
                    operands[i].trim());
            result = config.operator().apply(result,
                    Integer.parseInt(numeric));
        }
        return OperandValidator.validateAndReturnResult(result, operands);
    }

    /**
     * Processes an arithmetic expression with the given operator.
     *
     * @param input The input string containing the expression
     * @param operatorPattern The operator pattern to split on
     * @param config The configuration for processing operands
     * @return Result with the evaluated expression
     */
    public static Result<String, String> processArithmetic(
            final String input,
            final String operatorPattern,
            final OperandConfig config) {
        return ExpressionParser.splitAndValidate(input, operatorPattern)
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
    public static ExpressionParser.Tuple<java.util.List<Integer>,
            java.util.List<String>> processMultiplications(
            final String[] validOperands, final String[] operators) {
        final java.util.List<Integer> values =
                new java.util.ArrayList<>();
        final java.util.List<String> addSubOps =
                new java.util.ArrayList<>();
        int current = Integer.parseInt(
                StringParser.extractLeadingNumeric(validOperands[0].trim()));
        for (int i = 0; i < operators.length; i++) {
            final String op = operators[i];
            final int nextValue = Integer.parseInt(
                    StringParser.extractLeadingNumeric(
                            validOperands[i + 1].trim()));
            if ("*".equals(op)) {
                current *= nextValue;
            } else {
                values.add(current);
                addSubOps.add(op);
                current = nextValue;
            }
        }
        values.add(current);
        return new ExpressionParser.Tuple<>(values, addSubOps);
    }

    /**
     * Processes addition and subtraction operations.
     *
     * @param values List of values to operate on
     * @param addSubOps List of addition/subtraction operators
     * @return The final result
     */
    public static int processAdditionsAndSubtractions(
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
     * Processes an expression with operator precedence (multiplication before
     * addition/subtraction).
     *
     * @param input The input string containing the expression
     * @return Result with the evaluated expression
     */
    public static Result<String, String> processWithPrecedence(
            final String input) {
        return ExpressionParser.splitValidateAndProcess(input,
                " (?=[+\\-*])|(?<=[+\\-*]) ",
                tuple -> processPrecedenceTuple(tuple));
    }

    /**
     * Processes a mixed arithmetic expression with both addition and
     * subtraction.
     *
     * @param input The input string containing the expression
     * @return Result with the evaluated expression
     */
    public static Result<String, String> processMixedArithmetic(
            final String input) {
        return ExpressionParser.splitValidateAndProcess(input,
                " (?=[+-])|(?<=[+-]) ",
                tuple -> processMixedTuple(tuple));
    }

    /**
     * Processes a tuple for precedence operations.
     *
     * @param tuple The tuple containing operands and operators
     * @return Result with the evaluated expression
     */
    private static Result<String, String> processPrecedenceTuple(
            final ExpressionParser.Tuple<String[], String[]> tuple) {
        final String[] validOperands = tuple.first();
        final String[] operators = tuple.second();
        final var multResult =
                processMultiplications(validOperands, operators);
        final int result =
                processAdditionsAndSubtractions(
                        multResult.first(),
                        multResult.second());
        return OperandValidator.validateAndReturnResult(result,
                validOperands);
    }

    /**
     * Processes a tuple for mixed arithmetic operations.
     *
     * @param tuple The tuple containing operands and operators
     * @return Result with the evaluated expression
     */
    private static Result<String, String> processMixedTuple(
            final ExpressionParser.Tuple<String[], String[]> tuple) {
        final String[] validOperands = tuple.first();
        final String[] operators = tuple.second();
        // Process left-to-right
        int result = Integer.parseInt(
                StringParser.extractLeadingNumeric(
                        validOperands[0].trim()));
        for (int i = 0; i < operators.length; i++) {
            final int value = Integer.parseInt(
                    StringParser.extractLeadingNumeric(
                            validOperands[i + 1].trim()));
            if ("-".equals(operators[i])) {
                result -= value;
            } else {
                result += value;
            }
        }
        return OperandValidator.validateAndReturnResult(result,
                validOperands);
    }

    /**
     * Configuration for processing operands.
     *
     * @param initialValue The initial value to start with
     * @param operator The binary operator to apply
     * @param startIndex The index to start processing from
     */
    public record OperandConfig(int initialValue,
                                BinaryOperator<Integer> operator,
                                int startIndex) {
    }
}

