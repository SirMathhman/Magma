// Generated transpiled C++ from 'src\main\java\magma\compile\collect\Accumulator.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct Accumulator {};
public Accumulator_Accumulator() {
	this();
}
Result<R, List<CompileError>> merge_Accumulator() {
	Accumulator<R> identity=new_???();
	Stream<T> stream=elements.stream();
	Accumulator<R> reduce=stream.reduce();
	return reduce.toResult();
}
Accumulator<R> fold_Accumulator() {
	return /*???*/;
}
Accumulator<T> addError_Accumulator() {
	errors.addLast();
	return this;
}
Accumulator<T> setValue_Accumulator() {
	return new_???();
}
Result<T, List<CompileError>> toResult_Accumulator() {
	return /*???*/;
}
