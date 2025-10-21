// File generated from '.\src\main\java\magma\JavaImpl.java'. This is not source code!
#include "Main.h"
Path get_Paths(void* _ref, char* first, /*String...*/ more){
	Paths this = *((Paths*) _ref);
	return this.vtable.apply(this.data, first, more);
}
int main(){
	main_Main();
	return 0;
}