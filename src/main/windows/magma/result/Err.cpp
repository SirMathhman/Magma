// Generated transpiled C++ from 'src\main\java\magma\result\Err.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct Err {/*???*/ error;};
Result<> mapValue_Err(Function<> fn) {
	return new_???(error);
}
Result<> flatMap_Err(Function<> fn) {
	return new_???(error);
}
Result<> mapErr_Err(Function<> mapper) {
	return new_???(mapper.apply(error));
}
