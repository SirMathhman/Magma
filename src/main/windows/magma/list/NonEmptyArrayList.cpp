// Generated transpiled C++ from 'src\main\java\magma\list\NonEmptyArrayList.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct NonEmptyArrayList {List<> backing;};
List<> toList_NonEmptyArrayList() {
	/*???*/ backing;
}
Stream<> stream_NonEmptyArrayList() {
	return backing.stream();
}
NonEmptyList<> addLast_NonEmptyArrayList(/*???*/ element) {
	return new_???(backing.addLast(element));
}
NonEmptyList<> copy_NonEmptyArrayList() {
	return new_???(backing.copy());
}
NonEmptyList<> addAll_NonEmptyArrayList(List<> others) {
	return new_???(backing.addAll(others));
}
/*???*/ size_NonEmptyArrayList() {
	return backing.size();
}
Option<> get_NonEmptyArrayList(/*???*/ index) {
	return backing.get(index);
}
/*???*/ getLast_NonEmptyArrayList() {
	return backing.getLast().orElse(null);
}
/*???*/ getFirst_NonEmptyArrayList() {
	return backing.getFirst().orElse(null);
}
NonEmptyList<> sort_NonEmptyArrayList(Comparator<> comparator) {
	return new_???(backing.sort(comparator));
}
Option<> removeLast_NonEmptyArrayList() {
	return /*???*/;
}
