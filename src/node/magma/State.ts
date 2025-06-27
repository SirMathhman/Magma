
/*import java.util.Optional;*/
interface State {
	advance() : State {}
	append(c : char) : State {}
	unwrap() : ListLike<string> {}
	isLevel() : boolean {}
	enter() : State {}
	exit() : State {}
	isShallow() : boolean {}
	pop() : Optional<Tuple<State, Character>> {}
	popAndAppendToTuple() : Optional<Tuple<State, Character>> {}
	popAndAppendToOption() : Optional<State> {}
	peek() : Optional<Character> {}
	/**/}

