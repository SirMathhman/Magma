
package magma.option;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public sealed interface Option<Value> permits Some, None {
	void consume(Consumer<Value> ifSome, Runnable ifNone);

	boolean isNone();

	default boolean isSome() {
		return !isNone();
	}

	<U> Option<U> map(Function<? super Value, ? extends U> f);

	<U> Option<U> flatMap(Function<? super Value, Option<U>> f);

	Value orElse(Value other);

	Value get();

	static <T> Option<T> some(T v) {
		return new Some<>(v);
	}

	static <T> Option<T> none() {
		return new None<>();
	}

	static <T> Option<T> ofNullable(T v) {
		return v == null ? none() : some(v);
	}

	static <T> Option<T> ofSupplier(Supplier<T> s) {
		try {
			return ofNullable(s.get());
		} catch (Exception e) {
			return none();
		}
	}
}
