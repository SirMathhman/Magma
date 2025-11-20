package magma.ast;

public class NamedType implements Type {
	private final String name;

	public NamedType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitNamedType(this);
	}
}

