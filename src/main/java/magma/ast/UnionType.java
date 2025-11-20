package magma.ast;

import java.util.List;

public class UnionType implements Type {
	private final List<Type> variants;

	public UnionType(List<Type> variants) {
		this.variants = variants;
	}

	public List<Type> getVariants() {
		return variants;
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitUnionType(this);
	}
}

