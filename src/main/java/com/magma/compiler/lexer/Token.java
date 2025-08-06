package com.magma.compiler.lexer;

/**
 * Represents a token in the source code.
 * A token is the smallest unit of meaning in a program.
 */
public class Token {
    private final TokenType type;
    private final String lexeme;
    private final Object literal;
    private final int line;
    private final int column;

    /**
     * Creates a new token.
     *
     * @param type    The type of the token
     * @param lexeme  The actual text of the token
     * @param literal The literal value of the token (if applicable)
     * @param line    The line number where the token appears
     * @param column  The column number where the token starts
     */
    public Token(TokenType type, String lexeme, Object literal, int line, int column) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
        this.column = column;
    }

    /**
     * Gets the type of this token.
     *
     * @return The token type
     */
    public TokenType getType() {
        return type;
    }

    /**
     * Gets the lexeme (actual text) of this token.
     *
     * @return The lexeme
     */
    public String getLexeme() {
        return lexeme;
    }

    /**
     * Gets the literal value of this token, if any.
     *
     * @return The literal value, or null if not applicable
     */
    public Object getLiteral() {
        return literal;
    }

    /**
     * Gets the line number where this token appears.
     *
     * @return The line number
     */
    public int getLine() {
        return line;
    }

    /**
     * Gets the column number where this token starts.
     *
     * @return The column number
     */
    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return String.format("Token{type=%s, lexeme='%s', literal=%s, line=%d, column=%d}",
                type, lexeme, literal, line, column);
    }
}