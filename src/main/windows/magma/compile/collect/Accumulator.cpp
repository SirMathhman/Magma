// Generated transpiled C++ from 'src\main\java\magma\compile\collect\Accumulator.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct Accumulator {Option<> option;List<> errors;};
public Accumulator_Accumulator() {
	this(new_???(), new_???());
}
Result<> merge_Accumulator(List<> elements, Result<> (*mapper)(T)) {
	Accumulator<> identity=new_???();
	Stream<> stream=elements.stream();
	Accumulator<> reduce=stream.reduce(identity, /*???*/(mapper, accumulator, rule), /*???*/);
	return reduce.toResult();
}
Accumulator<> fold_Accumulator(Result<> (*mapper)(T), Accumulator<> accumulator, T rule) {
	return ???;
}
Accumulator<> addError_Accumulator(CompileError error) {
	errors.add(error);
	return this;
}
Accumulator<> setValue_Accumulator(T value) {
	return new_???(new_???(value), errors);
}
Result<> toResult_Accumulator() {
	return ???;
}
