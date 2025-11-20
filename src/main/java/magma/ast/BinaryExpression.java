package magma.ast;

import magma.lexer.Token;

public class BinaryExpression implements Node {
	private final Node left;
	private final Token operator;
	private final Node right;

	public BinaryExpression(Node left, Token operator, Node right) {
		this.left = left;
		this.operator = operator;
		this.right = right;
	}

	public Node getLeft() {
		return left;
	}

	public Token getOperator() {
		return operator;
	}

	public Node getRight() {
		return right;
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitBinaryExpression(this);
	}
}
