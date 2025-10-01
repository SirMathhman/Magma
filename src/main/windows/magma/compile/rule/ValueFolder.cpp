struct ValueFolder() implements Folder{};
DivideState fold_ValueFolder() implements Folder(DivideState state, char c) {/*
		if (c == ',' && state.isLevel()) return state.advance(); if (c == '<') return state.enter();
		if (c == '>') return state.exit(); return state.append(c);
	*/}
char* delimiter_ValueFolder() implements Folder() {/*
		return ", ";
	*/}
