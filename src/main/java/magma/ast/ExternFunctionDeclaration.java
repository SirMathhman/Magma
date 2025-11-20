package magma.ast;

import java.util.List;

public class ExternFunctionDeclaration implements Statement {
	private final String name;
	private final List<Type> typeArguments; // Generic type parameters
	private final List<FunctionParameter> parameters;
	private final Type returnType; // Optional return type

	public ExternFunctionDeclaration(String name, List<FunctionParameter> parameters, Type returnType) {
		this(name, null, parameters, returnType);
	}

	public ExternFunctionDeclaration(String name, List<Type> typeArguments, List<FunctionParameter> parameters, Type returnType) {
		this.name = name;
		this.typeArguments = typeArguments;
		this.parameters = parameters;
		this.returnType = returnType;
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

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitExternFunctionDeclaration(this);
	}
}

