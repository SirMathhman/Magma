// File generated from '.\src\main\java\magma\Results.java'. This is not source code!
#ifndef RESULTS_H
#define RESULTS_H
enum ResultTag {
	Err,
	Ok
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
template <typename T, typename X>
struct Err {
	X error;
};
struct Results {
};
#endif