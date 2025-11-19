package com.magma;

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
     * Checks if input is a boolean literal and returns it.
     *
     * @param input The input string to check
     * @return Ok with boolean value if it's a boolean literal, empty Optional
     *         otherwise
     */
    private static java.util.Optional<Result<String, String>>
            checkBooleanLiteral(final String input) {
        final String trimmed = input.trim();
        if ("true".equals(trimmed)) {
            return java.util.Optional.of(new Ok<>("true"));
        }
        if ("false".equals(trimmed)) {
            return java.util.Optional.of(new Ok<>("false"));
        }
        return java.util.Optional.empty();
    }

    /**
     * Processes arithmetic expressions based on operator types.
     *
     * @param input The input string containing the expression
     * @return Result with the evaluated expression, or empty Optional if not
     *         an arithmetic expression
     */
    private static java.util.Optional<Result<String, String>>
            processArithmeticExpression(final String input) {
        if (input.contains(" + ") || input.contains(" - ")
                || input.contains(" * ")) {
            // Check if it contains multiplication with addition/subtraction
            if (input.contains(" * ")
                    && (input.contains(" + ") || input.contains(" - "))) {
                return java.util.Optional.of(
                        ArithmeticProcessor.processWithPrecedence(input));
            }
            // Check if it contains both addition and subtraction
            if (input.contains(" + ") && input.contains(" - ")) {
                return java.util.Optional.of(
                        ArithmeticProcessor.processMixedArithmetic(input));
            }
            // Single operator type
            if (input.contains(" + ")) {
                return java.util.Optional.of(
                        ArithmeticProcessor.processArithmetic(input, " \\+ ",
                                new ArithmeticProcessor.OperandConfig(0,
                                        Integer::sum, 0)));
            }
            if (input.contains(" - ")) {
                return java.util.Optional.of(
                        ExpressionParser.splitAndValidate(input, " - ")
                                .flatMap(operands -> {
                                    final int firstValue = Integer.parseInt(
                                            StringParser.extractLeadingNumeric(
                                                    operands[0].trim()));
                                    final ArithmeticProcessor.OperandConfig
                                            config =
                                            new ArithmeticProcessor
                                                    .OperandConfig(
                                                    firstValue,
                                                    (a, b) -> a - b, 1);
                                    return ArithmeticProcessor.processOperands(
                                            operands, config);
                                }));
            }
            if (input.contains(" * ")) {
                return java.util.Optional.of(
                        ArithmeticProcessor.processArithmetic(input, " \\* ",
                                new ArithmeticProcessor.OperandConfig(1,
                                        (a, b) -> a * b, 0)));
            }
        }
        return java.util.Optional.empty();
    }

    /**
     * Checks if the declared type matches the value type.
     *
     * @param type  The declared type annotation
     * @param value The value to check
     * @return Err if types don't match, Ok otherwise
     */
    private static Result<String, String> checkTypeMatch(final String type,
            final String value) {
        // Extract numeric and unit parts for comparison
        final String declaredNumeric = StringParser.extractLeadingNumeric(type);
        final String declaredUnits = StringParser.hasUnits(type)
                ? StringParser.extractUnits(type) : "";
        final String valueNumeric = StringParser.extractLeadingNumeric(value);
        final String valueUnits = StringParser.hasUnits(value)
                ? StringParser.extractUnits(value) : "";
        // Check if types match
        // If type annotation has no numeric part (just unit type),
        // only check units
        final boolean isUnitOnlyType = declaredNumeric.isEmpty()
                && !declaredUnits.isEmpty();
        if (isUnitOnlyType) {
            // Only check that units match (value can have no units for
            // plain numbers)
            if (!valueUnits.isEmpty()
                    && !declaredUnits.equals(valueUnits)) {
                return new Err<>("Type mismatch: declared type is " + type
                        + " but value has units " + valueUnits);
            }
        } else {
            // Check both numeric and unit parts
            if (!declaredNumeric.equals(valueNumeric)
                    || !declaredUnits.equals(valueUnits)) {
                return new Err<>("Type mismatch: declared type is " + type
                        + " but value is " + value);
            }
        }
        return new Ok<>("");
    }

    /**
     * Parses a let statement and stores the variable.
     *
     * @param statement The let statement (e.g., "let x : 1U8 = 1U8")
     * @param context   The variable context
     * @return Ok if successful, Err if parsing fails
     */
    private static Result<String, String> parseLetStatement(
            final String statement, final VariableContext context) {
        final String trimmed = statement.trim();
        // Format: let x : type = value
        if (!trimmed.startsWith("let ")) {
            return new Err<>("Invalid let statement");
        }
        final String rest = trimmed.substring(4).trim();
        final int colonIndex = rest.indexOf(':');
        if (colonIndex < 0) {
            return new Err<>("Missing type in let statement");
        }
        final String varName = rest.substring(0, colonIndex).trim();
        if (varName.isEmpty()) {
            return new Err<>("Empty variable name");
        }
        final String afterColon = rest.substring(colonIndex + 1).trim();
        final int equalsIndex = afterColon.indexOf('=');
        if (equalsIndex < 0) {
            return new Err<>("Missing = in let statement");
        }
        final String type = afterColon.substring(0, equalsIndex).trim();
        final String value = afterColon.substring(equalsIndex + 1).trim();
        // Check type match
        final Result<String, String> typeCheck = checkTypeMatch(type, value);
        if (typeCheck instanceof Err<String, String>) {
            return typeCheck;
        }
        // Evaluate the value
        final Result<String, String> valueResult =
                interpretExpression(value, context);
        if (valueResult instanceof Err<String, String>) {
            return valueResult;
        }
        final String valueStr = ((Ok<String, String>) valueResult).getValue();
        // Store the variable
        context.setVariable(varName, valueStr);
        return new Ok<>("");
    }

    /**
     * Substitutes variables in an expression.
     *
     * @param expression The expression to substitute
     * @param context    The variable context
     * @return The expression with variables substituted
     */
    private static String substituteVariables(final String expression,
            final VariableContext context) {
        String result = expression;
        for (final String varName : context.getVariableNames()) {
            final java.util.Optional<String> varValue =
                    context.getVariable(varName);
            if (varValue.isPresent()) {
                // Replace variable name with its value
                result = result.replaceAll("\\b" + varName + "\\b",
                        varValue.get());
            }
        }
        return result;
    }

    /**
     * Interprets an expression (without handling let statements).
     *
     * @param expression The expression to interpret
     * @param context    The variable context
     * @return Result with the evaluated expression
     */
    private static Result<String, String> interpretExpression(
            final String expression, final VariableContext context) {
        // Substitute variables
        final String substituted = substituteVariables(expression, context);
        // Check for boolean literals
        final java.util.Optional<Result<String, String>> booleanResult =
                checkBooleanLiteral(substituted);
        if (booleanResult.isPresent()) {
            return booleanResult.get();
        }
        // Check for arithmetic expressions
        final java.util.Optional<Result<String, String>> arithmeticResult =
                processArithmeticExpression(substituted);
        if (arithmeticResult.isPresent()) {
            return arithmeticResult.get();
        }
        // Check if it's a variable reference
        final String trimmed = substituted.trim();
        final java.util.Optional<String> varValue =
                context.getVariable(trimmed);
        if (varValue.isPresent()) {
            return new Ok<>(varValue.get());
        }
        // Fall back to extracting leading numeric part
        // Check value range for single values with units
        final Result<String, String> rangeCheck =
                OperandValidator.checkValueRange(substituted);
        if (rangeCheck instanceof Err<String, String>) {
            return rangeCheck;
        }
        return new Ok<>(StringParser.extractLeadingNumeric(substituted));
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
        final VariableContext context = new VariableContext();
        // Split by semicolon to handle multiple statements
        final String[] statements = input.split(";");
        Result<String, String> lastResult = new Ok<>("");
        for (final String statement : statements) {
            final String trimmed = statement.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            if (trimmed.startsWith("let ")) {
                final Result<String, String> letResult =
                        parseLetStatement(trimmed, context);
                if (letResult instanceof Err<String, String>) {
                    return letResult;
                }
                lastResult = letResult;
            } else {
                lastResult = interpretExpression(trimmed, context);
                if (lastResult instanceof Err<String, String>) {
                    return lastResult;
                }
            }
        }
        return lastResult;
    }
}
