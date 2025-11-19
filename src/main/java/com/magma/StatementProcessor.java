package com.magma;

import com.magma.result.Err;
import com.magma.result.Ok;
import com.magma.result.Result;

/**
 * Processes statements in the Magma language.
 */
public final class StatementProcessor {
    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private StatementProcessor() {
        // Utility class
    }

    /**
     * Reconstructs an if expression from multiple statements.
     *
     * @param statements The array of statements
     * @param index      Array with current index (will be updated)
     * @return The reconstructed if expression
     */
    static String reconstructIfExpression(final String[] statements,
            final int[] index) {
        final StringBuilder ifExpr = new StringBuilder(statements[index[0]]);
        index[0]++;
        boolean foundElse = false;
        while (index[0] < statements.length && !foundElse) {
            final String next = statements[index[0]].trim();
            if (next.startsWith("else ")) {
                ifExpr.append("; ").append(next);
                foundElse = true;
                index[0]++;
            } else if (next.isEmpty()) {
                index[0]++;
            } else {
                // Part of the then branch
                ifExpr.append("; ").append(next);
                index[0]++;
            }
        }
        return ifExpr.toString();
    }

    /**
     * Processes a single statement.
     *
     * @param statement The statement to process
     * @param context   The variable context
     * @return Result of processing the statement
     */
    static Result<String, String> processStatement(
            final String statement, final VariableContext context) {
        if (statement.startsWith("extern let ")) {
            return Main.parseExternLetStatement(statement, context);
        }
        if (statement.startsWith("let ")) {
            return processLetStatement(statement, context);
        }
        return Main.interpretExpression(statement, context);
    }

    /**
     * Processes a let statement (with or without initializer).
     *
     * @param statement The let statement
     * @param context   The variable context
     * @return Result of processing the statement
     */
    private static Result<String, String> processLetStatement(
            final String statement, final VariableContext context) {
        final String rest = statement.substring(4).trim();
        final int colonIndex = rest.indexOf(':');
        if (colonIndex < 0) {
            // No colon, treat as regular let (will fail parsing)
            return Main.parseLetStatement(statement, context);
        }
        final String afterColon = rest.substring(colonIndex + 1).trim();
        final int equalsIndex = afterColon.indexOf('=');
        if (equalsIndex < 0) {
            // No = sign, treat as extern let
            return processLetWithoutInitializer(rest, context);
        }
        // Has = sign, treat as regular let
        return Main.parseLetStatement(statement, context);
    }

    /**
     * Processes a let statement without an initializer.
     *
     * @param rest    The rest of the statement after "let "
     * @param context The variable context
     * @return Result of processing the statement
     */
    private static Result<String, String> processLetWithoutInitializer(
            final String rest, final VariableContext context) {
        final Result<ExpressionParser.Tuple<String, String>, String>
                parseResult = Main.parseVarNameAndType(rest, "let statement",
                true);
        if (parseResult instanceof Err<ExpressionParser.Tuple<String, String>,
                String>) {
            return new Err<>(((Err<ExpressionParser.Tuple<String, String>,
                    String>) parseResult).getError());
        }
        final ExpressionParser.Tuple<String, String> tuple =
                ((Ok<ExpressionParser.Tuple<String, String>, String>)
                        parseResult).getValue();
        // Store the variable with empty value
        context.setVariable(tuple.first(), "");
        return new Ok<>("");
    }

    /**
     * Evaluates the condition and appropriate branch of an if expression.
     *
     * @param condition The condition to evaluate
     * @param branches  The branch values
     * @param context   The variable context
     * @return Result with the evaluated branch
     */
    static java.util.Optional<Result<String, String>>
            evaluateIfBranches(final String condition,
            final IfBranches branches, final VariableContext context) {
        // Evaluate condition (with variable substitution)
        final String substitutedCondition = Main.substituteVariables(condition,
                context);
        final Result<String, String> conditionResult =
                Main.interpretExpression(substitutedCondition, context);
        if (conditionResult instanceof Err<String, String>) {
            return java.util.Optional.of(conditionResult);
        }
        final String conditionValue =
                ((Ok<String, String>) conditionResult).getValue();
        if (!"true".equals(conditionValue)
                && !"false".equals(conditionValue)) {
            return java.util.Optional.of(new Err<>(
                    "Condition must be a boolean"));
        }
        // Evaluate the appropriate branch (without variable substitution in
        // the branch itself, as it may contain assignments)
        final String branchValue = "true".equals(conditionValue)
                ? branches.thenValue() : branches.elseValue();
        return java.util.Optional.of(Main.interpretExpression(branchValue,
                context));
    }

    /**
     * Record for if branch values.
     */
    record IfBranches(String thenValue, String elseValue) {
    }
}

