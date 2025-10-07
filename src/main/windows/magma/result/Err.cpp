// Generated transpiled C++ from 'src\main\java\magma\result\Err.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct Err {X error;};
Result<R, X> mapValue_Err(R (*fn)(T)) {
	return new_???(error);
}
Result<R, X> flatMap_Err(Result<R, X> (*fn)(T)) {
	return new_???(error);
}
Result<T, R> mapErr_Err(R (*mapper)(X)) {
	return new_???(mapper.apply(error));
}
