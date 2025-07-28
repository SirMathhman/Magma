/*import java.util.Optional;*/
/*import java.util.function.Function;*/
/*public record Ok<T, X>(T value) implements Result<T, X> {
	@Override
	public Optional<T> findValue() {
		return Optional.of(this.value);
	}

	@Override
	public <R> Result<R, X> mapValue(final Function<T, R> mapper) {
		return new Ok<>(mapper.apply(this.value));
	}

	@Override
	public <R> Result<R, X> flatMapValue(final Function<T, Result<R, X>> mapper) {
		return mapper.apply(this.value);
	}

	@Override
	public <R> R match(final Function<T, R> whenOk, final Function<X, R> whenErr) {
		return whenOk.apply(this.value);
	}

	@Override
	public <R> Result<T, R> mapErr(final Function<X, R> mapper) {
		return new Ok<>(this.value);
	}
}*/
