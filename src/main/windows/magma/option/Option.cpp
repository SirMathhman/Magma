struct Option{};
Option<T> of_Option(T value) {/*
		return new Some<>(value);
	*/}
Option<T> empty_Option() {/*
		return new None<>();
	*/}
Option<T> ofNullable_Option(T value) {/*
		return Objects.isNull(value) ? new None<>() : new Some<>(value);
	*/}
