// Generated transpiled C++ from 'src\main\java\magma\option\Some.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
template<typename T>
struct Some<T>{T value;};
Option<R> map_Some(R mapper) {/*
		return new Some<>(mapper.apply(value));
	*/}
Option<R> flatMap_Some(OptionR mapper) {/*
		return mapper.apply(value);
	*/}
T orElse_Some(T other) {/*
		return value;
	*/}
