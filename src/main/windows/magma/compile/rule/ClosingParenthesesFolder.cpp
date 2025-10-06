// Generated transpiled C++ from 'src\main\java\magma\compile\rule\ClosingParenthesesFolder.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct ClosingParenthesesFolder {};
/*???*/ fold_ClosingParenthesesFolder(/*???*/ state, /*???*/ c) {
	if (c=='(')return state.append(c).enter();
	if (c==')')
	{
	/*???*/ exit=state.exit();
	if (exit.isLevel())return exit.advance();
	return exit.append(c);}
	return state.append(c);
}
/*???*/ delimiter_ClosingParenthesesFolder() {
	return "";
}
