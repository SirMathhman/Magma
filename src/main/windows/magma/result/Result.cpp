// Generated transpiled C++ from 'src\main\java\magma\result\Result.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
template<typename T, typename X>
struct Result{mapValue(Function<T, R> fn);flatMap(Function<T, Result<R, X>> fn);mapErr(Function<X, R> mapper);};
