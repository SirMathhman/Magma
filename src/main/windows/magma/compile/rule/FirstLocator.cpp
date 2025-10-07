// Generated transpiled C++ from 'src\main\java\magma\compile\rule\FirstLocator.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct FirstLocator {};
Option</*???*/> locate_FirstLocator(/*???*/ input, /*???*/ infix) {
	/*???*/ index=input.indexOf(infix);
	if (index==/*???*/)return Option.empty();
	return Option.of(index);
}
