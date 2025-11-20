package magma.ast;

public class ForLoop implements Statement {
	private final String variableName;
	private final boolean mutable;
	private final Node start;
	private final Node end;
	private final Block body;

	public ForLoop(String variableName, boolean mutable, Node start, Node end, Block body) {
		this.variableName = variableName;
		this.mutable = mutable;
		this.start = start;
		this.end = end;
		this.body = body;
	}

	public String getVariableName() {
		return variableName;
	}

	public boolean isMutable() {
		return mutable;
	}

	public Node getStart() {
		return start;
	}

	public Node getEnd() {
		return end;
	}

	public Block getBody() {
		return body;
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitForLoop(this);
	}
}

