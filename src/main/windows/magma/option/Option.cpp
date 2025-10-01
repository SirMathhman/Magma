struct Option<T> permits Some, None{};
Option<T> of_Option<T> permits Some, None(T value) {/*
		return new Some<>(value);
	*/}
Option<T> empty_Option<T> permits Some, None() {/*
		return new None<>();
	*/}
Option<T> ofNullable_Option<T> permits Some, None(T value) {/*
		return Objects.isNull(value) ? new None<>() : new Some<>(value);
	*/}
