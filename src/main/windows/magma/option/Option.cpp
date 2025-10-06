// Generated transpiled C++ from 'src\main\java\magma\option\Option.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct Option {};
Option<> of_Option(T value) {
	return new_???(value);
}
Option<> empty_Option() {
	return new_???();
}
Option<> ofNullable_Option(T value) {
	if (Objects.isNull(value))return new_???();
	return new_???(value);
}
Option<> map_Option(R (*mapper)(T)) {
}
Option<> flatMap_Option(Option<> (*mapper)(T)) {
}
T orElse_Option(T other) {
}
Tuple<> toTuple_Option(T other) {
}
Option<> or_Option(Supplier<> other) {
}
T orElseGet_Option(Supplier<> other) {
}
