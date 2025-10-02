// Generated transpiled C++ from 'src\main\java\magma\compile\rule\DivideState.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct DivideState {/*public final List<String>*/ segments;/*private final String*/ input;/*private StringBuilder*/ buffer;/*private int*/ depth;/*private int*/ index;};
/*public*/ DivideState_DivideState(/*StringBuilder*/ buffer, /* int*/ depth, /* List<String>*/ segments, /* String*/ input) {
	/*this.buffer */=/* buffer*/;
	/*this.depth */=/* depth*/;
	/*this.segments */=/* segments*/;
	/*this.input */=/* input*/;
}
/*public*/ DivideState_DivideState(/*String*/ input) {
	/*this*/(/*new StringBuilder()*/, /* 0*/, /* new ArrayList<>()*/, /* input)*/;
}
/*Stream<String>*/ stream_DivideState() {
	/*return segments.stream*/(/*)*/;
}
/*DivideState*/ enter_DivideState() {
	/*this.depth */=/* depth + 1*/;
	return /*this*/;
}
/*DivideState*/ advance_DivideState() {
	/*segments.add*/(/*buffer.toString())*/;
	/*this.buffer */=/* new StringBuilder()*/;
	return /*this*/;
}
/*DivideState*/ append_DivideState(/*char*/ c) {
	/*buffer.append*/(/*c)*/;
	return /*this*/;
}
/*DivideState*/ exit_DivideState() {
	/*this.depth */=/* depth - 1*/;
	return /*this*/;
}
/*boolean*/ isShallow_DivideState() {
	/*return depth */=/*= 1*/;
}
/*boolean*/ isLevel_DivideState() {
	/*return depth */=/*= 0*/;
}
/*public Option<Character>*/ pop_DivideState() {
	/*if (index >*/=/* input.length()) return Option.empty()*/;
	/*final char c */=/* input.charAt(index)*/;
	/*index*/++;
	/*return Option.of*/(/*c)*/;
}
/*public Option<Tuple<DivideState, Character>>*/ popAndAppendToTuple_DivideState() {
	/*return pop*/(/*).map(popped -> new Tuple<>(append(popped), popped))*/;
}
/*public Option<DivideState>*/ popAndAppendToOption_DivideState() {
	/*return popAndAppendToTuple*/(/*).map(Tuple::left)*/;
}
/*public Option<Character>*/ peek_DivideState() {
	/*if */(/*index < input.length()) return new Some<>(input.charAt(index))*/;
	/*else return new None<>*/(/*)*/;
}
