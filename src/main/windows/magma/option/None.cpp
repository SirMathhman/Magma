// Generated transpiled C++ from 'src\main\java\magma\option\None.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
template<typename T>
struct None{};
template<typename R, typename T>
Option<R> map_None(R (*mapper)(T)) {/*
		return new None<>();*/}
template<typename R, typename T>
Option<R> flatMap_None(Option<R> (*mapper)(T)) {/*
		return new None<>();*/}
template<typename T>
T orElse_None(T other) {/*
		return other;*/}
