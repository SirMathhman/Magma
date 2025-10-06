// Generated transpiled C++ from 'src\main\java\magma\compile\rule\DivideState.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct DivideState {List<> segments;/*???*/ input;/*???*/ buffer;/*???*/ depth;/*???*/ index;};
/*???*/ DivideState_DivideState(/*???*/ buffer, /*???*/ depth, List<> segments, /*???*/ input) {
	this.buffer=buffer;
	this.depth=depth;
	this.segments=segments;
	this.input=input;
}
/*???*/ DivideState_DivideState(/*???*/ input) {
	this(new_???(), /*???*/, new_???(), input);
}
Stream<> stream_DivideState() {
	return segments.stream();
}
/*???*/ enter_DivideState() {
	this.depth=depth+/*???*/;
	/*???*/ this;
}
/*???*/ advance_DivideState() {
	segments.addLast(buffer.toString());
	this.buffer=new_???();
	/*???*/ this;
}
/*???*/ append_DivideState(/*???*/ c) {
	buffer.append(c);
	/*???*/ this;
}
/*???*/ exit_DivideState() {
	this.depth=/*???*/;
	/*???*/ this;
}
/*???*/ isShallow_DivideState() {
	return depth==/*???*/;
}
/*???*/ isLevel_DivideState() {
	return depth==/*???*/;
}
Option<> pop_DivideState() {
	if (/*???*/)return Option.empty();
	/*???*/ c=input.charAt(index);
	index++;
	return Option.of(c);
}
Option<> popAndAppendToTuple_DivideState() {
	return pop().map(/*???*/);
}
Option<> popAndAppendToOption_DivideState() {
	return popAndAppendToTuple().map(/*???*/);
}
Option<> peek_DivideState() {
	if (/*???*/)return new_???(input.charAt(index));
	else
	return new_???();
}
