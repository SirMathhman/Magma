package magma.compile;

public final class VariableSymbol {
	private final String name;
	private final Type type;
	private final boolean mutable;
	private final boolean global;
	private final String cName;

	public VariableSymbol(String name, Type type, boolean mutable, boolean global, String cName) {
		this.name = name;
		this.type = type;
		this.mutable = mutable;
		this.global = global;
		this.cName = cName;
	}

	public String name() {
		return name;
	}

	public Type type() {
		return type;
	}

	public boolean isImmutable() {
		return !mutable;
	}

	public boolean global() {
		return global;
	}

	public String cName() {
		return cName;
	}
}
