package magma.ast;

public class ArrayType implements Type {
	private final Type elementType;
	private final Node length;
	private final Node start; // Optional, for [Type; Start; End] syntax

	public ArrayType(Type elementType, Node length) {
		this(elementType, length, null);
	}

	public ArrayType(Type elementType, Node length, Node start) {
		this.elementType = elementType;
		this.length = length;
		this.start = start;
	}

	public Type getElementType() {
		return elementType;
	}

	public Node getLength() {
		return length;
	}

	public Node getStart() {
		return start;
	}

	public boolean hasStart() {
		return start != null;
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitArrayType(this);
	}
}

