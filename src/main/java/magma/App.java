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
        java.util.List<OperandParseResult> operands = tokenizeOperands(t);
        if (operands == null || operands.size() < 2)
            return null;
        String commonSuffix = null;
        long total = 0L;
        for (OperandParseResult r : operands) {
            if (r.suffix != null) {
                if (commonSuffix == null)
                    commonSuffix = r.suffix;
                else if (!commonSuffix.equals(r.suffix))
                    return null;
            }
            total += r.value;
        }
        return String.valueOf(total);
    }

    private static class OperandParseResult {
        final long value;
        final String suffix;
        final int nextPos;

        OperandParseResult(long value, String suffix, int nextPos) {
            this.value = value;
            this.suffix = suffix;
            this.nextPos = nextPos;
        }
    }

    private static OperandParseResult parseNumberWithSuffix(String t, int pos) {
        int n = t.length();
        if (pos >= n)
            return null;
        // optional sign as part of the number
        int sign = +1;
        if ((t.charAt(pos) == '+' || t.charAt(pos) == '-') && pos + 1 < n && Character.isDigit(t.charAt(pos + 1))) {
            if (t.charAt(pos) == '-')
                sign = -1;
            pos++;
        }
        int ds = pos;
        while (pos < n && Character.isDigit(t.charAt(pos)))
            pos++;
        if (pos == ds)
            return null;
        String digits = t.substring(ds, pos);
        String suf = null;
        for (String s : ALLOWED_SUFFIXES) {
            if (pos + s.length() <= n && t.startsWith(s, pos)) {
                suf = s;
                pos += s.length();
                break;
            }
        }
        long v;
        try {
            v = Long.parseLong((sign == -1 ? "-" : "") + digits);
        } catch (NumberFormatException ex) {
            return null;
        }
        return new OperandParseResult(v, suf, pos);
    }

    private static java.util.List<OperandParseResult> tokenizeOperands(String t) {
        int n = t.length();
        int pos = 0;
        java.util.List<OperandParseResult> out = new java.util.ArrayList<>();
        boolean expectNumber = true;
        int pendingOp = +1; // operator to apply to next number

        while (pos < n) {
            // skip whitespace
            while (pos < n && Character.isWhitespace(t.charAt(pos)))
                pos++;
            if (pos >= n)
                break;

            if (!expectNumber) {
                // expect an operator
                char c = t.charAt(pos);
                if (c == '+')
                    pendingOp = +1;
                else if (c == '-')
                    pendingOp = -1;
                else
                    return null;
                pos++;
                expectNumber = true;
                continue;
            }

            OperandParseResult r = parseNumberWithSuffix(t, pos);
            if (r == null)
                return null;
            // apply pending operator
            out.add(new OperandParseResult(pendingOp * r.value, r.suffix, r.nextPos));
            pos = r.nextPos;
            expectNumber = false;
            // after first number, ensure we saw operator between numbers; tokenizer will
            // accept sequences like "1 2" as invalid
        }

        return out;
    }

    // helpers removed: parseNumberWithSuffix now handles integer parsing and suffix
    // detection
}
