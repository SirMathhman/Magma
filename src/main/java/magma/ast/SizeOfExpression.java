package magma.ast;

public class SizeOfExpression implements Node {
	private final Type type;

	public SizeOfExpression(Type type) {
		this.type = type;
	}

	public Type getType() {
		return type;
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitSizeOfExpression(this);
	}
}

