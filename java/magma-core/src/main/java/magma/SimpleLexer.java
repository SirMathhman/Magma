package magma;

/** Minimal lexer implementation for stubbed pipeline. */
public class SimpleLexer implements Lexer {
    @Override
    public Token[] tokenize(String source) {
        if (source == null) return new Token[0];
        String s = source.trim();
        // Accept simple integer literal with optional I32 suffix (e.g. "5" or "5I32").
        if (s.matches("^[0-9]+(I32|i32)?$")) {
            return new Token[] { new Token("INT", s) };
        }
        // Fallback: unknown single token
        return new Token[] { new Token("UNKNOWN", s) };
    }
}
