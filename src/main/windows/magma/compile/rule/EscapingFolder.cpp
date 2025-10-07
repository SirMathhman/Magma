// Generated transpiled C++ from 'src\main\java\magma\compile\rule\EscapingFolder.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct EscapingFolder {Folder folder;};
DivideState fold_EscapingFolder(DivideState state, char c) {
	return handleSingleQuotes(state, c).or(/*???*/(state, c)).or(/*???*/(state, c)).orElseGet(/*???*/.fold(state, c));
}
Option<DivideState> handleSingleQuotes_EscapingFolder(DivideState state, char c) {
	if (/*???*/)return Option.empty();
	return Option.of(state.append(c).popAndAppendToTuple().map(/*???*/).flatMap(/*???*/).orElse(state));
}
Option<DivideState> handleDoubleQuotes_EscapingFolder(DivideState state, char c) {
	if (/*???*/)return Option.empty();
	DivideState current=state.append(c);
	while (true)
	{
	Option<Tuple<DivideState, Character>> tupleOption=current.popAndAppendToTuple();
	if (/*???*/)
	{
	current==t0.left();
	if (t0.right()=='\\')current==current.popAndAppendToOption().orElse(current);
	if (t0.right()=='\"')
	break}
	else break;}
	return Option.of(current);
}
Option<DivideState> handleComments_EscapingFolder(DivideState state, char c) {
	if (c=='/'&&state.isLevel())return handleLineComments(state).or(/*???*/(state, c));
	return Option.empty();
}
Option<DivideState> handleLineComments_EscapingFolder(DivideState state) {
	if (/*???*/)return Option.empty();
	while (true)
	{
	/*???*/();
	if (/*???*/)return Option.of(state);}
}
Option<DivideState> handleBlockComments_EscapingFolder(DivideState state, char c) {
	if (/*???*/)return Option.empty();
	DivideState withSlash=state.append(c);
	Tuple<Boolean, DivideState> current=new_???(true, withSlash.popAndAppendToOption().orElse(state));
	while (current.left())
	current==handle(current.right());
	return new_???(current.right());
}
Tuple<Boolean, DivideState> handle_EscapingFolder(DivideState current) {
	if (/*???*/)return new_???(false, current);
	DivideState temp=tuple.left();
	if (tuple.right()=='*'&&/*???*/&&tuple0=='/')return new_???(false, temp.popAndAppendToOption().orElse(temp).advance());
	return new_???(true, temp);
}
String delimiter_EscapingFolder() {
	return folder.delimiter();
}
DivideState foldEscape_EscapingFolder(Tuple<DivideState, Character> tuple) {
	if (tuple.right()=='\\')return tuple.left().popAndAppendToOption().orElse(tuple.left());
	else
	return tuple.left();
}
