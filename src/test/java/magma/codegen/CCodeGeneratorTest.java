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
		Program program = new Program(List.of(), List.of(), List.of(), List.of(), List.of(decl));
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
		Program program = new Program(List.of(), List.of(), List.of(), List.of(), List.of(stmt));
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
		Program program = new Program(List.of(), List.of(), List.of(), List.of(), List.of(loop));
		CCodeGenerator generator = new CCodeGenerator();

		String result = generator.generate(program);

		assertTrue(result.contains("for (int i = 0; i < 10; i++)"));
	}

	@Test
	void testGenerateImport() {
		ImportStatement imp = new ImportStatement("stdio", true);
		Program program = new Program(List.of(imp), List.of(), List.of(), List.of(), List.of());
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
		Program program = new Program(List.of(), List.of(), List.of(), List.of(), List.of(stmt));
		CCodeGenerator generator = new CCodeGenerator();

		String result = generator.generate(program);

		assertTrue(result.contains("array[5];"));
	}

	@Test
	void testGenerateVariableWithTypeAnnotation() {
		VariableDeclaration decl = new VariableDeclaration("x", true,
				new NamedType("I32"),
				new NumberLiteral(42));
		Program program = new Program(List.of(), List.of(), List.of(), List.of(), List.of(decl));
		CCodeGenerator generator = new CCodeGenerator();

		String result = generator.generate(program);

		assertTrue(result.contains("int32_t x = 42;"));
		assertTrue(result.contains("#include <stdint.h>"));
	}

	@Test
	void testGeneratePointerType() {
		PointerType ptrType = new PointerType(new NamedType("I32"));
		VariableDeclaration decl = new VariableDeclaration("ptr", true,
				ptrType,
				new NumberLiteral(0));
		Program program = new Program(List.of(), List.of(), List.of(), List.of(), List.of(decl));
		CCodeGenerator generator = new CCodeGenerator();

		String result = generator.generate(program);

		assertTrue(result.contains("int32_t* ptr = 0;"));
	}

	@Test
	void testGenerateArrayType() {
		ArrayType arrayType = new ArrayType(
				new NamedType("I32"),
				new NumberLiteral(100));
		VariableDeclaration decl = new VariableDeclaration("arr", true,
				arrayType,
				new NumberLiteral(0));
		Program program = new Program(List.of(), List.of(), List.of(), List.of(), List.of(decl));
		CCodeGenerator generator = new CCodeGenerator();

		String result = generator.generate(program);

		assertTrue(result.contains("int32_t arr[100] = 0;"));
	}

	@Test
	void testGenerateSizeOfExpression() {
		SizeOfExpression sizeof = new SizeOfExpression(new NamedType("I32"));
		BinaryExpression expr = new BinaryExpression(sizeof,
				new Token(TokenType.STAR, "*", 1, 1),
				new NumberLiteral(100));
		VariableDeclaration decl = new VariableDeclaration("size", true, expr);
		Program program = new Program(List.of(), List.of(), List.of(), List.of(), List.of(decl));
		CCodeGenerator generator = new CCodeGenerator();

		String result = generator.generate(program);

		assertTrue(result.contains("sizeof(int32_t) * 100"));
	}

	@Test
	void testGenerateFunctionDefinition() {
		FunctionParameter param1 = new FunctionParameter("x", new NamedType("I32"));
		FunctionParameter param2 = new FunctionParameter("y", new NamedType("I32"));
		Block body = new Block(List.of(
				new ExpressionStatement(new BinaryExpression(
						new Identifier("x"),
						new Token(TokenType.PLUS, "+", 1, 1),
						new Identifier("y")))));
		FunctionDefinition fn = new FunctionDefinition("add",
				List.of(param1, param2),
				new NamedType("I32"),
				body);
		Program program = new Program(List.of(), List.of(), List.of(fn), List.of(), List.of());
		CCodeGenerator generator = new CCodeGenerator();

		String result = generator.generate(program);

		assertTrue(result.contains("int32_t add(int32_t x, int32_t y)"));
		assertTrue(result.contains("(x + y);"));
	}

	@Test
	void testGenerateExternFunctionDeclaration() {
		FunctionParameter param = new FunctionParameter("format",
				new PointerType(new ArrayType(new NamedType("U8"), new NumberLiteral(0))));
		ExternFunctionDeclaration externFn = new ExternFunctionDeclaration("printf",
				List.of(param),
				new NamedType("Void"));
		Program program = new Program(List.of(), List.of(), List.of(), List.of(externFn), List.of());
		CCodeGenerator generator = new CCodeGenerator();

		String result = generator.generate(program);

		// Extern functions should emit whitespace only
		assertTrue(result.contains(" "));
		assertTrue(!result.contains("extern void printf"));
	}

	@Test
	void testGenerateTypeDefinition() {
		// Type definitions don't generate C code, they're compile-time aliases
		TypeDefinition typeDef = new TypeDefinition("MyInt", new NamedType("I32"));
		Program program = new Program(List.of(), List.of(typeDef), List.of(), List.of(), List.of());
		CCodeGenerator generator = new CCodeGenerator();

		String result = generator.generate(program);

		// Type definitions should not appear in generated code
		assertTrue(!result.contains("MyInt"));
	}

	@Test
	void testGenerateWithTypeAlias() {
		// Test that type aliases are resolved when used
		TypeDefinition typeDef = new TypeDefinition("MyInt", new NamedType("I32"));
		VariableDeclaration decl = new VariableDeclaration("x", false, new NamedType("MyInt"), new NumberLiteral(42));
		Program program = new Program(List.of(), List.of(typeDef), List.of(), List.of(), List.of(decl));
		CCodeGenerator generator = new CCodeGenerator();

		String result = generator.generate(program);

		// The type alias should be resolved to int32_t
		assertTrue(result.contains("int32_t x = 42;"));
	}

	@Test
	void testGenerateIfExpression() {
		// let x = if (true) 3 else 5;
		IfExpression ifExpr = new IfExpression(
			new NumberLiteral(1),
			new NumberLiteral(3),
			new NumberLiteral(5)
		);
		VariableDeclaration decl = new VariableDeclaration("x", false, null, ifExpr);
		Program program = new Program(List.of(), List.of(), List.of(), List.of(), List.of(decl));
		CCodeGenerator generator = new CCodeGenerator();

		String result = generator.generate(program);

		// Should generate ternary operator
		assertTrue(result.contains("x ="));
		assertTrue(result.contains("(1) ? (3) : (5)"));
	}

	@Test
	void testGenerateIfStatement() {
		// if (true) x = 3; else x = 5;
		Assignment thenAssign = new Assignment(new Identifier("x"), new NumberLiteral(3));
		Assignment elseAssign = new Assignment(new Identifier("x"), new NumberLiteral(5));
		IfStatement ifStmt = new IfStatement(
			new NumberLiteral(1),
			thenAssign,
			elseAssign
		);
		Program program = new Program(List.of(), List.of(), List.of(), List.of(), List.of(ifStmt));
		CCodeGenerator generator = new CCodeGenerator();

		String result = generator.generate(program);

		// Should generate if/else blocks
		assertTrue(result.contains("if (1) {"));
		assertTrue(result.contains("x = 3;"));
		assertTrue(result.contains("} else {"));
		assertTrue(result.contains("x = 5;"));
	}

	@Test
	void testGenerateIfStatementWithoutElse() {
		// if (true) x = 3;
		Assignment thenAssign = new Assignment(new Identifier("x"), new NumberLiteral(3));
		IfStatement ifStmt = new IfStatement(
			new NumberLiteral(1),
			thenAssign
		);
		Program program = new Program(List.of(), List.of(), List.of(), List.of(), List.of(ifStmt));
		CCodeGenerator generator = new CCodeGenerator();

		String result = generator.generate(program);

		// Should generate if block without else
		assertTrue(result.contains("if (1) {"));
		assertTrue(result.contains("x = 3;"));
		assertTrue(!result.contains("else"));
	}
}
