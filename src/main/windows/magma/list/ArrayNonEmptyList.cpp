// Generated transpiled C++ from 'src\main\java\magma\list\ArrayNonEmptyList.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
template<typename T>
struct ArrayNonEmptyList {};
T first_ArrayNonEmptyList() {
	return head;
}
T last_ArrayNonEmptyList() {
	Option<T> tailLast=tail.getLast();
	if (/*???*/)return value;
	return head;
}
List<T> rest_ArrayNonEmptyList() {
	return tail;
}
Stream<T> stream_ArrayNonEmptyList() {
	List<T> combined=new_???();
	return combined.stream();
}
NonEmptyList<T> addLast_ArrayNonEmptyList() {
	return new_???();
}
int size_ArrayNonEmptyList() {
	return /*???*/+tail.size();
}
Option<T> get_ArrayNonEmptyList() {
	if (index==/*???*/)return new_???();
	return tail.get();
}
List<T> toList_ArrayNonEmptyList() {
	return new_???();
}
