package magma.ast;

public class IfExpression implements Node {
	private final Node condition;
	private final Node thenExpr;
	private final Node elseExpr;

	public IfExpression(Node condition, Node thenExpr, Node elseExpr) {
		this.condition = condition;
		this.thenExpr = thenExpr;
		this.elseExpr = elseExpr;
	}

	public Node getCondition() {
		return condition;
	}

	public Node getThenExpr() {
		return thenExpr;
	}

	public Node getElseExpr() {
		return elseExpr;
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitIfExpression(this);
	}
}

