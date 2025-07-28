/*import java.util.Optional;*/
/*import java.util.function.Function;*/
/*public record Err<T, X>(X error) implements Result<T, X> {
	@Override
	public Optional<T> findValue() {
		return Optional.empty();
	}

	@Override
	public <R> Result<R, X> mapValue(final Function<T, R> mapper) {
		return new Err<>(this.error);
	}

	@Override
	public <R> Result<R, X> flatMapValue(final Function<T, Result<R, X>> mapper) {
		return new Err<>(this.error);
	}

	@Override
	public <R> R match(final Function<T, R> whenOk, final Function<X, R> whenErr) {
		return whenErr.apply(this.error);
	}

	@Override
	public <R> Result<T, R> mapErr(final Function<X, R> mapper) {
		return new Err<>(mapper.apply(this.error));
	}
}*/
