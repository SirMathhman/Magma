package magma.codegen;

import magma.ast.*;
import magma.lexer.Token;
import magma.lexer.TokenType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
				new Token(TokenType.PLUS, "+", 1, 1), right);
		CCodeGenerator generator = new CCodeGenerator();

		String result = generator.generate(node);

		assertEquals("(3 + 5)", result);
	}

	@Test
	void testGenerateVariableDeclaration() {
		VariableDeclaration decl = new VariableDeclaration("x", true,
				new NumberLiteral(42));
		Program program = new Program(List.of(), List.of(decl));
		CCodeGenerator generator = new CCodeGenerator();

		String result = generator.generate(program);

		assertTrue(result.contains("int x = 42;"));
		assertTrue(result.contains("int main(void)"));
	}

	@Test
	void testGenerateFunctionCall() {
		FunctionCall call = new FunctionCall("printf",
				List.of(new StringLiteral("hello"), new NumberLiteral(42)));
		ExpressionStatement stmt = new ExpressionStatement(call);
		Program program = new Program(List.of(), List.of(stmt));
		CCodeGenerator generator = new CCodeGenerator();

		String result = generator.generate(program);

		assertTrue(result.contains("printf(\"hello\", 42);"));
	}

	@Test
	void testGenerateForLoop() {
		ForLoop loop = new ForLoop("i", true,
				new NumberLiteral(0),
				new NumberLiteral(10),
				new Block(List.of()));
		Program program = new Program(List.of(), List.of(loop));
		CCodeGenerator generator = new CCodeGenerator();

		String result = generator.generate(program);

		assertTrue(result.contains("for (int i = 0; i < 10; i++)"));
	}

	@Test
	void testGenerateImport() {
		ImportStatement imp = new ImportStatement("stdio", true);
		Program program = new Program(List.of(imp), List.of());
		CCodeGenerator generator = new CCodeGenerator();

		String result = generator.generate(program);

		assertTrue(result.contains("#include <stdio.h>"));
	}

	@Test
	void testGenerateArrayIndex() {
		ArrayIndex index = new ArrayIndex(
				new Identifier("array"),
				new NumberLiteral(5));
		ExpressionStatement stmt = new ExpressionStatement(index);
		Program program = new Program(List.of(), List.of(stmt));
		CCodeGenerator generator = new CCodeGenerator();

		String result = generator.generate(program);

		assertTrue(result.contains("array[5];"));
	}
}
