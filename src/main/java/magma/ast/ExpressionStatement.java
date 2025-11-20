package magma.ast;

public class ExpressionStatement implements Statement {
	private final Node expression;

	public ExpressionStatement(Node expression) {
		this.expression = expression;
	}

	public Node getExpression() {
		return expression;
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitExpressionStatement(this);
	}
}

