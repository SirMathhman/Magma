// Generated transpiled C++ from 'src\main\java\magma\option\Some.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
template<typename T>
struct Some{T value;};
template<typename R>
Option<R> map_Some(R mapper) {/*
		return new Some<>(mapper.apply(value));
	*/}
template<typename R>
Option<R> flatMap_Some(OptionR mapper) {/*
		return mapper.apply(value);
	*/}
template<>
T orElse_Some(T other) {/*
		return value;
	*/}
