// Generated transpiled C++ from 'src\main\java\magma\result\Err.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
template<typename T, typename X>
struct Err<T, X>{X error;};
template<typename R>
/*X>*/ mapValue_Err(R fn) {/*
		return new Err<>(error);
	*/}
template<typename X>
/*X>*/ flatMap_Err(X fn) {/*
		return new Err<>(error);
	*/}
template<typename R>
/*R>*/ mapErr_Err(R mapper) {/*
		return new Err<>(mapper.apply(error));
	*/}
