/*package magma;*/
/*import java.util.ArrayList;*/
/*import java.util.Collections;*/
/*import java.util.List;*/
/*import java.util.Optional;*/
/*public */class MutableState /*implements State*/ {
	/*private final List<String>*/ segments = /* new ArrayList<>()*/;
	/*private final CharSequence input;*/
	/*private StringBuilder*/ buffer = /* new StringBuilder()*/;
	/*private int*/ depth = /* 0*/;
	/*private int*/ index = /* 0*/;
	/*public MutableState(final CharSequence input) {
        this.input = input;
    }*/
	/*@Override public State advance() {
        this.segments.add(this.buffer.toString());
        this.buffer = new StringBuilder();
        return this;
    }*/
	/*@Override public State append(final char c) {
        this.buffer.append(c);
        return this;
    }*/
	/*@Override public List<String> unwrap() {
        return Collections.unmodifiableList(this.segments);
    }*/
	/*@Override public boolean isLevel() {
        return 0 == this.depth;
    }*/
	/*@Override public State enter() {
        this.depth++;
        return this;
    }*/
	/*@Override public State exit() {
        this.depth--;
        return this;
    }*/
	/*@Override public boolean isShallow() {
        return 1 == this.depth;
    }*/
	/*@Override public Optional<Tuple<State, Character>> pop() {
        if (this.index >= this.input.length())
            return Optional.empty();

        final var c = this.input.charAt(this.index);
        this.index++;
        return Optional.of(new Tuple<>(this, c));
    }*/
	/*@Override public Optional<Tuple<State, Character>> popAndAppendToTuple() {
        return this.pop().map(tuple -> new Tuple<>(tuple.left().append(tuple.right()), tuple.right()));
    }*/
	/*@Override public Optional<State> popAndAppendToOption() {
        return this.popAndAppendToTuple().map(Tuple::left);
    }*/
	/**/}
/**/
