/*import magma.Tuple;*/
/*import java.util.ArrayList;*/
/*import java.util.Collection;*/
/*import java.util.Optional;*/
/*import java.util.stream.Stream;*/
export class MutableDivideState implements DivideState {
	/*private final Collection<String> segments = new ArrayList<>()*/;
	/*private final CharSequence input*/;
	/*private int index = 0*/;
	/*private StringBuilder buffer = new StringBuilder()*/;
	/*private int depth = 0*/;
	/*public MutableDivideState(final CharSequence input) {
        this.input = input;
    }*/
	/*@Override
    public DivideState advance() {
        this.segments.add(this.buffer.toString());
        this.buffer = new StringBuilder();
        return this;
    }*/
	/*@Override
    public DivideState append(final char c) {
        this.buffer.append(c);
        return this;
    }*/
	/*@Override
    public Stream<String> stream() {
        return this.segments.stream();
    }*/
	/*@Override
    public boolean isLevel() {
        return 0 == this.depth;
    }*/
	/*@Override
    public DivideState enter() {
        this.depth++;
        return this;
    }*/
	/*@Override
    public DivideState exit() {
        this.depth--;
        return this;
    }*/
	/*@Override
    public Optional<Tuple<DivideState, Character>> pop() {
        if (this.index >= this.input.length()) return Optional.empty();
        final var c = this.input.charAt(this.index);
        this.index++;
        return Optional.of(new Tuple<>(this, c));
    }*/
	/*@Override
    public Optional<Tuple<DivideState, Character>> popAndAppendToTuple() {
        return this.pop().map(tuple -> {
            final var appended = tuple.left().append(tuple.right());
            return new Tuple<>(appended, tuple.right());
        });
    }*/
	/*@Override
    public Optional<DivideState> popAndAppendToOption() {
        return this.popAndAppendToTuple().map(Tuple::left);
    }*/
	/*@Override
    public boolean isShallow() {
        return 1 == this.depth;
    }*/
}/**/
