// File generated from '.\src\main\java\magma\Utils.java'. This is not source code!
#include "Main.h"
auto __lambda0__(auto arg) {
	return new_Tuple<R, B>(mapper.apply(arg.left), arg.right);
}
template <typename A, typename B>
Function<Tuple<A, B>, Tuple<R, B>> mapLeft_Tuple(void* _ref, Function<A, R> mapper){
	return __lambda0__;
}
int main(){
	main_Main();
	return 0;
}