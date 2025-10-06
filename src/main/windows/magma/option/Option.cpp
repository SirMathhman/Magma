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
struct None {};
Option<> map_None(Function<> mapper) {
	return new_???();
}
Option<> flatMap_None(Function<> mapper) {
	return new_???();
}
/*???*/ orElse_None(/*???*/ other) {
	/*???*/ other;
}
Option<> or_None(Supplier<> other) {
	return other.get();
}
/*???*/ orElseGet_None(Supplier<> other) {
	return other.get();
}
struct Some {/*???*/ value;};
Option<> map_Some(Function<> mapper) {
	return new_???(mapper.apply(value));
}
Option<> flatMap_Some(Function<> mapper) {
	return mapper.apply(value);
}
/*???*/ orElse_Some(/*???*/ other) {
	/*???*/ value;
}
Option<> or_Some(Supplier<> other) {
	/*???*/ this;
}
/*???*/ orElseGet_Some(Supplier<> other) {
	/*???*/ value;
}
