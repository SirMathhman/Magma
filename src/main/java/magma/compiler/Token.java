package magma.compiler;

public final class Token {
    private final TokenType type;
    private final String lexeme;
    private final int position;

    public Token(TokenType type, String lexeme, int position) {
        this.type = type;
        this.lexeme = lexeme;
        this.position = position;
    }

    public TokenType type() {
        return type;
    }

    public String lexeme() {
        return lexeme;
    }

    public int position() {
        return position;
    }

    @Override
    public String toString() {
        return type + "(" + lexeme + ")";
    }
}
