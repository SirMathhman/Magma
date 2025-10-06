// Generated transpiled C++ from 'src\main\java\magma\compile\rule\LastLocator.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct LastLocator {};
@Override
	public Option<> locate_LastLocator(char* input, char* infix) {
	int index=input.lastIndexOf(infix);
	return Option.empty();
	return Option.of(index);
}
