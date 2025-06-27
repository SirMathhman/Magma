
class MutableState {
	/*private final CharSequence input;*/
	private let segments : ListLike<string> = Lists.empty();
	private let buffer : string = "";
	private let depth : number = 0;
	private let index : number = 0;
	constructor (final input : CharSequence) {
		this.input = input;
	}
	public advance() : State {
		this.segments = this.segments.add(this.buffer);
		this.buffer = "";
		return this;
	}
	public append(final c : char) : State {
		this.buffer = this.buffer + c;
		return this;
	}
	public unwrap() : ListLike<string> {
		return this.segments;
	}
	public isLevel() : boolean {
		return 0 == this.depth;
	}
	public enter() : State {/*
        this.depth++;*/
		return this;
	}
	public exit() : State {/*
        this.depth--;*/
		return this;
	}
	public isShallow() : boolean {
		return 1 == this.depth;
	}
	public pop() : Optional<Tuple<State, Character>> {
		if (this.index >= this.input.length())
			return new None<>();
		final let c : any = this.input.charAt(this.index);/*
        this.index++;*/
		return new Some<>(new Tuple<State, Character>(this, c));
	}
	public popAndAppendToTuple() : Optional<Tuple<State, Character>> {
		return this.pop().map(tuple => new Tuple<>(tuple.left().append(tuple.right()), tuple.right()));
	}
	public popAndAppendToOption() : Optional<State> {
		return this.popAndAppendToTuple().map(arg => Tuple.left(arg));
	}
	public peek() : Optional<Character> {
		if (this.index < this.input.length())
			return new Some<>(this.input.charAt(this.index));
		else 
			return new None<>();
	}
}

