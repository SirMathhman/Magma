// Generated transpiled C++ from 'src\main\java\magma\compile\rule\ValueFolder.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct ValueFolder {};
DivideState fold_ValueFolder(DivideState state, char c) {
	if (/*c == ',' && state.isLevel())*/)
	return /*state.advance())*/;
	if (/*c == '<')*/)
	return /*state.enter()).append(c))*/;
	if (/*c == '>')*/)
	return /*state.exit()).append(c))*/;
	return /*state.append(c)*/;
}
char* delimiter_ValueFolder() {
	return /*", "*/;
}
