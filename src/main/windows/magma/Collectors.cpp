// File generated from '.\src\main\java\magma\Collectors.java'. This is not source code!
#include "Main.h"
/*{
		public*/ ListCollector_Collectors(void* _ref){
	/*}

		@Override
		public ArrayList<T> createInitial() {
			return new ArrayList<T>()*/;
	/*}

		@Override
		public ArrayList<T> fold(ArrayList<T> current, T element) {
			return current.addLast(element)*/;
	/*}*/
}
Joiner new_Joiner(void* _ref, char* delimiter){
	Joiner this;
	this.delimiter = delimiter;
	return this;
}
Joiner new_Joiner(void* _ref){
	Joiner this;
	this("");
	return this;
}
char* createInitial_Joiner(void* _ref){
	return "";
}
char* fold_Joiner(void* _ref, char* current, char* element){
	if (current.isEmpty()) {
		return element;
	}
	return current + this.delimiter + element;
}
int main(){
	main_Main();
	return 0;
}