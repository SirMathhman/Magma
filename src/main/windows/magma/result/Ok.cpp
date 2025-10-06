// Generated transpiled C++ from 'src\main\java\magma\result\Ok.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct Ok {/*???*/ value;};
Result<> mapValue_Ok(Function<> fn) {
	return new_???(fn.apply(this.value));
}
Result<> flatMap_Ok(Function<> fn) {
	return fn.apply(this.value);
}
Result<> mapErr_Ok(Function<> mapper) {
	return new_???(value);
}
