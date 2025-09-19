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
        // Remove a leading top-level brace block if present (e.g. "{} 100" -> "100")
        t = StatementEvaluator.stripLeadingBraceBlock(t);
        if (t.isEmpty())
            return "";

        String runRes = EvaluatorDispatcher.run(t, input);
        if (runRes != null)
            return runRes;

        // Otherwise, there is no default behavior yet — throw a checked exception.
        throw new InterpretException("No interpretation available for: " + input);
    }

    static String tryEvalBooleanLiteral(String t) {
        if (t.equalsIgnoreCase("true"))
            return "true";
        if (t.equalsIgnoreCase("false"))
            return "false";
        return null;
    }

    static String tryEvalNumericPrefix(String t, String input) throws InterpretException {
        int end = parseNumPrefixEnd(t);
        if (end > 0) {
            if (end == t.length())
                return t.substring(0, end);
            String suffix = t.substring(end);
            if (isAllowedSuffix(suffix))
                return t.substring(0, end);
            throw new InterpretException("No interpretation available for: " + input);
        }
        return null;
    }

    static String tryEvalEquality(String t) {
        int depth = 0;
        for (int i = 0; i < t.length() - 1; i++) {
            char c = t.charAt(i);
            if (c == '(' || c == '{')
                depth++;
            else if (c == ')' || c == '}')
                depth--;
            else if (depth == 0 && c == '=' && t.charAt(i + 1) == '=') {
                String left = t.substring(0, i).trim();
                String right = t.substring(i + 2).trim();
                return evaluateEquality(left, right);
            }
        }
        return null;
    }

    private static String evaluateEquality(String left, String right) {
        // boolean equality
        if ((left.equalsIgnoreCase("true") || left.equalsIgnoreCase("false"))
                && (right.equalsIgnoreCase("true") || right.equalsIgnoreCase("false"))) {
            return String.valueOf(left.equalsIgnoreCase(right)).toLowerCase();
        }
        // numeric equality
        try {
            ExpressionParser pl = new ExpressionParser(left);
            long lv = pl.parseExpression();
            pl.skipWhitespace();
            if (!pl.isAtEnd())
                return null;
            ExpressionParser pr = new ExpressionParser(right);
            long rv = pr.parseExpression();
            pr.skipWhitespace();
            if (!pr.isAtEnd())
                return null;
            return String.valueOf(lv == rv);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    // Return the index just after the last digit in the leading numeric prefix,
    // or -1 if there is no leading digit sequence. Accepts an optional leading +/-.
    private static int parseNumPrefixEnd(String t) {
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
    static String parseAddEval(String t) {
        if (t == null || t.isEmpty())
            return null;
        // If the input contains parentheses or braces, use the recursive parser.
        if (t.indexOf('(') >= 0 || t.indexOf(')') >= 0 || t.indexOf('{') >= 0 || t.indexOf('}') >= 0) {
            ExpressionParser parser = new ExpressionParser(t);
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

        ExpressionTypes.ExpressionTokens tokens = ExpressionUtils.tokenizeExpression(t, ALLOWED_SUFFIXES);
        if (tokens == null || tokens.operands.size() < 1)
            return null;
        // If there are no operators, this is a plain number (possibly signed) and
        // should be handled by numeric-prefix logic so we return null here.
        if (tokens.operators.isEmpty())
            return null;

        // enforce suffix consistency across operands (if present)
        if (!ExpressionUtils.suffixesConsistent(tokens))
            return null;

        // First, apply multiplication (higher precedence) via helper to reduce
        // complexity.
        ExpressionTypes.Reduction red = ExpressionUtils.reduceMult(tokens);
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
}
