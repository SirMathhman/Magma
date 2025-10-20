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
Results.Result<C, X> createInitial_ResultCollector(){
	return new_Results.Ok<C, X>(this.collector.createInitial());
}
Results.Result<C, X> fold_ResultCollector(Results.Result<C, X> current, Results.Result<T, X> element){
	return /*switch (current) {
				case Results.Err<C, X> v -> new Results.Err<C, X>(v.error());
				case Results.Ok<C, X> v -> switch (element) {
					case Results.Err<T, X> v1 -> new Results.Err<C, X>(v1.error());
					case Results.Ok<T, X> v1 -> new Results.Ok<C, X>(this.collector.fold(v.value(), v1.value()));
				};
			}*/;
}
int main(){
	main_Main();
	return 0;
}