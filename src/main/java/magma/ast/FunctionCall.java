package magma.ast;

import java.util.List;

public class FunctionCall implements Node {
	private final String name;
	private final List<Node> arguments;

	public FunctionCall(String name, List<Node> arguments) {
		this.name = name;
		this.arguments = arguments;
	}

	public String getName() {
		return name;
	}

	public List<Node> getArguments() {
		return arguments;
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitFunctionCall(this);
	}
}

