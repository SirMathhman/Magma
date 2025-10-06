// Generated transpiled C++ from 'src\main\java\magma\option\Option.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct Option {};
Option<> of_Option(/*???*/ value) {
	return new_???(value);
}
Option<> empty_Option() {
	return new_???();
}
Option<> ofNullable_Option(/*???*/ value) {
	if (Objects.isNull(value))return new_???();
	return new_???(value);
}
Option<> map_Option(Function<> mapper) {
}
Option<> flatMap_Option(Function<> mapper) {
}
/*???*/ orElse_Option(/*???*/ other) {
}
Option<> or_Option(Supplier<> other) {
}
/*???*/ orElseGet_Option(Supplier<> other) {
}
