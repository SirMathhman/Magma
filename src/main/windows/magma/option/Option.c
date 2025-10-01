struct Option<T> permits Some, None{};
/*Option_?*/ of_Option<T> permits Some, None(T value) {/*
		return new Some<>(value);
	*/}
/*Option_?*/ empty_Option<T> permits Some, None() {/*
		return new None<>();
	*/}
/*Option_?*/ ofNullable_Option<T> permits Some, None(T value) {/*
		return Objects.isNull(value) ? new None<>() : new Some<>(value);
	*/}
