// Generated transpiled C++ from 'src\main\java\magma\list\ArrayNonEmptyList.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct ArrayNonEmptyList {T head;List<T> tail;};
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
	List<T> combined=new_???(tail);
	return combined.stream();
}
NonEmptyList<T> addLast_ArrayNonEmptyList(T element) {
	return new_???(head, tail.addLast(element));
}
int size_ArrayNonEmptyList() {
	return /*???*/+tail.size();
}
Option<T> get_ArrayNonEmptyList(int index) {
	if (index==/*???*/)return new_???(head);
	return tail.get(/*???*/);
}
List<T> toList_ArrayNonEmptyList() {
	return new_???(tail);
}
