package magma.codegen;

import magma.ast.BinaryExpression;
import magma.ast.Node;
import magma.ast.NumberLiteral;
import magma.ast.Visitor;
import magma.lexer.TokenType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CCodeGeneratorTest {
	@Test
	void testGenerateNumber() {
		Node node = new NumberLiteral(42);
		CCodeGenerator generator = new CCodeGenerator();

		String result = generator.generate(node);

		assertEquals("42", result);
	}

	@Test
	void testGenerateAddition() {
		Node left = new NumberLiteral(3);
		Node right = new NumberLiteral(5);
		Node node = new BinaryExpression(left,
				new magma.lexer.Token(TokenType.PLUS, "+", 1, 1), right);
		CCodeGenerator generator = new CCodeGenerator();

		String result = generator.generate(node);

		assertEquals("(3 + 5)", result);
	}

	@Test
	void testGenerateComplexExpression() {
		Node left = new NumberLiteral(2);
		Node right = new BinaryExpression(
				new NumberLiteral(3),
				new magma.lexer.Token(TokenType.STAR, "*", 1, 1),
				new NumberLiteral(4));
		Node node = new BinaryExpression(left,
				new magma.lexer.Token(TokenType.PLUS, "+", 1, 1), right);
		CCodeGenerator generator = new CCodeGenerator();

		String result = generator.generate(node);

		assertEquals("(2 + (3 * 4))", result);
	}
}
