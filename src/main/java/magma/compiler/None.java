package magma.compiler;

public final class None implements Option<Object> {
	private static final None INSTANCE = new None();

	private None() {
	}

	public static None instance() {
		return INSTANCE;
	}

	@Override
	public boolean isPresent() {
		return false;
	}

	@Override
	public Object get() {
		throw new java.util.NoSuchElementException("None.get()");
	}
}
