// Generated transpiled C++ from 'src\main\java\magma\list\ArrayList.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct ArrayList {List<T> elements;};
public ArrayList_ArrayList() {
	this(new_???());
}
Stream<T> stream_ArrayList() {
	return new_???(/*???*/);
}
List<T> addLast_ArrayList(T element) {
	elements.add(element);
	return this;
}
List<T> copy_ArrayList() {
	return new_???(new_???(elements));
}
List<T> addAll_ArrayList(List<T> others) {
	return others.stream().fold(getThis(), /*???*/);
}
List<T> getThis_ArrayList() {
	return this;
}
int size_ArrayList() {
	return elements.size();
}
Option<T> get_ArrayList(int index) {
	if (/*???*/)return new_???(elements.get(index));
	else
	return new_???();
}
boolean isEmpty_ArrayList() {
	return elements.isEmpty();
}
Option<T> getLast_ArrayList() {
	if (elements.isEmpty())return new_???();
	return new_???(elements.getLast());
}
List<T> sort_ArrayList(Comparator<T> comparator) {
	elements.sort(comparator);
	return this;
}
Option<Tuple<List<T>, T>> removeLast_ArrayList() {
	if (elements.isEmpty())return new_???();
	T last=elements.removeLast();
	return new_???(new_???(this, last));
}
Option<T> getFirst_ArrayList() {
	if (elements.isEmpty())return new_???();
	return new_???(elements.getFirst());
}
List<T> subListOrEmpty_ArrayList(int start, int end) {
	return new_???(elements.subList(start, end));
}
