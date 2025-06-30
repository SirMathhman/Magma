package magma;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class MutableDivideState implements DivideState {
    private final CharSequence input;
    private final List<String> segments;
    private int index;
    private StringBuilder buffer;

    MutableDivideState(final CharSequence input) {
        this.input = input;
        this.index = 0;
        this.segments = new ArrayList<>();
        this.buffer = new StringBuilder();
    }

    @Override
    public Stream<String> stream() {
        return this.segments.stream();
    }

    @Override
    public Optional<Character> pop() {
        if (this.index < this.input.length()) {
            final var c = this.input.charAt(this.index);
            this.index = this.index + 1;
            return Optional.of(c);
        } else return Optional.empty();
    }

    @Override
    public DivideState append(final char c) {
        this.buffer.append(c);
        return this;
    }

    @Override
    public DivideState advance() {
        final var slice = this.buffer.toString();
        this.segments.add(slice);
        this.buffer = new StringBuilder();
        return this;
    }
}
