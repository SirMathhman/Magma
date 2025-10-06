// Generated transpiled C++ from 'src\main\java\magma\compile\rule\BraceStartFolder.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct BraceStartFolder {};
DivideState fold_BraceStartFolder(DivideState state, char c) {
	if (c=='{')
	{
	DivideState entered=state.enter();
	if (entered.isShallow())return entered.advance();
	return entered.append(c);}
	DivideState state1=state.append(c);
	if (c=='(')return state1.enter();
	if (c==/*???*/==')')return state1.exit();
	return state1;
}
char* delimiter_BraceStartFolder() {
	return "";
}
