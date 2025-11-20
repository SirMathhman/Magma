package magma.ast;

public class FunctionParameter {
	private final String name;
	private final Type type; // Optional type annotation

	public FunctionParameter(String name) {
		this(name, null);
	}

	public FunctionParameter(String name, Type type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public Type getType() {
		return type;
	}

	public boolean hasType() {
		return type != null;
	}
}

