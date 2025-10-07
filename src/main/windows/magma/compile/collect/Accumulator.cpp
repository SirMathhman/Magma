// Generated transpiled C++ from 'src\main\java\magma\compile\collect\Accumulator.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct Accumulator {Option<T> option;List<CompileError> errors;};
public Accumulator_Accumulator() {
	this(new_???(), new_???());
}
Result<R, List<CompileError>> merge_Accumulator(List<T> elements, Result<R, CompileError> (*mapper)(T)) {
	Accumulator<R> identity=new_???();
	Stream<T> stream=elements.stream();
	Accumulator<R> reduce=stream.reduce(identity, /*???*/(mapper, accumulator, rule));
	return reduce.toResult();
}
Accumulator<R> fold_Accumulator(Result<R, CompileError> (*mapper)(T), Accumulator<R> accumulator, T rule) {
	return /*???*/;
}
Accumulator<T> addError_Accumulator(CompileError error) {
	errors.addLast(error);
	return this;
}
Accumulator<T> setValue_Accumulator(T value) {
	return new_???(new_???(value), errors);
}
Result<T, List<CompileError>> toResult_Accumulator() {
	return /*???*/;
}
