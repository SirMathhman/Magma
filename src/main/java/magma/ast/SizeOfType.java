package magma.ast;

public class SizeOfType implements Type {
	private final Type type;

	public SizeOfType(Type type) {
		this.type = type;
	}

	public Type getType() {
		return type;
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitSizeOfType(this);
	}
}

