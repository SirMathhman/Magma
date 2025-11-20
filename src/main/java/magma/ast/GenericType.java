package magma.ast;

import java.util.List;

public class GenericType implements Type {
	private final String baseName;
	private final List<Type> typeArguments;

	public GenericType(String baseName, List<Type> typeArguments) {
		this.baseName = baseName;
		this.typeArguments = typeArguments;
	}

	public String getBaseName() {
		return baseName;
	}

	public List<Type> getTypeArguments() {
		return typeArguments;
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitGenericType(this);
	}
}

