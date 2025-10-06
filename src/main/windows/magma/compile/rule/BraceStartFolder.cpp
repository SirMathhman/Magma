// Generated transpiled C++ from 'src\main\java\magma\compile\rule\BraceStartFolder.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct BraceStartFolder {};
/*???*/ fold_BraceStartFolder(/*???*/ state, /*???*/ c) {
	if (c=='{')
	{
	/*???*/ entered=state.enter();
	if (entered.isShallow())return entered.advance();
	return entered.append(c);}
	/*???*/ state1=state.append(c);
	if (c=='(')return state1.enter();
	if (c==/*???*/==')')return state1.exit();
	/*???*/ state1;
}
/*???*/ delimiter_BraceStartFolder() {
	return "";
}
