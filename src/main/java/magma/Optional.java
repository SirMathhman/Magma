package magma;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public sealed interface Optional<T> {
	record Some<T>(T value) implements Optional<T> {}

	final class None<T> implements Optional<T> {}

	default Optional<T> filter(Predicate<T> filter) {
		return switch (this) {
			case Optional.None<T> v -> v;
			case Optional.Some<T> v -> filter.test(v.value) ? this : new None<>();
		};
	}

	default T get() {
		return switch (this) {
			case Optional.None<T> v -> null;
			case Optional.Some<T> v -> v.value;
		};
	}

	default T orElseGet(Supplier<T> supplier) {
		return switch (this) {
			case Optional.None<T> v -> supplier.get();
			case Optional.Some<T> v -> v.value;
		};
	}

	default <R> Optional<R> map(Function<T, R> mapper) {
		return switch (this) {
			case Optional.None<T> v -> new None<>();
			case Optional.Some<T> v -> new Some<>(mapper.apply(v.value));
		};
	}
}
