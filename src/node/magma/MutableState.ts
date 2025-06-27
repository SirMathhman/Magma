/*package magma;*/
/*import java.util.Optional;*/
/*public*/class MutableState/*implements State*/ {
	/*private final CharSequence input;*/
	/*private*/ segments : ListLike<string> = Lists.empty();
	/*private*/ buffer : string = "";
	/*private*/ depth : number = 0;
	/*private*/ index : number = 0;
	constructor (/*final CharSequence input*/) {
		this.input = input;/*
    */}
	/*@Override
    public*/ advance(/**/) : State {
		this.segments = this.segments.add(this.buffer);
		this.buffer = "";
		return this;/*
    */}
	/*@Override
    public*/ append(/*final char c*/) : State {
		this.buffer = this.buffer + c;
		return this;/*
    */}
	/*@Override
    public*/ unwrap(/**/) : ListLike<string> {
		return this.segments;/*
    */}
	/*@Override
    public*/ isLevel(/**/) : boolean {
		return 0 == this.depth;/*
    */}
	/*@Override
    public*/ enter(/**/) : State {/*
        this.depth++;*/
		return this;/*
    */}
	/*@Override
    public*/ exit(/**/) : State {/*
        this.depth--;*/
		return this;/*
    */}
	/*@Override
    public*/ isShallow(/**/) : boolean {
		return 1 == this.depth;/*
    */}
	/*@Override
    public*/ pop(/**/) : Optional<Tuple<State, Character>> {
		if (this.index >= this.input.length())/*
            return Optional.empty();*/
		/*final*/ c : any = this.input.charAt(this.index);/*
        this.index++;*/
		return Optional.of(new Tuple<>(this, c));/*
    */}
	/*@Override
    public*/ popAndAppendToTuple(/**/) : Optional<Tuple<State, Character>> {
		return this.pop().map(/*tuple -> new Tuple<>(tuple.left().append(tuple.right()), tuple.right())*/);/*
    */}
	/*@Override
    public*/ popAndAppendToOption(/**/) : Optional<State> {
		return this.popAndAppendToTuple().map(/*Tuple::left*/);/*
    */}
	/*@Override
    public*/ peek(/**/) : Optional<Character> {
		if (this.index < this.input.length())/*
            return Optional.of(this.input.charAt(this.index));*//*
        else
            return Optional.empty();*//*
    */}
	/**/}
/**/
