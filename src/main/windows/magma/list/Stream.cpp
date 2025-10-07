// Generated transpiled C++ from 'src\main\java\magma\list\Stream.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct Stream {};
Stream<Integer> range_Stream(int start, int end) {
	return new_???(/*???*/+offset);
}
Stream<R> map_Stream(R (*mapper)(T)) {
}
R fold_Stream(R initial, BiFunction<R, T, R> folder) {
}
R collect_Stream(Collector<T, R> collector) {
}
List<T> toList_Stream() {
	return collect(new_???());
}
void forEach_Stream(Consumer<T> consumer) {
}
Stream<R> flatMap_Stream(Stream<R> (*mapper)(T)) {
}
Stream<T> filter_Stream(Predicate<T> predicate) {
}
R reduce_Stream(R initial, BiFunction<R, T, R> folder) {
	return fold(initial, folder);
}
boolean allMatch_Stream(Predicate<T> predicate) {
}
boolean anyMatch_Stream(Predicate<T> predicate) {
}
