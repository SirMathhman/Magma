package magma.compiler;

public enum Type {
	I32,
	BOOL,
	VOID;

	public boolean isNumeric() {
		return this == I32;
	}

	public boolean isBoolean() {
		return this == BOOL;
	}
}
