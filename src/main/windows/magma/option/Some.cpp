// Generated transpiled C++ from 'src\main\java\magma\option\Some.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct Some {T value;};
Option<R> map_Some(R (*mapper)(T)) {
	return new_???(mapper.apply(value));
}
Option<R> flatMap_Some(Option<R> (*mapper)(T)) {
	return mapper.apply(value);
}
T orElse_Some(T other) {
	return value;
}
Option<T> or_Some(Supplier<Option<T>> other) {
	return this;
}
T orElseGet_Some(Supplier<T> other) {
	return value;
}
Option<T> filter_Some(Predicate<T> predicate) {
	return this;
	else
	return new_???();
}
