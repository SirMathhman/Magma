// Generated transpiled C++ from 'src\main\java\magma\result\Ok.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
template<typename T, typename X>\nstruct Ok<T, X>{T value;};
/*X>*/ mapValue_Ok(R fn) {/*
		return new Ok<>(fn.apply(this.value));
	*/}
/*X>*/ flatMap_Ok(X fn) {/*
		return fn.apply(this.value);
	*/}
/*R>*/ mapErr_Ok(R mapper) {/*
		return new Ok<>(value);
	*/}
