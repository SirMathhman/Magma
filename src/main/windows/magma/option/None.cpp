// Generated transpiled C++ from 'src\main\java\magma\option\None.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct None {};
Option<R> map_None(R (*mapper)(T)) {
	return new_???();
}
Option<R> flatMap_None(Option<R> (*mapper)(T)) {
	return new_???();
}
T orElse_None(T other) {
	return other;
}
Option<T> or_None(Supplier<Option<T>> other) {
	return other.get();
}
T orElseGet_None(Supplier<T> other) {
	return other.get();
}
Option<T> filter_None(Predicate<T> predicate) {
	return this;
}
