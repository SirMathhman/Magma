

class MutableState {
	/*private final CharSequence input;*/
	let segments : ListLike<string> = Lists.empty();
	let buffer : string = "";
	let depth : number = 0;
	let index : number = 0;
	constructor (input : CharSequence) {
		this.input = input;}
	advance() : State {
		this.segments = this.segments.add(this.buffer);
		this.buffer = "";
		return this;}
	append(c : char) : State {
		this.buffer = this.buffer + c;
		return this;}
	unwrap() : ListLike<string> {
		return this.segments;}
	isLevel() : boolean {
		return 0 == this.depth;}
	enter() : State {/*
        this.depth++;*/
		return this;}
	exit() : State {/*
        this.depth--;*/
		return this;}
	isShallow() : boolean {
		return 1 == this.depth;}
	pop() : Optional<Tuple<State, Character>> {
		if (this.index >= this.input.length())/*
            return Optional.empty();*/
		let c : any = this.input.charAt(this.index);/*
        this.index++;*/
		return Optional.of(new Tuple<>(this, c));}
	popAndAppendToTuple() : Optional<Tuple<State, Character>> {
		return this.pop().map(tuple => new Tuple<>(tuple.left().append(tuple.right()), tuple.right()));}
	popAndAppendToOption() : Optional<State> {
		return this.popAndAppendToTuple().map(/*Tuple::left*/);}
	peek() : Optional<Character> {
		if (this.index < this.input.length())/*
            return Optional.of(this.input.charAt(this.index));*//*
        else
            return Optional.empty();*/}
}

