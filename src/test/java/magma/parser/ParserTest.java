package magma.parser;

import magma.ast.*;
import magma.lexer.Lexer;
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
		Token semicolon = new Token(TokenType.SEMICOLON, ";", 1, 3);
		Token eof = new Token(TokenType.EOF, "", 1, 4);
		Parser parser = new Parser(List.of(number, semicolon, eof));

		magma.ast.Program program = parser.parse();

		assertEquals(1, program.getStatements().size());
		assertInstanceOf(ExpressionStatement.class, program.getStatements().get(0));
		ExpressionStatement stmt = (ExpressionStatement) program.getStatements().get(0);
		assertInstanceOf(NumberLiteral.class, stmt.getExpression());
		assertEquals(42, ((NumberLiteral) stmt.getExpression()).getValue());
	}

	@Test
	void testParseAddition() {
		Lexer lexer = new Lexer("3 + 5;");
		Parser parser = new Parser(lexer.tokenize());

		magma.ast.Program program = parser.parse();

		assertEquals(1, program.getStatements().size());
		ExpressionStatement stmt = (ExpressionStatement) program.getStatements().get(0);
		assertInstanceOf(BinaryExpression.class, stmt.getExpression());
		BinaryExpression expr = (BinaryExpression) stmt.getExpression();
		assertEquals(TokenType.PLUS, expr.getOperator().type());
	}

	@Test
	void testParseVariableDeclaration() {
		Lexer lexer = new Lexer("let mut x = 42;");
		Parser parser = new Parser(lexer.tokenize());

		magma.ast.Program program = parser.parse();

		assertEquals(1, program.getStatements().size());
		assertInstanceOf(VariableDeclaration.class, program.getStatements().get(0));
		VariableDeclaration decl = (VariableDeclaration) program.getStatements().get(0);
		assertEquals("x", decl.getName());
		assertEquals(true, decl.isMutable());
		assertInstanceOf(NumberLiteral.class, decl.getInitializer());
	}

	@Test
	void testParseFunctionCall() {
		Lexer lexer = new Lexer("printf(\"hello\", 42);");
		Parser parser = new Parser(lexer.tokenize());

		magma.ast.Program program = parser.parse();

		assertEquals(1, program.getStatements().size());
		ExpressionStatement stmt = (ExpressionStatement) program.getStatements().get(0);
		assertInstanceOf(FunctionCall.class, stmt.getExpression());
		FunctionCall call = (FunctionCall) stmt.getExpression();
		assertEquals("printf", call.getName());
		assertEquals(2, call.getArguments().size());
	}

	@Test
	void testParseArrayIndex() {
		Lexer lexer = new Lexer("array[5];");
		Parser parser = new Parser(lexer.tokenize());

		magma.ast.Program program = parser.parse();

		assertEquals(1, program.getStatements().size());
		ExpressionStatement stmt = (ExpressionStatement) program.getStatements().get(0);
		assertInstanceOf(ArrayIndex.class, stmt.getExpression());
		ArrayIndex index = (ArrayIndex) stmt.getExpression();
		assertInstanceOf(Identifier.class, index.getArray());
		assertInstanceOf(NumberLiteral.class, index.getIndex());
	}

	@Test
	void testParseAssignment() {
		Lexer lexer = new Lexer("x = 10;");
		Parser parser = new Parser(lexer.tokenize());

		magma.ast.Program program = parser.parse();

		assertEquals(1, program.getStatements().size());
		assertInstanceOf(Assignment.class, program.getStatements().get(0));
		Assignment assign = (Assignment) program.getStatements().get(0);
		assertInstanceOf(Identifier.class, assign.getTarget());
		assertInstanceOf(NumberLiteral.class, assign.getValue());
	}

	@Test
	void testParseForLoop() {
		Lexer lexer = new Lexer("for (let mut i in 0..10) { let x = i; }");
		Parser parser = new Parser(lexer.tokenize());

		magma.ast.Program program = parser.parse();

		assertEquals(1, program.getStatements().size());
		assertInstanceOf(ForLoop.class, program.getStatements().get(0));
		ForLoop loop = (ForLoop) program.getStatements().get(0);
		assertEquals("i", loop.getVariableName());
		assertEquals(true, loop.isMutable());
		assertInstanceOf(NumberLiteral.class, loop.getStart());
		assertInstanceOf(NumberLiteral.class, loop.getEnd());
	}

	@Test
	void testParseImport() {
		Lexer lexer = new Lexer("import extern stdio; let x = 5;");
		Parser parser = new Parser(lexer.tokenize());

		magma.ast.Program program = parser.parse();

		assertEquals(1, program.getImports().size());
		ImportStatement imp = program.getImports().get(0);
		assertEquals("stdio", imp.getModule());
		assertEquals(true, imp.isExtern());
		assertEquals(1, program.getStatements().size());
	}

	@Test
	void testParseVariableWithTypeAnnotation() {
		Lexer lexer = new Lexer("let mut x : I32 = 42;");
		Parser parser = new Parser(lexer.tokenize());

		magma.ast.Program program = parser.parse();

		assertEquals(1, program.getStatements().size());
		assertInstanceOf(VariableDeclaration.class, program.getStatements().get(0));
		VariableDeclaration decl = (VariableDeclaration) program.getStatements().get(0);
		assertEquals("x", decl.getName());
		assertEquals(true, decl.hasTypeAnnotation());
		assertInstanceOf(NamedType.class, decl.getTypeAnnotation());
		assertEquals("I32", ((NamedType) decl.getTypeAnnotation()).getName());
	}

	@Test
	void testParsePointerType() {
		Lexer lexer = new Lexer("let mut ptr : *I32 = 0;");
		Parser parser = new Parser(lexer.tokenize());

		magma.ast.Program program = parser.parse();

		VariableDeclaration decl = (VariableDeclaration) program.getStatements().get(0);
		assertInstanceOf(PointerType.class, decl.getTypeAnnotation());
		PointerType ptrType = (PointerType) decl.getTypeAnnotation();
		assertInstanceOf(NamedType.class, ptrType.getBaseType());
		assertEquals("I32", ((NamedType) ptrType.getBaseType()).getName());
	}

	@Test
	void testParseArrayType() {
		Lexer lexer = new Lexer("let mut arr : [I32; 100] = 0;");
		Parser parser = new Parser(lexer.tokenize());

		magma.ast.Program program = parser.parse();

		VariableDeclaration decl = (VariableDeclaration) program.getStatements().get(0);
		assertInstanceOf(ArrayType.class, decl.getTypeAnnotation());
		ArrayType arrayType = (ArrayType) decl.getTypeAnnotation();
		assertInstanceOf(NamedType.class, arrayType.getElementType());
		assertEquals("I32", ((NamedType) arrayType.getElementType()).getName());
		assertInstanceOf(NumberLiteral.class, arrayType.getLength());
	}

	@Test
	void testParseSizeOfExpression() {
		Lexer lexer = new Lexer("let mut size = SizeOf<I32> * 100;");
		Parser parser = new Parser(lexer.tokenize());

		magma.ast.Program program = parser.parse();

		VariableDeclaration decl = (VariableDeclaration) program.getStatements().get(0);
		assertInstanceOf(BinaryExpression.class, decl.getInitializer());
		BinaryExpression expr = (BinaryExpression) decl.getInitializer();
		assertInstanceOf(SizeOfExpression.class, expr.getLeft());
		SizeOfExpression sizeof = (SizeOfExpression) expr.getLeft();
		assertInstanceOf(NamedType.class, sizeof.getType());
		assertEquals("I32", ((NamedType) sizeof.getType()).getName());
	}
}
