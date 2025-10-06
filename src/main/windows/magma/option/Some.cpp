// Generated transpiled C++ from 'src\main\java\magma\option\Some.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct Some {T value;};
Option<> map_Some(R (*mapper)(T)) {
	return new_???(mapper.apply(value));
}
Option<> flatMap_Some(Option<> (*mapper)(T)) {
	return mapper.apply(value);
}
T orElse_Some(T other) {
	return value;
}
Tuple<> toTuple_Some(T other) {
	return new_???(true, value);
}
Option<> or_Some(Supplier<> other) {
	return this;
}
T orElseGet_Some(Supplier<> other) {
	return value;
}
