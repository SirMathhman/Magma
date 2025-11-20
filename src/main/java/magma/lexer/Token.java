package magma.lexer;

public record Token(TokenType type, String lexeme, int line, int column) {
}
