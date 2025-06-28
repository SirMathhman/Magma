interface State {
	append() : State;
	unwrap() : ListLike<Some[value=]>;
	isLevel() : boolean;
	enter() : State;
	exit() : State;
	isShallow() : boolean;
	pop() : Optional<Some[value=]>;
	popAndAppendToTuple() : Optional<Some[value=]>;
	popAndAppendToOption() : Optional<Some[value=]>;
	peek() : Optional<Some[value=]>;
}

