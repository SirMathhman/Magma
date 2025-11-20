package magma.ast;

import java.util.List;

public class TraitDefinition implements Statement {
	private final String name;
	private final List<GenericParameter> genericParameters;
	private final List<FunctionDefinition> methods; // Method signatures only (no bodies)
	private final boolean isIntrinsic;

	public TraitDefinition(String name, List<GenericParameter> genericParameters, List<FunctionDefinition> methods, boolean isIntrinsic) {
		this.name = name;
		this.genericParameters = genericParameters;
		this.methods = methods;
		this.isIntrinsic = isIntrinsic;
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

	public List<FunctionDefinition> getMethods() {
		return methods;
	}

	public boolean isIntrinsic() {
		return isIntrinsic;
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitTraitDefinition(this);
	}
}

