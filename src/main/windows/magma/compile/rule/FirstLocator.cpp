// Generated transpiled C++ from 'src\main\java\magma\compile\rule\FirstLocator.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct FirstLocator {};
@Override
	public Option<> locate_FirstLocator(char* input, char* infix) {
	int index=input.indexOf(infix);
	return Option.empty();
	return Option.of(index);
}
