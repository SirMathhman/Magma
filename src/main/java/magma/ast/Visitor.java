package magma.ast;

public interface Visitor<T> {
	T visitNumberLiteral(NumberLiteral node);

	T visitBinaryExpression(BinaryExpression node);

	T visitIdentifier(Identifier node);

	T visitStringLiteral(StringLiteral node);

	T visitVariableDeclaration(VariableDeclaration node);

	T visitAssignment(Assignment node);

	T visitFunctionCall(FunctionCall node);

	T visitArrayIndex(ArrayIndex node);

	T visitForLoop(ForLoop node);

	T visitBlock(Block node);

	T visitProgram(Program node);

	T visitImportStatement(ImportStatement node);

	T visitExpressionStatement(ExpressionStatement node);
}
