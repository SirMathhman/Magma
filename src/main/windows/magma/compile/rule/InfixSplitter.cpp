// Generated transpiled C++ from 'src\main\java\magma\compile\rule\InfixSplitter.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct InfixSplitter {/*String*/ infix;/* Locator*/ locator;};
/*@Override
	public Option<Tuple<String, String>>*/ split_InfixSplitter(/*String*/ input) {
	/*return switch (locator.locate(input, infix)) {
			case None<Integer> _ -> new None<>();
			case Some<Integer>(Integer index) -> {
				final String left */=/* input.substring(0, index);
				final String right = input.substring(index + infix.length());
				yield new Some<>(new Tuple<>(left, right));
			}
		}*/;
}
/*@Override
	public String*/ createErrorMessage_InfixSplitter() {
	return /*"Infix '" + infix + "' not present"*/;
}
/*@Override
	public String*/ merge_InfixSplitter(/*String*/ left, /* String*/ right) {
	return /*left + infix + right*/;
}
