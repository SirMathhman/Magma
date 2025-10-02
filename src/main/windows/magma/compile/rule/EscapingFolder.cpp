// Generated transpiled C++ from 'src\main\java\magma\compile\rule\EscapingFolder.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct EscapingFolder {Folder folder;};
DivideState fold_EscapingFolder(DivideState state, char c) {
	if (/*c == '\'')*/
	return /*state.append(c)
															 .popAndAppendToTuple()
															 .map(this::foldEscape)
															 .flatMap(DivideState::popAndAppendToOption)
															 .orElse(state)*/;
	if (/*c == '\"')*/
	{
	/*DivideState current = state.append*/(/*c)*/;
	while (/*true)*/
	{
	/*final Option<Tuple<DivideState, Character>> tupleOption = current.popAndAppendToTuple*/(/*)*/;
	if (/*tupleOption instanceof Some<Tuple<DivideState, Character>>(Tuple<DivideState, Character> t0))*/
	{
	/*current = t0.left*/(/*)*/;
	if (/*t0.right() == '\\')*/
	/*current = current.popAndAppendToOption*/(/*).orElse(current)*/;
	if (/*t0.right() == '\"')*/
	break}
	else 
	break}
	return /*current*/;}
	return /*handleComments(state, c).orElseGet(() -> folder.fold(state, c))*/;
}
Option<DivideState> handleComments_EscapingFolder(DivideState state, char c) {
	// handle comments
	if (/*c == '/' && state.isLevel())*/
	return /*handleLineComments(state, c).or(() -> handleBlockComments(state, c))*/;
	return /*Option.empty()*/;
}
Option<DivideState> handleLineComments_EscapingFolder(DivideState state, char c) {
	if (/*state.peek() instanceof Some<Character>(Character next) && next == '/')*/
	{
	/*final DivideState withSlash = state.append*/(/*c)*/;
	/*DivideState current = withSlash.popAndAppendToOption*/(/*).orElse(state)*/;
	while (/*true)*/
	if (/*current.popAndAppendToTuple() instanceof Some<Tuple<DivideState, Character>>(
					Tuple<DivideState, Character> tuple
			))*/
	{
	/*current = tuple.left*/(/*)*/;
	if (/*tuple.right() == '\n')*/
	{
	/*current = current.advance*/(/*)*/;
	break}}
	else 
	break
	return /*Option.of(current)*/;}
	return /*Option.empty()*/;
}
Option<DivideState> handleBlockComments_EscapingFolder(DivideState state, char c) {
	if (/*!(state.peek() instanceof Some<Character>(Character next)) || next != '*')*/
	return /*Option.empty()*/;
	/*final DivideState withSlash = state.append*/(/*c)*/;
	/*DivideState current = withSlash.popAndAppendToOption*/(/*).orElse(state)*/;
	while (/*true)*/
	if (/*current.popAndAppendToTuple() instanceof Some<Tuple<DivideState, Character>>(
				Tuple<DivideState, Character> tuple
		))*/
	{
	/*current = tuple.left*/(/*)*/;
	if (/*tuple.right() == '*')*/
	if (/*current.peek() instanceof Some<Character>(Character tuple0) && tuple0 == '/')*/
	{
	/*current = current.popAndAppendToOption*/(/*).orElse(current).advance()*/;
	break}}
	else 
	break
	return /*new Some<>(current)*/;
}
char* delimiter_EscapingFolder() {
	return /*folder.delimiter()*/;
}
DivideState foldEscape_EscapingFolder(Tuple<DivideState, Character> tuple) {
	if (/*tuple.right() == '\\')*/
	return /*tuple.left().popAndAppendToOption().orElse(tuple.left())*/;
	else 
	return /*tuple.left()*/;
}
