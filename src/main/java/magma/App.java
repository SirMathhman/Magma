package magma;

public class App {

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
        if (t == null)
            return null;
        int plusIndex = -1;
        // find a '+' that is not the first character (to avoid leading sign)
        for (int i = 1; i < t.length(); i++) {
            if (t.charAt(i) == '+') {
                plusIndex = i;
                break;
            }
        }
        if (plusIndex <= 0)
            return null;
        String left = t.substring(0, plusIndex).trim();
        String right = t.substring(plusIndex + 1).trim();
        if (left.isEmpty() || right.isEmpty())
            return null;
        // Validate left and right as integer strings (no regex)
        if (!isIntegerString(left) || !isIntegerString(right))
            return null;
        try {
            long a = Long.parseLong(left);
            long b = Long.parseLong(right);
            long sum = a + b;
            return String.valueOf(sum);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static boolean isIntegerString(String s) {
        if (s == null || s.isEmpty())
            return false;
        int idx = 0;
        if (s.charAt(0) == '+' || s.charAt(0) == '-') {
            if (s.length() == 1)
                return false;
            idx = 1;
        }
        for (int i = idx; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i)))
                return false;
        }
        return true;
    }

}
