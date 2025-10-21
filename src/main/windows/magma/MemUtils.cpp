// File generated from '.\src\main\java\magma\MemUtils.java'. This is not source code!
#include "Main.h"
T* malloc_MemUtils(void* _ref, int length){
	MemUtils this = *((MemUtils*) _ref);
	return this.vtable.apply(this.data, length);
}
int main(){
	main_Main();
	return 0;
}