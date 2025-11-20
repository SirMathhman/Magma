package magma.ast;

import java.util.List;

public class TraitImplementation implements Statement {
	private final String traitName;
	private final Type implementingType;
	private final List<FunctionDefinition> methods; // Full method implementations

	public TraitImplementation(String traitName, Type implementingType, List<FunctionDefinition> methods) {
		this.traitName = traitName;
		this.implementingType = implementingType;
		this.methods = methods;
	}

	public String getTraitName() {
		return traitName;
	}

	public Type getImplementingType() {
		return implementingType;
	}

	public List<FunctionDefinition> getMethods() {
		return methods;
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitTraitImplementation(this);
	}
}

