package magma.lexer;

public enum TokenType {
	// Literals
	NUMBER,
	STRING,
	IDENTIFIER,

	// Keywords
	LET,
	MUT,
	FOR,
	IN,
	IMPORT,
	EXTERN,
	FN,
	TYPE,
	IMPL,
	TRAIT,
	INTRINSIC,
	IF,
	ELSE,
	RETURN,
	VOID,
	SIZEOF,

	// Operators
	ASSIGN,        // =
	EQUAL,         // ==
	NOT_EQUAL,     // !=
	LESS,          // <
	GREATER,       // >
	LESS_EQUAL,    // <=
	GREATER_EQUAL, // >=
	RANGE,         // ..
	PIPE,          // |
	ARROW,         // =>
	AMPERSAND,     // &
	PLUS,          // +
	MINUS,         // -
	STAR,          // *
	SLASH,         // /

	// Punctuation
	LBRACKET,  // [
	RBRACKET,  // ]
	LBRACE,    // {
	RBRACE,    // }
	LPAREN,    // (
	RPAREN,    // )
	LT,        // < (for generics)
	GT,        // > (for generics)
	COLON,     // :
	SEMICOLON, // ;
	COMMA,     // ,
	DOT,       // .

	EOF
}
