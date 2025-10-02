// Generated transpiled C++ from 'src\main\java\magma\compile\rule\InfixSplitter.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct InfixSplitter{char* infix;, Locator locator;};
Option<Tuple<String, String>> split_InfixSplitter(char* input) {/*
		return switch (locator.locate(input, infix)) {
			case None<Integer> _ -> new None<>();
			case Some<Integer>(Integer index) -> {
				final String left = input.substring(0, index);
				final String right = input.substring(index + infix.length());
				yield new Some<>(new Tuple<>(left, right));
			}
		};
	*/}
char* createErrorMessage_InfixSplitter() {/*
		return "Infix '" + infix + "' not present";
	*/}
char* merge_InfixSplitter(char* left, char* right) {/*
		return left + infix + right;
	*/}
