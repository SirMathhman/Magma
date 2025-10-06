// Generated transpiled C++ from 'src\main\java\magma\compile\rule\TypeFolder.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct TypeFolder {};
DivideState fold_TypeFolder(DivideState state, char c) {
	return state.advance();
	DivideState append=state.append(c);
	if (c=='<')return append.enter();
	if (c=='>')return append.exit();
	if (c=='(')return append.enter();
	if (c==')')return append.exit();
	return append;
}
char* delimiter_TypeFolder() {
	return "";
}
