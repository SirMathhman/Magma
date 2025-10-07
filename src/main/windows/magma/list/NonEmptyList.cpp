// Generated transpiled C++ from 'src\main\java\magma\list\NonEmptyList.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct NonEmptyList {};
NonEmptyList</*???*/> of_NonEmptyList(/*???*/ element) {
	return new_???(element, new_???());
}
NonEmptyList</*???*/> of_NonEmptyList(/*???*/ first, /*???*/ others) {
	return new_???(first, List.of(others));
}
Option<NonEmptyList</*???*/>> fromList_NonEmptyList(List</*???*/> list) {
	if (list.isEmpty())return new_???();
	return list.getFirst().map(/*???*/);
}
/*???*/ first_NonEmptyList() {
}
/*???*/ last_NonEmptyList() {
}
List</*???*/> rest_NonEmptyList() {
}
Stream</*???*/> stream_NonEmptyList() {
}
NonEmptyList</*???*/> addLast_NonEmptyList(/*???*/ element) {
}
/*???*/ size_NonEmptyList() {
}
Option</*???*/> get_NonEmptyList(/*???*/ index) {
}
List</*???*/> toList_NonEmptyList() {
}
