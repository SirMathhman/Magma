// Generated transpiled C++ from 'src\main\java\magma\option\Option.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct Option {};
Option<T> of_Option(T value) {
	return new_???(value);
}
Option<T> empty_Option() {
	return new_???();
}
Option<T> ofNullable_Option(T value) {
	if (Objects.isNull(value))return new_???();
	return new_???(value);
}
Option<R> map_Option(R (*mapper)(T)) {
}
Option<R> flatMap_Option(Option<R> (*mapper)(T)) {
}
T orElse_Option(T other) {
}
Option<T> or_Option(Supplier<Option<T>> other) {
}
T orElseGet_Option(Supplier<T> other) {
}
Option<T> filter_Option(Predicate<T> predicate) {
}
