// File generated from '.\src\main\java\magma\Collectors.java'. This is not source code!
#include "Main.h"
template <typename T, typename C>
C createInitial_Collector(void* _ref){
	Collector this = *((Collector*) _ref);
	return this.vtable.apply(this.data);
}
template <typename T, typename C>
C fold_Collector(void* _ref, C current, T element){
	Collector this = *((Collector*) _ref);
	return this.vtable.apply(this.data, current, element);
}
template <typename T>
ListCollector new_ListCollector(void* _ref){
	ListCollector this;
	return this;
}
template <typename T>
ArrayList<T> createInitial_ListCollector(void* _ref){
	return new_ArrayList<T>();
}
template <typename T>
ArrayList<T> fold_ListCollector(void* _ref, ArrayList<T> current, T element){
	return current.addLast(element);
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
template <typename T>
Boolean createInitial_AnyMatch(void* _ref){
	return false;
}
template <typename T>
Boolean fold_AnyMatch(void* _ref, Boolean current, T element){
	return current || this.predicate.test(element);
}
template <typename T, typename X, typename C>
Result<C, X> createInitial_ResultCollector(void* _ref){
	return new_Ok<C, X>(this.collector.createInitial());
}
template <typename T, typename X, typename C>
Result<C, X> fold_ResultCollector(void* _ref, Result<C, X> current, Result<T, X> element){
	return /*switch (current) {
				case Err<C, X> v -> new Err<C, X>(v.error());
				case Ok<C, X> v -> switch (element) {
					case Err<T, X> v1 -> new Err<C, X>(v1.error());
					case Ok<T, X> v1 -> new Ok<C, X>(this.collector.fold(v.value(), v1.value()));
				};
			}*/;
}
template <typename T>
Boolean createInitial_AllMatch(void* _ref){
	return true;
}
template <typename T>
Boolean fold_AllMatch(void* _ref, Boolean current, T element){
	return current && this.predicate.test(element);
}
template <typename K, typename V>
ListMap<K, V> createInitial_MapCollector(void* _ref){
	return new_ListMap<K, V>();
}
template <typename K, typename V>
ListMap<K, V> fold_MapCollector(void* _ref, ListMap<K, V> current, Tuple<K, V> element){
	return current.put(element.left(), element.right());
}
int main(){
	main_Main();
	return 0;
}