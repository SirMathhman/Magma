package magma;

public class App {

    private static final String[] ALLOWED_SUFFIXES = new String[] { "U8", "U16", "U32", "U64", "I8", "I16", "I32",
            "I64" };

    /**
     * Interpret a simple command string and return a response.
     * <p>
     * Supported commands (case-insensitive, trimmed):
     * <ul>
     * <li>"hello" → returns the same as {@link #greet()}</li>
     * <li>"ping" → returns "pong"</li>
     * <li>"repeat:&lt;text&gt;" → returns &lt;text&gt; (everything after the first
     * colon)</li>
     * <li>null → returns "null"</li>
     * <li>anything else → returns a default "I don't understand: {input}"
     * message</li>
     * </ul>
     *
     * @param input the command to interpret
     * @return the interpreted response
     */
    public static String interpret(String input) throws InterpretException {
        // If input is null or empty (after trimming) return empty string.
        if (input == null)
            return "";
        String t = input.trim();
        if (t.isEmpty())
            return "";

        // Try parsing statements (let bindings) first
        String stmtResult = parseAndEvaluateStatements(t);
        if (stmtResult != null)
            return stmtResult;

        // Try parsing a simple addition expression like "2 + 3" (no regex).
        String plusResult = parseAndEvaluateAddition(t);
        if (plusResult != null)
            return plusResult;
        int end = parseNumericPrefixEnd(t);
        if (end > 0) {
            if (end == t.length())
                return t.substring(0, end);
            String suffix = t.substring(end);
            if (isAllowedSuffix(suffix))
                return t.substring(0, end);
            throw new InterpretException("No interpretation available for: " + input);
        }

        // Otherwise, there is no default behavior yet — throw a checked exception.
        throw new InterpretException("No interpretation available for: " + input);
    }

    // Return the index just after the last digit in the leading numeric prefix,
    // or -1 if there is no leading digit sequence. Accepts an optional leading +/-.
    private static int parseNumericPrefixEnd(String t) {
        if (t == null || t.isEmpty())
            return -1;
        int idx = 0;
        if (t.charAt(0) == '+' || t.charAt(0) == '-') {
            idx = 1;
            if (t.length() == 1)
                return -1; // just a sign
        }
        int start = idx;
        while (idx < t.length() && Character.isDigit(t.charAt(idx)))
            idx++;
        return (idx - start) > 0 ? idx : -1;
    }

    private static boolean isAllowedSuffix(String s) {
        if (s == null)
            return false;
        switch (s) {
            case "U8":
            case "U16":
            case "U32":
            case "U64":
            case "I8":
            case "I16":
            case "I32":
            case "I64":
                return true;
            default:
                return false;
        }
    }

    /**
     * If the input is a simple addition expression (left <op> right) with a single
     * '+',
     * where left and right are integers (optional +/-), returns the sum as string.
     * Otherwise returns null.
     */
    private static String parseAndEvaluateAddition(String t) {
        if (t == null || t.isEmpty())
            return null;
        // If the input contains parentheses, use the recursive parser.
        if (t.indexOf('(') >= 0 || t.indexOf(')') >= 0) {
            ExpressionUtils.ExprParser parser = new ExpressionUtils.ExprParser(t);
            try {
                long v = parser.parseExpression();
                parser.skipWhitespace();
                if (!parser.isAtEnd())
                    return null;
                return String.valueOf(v);
            } catch (IllegalArgumentException ex) {
                return null;
            }
        }

        ExpressionUtils.ExpressionTokens tokens = tokenizeExpression(t);
        if (tokens == null || tokens.operands.size() < 1)
            return null;
        // If there are no operators, this is a plain number (possibly signed) and
        // should be handled by numeric-prefix logic so we return null here.
        if (tokens.operators.isEmpty())
            return null;

        // enforce suffix consistency across operands (if present)
        if (!suffixesConsistent(tokens))
            return null;

        // First, apply multiplication (higher precedence) via helper to reduce
        // complexity.
        ExpressionUtils.Reduction red = reduceMultiplications(tokens);
        // Now evaluate + and - left to right using reduced lists
        long result = red.values.get(0);
        for (int i = 0; i < red.ops.size(); i++) {
            char op = red.ops.get(i);
            long v = red.values.get(i + 1);
            if (op == '+')
                result = result + v;
            else
                result = result - v;
        }
        return String.valueOf(result);
    }

