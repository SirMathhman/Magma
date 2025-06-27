
class MutableState {
	/*private final CharSequence input;*/
	private ListLike<String> segments = Lists.empty();
	private String buffer = "";
	private int depth = 0;
	private int index = 0;
	constructor (final CharSequence input) {
		this.input = input;
	}
	/*@Override
    public State advance*/() {
		this.segments = this.segments.add(this.buffer);
		this.buffer = "";
		return this;
	}
	/*@Override
    public State append*/(final char c) {
		this.buffer = this.buffer + c;
		return this;
	}
	/*@Override
    public ListLike<String> unwrap*/() {
		return this.segments;
	}
	/*@Override
    public boolean isLevel*/() {
		return 0 == this.depth;
	}
	/*@Override
    public State enter*/() {/*
        this.depth++;*/
		return this;
	}
	/*@Override
    public State exit*/() {/*
        this.depth--;*/
		return this;
	}
	/*@Override
    public boolean isShallow*/() {
		return 1 == this.depth;
	}
	/*@Override
    public Optional<Tuple<State, Character>> pop*/() {
		if (this.index >= this.input.length())
			return new None<>();
		final var c = this.input.charAt(this.index);/*
        this.index++;*/
		return new Some<>(new Tuple<State, Character>(this, c));
	}
	/*@Override
    public Optional<Tuple<State, Character>> popAndAppendToTuple*/() {
		return this.pop().map(tuple => new Tuple<>(tuple.left().append(tuple.right()), tuple.right()));
	}
	/*@Override
    public Optional<State> popAndAppendToOption*/() {
		return this.popAndAppendToTuple().map(arg => Tuple.left(arg));
	}
	/*@Override
    public Optional<Character> peek*/() {
		if (this.index < this.input.length())
			return new Some<>(this.input.charAt(this.index));
		else 
			return new None<>();
	}
}

