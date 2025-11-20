package magma.ast;

public class Identifier implements Node {
	private final String name;

	public Identifier(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitIdentifier(this);
	}
}

