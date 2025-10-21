// File generated from '.\src\main\java\magma\Functions.java'. This is not source code!
#ifndef FUNCTIONS_H
#define FUNCTIONS_H
template <typename T, typename R>
struct Function;
template <typename A, typename B, typename R>
struct BiFunction;
struct Functions;
struct FunctionVTable {};
template <typename T, typename R>
struct Function {
	void* data;
	FunctionVTable vtable;
};
struct BiFunctionVTable {};
template <typename A, typename B, typename R>
struct BiFunction {
	void* data;
	BiFunctionVTable vtable;
};
struct Functions {
};
#endif