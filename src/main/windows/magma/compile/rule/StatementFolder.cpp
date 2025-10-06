// Generated transpiled C++ from 'src\main\java\magma\compile\rule\StatementFolder.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct StatementFolder {};
DivideState fold_StatementFolder(DivideState state, char c) {
	DivideState appended=state.append(c);
	if (c=='-')
	{
	if (/*???*/&&next=='>')
	{
	return state.popAndAppendToOption().orElse(state);}}
	return appended.advance();
	if (c=='}'&&appended.isShallow())
	{
	if((/*???*/.orElse((appended).advance(().exit(();
	return appended.advance().exit();}
	if (c=='{' || c == '(')return appended.enter();
	if (c=='}' || c == ')')return appended.exit();
	return appended;
}
char* delimiter_StatementFolder() {
	return "";
}
