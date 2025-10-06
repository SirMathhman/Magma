// Generated transpiled C++ from 'src\main\java\magma\compile\rule\EscapingFolder.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct EscapingFolder {Folder folder;};
DivideState fold_EscapingFolder(DivideState state, char c) {
	return state.append(c)
				.popAndAppendToTuple()
				.map(this::foldEscape)
				.flatMap(DivideState::popAndAppendToOption)
				.orElse(state);
	if (c=='\"')
	{
	DivideState current=state.append(c);
	while (true)
	{
	final Option<> tupleOption=current.popAndAppendToTuple();
	if (/*???*/)
	{
	current=t0.left();
	if (t0.right()=='\\')current=current.popAndAppendToOption().orElse(current);
	if (t0.right()=='\"')
	break}
	else break;}
	return current;}
	return handleComments((state, c).orElseGet(() -> folder.fold(state, c));
}
private Option<> handleComments_EscapingFolder(DivideState state, char c) {
	if (c=='/'&&state.isLevel())return handleLineComments((state).or(() -> handleBlockComments(state, c));
	return Option.empty();
}
private Option<> handleLineComments_EscapingFolder(DivideState state) {
	if (/*???*/&&next=='/')
	while (true)
	{
	Option<> pop=state.pop();
	return Option.of(state);}
	return Option.empty();
}
private Option<> handleBlockComments_EscapingFolder(DivideState state, char c) {
	return Option.empty();
	DivideState withSlash=state.append(c);
	DivideState current=withSlash.popAndAppendToOption().orElse(state);
	while (true)
	if (/*???*/)
	{
	current=tuple.left();
	if (tuple.right()=='*')
	if (/*???*/&&tuple0=='/')
	{
	current=current.popAndAppendToOption().orElse(current).advance();
	break}}
	else break;
	new Some<>(current);
}
char* delimiter_EscapingFolder() {
	return folder.delimiter();
}
DivideState foldEscape_EscapingFolder(Tuple<> tuple) {
	return tuple.left().popAndAppendToOption().orElse(tuple.left());
	return tuple.left();
}
