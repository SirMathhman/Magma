// Generated transpiled C++ from 'src\main\java\magma\result\Err.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct Err {X error;};
Result<> mapValue_Err(R (*fn)(T)) {
	return new_???(error);
}
Result<> flatMap_Err(Result<> (*fn)(T)) {
	return new_???(error);
}
Result<> mapErr_Err(R (*mapper)(X)) {
	return new_???(mapper.apply(error));
}
