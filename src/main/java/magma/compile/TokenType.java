package magma.compile;

public enum TokenType {
    // Special
    EOF,

    // Identifiers and literals
    IDENTIFIER,
    NUMBER,

    // Keywords
    INTRINSIC,
    FN,
    LET,
    MUT,
    IF,
    ELSE,
    WHILE,
    RETURN,
    TRUE,
    FALSE,
    I32,
    BOOL,
    VOID,
    STRUCT,

    // Symbols
    LEFT_PAREN,
    RIGHT_PAREN,
    LEFT_BRACE,
    RIGHT_BRACE,
    COLON,
    COMMA,
    SEMICOLON,
    DOT,

    // Operators
    PLUS,
    MINUS,
    STAR,
    SLASH,
    EQUAL,
    AMPERSAND,
    DOUBLE_EQUAL,
    ARROW,
    DOUBLE_ARROW,
    PLUS_EQUAL,
    MINUS_EQUAL,
    STAR_EQUAL,
    SLASH_EQUAL,
    LESS,
    INCREMENT
}
