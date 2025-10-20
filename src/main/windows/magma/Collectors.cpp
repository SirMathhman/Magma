// File generated from 'JavaPath[path=.\src\main\java\magma\Collectors.java]'. This is not source code!
#include "Main.h"
C createInitial_Collector();
C fold_Collector(C current, T element);
ListCollector new_ListCollector(){
	ListCollector this;
	return this;
}
ArrayList<T> createInitial_ListCollector(){
	return new_ArrayList<T>();
}
ArrayList<T> fold_ListCollector(ArrayList<T> current, T element){
	return current.addLast(element);
}
Joiner new_Joiner(char* delimiter){
	Joiner this;
	this.delimiter = delimiter;
	return this;
}
Joiner new_Joiner(){
	Joiner this;
	this("");
	return this;
}
char* createInitial_Joiner(){
	return "";
}
char* fold_Joiner(char* current, char* element){
	if (current.isEmpty()) {
		return element;
	}
	return current + this.delimiter + element;
}
Boolean createInitial_AnyMatch(){
	return false;
}
Boolean fold_AnyMatch(Boolean current, T element){
	return current || this.predicate.test(element);
}
Result<C, X> createInitial_ResultCollector(){
	return new_Ok<C, X>(this.collector.createInitial());
}
Result<C, X> fold_ResultCollector(Result<C, X> current, Result<T, X> element){
	return /*switch (current) {
				case Err<C, X> v -> new Err<C, X>(v.error());
				case Ok<C, X> v -> switch (element) {
					case Err<T, X> v1 -> new Err<C, X>(v1.error());
					case Ok<T, X> v1 -> new Ok<C, X>(this.collector.fold(v.value(), v1.value()));
				};
			}*/;
}
Boolean createInitial_AllMatch(){
	return true;
}
Boolean fold_AllMatch(Boolean current, T element){
	return current && this.predicate.test(element);
}
int main(){
	main_Main();
	return 0;
}