// Generated transpiled C++ from 'src\main\java\magma\compile\rule\StatementFolder.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct StatementFolder {};
/*???*/ fold_StatementFolder(/*???*/ state, /*???*/ c) {
	/*???*/ appended=state.append(c);
	if (c=='-')
	{
	if (/*???*/&&next=='>')
	{
	return state.popAndAppendToOption().orElse(state);}}
	if (c==';'&&appended.isLevel())return appended.advance();
	if (c=='}'&&appended.isShallow())
	{
	if (/*???*/&&next==';')return appended.popAndAppendToOption().orElse(appended).advance().exit();
	return appended.advance().exit();}
	if (c==/*???*/=='(')return appended.enter();
	if (c==/*???*/==')')return appended.exit();
	/*???*/ appended;
}
/*???*/ delimiter_StatementFolder() {
	return "";
}
