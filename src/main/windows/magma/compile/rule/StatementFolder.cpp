// Generated transpiled C++ from 'src\main\java\magma\compile\rule\StatementFolder.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct StatementFolder {};
/*@Override
	public DivideState*/ fold_StatementFolder(/*DivideState*/ state, /* char*/ c) {
	/*final DivideState appended */=/* state.append(c)*/;
	/*if (c */=/*= ';' && appended.isLevel()) return appended.advance()*/;
	if (/*c == '}' && appended.isShallow())*/
	{
	/*if (appended.peek() instanceof Some<Character>(Character next) && next */=/*= ';')
				return appended.popAndAppendToOption().orElse(appended).advance().exit()*/;
	/*return appended.advance*/(/*).exit()*/;}
	/*if (c */=/*= '{' || c == '(') return appended.enter()*/;
	/*if (c */=/*= '}' || c == ')') return appended.exit()*/;
	return /*appended*/;
}
/*@Override
	public String*/ delimiter_StatementFolder() {
	return /*""*/;
}
