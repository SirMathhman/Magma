package magma.compile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class Lexer {
    public record Result(List<Token> tokens, List<String> errors) {
    }

    private static final Map<Character, TokenType> SIMPLE_TOKENS = Map.of(
            '(', TokenType.LEFT_PAREN,
            ')', TokenType.RIGHT_PAREN,
            '{', TokenType.LEFT_BRACE,
            '}', TokenType.RIGHT_BRACE,
            ':', TokenType.COLON,
            ',', TokenType.COMMA,
            ';', TokenType.SEMICOLON,
            '<', TokenType.LESS,
            '.', TokenType.DOT);

    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private final List<String> errors = new ArrayList<>();
    private int start = 0;
    private int current = 0;

    public Lexer(String source) {
        this.source = Objects.requireNonNullElse(source, "");
    }

    public Result lex() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }
        tokens.add(new Token(TokenType.EOF, "", source.length()));
        return new Result(tokens, errors);
    }

    private void scanToken() {
        char c = advance();
        if (isWhitespace(c)) {
            return;
        }
        if (handleSimpleToken(c)) {
            return;
        }
        if (handleOperatorToken(c)) {
            return;
        }
        if (isDigit(c)) {
            number();
            return;
        }
        if (isAlpha(c)) {
            identifier();
            return;
        }
        errors.add("Unexpected character '" + c + "' at position " + (current - 1));
    }

    private boolean isWhitespace(char c) {
        return c == ' ' || c == '\r' || c == '\t' || c == '\n';
    }

    private boolean handleSimpleToken(char c) {
        if (SIMPLE_TOKENS.containsKey(c)) {
            addToken(SIMPLE_TOKENS.get(c));
            return true;
        }
        return false;
    }

    private boolean handleOperatorToken(char c) {
			return switch (c) {
				case '+' -> {
					handlePlusToken();
					yield true;
				}
				case '-' -> {
					handleMinusToken();
					yield true;
				}
				case '*' -> {
					handleStarToken();
					yield true;
				}
				case '/' -> {
					handleSlashToken();
					yield true;
				}
				case '=' -> {
					handleEqualToken();
					yield true;
				}
				case '&' -> {
					addToken(TokenType.AMPERSAND);
					yield true;
				}
				default -> false;
			};
    }

    private void handlePlusToken() {
        if (match('+')) {
            addToken(TokenType.INCREMENT);
        } else if (match('=')) {
            addToken(TokenType.PLUS_EQUAL);
        } else {
            addToken(TokenType.PLUS);
        }
    }

    private void handleMinusToken() {
        if (match('=')) {
            addToken(TokenType.MINUS_EQUAL);
        } else if (match('>')) {
            addToken(TokenType.ARROW);
        } else {
            addToken(TokenType.MINUS);
        }
    }

    private void handleStarToken() {
        if (match('=')) {
            addToken(TokenType.STAR_EQUAL);
        } else {
            addToken(TokenType.STAR);
        }
    }

    private void handleSlashToken() {
        if (match('/')) {
            skipLineComment();
        } else if (match('=')) {
            addToken(TokenType.SLASH_EQUAL);
        } else {
            addToken(TokenType.SLASH);
        }
    }

    private void handleEqualToken() {
        if (match('=')) {
            addToken(TokenType.DOUBLE_EQUAL);
        } else if (match('>')) {
            addToken(TokenType.ARROW);
        } else {
            addToken(TokenType.EQUAL);
        }
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) {
            advance();
        }
        String text = source.substring(start, current);
        addToken(keywordType(text));
    }

    private void number() {
        while (isDigit(peek())) {
            advance();
        }
        addToken(TokenType.NUMBER);
    }

    private void skipLineComment() {
        while (peek() != '\n' && !isAtEnd()) {
            advance();
        }
    }

    private TokenType keywordType(String text) {
        return switch (text) {
            case "intrinsic" -> TokenType.INTRINSIC;
            case "fn" -> TokenType.FN;
            case "let" -> TokenType.LET;
            case "mut" -> TokenType.MUT;
            case "if" -> TokenType.IF;
            case "else" -> TokenType.ELSE;
            case "while" -> TokenType.WHILE;
            case "return" -> TokenType.RETURN;
            case "true" -> TokenType.TRUE;
            case "false" -> TokenType.FALSE;
            case "I32" -> TokenType.I32;
            case "Bool" -> TokenType.BOOL;
            case "Void" -> TokenType.VOID;
            case "struct" -> TokenType.STRUCT;
            default -> TokenType.IDENTIFIER;
        };
    }

    private boolean match(char expected) {
        if (isAtEnd()) {
            return false;
        }
        if (source.charAt(current) != expected) {
            return false;
        }
        current++;
        return true;
    }

    private char advance() {
        return source.charAt(current++);
    }

    private char peek() {
        if (isAtEnd()) {
            return '\0';
        }
        return source.charAt(current);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private void addToken(TokenType type) {
        String lexeme = source.substring(start, current);
        tokens.add(new Token(type, lexeme, start));
    }
}
