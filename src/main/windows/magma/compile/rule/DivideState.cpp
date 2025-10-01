// Generated transpiled C++ from 'src\main\java\magma\compile\rule\DivideState.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
template<>
struct DivideState<>{List<String> segments;, char* input;, StringBuilder buffer;, int depth;, int index;};
template<>
public DivideState_DivideState(StringBuilder buffer, int depth, ListString segments, char* input) {/*
		this.buffer = buffer; this.depth = depth; this.segments = segments; this.input = input;
	*/}
template<>
public DivideState_DivideState(char* input) {/*
		this(new StringBuilder(), 0, new ArrayList<>(), input);
	*/}
template<>
Stream<String> stream_DivideState() {/*
		return segments.stream();
	*/}
template<>
DivideState enter_DivideState() {/*
		this.depth = depth + 1; return this;
	*/}
template<>
DivideState advance_DivideState() {/*
		segments.add(buffer.toString()); this.buffer = new StringBuilder(); return this;
	*/}
template<>
DivideState append_DivideState(char c) {/*
		buffer.append(c); return this;
	*/}
template<>
DivideState exit_DivideState() {/*
		this.depth = depth - 1; return this;
	*/}
template<>
boolean isShallow_DivideState() {/*
		return depth == 1;
	*/}
template<>
boolean isLevel_DivideState() {/*
		return depth == 0;
	*/}
template<>
Option<Character> pop_DivideState() {/*
		if (index >= input.length()) return Option.empty(); final char c = input.charAt(index); index++;
		return Option.of(c);
	*/}
template<>
/*Character>>*/ popAndAppendToTuple_DivideState() {/*
		return pop().map(popped -> new Tuple<>(append(popped), popped));
	*/}
template<>
Option<DivideState> popAndAppendToOption_DivideState() {/*
		return popAndAppendToTuple().map(Tuple::left);
	*/}
