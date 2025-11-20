package magma.ast;

public class GenericParameter {
	private final String name;
	private final Type constraint; // Optional constraint (e.g., Length : USize)

	public GenericParameter(String name) {
		this(name, null);
	}

	public GenericParameter(String name, Type constraint) {
		this.name = name;
		this.constraint = constraint;
	}

	public String getName() {
		return name;
	}

	public Type getConstraint() {
		return constraint;
	}

	public boolean hasConstraint() {
		return constraint != null;
	}
}

