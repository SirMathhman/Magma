package magma.ast;

import magma.lexer.Token;

public class BinaryType implements Type {
	private final Type left;
	private final Token operator;
	private final Type right;

	public BinaryType(Type left, Token operator, Type right) {
		this.left = left;
		this.operator = operator;
		this.right = right;
	}

	public Type getLeft() {
		return left;
	}

	public Token getOperator() {
		return operator;
	}

	public Type getRight() {
		return right;
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitBinaryType(this);
	}
}

