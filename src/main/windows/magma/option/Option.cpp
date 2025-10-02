// Generated transpiled C++ from 'src\main\java\magma\option\Option.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
template<typename T>
struct Option {map(Function<T, R> mapper);flatMap(Function<T, Option<R>> mapper);/*orElse(T*/ other);};
template<typename T>
Option<T> of_Option(T value) {
	return /*new Some<>(value)*/;
}
template<typename T>
Option<T> empty_Option() {
	return /*new None<>()*/;
}
template<typename T>
Option<T> ofNullable_Option(T value) {
	return /*Objects.isNull(value) ? new None<>() : new Some<>(value)*/;
}
