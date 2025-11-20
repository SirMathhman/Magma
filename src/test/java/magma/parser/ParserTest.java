package magma.parser;

import magma.ast.BinaryExpression;
import magma.ast.Node;
import magma.ast.NumberLiteral;
import magma.lexer.Token;
import magma.lexer.TokenType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ParserTest {
	@Test
	void testParseSingleNumber() {
		Token number = new Token(TokenType.NUMBER, "42", 1, 1);
		Token eof = new Token(TokenType.EOF, "", 1, 3);
		Parser parser = new Parser(List.of(number, eof));

		Node result = parser.parse();

		assertInstanceOf(NumberLiteral.class, result);
		assertEquals(42, ((NumberLiteral) result).getValue());
	}

	@Test
	void testParseAddition() {
		Token num1 = new Token(TokenType.NUMBER, "3", 1, 1);
		Token plus = new Token(TokenType.PLUS, "+", 1, 3);
		Token num2 = new Token(TokenType.NUMBER, "5", 1, 5);
		Token eof = new Token(TokenType.EOF, "", 1, 6);
		Parser parser = new Parser(List.of(num1, plus, num2, eof));

		Node result = parser.parse();

		assertInstanceOf(BinaryExpression.class, result);
		BinaryExpression expr = (BinaryExpression) result;
		assertEquals(TokenType.PLUS, expr.getOperator().type());
		assertInstanceOf(NumberLiteral.class, expr.getLeft());
		assertInstanceOf(NumberLiteral.class, expr.getRight());
	}

	@Test
	void testParseMultiplicationPrecedence() {
		Token num1 = new Token(TokenType.NUMBER, "2", 1, 1);
		Token plus = new Token(TokenType.PLUS, "+", 1, 3);
		Token num2 = new Token(TokenType.NUMBER, "3", 1, 5);
		Token star = new Token(TokenType.STAR, "*", 1, 7);
		Token num3 = new Token(TokenType.NUMBER, "4", 1, 9);
		Token eof = new Token(TokenType.EOF, "", 1, 10);
		Parser parser = new Parser(List.of(num1, plus, num2, star, num3, eof));

		Node result = parser.parse();

		assertInstanceOf(BinaryExpression.class, result);
		BinaryExpression expr = (BinaryExpression) result;
		assertEquals(TokenType.PLUS, expr.getOperator().type());
		assertInstanceOf(NumberLiteral.class, expr.getLeft());
		assertInstanceOf(BinaryExpression.class, expr.getRight());
		BinaryExpression rightExpr = (BinaryExpression) expr.getRight();
		assertEquals(TokenType.STAR, rightExpr.getOperator().type());
	}
}
