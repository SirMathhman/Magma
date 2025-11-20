package magma.ast;

public class IfStatement implements Statement {
	private final Node condition;
	private final Statement thenStmt;
	private final Statement elseStmt;

	public IfStatement(Node condition, Statement thenStmt) {
		this(condition, thenStmt, null);
	}

	public IfStatement(Node condition, Statement thenStmt, Statement elseStmt) {
		this.condition = condition;
		this.thenStmt = thenStmt;
		this.elseStmt = elseStmt;
	}

	public Node getCondition() {
		return condition;
	}

	public Statement getThenStmt() {
		return thenStmt;
	}

	public Statement getElseStmt() {
		return elseStmt;
	}

	public boolean hasElse() {
		return elseStmt != null;
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitIfStatement(this);
	}
}

