// File generated from '.\src\main\java\magma\Results.java'. This is not source code!
#ifndef RESULTS_H
#define RESULTS_H
template <typename T, typename X>
struct Result;
template <typename T, typename X>
struct Ok;
template <typename T, typename X>
struct Err;
struct Results;
enum ResultTag {
	ErrType,
	OkType
};
template <typename T, typename X>
union ResultData {
	Err<T, X> err;
	Ok<T, X> ok;
};
template <typename T, typename X>
struct Result {
	ResultTag tag;
	ResultData<T, X> data;
};
template <typename T, typename X>
struct Ok {
	T value;
};
struct Results {
};
template <typename T, typename X>
struct Err {
	X error;
};
#endif