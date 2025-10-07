// Generated transpiled C++ from 'src\main\java\magma\list\NonEmptyList.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct NonEmptyList {};
NonEmptyList<T> of_NonEmptyList(T element) {
	return new_???(element, new_???());
}
NonEmptyList<T> of_NonEmptyList(T first, T others) {
	return new_???(first, List.of(others));
}
Option<NonEmptyList<T>> fromList_NonEmptyList(List<T> list) {
	if (list.isEmpty())return new_???();
	return list.getFirst().map(/*???*/);
}
T first_NonEmptyList() {
}
T last_NonEmptyList() {
}
List<T> rest_NonEmptyList() {
}
Stream<T> stream_NonEmptyList() {
}
NonEmptyList<T> addLast_NonEmptyList(T element) {
}
int size_NonEmptyList() {
}
Option<T> get_NonEmptyList(int index) {
}
List<T> toList_NonEmptyList() {
}
