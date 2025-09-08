package magma.simple;

import magma.Lexer;
import magma.Token;

/** Minimal lexer implementation for stubbed pipeline. */
public class SimpleLexer implements Lexer {
    @Override
    public Token[] tokenize(String source) {
        if (source == null) return new Token[0];
        String s = source.trim();
        // Recognize a function definition (very small subset) and return as a single FNDEF token.
        // This lets the SimpleParser decide how to handle functions like:
        // fn main() : I32 => { return 42; }
        if (s.startsWith("fn ")) {
            return new Token[] { new Token("FNDEF", s) };
        }
        // Accept simple integer literal with optional I32 suffix (e.g. "5" or "5I32").
        // Also accept a binary addition expression like "5+3" or "5 + 3".
        String noSpaces = s.replaceAll("\\s+", "");
        if (noSpaces.matches("^[0-9]+\\+[0-9]+(I32|i32)?$")) {
            // split into three tokens: INT, PLUS, INT
            int plusIdx = noSpaces.indexOf('+');
            String a = noSpaces.substring(0, plusIdx);
            String b = noSpaces.substring(plusIdx + 1);
            return new Token[] { new Token("INT", a), new Token("PLUS", "+"), new Token("INT", b) };
        }
        if (s.matches("^[0-9]+(I32|i32)?$")) {
            return new Token[] { new Token("INT", s) };
        }
        // Fallback: unknown single token
        return new Token[] { new Token("UNKNOWN", s) };
    }
}
