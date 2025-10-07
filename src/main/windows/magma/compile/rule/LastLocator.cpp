// Generated transpiled C++ from 'src\main\java\magma\compile\rule\LastLocator.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct LastLocator {};
Option<Integer> locate_LastLocator(String input, String infix) {
	int index=input.lastIndexOf(infix);
	if (index==/*???*/)return Option.empty();
	return Option.of(index);
}
