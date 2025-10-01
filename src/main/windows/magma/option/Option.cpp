// Generated transpiled C++ from 'src\main\java\magma\option\Option.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct Option<>{};
Option<T> of_Option(T value) {/*
		return new Some<>(value);
	*/}
Option<T> empty_Option() {/*
		return new None<>();
	*/}
Option<T> ofNullable_Option(T value) {/*
		return Objects.isNull(value) ? new None<>() : new Some<>(value);
	*/}
