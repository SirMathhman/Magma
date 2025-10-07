// Generated transpiled C++ from 'src\main\java\magma\compile\rule\ValueFolder.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct ValueFolder {};
DivideState fold_ValueFolder() {
	if (c==','&&state.isLevel())return state.advance();
	DivideState appended=state.append();
	if (c=='-')if (/*???*/&&next=='>')return appended.popAndAppendToOption().orElse();
	if (c==/*???*/=='(')return appended.enter();
	if (c==/*???*/==')')return appended.exit();
	return appended;
}
String delimiter_ValueFolder() {
	return "";
}
