// Generated transpiled C++ from 'src\main\java\magma\result\Result.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct Result {};
Result<R, X> mapValue_Result(R (*fn)(T)) {
}
Result<R, X> flatMap_Result(Result<R, X> (*fn)(T)) {
}
Result<T, R> mapErr_Result(R (*mapper)(X)) {
}
