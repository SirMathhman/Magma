package magma.compile;

public enum Type {
	I32,
	BOOL,
	VOID;

	public boolean isNonNumeric() {
		return this != I32;
	}

	public boolean isNonBoolean() {
		return this != BOOL;
	}
}
