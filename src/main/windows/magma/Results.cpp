// File generated from 'JavaPath[path=.\src\main\java\magma\Results.java]'. This is not source code!
#include "Main.h"
enum ResultTag {
	Err,
	Ok
};
template <typeparam T, typeparam X>
union ResultData {
	Err<T, X> err;
	Ok<T, X> ok;
};
template <typeparam T, typeparam X>
struct Result {
	ResultTag tag;
	ResultData<T, X> data;
};
template <typeparam T, typeparam X>
struct Ok {
	T value;
};
template <typeparam T, typeparam X>
struct Err {
	X error;
};
struct Results {
};
int main(){
	main_Main();
	return 0;
}