package magma;

/**
 * Lexer interface and a minimal stub implementation will live here.
 * See docs/architecture.md for design notes.
 */
public interface Lexer {
	Token[] tokenize(String source);
}
