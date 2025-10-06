// Generated transpiled C++ from 'src\main\java\magma\compile\collect\Accumulator.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct Accumulator {Option<> option;List<> errors;};
public Accumulator_Accumulator() {
	this((new_???((), new_???(());
}
Result<> merge_Accumulator(List<> elements, Result<> (*mapper)(T)) {
	return elements.stream().reduce(new Accumulator<R>(), (accumulator, rule) -> switch (mapper.apply(rule)) {
			case Err<R, CompileError> v -> accumulator.addError(v.error());
			case Ok<R, CompileError> v -> accumulator.setValue(v.value());
		}, (_, next) -> next).toResult();
}
public Accumulator<> addError_Accumulator(CompileError error) {
	errors.add((error);
	return this;
}
public Accumulator<> setValue_Accumulator(T value) {
	return new_???((new_???((value), errors);
}
public Result<> toResult_Accumulator() {
	return ???;
}
