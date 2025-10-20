// File generated from 'JavaPath[path=.\src\main\java\magma\Collectors.java]'. This is not source code!
#include "Main.h"
template <typeparam T, typeparam C>
struct Collector {
};
template <typeparam T>
struct ListCollector {
};
struct Joiner {
	char* delimiter;
};
template <typeparam T>
struct AnyMatch {
	Predicate<T> predicate;
};
struct Collectors {
};
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
	return current.add(element);
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
int main(){
	main_Main();
	return 0;
}