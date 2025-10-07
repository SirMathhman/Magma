// Generated transpiled C++ from 'src\main\java\magma\list\ArrayNonEmptyList.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct ArrayNonEmptyList {/*???*/ head;List<> tail;};
/*???*/ first_ArrayNonEmptyList() {
	/*???*/ head;
}
/*???*/ last_ArrayNonEmptyList() {
	Option<> tailLast=tail.getLast();
	if (/*???*/)/*???*/ value;
	/*???*/ head;
}
List<> rest_ArrayNonEmptyList() {
	/*???*/ tail;
}
Stream<> stream_ArrayNonEmptyList() {
	List<> combined=new_???(tail);
	return combined.stream();
}
NonEmptyList<> addLast_ArrayNonEmptyList(/*???*/ element) {
	return new_???(head, tail.addLast(element));
}
/*???*/ size_ArrayNonEmptyList() {
	return /*???*/+tail.size();
}
Option<> get_ArrayNonEmptyList(/*???*/ index) {
	if (index==/*???*/)return new_???(head);
	return tail.get(/*???*/);
}
List<> toList_ArrayNonEmptyList() {
	return new_???(tail);
}
