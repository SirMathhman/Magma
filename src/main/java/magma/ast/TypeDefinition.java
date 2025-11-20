package magma.ast;

import java.util.List;

public class TypeDefinition implements Statement {
	private final String name;
	private final List<GenericParameter> genericParameters;
	private final Type aliasedType;

	public TypeDefinition(String name, Type aliasedType) {
		this(name, null, aliasedType);
	}

	public TypeDefinition(String name, List<GenericParameter> genericParameters, Type aliasedType) {
		this.name = name;
		this.genericParameters = genericParameters;
		this.aliasedType = aliasedType;
	}

	public String getName() {
		return name;
	}

	public List<GenericParameter> getGenericParameters() {
		return genericParameters;
	}

	public boolean hasGenericParameters() {
		return genericParameters != null && !genericParameters.isEmpty();
	}

	public Type getAliasedType() {
		return aliasedType;
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitTypeDefinition(this);
	}
}

