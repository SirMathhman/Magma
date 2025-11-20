package magma.ast;

public class NumberLiteral implements Node {
	private final int value;

	public NumberLiteral(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitNumberLiteral(this);
	}
}
