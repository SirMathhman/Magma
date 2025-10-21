// File generated from '.\src\main\java\magma\Functions.java'. This is not source code!
#ifndef FUNCTIONS_H
#define FUNCTIONS_H
template <typename T, typename R>
struct Function;
template <typename A, typename B, typename R>
struct BiFunction;
struct Functions;
template <typename T, typename R>
struct FunctionVTable {
	R (*apply)(void*, T);
};
template <typename T, typename R>
struct Function {
	void* data;
	FunctionVTable<T, R> vtable;
};
template <typename A, typename B, typename R>
struct BiFunctionVTable {
	R (*apply)(void*, A, B);
};
template <typename A, typename B, typename R>
struct BiFunction {
	void* data;
	BiFunctionVTable<A, B, R> vtable;
};
struct Functions {
};
#endif