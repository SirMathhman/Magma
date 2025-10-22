// File generated from '.\src\main\java\magma\Functions.java'. This is not source code!
#ifndef FUNCTIONS_H
#define FUNCTIONS_H
template <typename T, typename R>
struct Function;
template <typename A, typename B, typename R>
struct BiFunction;
struct Functions;
struct Functions {
};
template <typename A, typename B, typename R>
struct BiFunctionVTable {
	R (*apply)(void*, A, B);
};
template <typename A, typename B, typename R>
struct BiFunction {
};
template <typename T, typename R>
struct FunctionVTable {
	R (*apply)(void*, T);
};
template <typename T, typename R>
struct Function {
};
template <typename T, typename R>
R apply_Function(void* _ref, T arg);
template <typename A, typename B, typename R>
R apply_BiFunction(void* _ref, A first, B second);
#endif