package magma.result;

public record Err<V, E>(E error) implements Result<V, E> {
	@Override
	public boolean isErr() {
		return true;
	}
}
