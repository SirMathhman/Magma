package magma.ast;

import java.util.List;

public class FunctionDefinition implements Statement {
	private final String name;
	private final List<Type> typeArguments; // Generic type parameters
	private final List<FunctionParameter> parameters;
	private final Type returnType; // Optional return type
	private final Node body; // Block or expression (for arrow syntax)

	public FunctionDefinition(String name, List<FunctionParameter> parameters, Type returnType, Node body) {
		this(name, null, parameters, returnType, body);
	}

	public FunctionDefinition(String name, List<Type> typeArguments, List<FunctionParameter> parameters, Type returnType, Node body) {
		this.name = name;
		this.typeArguments = typeArguments;
		this.parameters = parameters;
		this.returnType = returnType;
		this.body = body;
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

	public List<FunctionParameter> getParameters() {
		return parameters;
	}

	public Type getReturnType() {
		return returnType;
	}

	public boolean hasReturnType() {
		return returnType != null;
	}

	public Node getBody() {
		return body;
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitFunctionDefinition(this);
	}
}

