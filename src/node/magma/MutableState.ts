/*package magma;*/
/*import java.util.Optional;*/
/*public */class MutableState /*implements State*/ {
	/*private final CharSequence input;*/
	/*private*/ segments : /*ListLike<String>*/ = /*Lists.empty()*/;
	/*private*/ buffer : string = "";
	/*private*/ depth : number = 0;
	/*private*/ index : number = 0;
	/*public MutableState*/(/*final CharSequence input*/) {/*
        this.input = input;
    */}
	/*@Override
    public State advance*/(/**/) {/*
        this.segments = this.segments.add(this.buffer);
        this.buffer = "";
        return this;
    */}
	/*@Override
    public State append*/(/*final char c*/) {/*
        this.buffer = this.buffer + c;
        return this;
    */}
	/*@Override
    public ListLike<String> unwrap*/(/**/) {/*
        return this.segments;
    */}
	/*@Override
    public boolean isLevel*/(/**/) {/*
        return 0 == this.depth;
    */}
	/*@Override
    public State enter*/(/**/) {/*
        this.depth++;
        return this;
    */}
	/*@Override
    public State exit*/(/**/) {/*
        this.depth--;
        return this;
    */}
	/*@Override
    public boolean isShallow*/(/**/) {/*
        return 1 == this.depth;
    */}
	/*@Override
    public Optional<Tuple<State, Character>> pop*/(/**/) {/*
        if (this.index >= this.input.length())
            return Optional.empty();

        final var c = this.input.charAt(this.index);
        this.index++;
        return Optional.of(new Tuple<>(this, c));
    */}
	/*@Override
    public Optional<Tuple<State, Character>> popAndAppendToTuple*/(/**/) {/*
        return this.pop().map(tuple -> new Tuple<>(tuple.left().append(tuple.right()), tuple.right()));
    */}
	/*@Override
    public Optional<State> popAndAppendToOption*/(/**/) {/*
        return this.popAndAppendToTuple().map(Tuple::left);
    */}
	/**/}
/**/
