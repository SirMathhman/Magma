package magma.option;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public record Some<T>(T value) implements Optional<T> {
	@Override
	public void ifPresent(Consumer<T> consumer) {
		consumer.accept(value);
	}

	public boolean isEmpty() {
		return false;
	}

	@Override
	public Optional<T> or(Supplier<Optional<T>> other) {
		return this;
	}

	public boolean isPresent() {
		return true;
	}

	@Override
	public T orElse(T other) {
		return value;
	}

	@Override
	public <R> Optional<R> map(Function<T, R> mapper) {
		return new Some<>(mapper.apply(value));
	}

	@Override
	public T orElseGet(Supplier<T> supplier) {
		return value;
	}
}
