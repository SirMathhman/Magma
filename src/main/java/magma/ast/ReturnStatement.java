package magma.ast;

public class ReturnStatement implements Statement {
	private final Node value; // Optional return value

	public ReturnStatement() {
		this(null);
	}

	public ReturnStatement(Node value) {
		this.value = value;
	}

	public Node getValue() {
		return value;
	}

	public boolean hasValue() {
		return value != null;
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitReturnStatement(this);
	}
}

