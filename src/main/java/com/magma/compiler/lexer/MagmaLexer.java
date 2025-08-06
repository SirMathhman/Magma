package com.magma.compiler.lexer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the Lexer interface for the Magma language.
 */
public class MagmaLexer implements Lexer {
	private static final Map<String, TokenType> keywords;

	static {
		Map<String, TokenType> keywordsMap = new HashMap<>();
		keywordsMap.put("and", TokenType.AND);
		keywordsMap.put("class", TokenType.CLASS);
		keywordsMap.put("else", TokenType.ELSE);
		keywordsMap.put("false", TokenType.FALSE);
		keywordsMap.put("for", TokenType.FOR);
		keywordsMap.put("fun", TokenType.FUN);
		keywordsMap.put("if", TokenType.IF);
		keywordsMap.put("let", TokenType.LET);
		keywordsMap.put("nil", TokenType.NIL);
		keywordsMap.put("or", TokenType.OR);
		keywordsMap.put("print", TokenType.PRINT);
		keywordsMap.put("return", TokenType.RETURN);
		keywordsMap.put("super", TokenType.SUPER);
		keywordsMap.put("this", TokenType.THIS);
		keywordsMap.put("true", TokenType.TRUE);
		keywordsMap.put("var", TokenType.VAR);
		keywordsMap.put("while", TokenType.WHILE);
		keywords = Collections.unmodifiableMap(keywordsMap);
	}

	private final String source;
	private final List<Token> tokens = new ArrayList<>();
	private int start = 0;
	private int current = 0;
	private int line = 1;
	private int column = 1;
	private int tokenIndex = 0;

	/**
	 * Creates a new lexer for the given source code.
	 *
	 * @param source The source code to tokenize
	 */
	public MagmaLexer(String source) {
		this.source = source;
	}

	@Override
	public List<Token> tokenize() {
		while (!isAtEnd()) {
			// We are at the beginning of the next lexeme
			start = current;
			scanToken();
		}

		tokens.add(new Token(TokenType.EOF, "", null, line, column));
		return Collections.unmodifiableList(tokens);
	}

	@Override
	public Token peek() {
		if (tokenIndex >= tokens.size()) {
			return new Token(TokenType.EOF, "", null, line, column);
		}
		return tokens.get(tokenIndex);
	}

	@Override
	public Token nextToken() {
		if (tokenIndex >= tokens.size()) {
			return new Token(TokenType.EOF, "", null, line, column);
		}
		return tokens.get(tokenIndex++);
	}

	@Override
	public boolean hasMoreTokens() {
		return tokenIndex < tokens.size();
	}

	/**
	 * Scans the next token from the source code.
	 */
	private void scanToken() {
		char c = advance();
		switch (c) {
			// Single-character tokens
			case '(':
				addToken(TokenType.LEFT_PAREN);
				break;
			case ')':
				addToken(TokenType.RIGHT_PAREN);
				break;
			case '{':
				addToken(TokenType.LEFT_BRACE);
				break;
			case '}':
				addToken(TokenType.RIGHT_BRACE);
				break;
			case ',':
				addToken(TokenType.COMMA);
				break;
			case '.':
				addToken(TokenType.DOT);
				break;
			case '-':
				addToken(TokenType.MINUS);
				break;
			case '+':
				addToken(TokenType.PLUS);
				break;
			case ';':
				addToken(TokenType.SEMICOLON);
				break;
			case '*':
				addToken(TokenType.STAR);
				break;
			case ':':
				addToken(TokenType.COLON);
				break;

			// One or two character tokens
			case '!':
				addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
				break;
			case '=':
				addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
				break;
			case '<':
				addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
				break;
			case '>':
				addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
				break;

			// Handle division and comments
			case '/':
				if (match('/')) {
					// A comment goes until the end of the line
					while (peekChar() != '\n' && !isAtEnd()) advance();
				} else {
					addToken(TokenType.SLASH);
				}
				break;

			// Ignore whitespace
			case ' ':
			case '\r':
			case '\t':
				// Update column but ignore the whitespace
				break;

			case '\n':
				line++;
				column = 1;
				break;

			// String literals
			case '"':
				string();
				break;

			default:
				if (isDigit(c)) {
					number();
				} else if (isAlpha(c)) {
					identifier();
				} else {
					// For simplicity, we'll just ignore unrecognized characters
					// In a real compiler, we would report an error
					// Do nothing - the character is already consumed by advance()
				}
				break;
		}
	}

