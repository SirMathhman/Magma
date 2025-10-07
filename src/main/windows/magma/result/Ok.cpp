// Generated transpiled C++ from 'src\main\java\magma\result\Ok.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct Ok {T value;};
Result<R, X> mapValue_Ok(R (*fn)(T)) {
	return new_???(fn.apply(this.value));
}
Result<R, X> flatMap_Ok(Result<R, X> (*fn)(T)) {
	return fn.apply(this.value);
}
Result<T, R> mapErr_Ok(R (*mapper)(X)) {
	return new_???(value);
}
