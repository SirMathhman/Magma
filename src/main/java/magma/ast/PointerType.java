package magma.ast;

public class PointerType implements Type {
	private final Type baseType;

	public PointerType(Type baseType) {
		this.baseType = baseType;
	}

	public Type getBaseType() {
		return baseType;
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitPointerType(this);
	}
}

