package org.example.core;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Minimal Option type implemented as a sealed interface with two variants:
 * Some and None.
 */
public sealed interface Option<T> permits Option.Some, Option.None {
	boolean isSome();

	default boolean isNone() {
		return !isSome();
	}

	/**
	 * Get the contained value. Throws if this is None.
	 */
	T get();

	<U> Option<U> map(Function<? super T, ? extends U> mapper);

	<U> Option<U> flatMap(Function<? super T, Option<U>> mapper);

	T orElse(T other);

	T orElseGet(Supplier<? extends T> supplier);

	void ifPresent(Consumer<? super T> consumer);

	static <T> Option<T> some(T value) {
		return new Some<>(value);
	}

	@SuppressWarnings("unchecked")
	static <T> Option<T> none() {
		return (Option<T>) None.INSTANCE;
	}

	/**
	 * Create an Option from a possibly-null value.
	 */
	static <T> Option<T> ofNullable(T value) {
		return value == null ? none() : some(value);
	}

	/**
	 * Some variant implemented as a record.
	 */
	public static final record Some<T>(T value) implements Option<T> {
		public Some {
			Objects.requireNonNull(value, "Some value cannot be null");
		}

		@Override
		public boolean isSome() {
			return true;
		}

		@Override
		public T get() {
			return value;
		}

		@Override
		public <U> Option<U> map(Function<? super T, ? extends U> mapper) {
			return Option.some(mapper.apply(value));
		}

		@Override
		public <U> Option<U> flatMap(Function<? super T, Option<U>> mapper) {
			return Objects.requireNonNull(mapper.apply(value));
		}

		@Override
		public T orElse(T other) {
			return value;
		}

		@Override
		public T orElseGet(Supplier<? extends T> supplier) {
			return value;
		}

		@Override
		public void ifPresent(Consumer<? super T> consumer) {
			consumer.accept(value);
		}
	}

	/**
	 * None singleton implementing Option<Object> and used as a canonical none.
	 */
	public static final class None implements Option<Object> {
		private static final None INSTANCE = new None();

		private None() {
		}

		@Override
		public boolean isSome() {
			return false;
		}

		@Override
		public Object get() {
			throw new IllegalStateException("get() on None");
		}

		@Override
		public <U> Option<U> map(Function<? super Object, ? extends U> mapper) {
			return Option.none();
		}

		@Override
		public <U> Option<U> flatMap(Function<? super Object, Option<U>> mapper) {
			return Option.none();
		}

		@Override
		public Object orElse(Object other) {
			return other;
		}

		@Override
		public Object orElseGet(Supplier<? extends Object> supplier) {
			return supplier.get();
		}

		@Override
		public void ifPresent(Consumer<? super Object> consumer) {
			// no-op
		}

		@Override
		public boolean equals(Object o) {
			return this == o || o instanceof None;
		}

		@Override
		public int hashCode() {
			return 0;
		}

		@Override
		public String toString() {
			return "None";
		}
	}
}
