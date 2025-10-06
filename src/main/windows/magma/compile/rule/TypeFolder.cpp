// Generated transpiled C++ from 'src\main\java\magma\compile\rule\TypeFolder.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct TypeFolder {};
/*???*/ fold_TypeFolder(/*???*/ state, /*???*/ c) {
	if (c==' '&&state.isLevel())return state.advance();
	/*???*/ append=state.append(c);
	if (c=='<')return append.enter();
	if (c=='>')return append.exit();
	if (c=='(')return append.enter();
	if (c==')')return append.exit();
	/*???*/ append;
}
/*???*/ delimiter_TypeFolder() {
	return "";
}
