package magma.compiler;

public final class None<T> implements Option<T> {
	public None() {
	}

	@Override
	public java.util.Optional<T> asOptional() {
		return java.util.Optional.empty();
	}
}
