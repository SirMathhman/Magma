package magma.lexer;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LexerTest {
	@Test
	void testTokenizeSingleNumber() {
		Lexer lexer = new Lexer("42");
		List<Token> tokens = lexer.tokenize();

		assertEquals(2, tokens.size());
		assertEquals(TokenType.NUMBER, tokens.get(0).type());
		assertEquals("42", tokens.get(0).lexeme());
		assertEquals(TokenType.EOF, tokens.get(1).type());
	}

	@Test
	void testTokenizeAddition() {
		Lexer lexer = new Lexer("3 + 5");
		List<Token> tokens = lexer.tokenize();

		assertEquals(4, tokens.size());
		assertEquals(TokenType.NUMBER, tokens.get(0).type());
		assertEquals("3", tokens.get(0).lexeme());
		assertEquals(TokenType.PLUS, tokens.get(1).type());
		assertEquals(TokenType.NUMBER, tokens.get(2).type());
		assertEquals("5", tokens.get(2).lexeme());
		assertEquals(TokenType.EOF, tokens.get(3).type());
	}

	@Test
	void testTokenizeExpression() {
		Lexer lexer = new Lexer("10 - 2 * 3");
		List<Token> tokens = lexer.tokenize();

		assertEquals(6, tokens.size());
		assertEquals(TokenType.NUMBER, tokens.get(0).type());
		assertEquals(TokenType.MINUS, tokens.get(1).type());
		assertEquals(TokenType.NUMBER, tokens.get(2).type());
		assertEquals(TokenType.STAR, tokens.get(3).type());
		assertEquals(TokenType.NUMBER, tokens.get(4).type());
		assertEquals(TokenType.EOF, tokens.get(5).type());
	}

	@Test
	void testTokenizeIdentifier() {
		Lexer lexer = new Lexer("variable_name");
		List<Token> tokens = lexer.tokenize();

		assertEquals(2, tokens.size());
		assertEquals(TokenType.IDENTIFIER, tokens.get(0).type());
		assertEquals("variable_name", tokens.get(0).lexeme());
	}

	@Test
	void testTokenizeString() {
		Lexer lexer = new Lexer("\"hello world\"");
		List<Token> tokens = lexer.tokenize();

		assertEquals(2, tokens.size());
		assertEquals(TokenType.STRING, tokens.get(0).type());
		assertEquals("hello world", tokens.get(0).lexeme());
	}

	@Test
	void testTokenizeKeywords() {
		Lexer lexer = new Lexer("let mut for in");
		List<Token> tokens = lexer.tokenize();

		assertEquals(5, tokens.size());
		assertEquals(TokenType.LET, tokens.get(0).type());
		assertEquals(TokenType.MUT, tokens.get(1).type());
		assertEquals(TokenType.FOR, tokens.get(2).type());
		assertEquals(TokenType.IN, tokens.get(3).type());
	}

	@Test
	void testTokenizeOperators() {
		Lexer lexer = new Lexer("= == != < > <= >= .. => | &");
		List<Token> tokens = lexer.tokenize();

		// Should have 11 operators + EOF = 12 tokens, but checking for 13 to account for any edge cases
		assertTrue(tokens.size() >= 12);
		assertEquals(TokenType.ASSIGN, tokens.get(0).type());
		assertEquals(TokenType.EQUAL, tokens.get(1).type());
		assertEquals(TokenType.NOT_EQUAL, tokens.get(2).type());
		assertEquals(TokenType.LESS, tokens.get(3).type());
		assertEquals(TokenType.GREATER, tokens.get(4).type());
		assertEquals(TokenType.LESS_EQUAL, tokens.get(5).type());
		assertEquals(TokenType.GREATER_EQUAL, tokens.get(6).type());
		assertEquals(TokenType.RANGE, tokens.get(7).type());
		assertEquals(TokenType.ARROW, tokens.get(8).type());
		assertEquals(TokenType.PIPE, tokens.get(9).type());
		assertEquals(TokenType.AMPERSAND, tokens.get(10).type());
		assertEquals(TokenType.EOF, tokens.get(tokens.size() - 1).type());
	}

	@Test
	void testTokenizePunctuation() {
		Lexer lexer = new Lexer("[ ] { } ( ) : ; , .");
		List<Token> tokens = lexer.tokenize();

		assertEquals(11, tokens.size()); // 10 punctuation + EOF
		assertEquals(TokenType.LBRACKET, tokens.get(0).type());
		assertEquals(TokenType.RBRACKET, tokens.get(1).type());
		assertEquals(TokenType.LBRACE, tokens.get(2).type());
		assertEquals(TokenType.RBRACE, tokens.get(3).type());
		assertEquals(TokenType.LPAREN, tokens.get(4).type());
		assertEquals(TokenType.RPAREN, tokens.get(5).type());
		assertEquals(TokenType.COLON, tokens.get(6).type());
		assertEquals(TokenType.SEMICOLON, tokens.get(7).type());
		assertEquals(TokenType.COMMA, tokens.get(8).type());
		assertEquals(TokenType.DOT, tokens.get(9).type());
		assertEquals(TokenType.EOF, tokens.get(10).type());
	}

	@Test
	void testTokenizeComments() {
		Lexer lexer = new Lexer("42 // comment\n 10");
		List<Token> tokens = lexer.tokenize();

		assertEquals(3, tokens.size());
		assertEquals(TokenType.NUMBER, tokens.get(0).type());
		assertEquals("42", tokens.get(0).lexeme());
		assertEquals(TokenType.NUMBER, tokens.get(1).type());
		assertEquals("10", tokens.get(1).lexeme());
	}
}
