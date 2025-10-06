// Generated transpiled C++ from 'src\main\java\magma\option\None.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct None {};
Option<> map_None(R (*mapper)(T)) {
	return new_???();
}
Option<> flatMap_None(Option<> (*mapper)(T)) {
	return new_???();
}
T orElse_None(T other) {
	return other;
}
Tuple<> toTuple_None(T other) {
	return new_???(false, other);
}
Option<> or_None(Supplier<> other) {
	return other.get();
}
T orElseGet_None(Supplier<> other) {
	return other.get();
}
