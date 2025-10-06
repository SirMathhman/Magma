// Generated transpiled C++ from 'src\main\java\magma\compile\rule\ClosingParenthesesFolder.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct ClosingParenthesesFolder {};
DivideState fold_ClosingParenthesesFolder(DivideState state, char c) {
	if (c=='(')return state.append(c).enter();
	if (c==')')
	{
	DivideState exit=state.exit();
	return exit.advance();
	return exit.append(c);}
	return state.append(c);
}
char* delimiter_ClosingParenthesesFolder() {
	return ")";
}
