package magma.compiler;

public final class None implements Option<Object> {
	private static final None INSTANCE = new None();

	private None() {
	}

	public static None instance() {
		return INSTANCE;
	}

	@Override
	public java.util.Optional<Object> asOptional() {
		return java.util.Optional.empty();
	}
}
