// Generated transpiled C++ from 'src\main\java\magma\compile\rule\DivideState.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct DivideState {};
new StringBuilder_DivideState() {/*???*/
}
public DivideState_DivideState() {
	this.input=input;
}
Stream<TokenSequence> stream_DivideState() {
	return segments.stream();
}
DivideState enter_DivideState() {
	this.depth=depth+/*???*/;
	return this;
}
DivideState advance_DivideState() {
	segments.addLast();
	this.buffer=new_???();
	return this;
}
DivideState append_DivideState() {
	buffer.append();
	return this;
}
DivideState exit_DivideState() {
	this.depth=/*???*/;
	return this;
}
boolean isShallow_DivideState() {
	return depth==/*???*/;
}
boolean isLevel_DivideState() {
	return depth==/*???*/;
}
Option<Character> pop_DivideState() {
	Option<Character> maybeNext=input.charAt();
	if (/*???*/)
	{
	index++;}
	return maybeNext;
}
Option<Tuple<DivideState, Character>> popAndAppendToTuple_DivideState() {
	return pop().map();
}
Option<DivideState> popAndAppendToOption_DivideState() {
	return popAndAppendToTuple().map();
}
Option<Character> peek_DivideState() {
	return input.charAt();
}
