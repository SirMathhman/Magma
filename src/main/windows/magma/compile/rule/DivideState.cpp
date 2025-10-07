// Generated transpiled C++ from 'src\main\java\magma\compile\rule\DivideState.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct DivideState {};
public DivideState_DivideState() {
	this.buffer=buffer;
	this.depth=depth;
	this.segments=segments;
	this.input=input;
}
public DivideState_DivideState() {
	this();
}
Stream<String> stream_DivideState() {
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
	if (/*???*/)
	{
	return Option.empty();}
	char c=input.charAt();
	index++;
	return Option.of();
}
Option<Tuple<DivideState, Character>> popAndAppendToTuple_DivideState() {
	return pop().map();
}
Option<DivideState> popAndAppendToOption_DivideState() {
	return popAndAppendToTuple().map();
}
Option<Character> peek_DivideState() {
	if (/*???*/)
	{
	return new_???();}
	else
	return new_???();
}
