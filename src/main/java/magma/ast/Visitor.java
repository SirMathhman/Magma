package magma.ast;

public interface Visitor<T> {
	T visitNumberLiteral(NumberLiteral node);

	T visitBinaryExpression(BinaryExpression node);
}
