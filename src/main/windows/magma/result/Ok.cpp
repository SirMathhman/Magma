// Generated transpiled C++ from 'src\main\java\magma\result\Ok.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
template<typename T, typename X>
struct Ok{T value;};
template<typename R, typename T, typename X>
@Override
	public</*R> Result<R*/, X> mapValue_Ok(R (*fn)(T)) {/*
		return new Ok<>(fn.apply(this.value));
	*/}
template<typename R, typename T, typename X>
@Override
	public</*R> Result<R*/, X> flatMap_Ok(Result<R, X> (*fn)(T)) {/*
		return fn.apply(this.value);
	*/}
template<typename R, typename X>
@Override
	public</*R> Result<T*/, R> mapErr_Ok(R (*mapper)(X)) {/*
		return new Ok<>(value);
	*/}
