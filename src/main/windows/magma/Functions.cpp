// File generated from '.\src\main\java\magma\Functions.java'. This is not source code!
#include "Main.h"
R apply_Function(void* _ref, T arg){
	Function this = *((Function*) _ref);
	return this.vtable.apply(this.data, arg);
}
R apply_BiFunction(void* _ref, A first, B second){
	BiFunction this = *((BiFunction*) _ref);
	return this.vtable.apply(this.data, first, second);
}
int main(){
	main_Main();
	return 0;
}