package magma;

public class App {
    public static void main(String[] args) {
        System.out.println("Hello from magma App!");
    }

    public static String greet() {
        return "Hello, magma";
    }

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
    public static String interpret(String input) {
        // Stub: no default behavior implemented yet.
        throw new UnsupportedOperationException("interpret is not implemented");
    }
}
