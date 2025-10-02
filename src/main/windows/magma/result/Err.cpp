// Generated transpiled C++ from 'src\main\java\magma\result\Err.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
template<typename T, typename X>
struct Err {X error;};
template<typename R, typename T, typename X>
Result<R, X> mapValue_Err(R (*fn)(T)) {
	return /*new Err<>(error)*/;
}
template<typename R, typename T, typename X>
Result<R, X> flatMap_Err(Result<R, X> (*fn)(T)) {
	return /*new Err<>(error)*/;
}
template<typename R, typename T, typename X>
Result<T, R> mapErr_Err(R (*mapper)(X)) {
	return /*new Err<>(mapper.apply(error))*/;
}
