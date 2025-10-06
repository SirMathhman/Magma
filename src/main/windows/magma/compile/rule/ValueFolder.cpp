// Generated transpiled C++ from 'src\main\java\magma\compile\rule\ValueFolder.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct ValueFolder {};
DivideState fold_ValueFolder(DivideState state, char c) {
	return state.advance();
	DivideState appended=state.append(c);
	if (c=='-')
	{
	if (/*???*/&&next=='>')
	{
	return appended.popAndAppendToOption().orElse(appended);}}
	if (c=='<' || c == '(')return appended.enter();
	if (c=='>' || c == ')')return appended.exit();
	return appended;
}
char* delimiter_ValueFolder() {
	return "";
}