    // delegate parsing helpers to ExpressionUtils
    private static ExpressionUtils.Reduction reduceMultiplications(ExpressionUtils.ExpressionTokens tokens) {
        return ExpressionUtils.reduceMultiplications(tokens);
    }

    private static boolean suffixesConsistent(ExpressionUtils.ExpressionTokens tokens) {
        return ExpressionUtils.suffixesConsistent(tokens);
    }

    private static ExpressionUtils.ExpressionTokens tokenizeExpression(String t) {
        return ExpressionUtils.tokenizeExpression(t, ALLOWED_SUFFIXES);
    }

    // Very small statements evaluator: supports sequences like
    // "let x : I32 = 1; x" and returns the value of the last expression as string.
    private static String parseAndEvaluateStatements(String t) {
        if (t == null)
            return null;
        if (!t.contains("let") && !t.contains(";"))
            return null;
        String[] parts = t.split(";");
        java.util.Map<String, Long> env = new java.util.HashMap<>();
        for (int i = 0; i < parts.length; i++) {
            String stmt = parts[i].trim();
            if (stmt.isEmpty())
                continue;
            Long maybeValue = evaluateStatement(stmt, env);
            if (maybeValue == null)
                continue; // let-binding handled, continue
            // if it's an expression value, determine if this is the last non-empty part
            boolean anyLater = false;
            for (int k = i + 1; k < parts.length; k++)
                if (!parts[k].trim().isEmpty()) {
                    anyLater = true;
                    break;
                }
            if (!anyLater)
                return String.valueOf(maybeValue.longValue());
        }
        return null;
    }

    // Evaluate a single statement. Returns null for let-binding statements (no
    // result),
    // or the evaluated numeric value for an expression statement.
    private static Long evaluateStatement(String stmt, java.util.Map<String, Long> env) {
        if (stmt.startsWith("let")) {
            if (!parseLetStatement(stmt, env))
                throw new IllegalArgumentException("Invalid let");
            return null;
        }
        // expression
        return Long.valueOf(evaluateExprWithEnv(stmt, env));
    }

    private static long evaluateExprWithEnv(String expr, java.util.Map<String, Long> env) {
        ExpressionUtils.ExprParser p = new ExpressionUtils.ExprParser(expr);
        long v = p.parseExpressionWithResolver(new ExpressionUtils.ExprParser.VarResolver() {
            public long resolve(String name) {
                Long val = env.get(name);
                if (val == null)
                    throw new IllegalArgumentException("Unknown var");
                return val.longValue();
            }
        });
        p.skipWhitespace();
        if (!p.isAtEnd())
            throw new IllegalArgumentException("Trailing data");
        return v;
    }

    private static boolean parseLetStatement(String stmt, java.util.Map<String, Long> env) {
        String after = stmt.substring(3).trim();
        // split on '=' first to separate LHS and RHS
        int eq = after.indexOf('=');
        if (eq < 0)
            return false;
        String lhs = after.substring(0, eq).trim();
        String rhs = after.substring(eq + 1).trim();
        if (rhs.isEmpty())
            return false;
        // lhs should be like: <ident> or <ident> : <type>
        String[] lhsParts = lhs.split(":");
        String ident = lhsParts[0].trim();
        IdParseResult id = parseIdentifier(ident, 0);
        if (id == null || id.next != ident.length())
            return false;
        String name = id.name;
        if (lhsParts.length > 1) {
            String type = lhsParts[1].trim();
            if (!isAllowedSuffix(type))
                return false;
        }
        try {
            long val = evaluateExprWithEnv(rhs, env);
            env.put(name, val);
            return true;
        } catch (RuntimeException ex) {
            return false;
        }
    }

    private static class IdParseResult {
        final String name;
        final int next;

        IdParseResult(String name, int next) {
            this.name = name;
            this.next = next;
        }
    }

    private static IdParseResult parseIdentifier(String s, int pos) {
        int n = s.length();
        int i = pos;
        while (i < n && Character.isWhitespace(s.charAt(i)))
            i++;
        if (i >= n || !Character.isJavaIdentifierStart(s.charAt(i)))
            return null;
        int start = i;
        i++;
        while (i < n && Character.isJavaIdentifierPart(s.charAt(i)))
            i++;
        return new IdParseResult(s.substring(start, i), i);
    }

    // Extend ExprParser with a resolver-capable expression entry point
    // We'll add a small interface and method via insertion into ExprParser below.
}
