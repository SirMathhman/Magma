// Generated transpiled C++ from 'src\main\java\magma\compile\collect\Accumulator.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct Accumulator {Option<> option;List<> errors;};
/*???*/ Accumulator_Accumulator() {
	this(new_???(), new_???());
}
Result<> merge_Accumulator(List<> elements, Function<> mapper) {
	Accumulator<> identity=new_???();
	Stream<> stream=elements.stream();
	Accumulator<> reduce=stream.reduce(identity, /*???*/(mapper, accumulator, rule));
	return reduce.toResult();
}
Accumulator<> fold_Accumulator(Function<> mapper, Accumulator<> accumulator, /*???*/ rule) {
	return /*???*/;
}
Accumulator<> addError_Accumulator(/*???*/ error) {
	errors.addLast(error);
	/*???*/ this;
}
Accumulator<> setValue_Accumulator(/*???*/ value) {
	return new_???(new_???(value), errors);
}
Result<> toResult_Accumulator() {
	return /*???*/;
}
