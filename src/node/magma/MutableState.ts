class MutableState {
	segments : ListLike<Some[value=]> = /* Lists.empty()*/;
	buffer : string = "";
	depth : number = 0;
	index : number = 0;
	constructor () {
	}
	advance() : State {
		this.buffer = "";
		return this;
	}
	append() : State {
		return this;
	}
	unwrap() : ListLike<Some[value=]> {
	}
	isLevel() : boolean {
	}
	enter() : State {
		return this;
	}
	exit() : State {
		return this;
	}
	isShallow() : boolean {
	}
	pop() : Optional<Some[value=]> {
		c : any = /* this.input.charAt(this.index)*/;/*
        this.index++;*/
		return /*new Some<>(new Tuple<State, Character>(this, c))*/;
	}
	popAndAppendToTuple() : Optional<Some[value=]> {
	}
	popAndAppendToOption() : Optional<Some[value=]> {
	}
	peek() : Optional<Some[value=]> {
		else 
			return /*new None<>()*/;
	}
}

