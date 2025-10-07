// Generated transpiled C++ from 'src\main\java\magma\compile\rule\ClosingParenthesesFolder.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct ClosingParenthesesFolder {};
DivideState fold_ClosingParenthesesFolder() {
	if (c=='(')
	{
	return state.append().enter();}
	if (c==')')
	{
	DivideState exit=state.exit();
	if (exit.isLevel())
	{
	return exit.advance();}
	return exit.append();}
	return state.append();
}
String delimiter_ClosingParenthesesFolder() {
	return "";
}
