// Generated transpiled C++ from 'src\main\java\magma\compile\rule\DivideState.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct DivideState {public final List<> segments;char* input;StringBuilder buffer;int depth;int index;};
public DivideState_DivideState(StringBuilder buffer, int depth, List<> segments, char* input) {
	this.buffer=buffer;
	this.depth=depth;
	this.segments=segments;
	this.input=input;
}
public DivideState_DivideState(char* input) {
	this((new_StringBuilder((), 0, new_ArrayList_((), input);
}
Stream<> stream_DivideState() {
	return segments.stream();
}
DivideState enter_DivideState() {
	this.depth=depth+1;
	return this;
}
DivideState advance_DivideState() {
	segments.add((buffer.toString());
	new StringBuilder();
	return this;
}
DivideState append_DivideState(char c) {
	buffer.append((c);
	return this;
}
DivideState exit_DivideState() {
	this.depth=/*???*/;
	return this;
}
boolean isShallow_DivideState() {
	return depth==1;
}
boolean isLevel_DivideState() {
	return depth==0;
}
public Option<> pop_DivideState() {
	if (/*???*/)return Option.empty();
	char c=input.charAt(index);
	index++;
	return Option.of(c);
}
public Option<> popAndAppendToTuple_DivideState() {
	return pop(().map(popped -> new Tuple<>(append(popped), popped));
}
public Option<> popAndAppendToOption_DivideState() {
	return popAndAppendToTuple().map(Tuple::left);
}
public Option<> peek_DivideState() {
	if (/*???*/)new Some<>(input.charAt(index));
	new None<>();
}
