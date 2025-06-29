package magma;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

public class MutableDivideState implements DivideState {
    private final Collection<String> segments;
    private final CharSequence input;
    private int index = 0;
    private StringBuilder buffer;
    private int depth = 0;

    public MutableDivideState(final CharSequence input) {
        this.input = input;
        this.segments = new ArrayList<>(new ArrayList<>());
        this.buffer = new StringBuilder();
    }

    @Override
    public DivideState advance() {
        final var slice = this.buffer.toString();
        this.segments.add(slice);
        this.buffer = new StringBuilder();
        return this;
    }

    @Override
    public DivideState append(final char c) {
        this.buffer.append(c);
        return this;
    }

    @Override
    public Stream<String> stream() {
        return this.segments.stream();
    }

    @Override
    public DivideState enter() {
        this.depth++;
        return this;
    }

    @Override
    public DivideState exit() {
        this.depth--;
        return this;
    }

    @Override
    public boolean isLevel() {
        return 0 == this.depth;
    }

    @Override
    public Optional<Tuple<DivideState, Character>> pop() {
        if (this.index < this.input.length()) {
            final var value = this.input.charAt(this.index);
            this.index++;
            return Optional.of(new Tuple<>(this, value));
        } else return Optional.empty();
    }
}
