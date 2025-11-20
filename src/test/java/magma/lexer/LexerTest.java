package magma.lexer;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
