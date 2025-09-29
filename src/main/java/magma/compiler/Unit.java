package magma.compiler;

public final class Unit {
	private static final Unit INSTANCE = new Unit();

	private Unit() {
	}

	public static Unit instance() {
		return INSTANCE;
	}
}