	/**
	 * Processes an identifier or keyword.
	 */
	private void identifier() {
		while (isAlphaNumeric(peekChar())) advance();

		// See if the identifier is a reserved word
		String text = source.substring(start, current);
		TokenType type = keywords.get(text);
		if (type == null) type = TokenType.IDENTIFIER;

		addToken(type);
	}

	/**
	 * Processes a number literal.
	 * All numbers are parsed as Double for consistency with the parser tests.
	 */
	private void number() {
		while (isDigit(peekChar())) advance();

		// Look for a fractional part
		if (peekChar() == '.' && isDigit(peekNext())) {
			// Consume the "."
			advance();
			
			while (isDigit(peekChar())) advance();
		}

		String numberStr = source.substring(start, current);
		// Always parse as Double to match test expectations
		addToken(TokenType.NUMBER, Double.parseDouble(numberStr));
	}

	/**
	 * Processes a string literal.
	 */
	private void string() {
		while (peekChar() != '"' && !isAtEnd()) {
			if (peekChar() == '\n') {
				line++;
				column = 1;
			}
			advance();
		}

		// Unterminated string
		if (isAtEnd()) {
			// In a real compiler, we would report an error
			return;
		}

		// The closing "
		advance();

		// Trim the surrounding quotes
		String value = source.substring(start + 1, current - 1);
		addToken(TokenType.STRING, value);
	}

	/**
	 * Checks if the current character matches the expected character.
	 *
	 * @param expected The expected character
	 * @return true if the current character matches, false otherwise
	 */
	private boolean match(char expected) {
		if (isAtEnd()) return false;
		if (source.charAt(current) != expected) return false;

		current++;
		column++;
		return true;
	}

	/**
	 * Returns the current character without consuming it.
	 *
	 * @return The current character
	 */
	private char peekChar() {
		if (isAtEnd()) return '\0';
		return source.charAt(current);
	}

	/**
	 * Returns the next character without consuming it.
	 *
	 * @return The next character
	 */
	private char peekNext() {
		if (current + 1 >= source.length()) return '\0';
		return source.charAt(current + 1);
	}

	/**
	 * Checks if the given character is alphabetic.
	 *
	 * @param c The character to check
	 * @return true if the character is alphabetic, false otherwise
	 */
	private boolean isAlpha(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
	}

	/**
	 * Checks if the given character is alphanumeric.
	 *
	 * @param c The character to check
	 * @return true if the character is alphanumeric, false otherwise
	 */
	private boolean isAlphaNumeric(char c) {
		return isAlpha(c) || isDigit(c);
	}

	/**
	 * Checks if the given character is a digit.
	 *
	 * @param c The character to check
	 * @return true if the character is a digit, false otherwise
	 */
	private boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}

	/**
	 * Checks if we have reached the end of the source code.
	 *
	 * @return true if we are at the end, false otherwise
	 */
	private boolean isAtEnd() {
		return current >= source.length();
	}

	/**
	 * Consumes the current character and returns it.
	 *
	 * @return The current character
	 */
	private char advance() {
		column++;
		return source.charAt(current++);
	}

	/**
	 * Adds a token with no literal value.
	 *
	 * @param type The token type
	 */
	private void addToken(TokenType type) {
		addToken(type, null);
	}

	/**
	 * Adds a token with a literal value.
	 *
	 * @param type    The token type
	 * @param literal The literal value
	 */
	private void addToken(TokenType type, Object literal) {
		String text = source.substring(start, current);
		tokens.add(new Token(type, text, literal, line, column - text.length()));
	}
}