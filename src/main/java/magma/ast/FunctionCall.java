package magma.ast;

import java.util.List;

public class FunctionCall implements Node {
	private final String name;
	private final List<Type> typeArguments; // Generic type arguments
	private final List<Node> arguments;

	public FunctionCall(String name, List<Node> arguments) {
		this(name, null, arguments);
	}

	public FunctionCall(String name, List<Type> typeArguments, List<Node> arguments) {
		this.name = name;
		this.typeArguments = typeArguments;
		this.arguments = arguments;
	}

	public String getName() {
		return name;
	}

	public List<Type> getTypeArguments() {
		return typeArguments;
	}

	public boolean hasTypeArguments() {
		return typeArguments != null && !typeArguments.isEmpty();
	}

	public List<Node> getArguments() {
		return arguments;
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitFunctionCall(this);
	}
}

