package magma.result;

public record Ok<V, E>(V value) implements Result<V, E> {
	@Override
	public boolean isErr() {
		return false;
	}
}
