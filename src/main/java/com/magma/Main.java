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
            // Check for negative values with unsigned types
            if (declaredUnits.startsWith("U") && valueNumeric.startsWith("-")) {
                return new Err<>("Negative values with unsigned types are not "
                        + "allowed");
            }
            // Only check that units match (value can have no units for
            // plain numbers)
            if (!valueUnits.isEmpty()
                    && !declaredUnits.equals(valueUnits)) {
                return new Err<>("Type mismatch: declared type is " + type
                        + " but value has units " + valueUnits);
            }
            // Check value range for unit-only types
            // Construct value with units to check range
            final String valueWithUnits = valueNumeric + declaredUnits;
            final Result<String, String> rangeCheck =
                    OperandValidator.checkValueRange(valueWithUnits);
            if (rangeCheck instanceof Err<String, String>) {
                return rangeCheck;
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
     * Parses variable name and type from a statement.
     *
     * @param rest                  The statement after the "let" or
     *                              "extern let" prefix
     * @param errorMsg              Error message prefix for validation errors
     * @param requireTypeEmptyCheck Whether to check if type is empty
     * @return Tuple with variable name and type, or Err if parsing fails
     */
    private static Result<ExpressionParser.Tuple<String, String>, String>
            parseVarNameAndType(final String rest, final String errorMsg,
            final boolean requireTypeEmptyCheck) {
        final int colonIndex = rest.indexOf(':');
        if (colonIndex < 0) {
            return new Err<>("Missing type in " + errorMsg);
        }
        final String varName = rest.substring(0, colonIndex).trim();
        if (varName.isEmpty()) {
            return new Err<>("Empty variable name");
        }
        final String type = rest.substring(colonIndex + 1).trim();
        if (requireTypeEmptyCheck && type.isEmpty()) {
            return new Err<>("Empty type in " + errorMsg);
        }
        return new Ok<>(new ExpressionParser.Tuple<>(varName, type));
    }

    /**
     * Validates and extracts the rest of a statement after a prefix.
     *
     * @param statement The full statement
     * @param prefix    The expected prefix
     * @param errorMsg  Error message if prefix doesn't match
     * @return The rest of the statement after the prefix, or Err if invalid
     */
    private static Result<String, String> validateAndExtractRest(
            final String statement, final String prefix,
            final String errorMsg) {
        final String trimmed = statement.trim();
        if (!trimmed.startsWith(prefix)) {
            return new Err<>(errorMsg);
        }
        return new Ok<>(trimmed.substring(prefix.length()).trim());
    }

    /**
     * Parses an extern let statement and stores the variable.
     *
     * @param statement The extern let statement (e.g., "extern let x : U8")
     * @param context   The variable context
     * @return Ok if successful, Err if parsing fails
     */
    private static Result<String, String> parseExternLetStatement(
            final String statement, final VariableContext context) {
        // Format: extern let x : type
        return validateAndExtractRest(statement, "extern let ",
                "Invalid extern let statement")
                .flatMap(rest -> parseVarNameAndType(rest,
                        "extern let statement", true)
                        .flatMap(tuple -> {
                            // Store the variable with empty value (extern
                            // variables are not initialized)
                            context.setVariable(tuple.first(), "");
                            return new Ok<String, String>("");
                        }));
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
        // Format: let x : type = value
        final Result<String, String> restResult =
                validateAndExtractRest(statement, "let ",
                        "Invalid let statement");
        return restResult.flatMap(rest -> {
            final int colonIndex = rest.indexOf(':');
            if (colonIndex < 0) {
                return new Err<String, String>(
                        "Missing type in let statement");
            }
            final String varName = rest.substring(0, colonIndex).trim();
            if (varName.isEmpty()) {
                return new Err<String, String>("Empty variable name");
            }
            final String afterColon = rest.substring(colonIndex + 1).trim();
            final int equalsIndex = afterColon.indexOf('=');
            if (equalsIndex < 0) {
                return new Err<String, String>("Missing = in let statement");
            }
            final String type = afterColon.substring(0, equalsIndex).trim();
            final String value = afterColon.substring(equalsIndex + 1).trim();
            // Check type match
            return checkTypeMatch(type, value).flatMap(ignored -> {
                // Evaluate the value
                return interpretExpression(value, context)
                        .flatMap(valueStr -> {
                            // Store the variable
                            context.setVariable(varName, valueStr);
                            return new Ok<String, String>("");
                        });
            });
        });
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
            if (trimmed.startsWith("extern let ")) {
                final Result<String, String> externLetResult =
                        parseExternLetStatement(trimmed, context);
                if (externLetResult instanceof Err<String, String>) {
                    return externLetResult;
                }
                lastResult = externLetResult;
            } else if (trimmed.startsWith("let ")) {
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
