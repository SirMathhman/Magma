package magma.ast;

public class Assignment implements Statement {
	private final Node target;
	private final Node value;

	public Assignment(Node target, Node value) {
		this.target = target;
		this.value = value;
	}

	public Node getTarget() {
		return target;
	}

	public Node getValue() {
		return value;
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitAssignment(this);
	}
}

