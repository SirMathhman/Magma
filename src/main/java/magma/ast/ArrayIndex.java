package magma.ast;

public class ArrayIndex implements Node {
	private final Node array;
	private final Node index;

	public ArrayIndex(Node array, Node index) {
		this.array = array;
		this.index = index;
	}

	public Node getArray() {
		return array;
	}

	public Node getIndex() {
		return index;
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitArrayIndex(this);
	}
}

