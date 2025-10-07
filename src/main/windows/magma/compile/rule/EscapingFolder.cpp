// Generated transpiled C++ from 'src\main\java\magma\compile\rule\EscapingFolder.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct EscapingFolder {};
DivideState fold_EscapingFolder() {
	return handleSingleQuotes().or().or().orElseGet();
}
Option<DivideState> handleSingleQuotes_EscapingFolder() {
	if (/*???*/)return Option.empty();
	return Option.of();
}
Option<DivideState> handleDoubleQuotes_EscapingFolder() {
	if (/*???*/)return Option.empty();
	DivideState current=state.append();
	while (true)
	{
	Option<Tuple<DivideState, Character>> tupleOption=current.popAndAppendToTuple();
	if (/*???*/)
	{
	current==t0.left();
	if (t0.right()=='\\')current==current.popAndAppendToOption().orElse();
	if (t0.right()=='\"')
	break}
	else break;}
	return Option.of();
}
Option<DivideState> handleComments_EscapingFolder() {
	if (c=='/'&&state.isLevel())return handleLineComments().or();
	return Option.empty();
}
Option<DivideState> handleLineComments_EscapingFolder() {
	if (/*???*/)return Option.empty();
	while (true)
	{
	/*???*/();
	if (/*???*/)return Option.of();}
}
Option<DivideState> handleBlockComments_EscapingFolder() {
	if (/*???*/)return Option.empty();
	DivideState withSlash=state.append();
	Tuple<Boolean, DivideState> current=new_???();
	while (current.left())
	current==handle();
	return new_???();
}
Tuple<Boolean, DivideState> handle_EscapingFolder() {
	if (/*???*/)return new_???();
	DivideState temp=tuple.left();
	if (tuple.right()=='*'&&/*???*/&&tuple0=='/')return new_???();
	return new_???();
}
String delimiter_EscapingFolder() {
	return folder.delimiter();
}
DivideState foldEscape_EscapingFolder() {
	if (tuple.right()=='\\')return tuple.left().popAndAppendToOption().orElse();
	else
	return tuple.left();
}
