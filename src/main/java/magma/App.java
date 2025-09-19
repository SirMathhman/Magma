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
        if (t.isEmpty()) return "";

        // If the trimmed input is a numeric string (digits, optionally prefixed with + or -), return it.
        int start = 0;
        if (t.charAt(0) == '+' || t.charAt(0) == '-') {
            if (t.length() == 1) {
                // Just a sign, not a number.
                throw new InterpretException("No interpretation available for: " + input);
            }
            start = 1;
        }
        boolean allDigits = true;
        for (int i = start; i < t.length(); i++) {
            if (!Character.isDigit(t.charAt(i))) {
                allDigits = false;
                break;
            }
        }
        if (allDigits) return t;

        // Otherwise, there is no default behavior yet — throw a checked exception.
        throw new InterpretException("No interpretation available for: " + input);
    }
}
