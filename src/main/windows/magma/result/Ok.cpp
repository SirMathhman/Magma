// Generated transpiled C++ from 'src\main\java\magma\result\Ok.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct Ok {/*???*/ value;};
Result<> mapValue_Ok(/*???*/ (*fn)(/*???*/)) {
	return new_???(fn.apply(this.value));
}
Result<> flatMap_Ok(Result<> (*fn)(/*???*/)) {
	return fn.apply(this.value);
}
Result<> mapErr_Ok(/*???*/ (*mapper)(/*???*/)) {
	return new_???(value);
}
