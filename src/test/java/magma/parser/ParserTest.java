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

	@Test
	void testParseFunctionDefinition() {
		Lexer lexer = new Lexer("fn add(x : I32, y : I32) : I32 { let result = x + y; }");
		Parser parser = new Parser(lexer.tokenize());

		magma.ast.Program program = parser.parse();

		assertEquals(1, program.getFunctions().size());
		FunctionDefinition fn = program.getFunctions().get(0);
		assertEquals("add", fn.getName());
		assertEquals(2, fn.getParameters().size());
		assertEquals("x", fn.getParameters().get(0).getName());
		assertEquals("y", fn.getParameters().get(1).getName());
		assertEquals(true, fn.hasReturnType());
		assertInstanceOf(NamedType.class, fn.getReturnType());
		assertEquals("I32", ((NamedType) fn.getReturnType()).getName());
		assertInstanceOf(Block.class, fn.getBody());
	}

	@Test
	void testParseFunctionDefinitionArrowSyntax() {
		Lexer lexer = new Lexer("fn square(x : I32) => x * x;");
		Parser parser = new Parser(lexer.tokenize());

		magma.ast.Program program = parser.parse();

		assertEquals(1, program.getFunctions().size());
		FunctionDefinition fn = program.getFunctions().get(0);
		assertEquals("square", fn.getName());
		assertEquals(1, fn.getParameters().size());
		assertEquals(false, fn.hasReturnType());
		assertInstanceOf(Block.class, fn.getBody());
	}

	@Test
	void testParseExternFunctionDeclaration() {
		Lexer lexer = new Lexer("extern fn printf(format : I32) : I32;");
		Parser parser = new Parser(lexer.tokenize());

		magma.ast.Program program = parser.parse();

		assertEquals(1, program.getExternFunctions().size());
		ExternFunctionDeclaration externFn = program.getExternFunctions().get(0);
		assertEquals("printf", externFn.getName());
		assertEquals(1, externFn.getParameters().size());
		assertEquals("format", externFn.getParameters().get(0).getName());
		assertEquals(true, externFn.hasReturnType());
		assertInstanceOf(NamedType.class, externFn.getReturnType());
		assertEquals("I32", ((NamedType) externFn.getReturnType()).getName());
	}

	@Test
	void testParseFunctionWithGenericParameters() {
		Lexer lexer = new Lexer("fn identity<T>(x : T) : T { let result = x; }");
		Parser parser = new Parser(lexer.tokenize());

		magma.ast.Program program = parser.parse();

		assertEquals(1, program.getFunctions().size());
		FunctionDefinition fn = program.getFunctions().get(0);
		assertEquals("identity", fn.getName());
		assertEquals(true, fn.hasTypeArguments());
		assertEquals(1, fn.getTypeArguments().size());
	}

	@Test
	void testParseTypeDefinition() {
		Lexer lexer = new Lexer("type MyInt = I32;");
		Parser parser = new Parser(lexer.tokenize());

		magma.ast.Program program = parser.parse();

		assertEquals(1, program.getTypeDefinitions().size());
		TypeDefinition typeDef = program.getTypeDefinitions().get(0);
		assertEquals("MyInt", typeDef.getName());
		assertEquals(false, typeDef.hasGenericParameters());
		assertInstanceOf(NamedType.class, typeDef.getAliasedType());
		assertEquals("I32", ((NamedType) typeDef.getAliasedType()).getName());
	}

	@Test
	void testParseTypeDefinitionWithGenerics() {
		Lexer lexer = new Lexer("type Allocated<Type, Length : USize> = *[Type; 0; Length];");
		Parser parser = new Parser(lexer.tokenize());

		magma.ast.Program program = parser.parse();

		assertEquals(1, program.getTypeDefinitions().size());
		TypeDefinition typeDef = program.getTypeDefinitions().get(0);
		assertEquals("Allocated", typeDef.getName());
		assertEquals(true, typeDef.hasGenericParameters());
		assertEquals(2, typeDef.getGenericParameters().size());
		assertEquals("Type", typeDef.getGenericParameters().get(0).getName());
		assertEquals(false, typeDef.getGenericParameters().get(0).hasConstraint());
		assertEquals("Length", typeDef.getGenericParameters().get(1).getName());
		assertEquals(true, typeDef.getGenericParameters().get(1).hasConstraint());
		assertInstanceOf(PointerType.class, typeDef.getAliasedType());
	}

	@Test
	void testParseTypeDefinitionWithConstraint() {
		Lexer lexer = new Lexer("type MyType<Param : I32> = Param;");
		Parser parser = new Parser(lexer.tokenize());

		magma.ast.Program program = parser.parse();

		assertEquals(1, program.getTypeDefinitions().size());
		TypeDefinition typeDef = program.getTypeDefinitions().get(0);
		assertEquals("MyType", typeDef.getName());
		assertEquals(1, typeDef.getGenericParameters().size());
		GenericParameter param = typeDef.getGenericParameters().get(0);
		assertEquals("Param", param.getName());
		assertEquals(true, param.hasConstraint());
		assertInstanceOf(NamedType.class, param.getConstraint());
		assertEquals("I32", ((NamedType) param.getConstraint()).getName());
	}

	@Test
	void testParseIfExpression() {
		Lexer lexer = new Lexer("let x = if (1) 3 else 5;");
		Parser parser = new Parser(lexer.tokenize());

		magma.ast.Program program = parser.parse();

		assertEquals(1, program.getStatements().size());
		VariableDeclaration decl = (VariableDeclaration) program.getStatements().get(0);
		assertInstanceOf(IfExpression.class, decl.getInitializer());
		IfExpression ifExpr = (IfExpression) decl.getInitializer();
		assertInstanceOf(NumberLiteral.class, ifExpr.getCondition());
		assertInstanceOf(NumberLiteral.class, ifExpr.getThenExpr());
		assertInstanceOf(NumberLiteral.class, ifExpr.getElseExpr());
	}

	@Test
	void testParseIfStatement() {
		Lexer lexer = new Lexer("if (1) x = 3; else x = 5;");
		Parser parser = new Parser(lexer.tokenize());

		magma.ast.Program program = parser.parse();

		assertEquals(1, program.getStatements().size());
		assertInstanceOf(IfStatement.class, program.getStatements().get(0));
		IfStatement ifStmt = (IfStatement) program.getStatements().get(0);
		assertInstanceOf(NumberLiteral.class, ifStmt.getCondition());
		assertInstanceOf(Assignment.class, ifStmt.getThenStmt());
		assertEquals(true, ifStmt.hasElse());
		assertInstanceOf(Assignment.class, ifStmt.getElseStmt());
	}

	@Test
	void testParseIfStatementWithoutElse() {
		Lexer lexer = new Lexer("if (1) x = 3;");
		Parser parser = new Parser(lexer.tokenize());

		magma.ast.Program program = parser.parse();

		assertEquals(1, program.getStatements().size());
		assertInstanceOf(IfStatement.class, program.getStatements().get(0));
		IfStatement ifStmt = (IfStatement) program.getStatements().get(0);
		assertEquals(false, ifStmt.hasElse());
	}
}
