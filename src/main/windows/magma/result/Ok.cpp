// Generated transpiled C++ from 'src\main\java\magma\result\Ok.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct Ok {T value;};
Result<> mapValue_Ok(R (*fn)(T)) {
	return new_???(fn.apply(this.value));
}
Result<> flatMap_Ok(Result<> (*fn)(T)) {
	return fn.apply(this.value);
}
Result<> mapErr_Ok(R (*mapper)(X)) {
	return new_???(value);
}
