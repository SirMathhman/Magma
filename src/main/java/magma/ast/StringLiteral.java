package magma.ast;

public class StringLiteral implements Node {
	private final String value;

	public StringLiteral(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitStringLiteral(this);
	}
}

