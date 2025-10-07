// Generated transpiled C++ from 'src\main\java\magma\compile\rule\EscapingFolder.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct EscapingFolder {/*???*/ folder;};
/*???*/ fold_EscapingFolder(/*???*/ state, /*???*/ c) {
	return handleSingleQuotes(state, c).or(/*???*/(state, c)).or(/*???*/(state, c)).orElseGet(/*???*/.fold(state, c));
}
Option</*???*/> handleSingleQuotes_EscapingFolder(/*???*/ state, /*???*/ c) {
	if (/*???*/)return Option.empty();
	return Option.of(state.append(c).popAndAppendToTuple().map(/*???*/).flatMap(/*???*/).orElse(state));
}
Option</*???*/> handleDoubleQuotes_EscapingFolder(/*???*/ state, /*???*/ c) {
	if (/*???*/)return Option.empty();
	/*???*/ current=state.append(c);
	while (true)
	{
	Option<Tuple</*???*/, /*???*/>> tupleOption=current.popAndAppendToTuple();
	if (/*???*/)
	{
	current==t0.left();
	if (t0.right()=='\\')current==current.popAndAppendToOption().orElse(current);
	if (t0.right()=='\"')
	break}
	/*???*/ break;}
	return Option.of(current);
}
Option</*???*/> handleComments_EscapingFolder(/*???*/ state, /*???*/ c) {
	if (c=='/'&&state.isLevel())return handleLineComments(state).or(/*???*/(state, c));
	return Option.empty();
}
Option</*???*/> handleLineComments_EscapingFolder(/*???*/ state) {
	if (/*???*/)return Option.empty();
	while (true)
	{
	/*???*/();
	if (/*???*/)return Option.of(state);}
}
Option</*???*/> handleBlockComments_EscapingFolder(/*???*/ state, /*???*/ c) {
	if (/*???*/)return Option.empty();
	/*???*/ withSlash=state.append(c);
	Tuple</*???*/, /*???*/> current=new_???(true, withSlash.popAndAppendToOption().orElse(state));
	while (current.left())
	current==handle(current.right());
	return new_???(current.right());
}
Tuple</*???*/, /*???*/> handle_EscapingFolder(/*???*/ current) {
	if (/*???*/)return new_???(false, current);
	/*???*/ temp=tuple.left();
	if (tuple.right()=='*'&&/*???*/&&tuple0=='/')return new_???(false, temp.popAndAppendToOption().orElse(temp).advance());
	return new_???(true, temp);
}
/*???*/ delimiter_EscapingFolder() {
	return folder.delimiter();
}
/*???*/ foldEscape_EscapingFolder(Tuple</*???*/, /*???*/> tuple) {
	if (tuple.right()=='\\')return tuple.left().popAndAppendToOption().orElse(tuple.left());
	else
	return tuple.left();
}
