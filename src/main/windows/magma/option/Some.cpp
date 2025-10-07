// Generated transpiled C++ from 'src\main\java\magma\option\Some.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct Some {/*???*/ value;};
Option</*???*/> map_Some(/*???*/ (*mapper)(/*???*/)) {
	return new_???(mapper.apply(value));
}
Option</*???*/> flatMap_Some(Option</*???*/> (*mapper)(/*???*/)) {
	return mapper.apply(value);
}
/*???*/ orElse_Some(/*???*/ other) {
	/*???*/ value;
}
Option</*???*/> or_Some(Supplier<Option</*???*/>> other) {
	/*???*/ this;
}
/*???*/ orElseGet_Some(Supplier</*???*/> other) {
	/*???*/ value;
}
Option</*???*/> filter_Some(Predicate</*???*/> predicate) {
	/*???*/ this;
	else
	return new_???();
}
