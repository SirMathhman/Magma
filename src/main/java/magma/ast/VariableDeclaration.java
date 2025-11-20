package magma.ast;

public class VariableDeclaration implements Statement {
	private final String name;
	private final boolean mutable;
	private final Node initializer;

	public VariableDeclaration(String name, boolean mutable, Node initializer) {
		this.name = name;
		this.mutable = mutable;
		this.initializer = initializer;
	}

	public String getName() {
		return name;
	}

	public boolean isMutable() {
		return mutable;
	}

	public Node getInitializer() {
		return initializer;
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitVariableDeclaration(this);
	}
}

