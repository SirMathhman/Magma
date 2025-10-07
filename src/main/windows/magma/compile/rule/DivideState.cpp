// Generated transpiled C++ from 'src\main\java\magma\compile\rule\DivideState.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct DivideState {List<String> segments;String input;StringBuilder buffer;int depth;int index;};
public DivideState_DivideState(StringBuilder buffer, int depth, List<String> segments, String input) {
	this.buffer=buffer;
	this.depth=depth;
	this.segments=segments;
	this.input=input;
}
public DivideState_DivideState(String input) {
	this(new_???(), /*???*/, new_???(), input);
}
Stream<String> stream_DivideState() {
	return segments.stream();
}
DivideState enter_DivideState() {
	this.depth=depth+/*???*/;
	return this;
}
DivideState advance_DivideState() {
	segments.addLast(buffer.toString());
	this.buffer=new_???();
	return this;
}
DivideState append_DivideState(char c) {
	buffer.append(c);
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
	if (/*???*/)return Option.empty();
	char c=input.charAt(index);
	index++;
	return Option.of(c);
}
Option<Tuple<DivideState, Character>> popAndAppendToTuple_DivideState() {
	return pop().map(/*???*/);
}
Option<DivideState> popAndAppendToOption_DivideState() {
	return popAndAppendToTuple().map(/*???*/);
}
Option<Character> peek_DivideState() {
	if (/*???*/)return new_???(input.charAt(index));
	else
	return new_???();
}
