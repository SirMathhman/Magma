// Generated transpiled C++ from 'src\main\java\magma\list\ArrayList.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct ArrayList {List<> elements;};
/*???*/ ArrayList_ArrayList() {
	this(new_???());
}
Stream<> stream_ArrayList() {
	return new_???(/*???*/);
}
List<> addLast_ArrayList(/*???*/ element) {
	elements.add(element);
	/*???*/ this;
}
List<> copy_ArrayList() {
	return new_???(new_???(elements));
}
List<> addAll_ArrayList(List<> others) {
	return others.stream().fold(getThis(), /*???*/);
}
List<> getThis_ArrayList() {
	/*???*/ this;
}
/*???*/ size_ArrayList() {
	return elements.size();
}
/*???*/ getOrNull_ArrayList(/*???*/ index) {
	return elements.get(index);
}
/*???*/ isEmpty_ArrayList() {
	return elements.isEmpty();
}
Option<> getLast_ArrayList() {
	if (elements.isEmpty())return new_???();
	return new_???(elements.getLast());
}
/*???*/ getLastOrNull_ArrayList() {
	return getLast().orElseGet(/*???*/);
}
List<> sort_ArrayList(Comparator<> comparator) {
	elements.sort(comparator);
	/*???*/ this;
}
Option<> pop_ArrayList() {
	if (elements.isEmpty())return new_???();
	/*???*/ last=elements.removeLast();
	return new_???(new_???(this, last));
}
Option<> getFirst_ArrayList() {
	if (elements.isEmpty())return new_???();
	return new_???(elements.getFirst());
}
/*???*/ getFirstOrNull_ArrayList() {
	return getFirst().orElseGet(/*???*/);
}
List<> subListOrEmpty_ArrayList(/*???*/ start, /*???*/ end) {
	return new_???(elements.subList(start, end));
}
