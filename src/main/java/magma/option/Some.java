package magma.option;

import java.util.function.Function;

public record Some<T>(T value) implements Optional<T> {
	@Override
	public <R> Optional<R> map(Function<T, R> mapper) {
		return new Some<>(mapper.apply(value));
	}
}
