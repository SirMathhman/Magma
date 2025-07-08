package magma;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

public class MutableDivideState implements DivideState {
    private final Collection<String> segments = new ArrayList<>();
    private final CharSequence input;
    private int depth = 0;
    private int index = 0;
    private StringBuilder buffer = new StringBuilder();

    public MutableDivideState(final CharSequence input) {
        this.input = input;
    }

    @Override
    public Stream<String> stream() {
        return this.segments.stream();
    }

    @Override
    public DivideState advance() {
        this.segments.add(this.buffer.toString());
        this.buffer = new StringBuilder();
        return this;
    }

    @Override
    public DivideState append(final char c) {
        this.buffer.append(c);
        return this;
    }

    @Override
    public boolean isLevel() {
        return 0 == this.depth;
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
    public Optional<Tuple<DivideState, Character>> pop() {
        if (this.index >= this.input.length()) return Optional.empty();

        final var c = this.input.charAt(this.index);
        this.index++;
        return Optional.of(new Tuple<>(this, c));
    }
}
