// Generated transpiled C++ from 'src\main\java\magma\compile\collect\Accumulator.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct Accumulator {/*Option<T>*/ option;/* List<CompileError>*/ errors;};
/*public*/ Accumulator_Accumulator() {
	/*this*/(/*new None<>()*/, /* new ArrayList<>())*/;
}
/*public static <T, R> Result<R, List<CompileError>>*/ merge_Accumulator(/*List<T>*/ elements, /*
																													 Function<T, Result<R, CompileError>>*/ mapper) {
	/*return elements.stream*/(/*).reduce(new Accumulator<R>()*/, /* (accumulator*/, /* rule) -> switch (mapper.apply(rule)) {
			case Err<R*/, /* CompileError> v -> accumulator.addError(v.error());
			case Ok<R, CompileError> v -> accumulator.setValue(v.value());
		}, (_, next) -> next).toResult()*/;
}
/*public Accumulator<T>*/ addError_Accumulator(/*CompileError*/ error) {
	/*errors.add*/(/*error)*/;
	return /*this*/;
}
/*public Accumulator<T>*/ setValue_Accumulator(/*T*/ value) {
	/*return new Accumulator<>*/(/*new Some<>(value)*/, /* errors)*/;
}
/*public Result<T, List<CompileError>>*/ toResult_Accumulator() {
	/*return switch */(/*option) {
			case None<T> _ -> new Err<>(errors);
			case Some<T> v -> new Ok<>(v.value());
		}*/;
}
