package magma.ast;

public class VariableDeclaration implements Statement {
	private final String name;
	private final boolean mutable;
	private final Type typeAnnotation; // Optional type annotation
	private final Node initializer;

	public VariableDeclaration(String name, boolean mutable, Node initializer) {
		this(name, mutable, null, initializer);
	}

	public VariableDeclaration(String name, boolean mutable, Type typeAnnotation, Node initializer) {
		this.name = name;
		this.mutable = mutable;
		this.typeAnnotation = typeAnnotation;
		this.initializer = initializer;
	}

	public String getName() {
		return name;
	}

	public boolean isMutable() {
		return mutable;
	}

	public Type getTypeAnnotation() {
		return typeAnnotation;
	}

	public boolean hasTypeAnnotation() {
		return typeAnnotation != null;
	}

	public Node getInitializer() {
		return initializer;
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitVariableDeclaration(this);
	}
}

