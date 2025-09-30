package magma.option;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class None<T> implements Optional<T> {
	@Override
	public void ifPresent(Consumer<T> consumer) {
	}

	public boolean isEmpty() {
		return true;
	}

	@Override
	public Optional<T> or(Supplier<Optional<T>> other) {
		return other.get();
	}

	public boolean isPresent() {
		return false;
	}

	@Override
	public T orElse(T other) {
		return other;
	}

	@Override
	public <R> Optional<R> map(Function<T, R> mapper) {
		return new None<>();
	}

	@Override
	public T orElseGet(Supplier<T> supplier) {
		return supplier.get();
	}
}
