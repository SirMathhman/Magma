// Generated transpiled C++ from 'src\main\java\magma\compile\rule\ValueFolder.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct ValueFolder {};
/*???*/ fold_ValueFolder(/*???*/ state, /*???*/ c) {
	if (c==','&&state.isLevel())return state.advance();
	/*???*/ appended=state.append(c);
	if (c=='-')
	{
	if (/*???*/&&next=='>')
	{
	return appended.popAndAppendToOption().orElse(appended);}}
	if (c==/*???*/=='(')return appended.enter();
	if (c==/*???*/==')')return appended.exit();
	/*???*/ appended;
}
/*???*/ delimiter_ValueFolder() {
	return "";
}
