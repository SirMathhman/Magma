package magma;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;

public class MutableDivideState implements DivideState {
    private final Collection<String> segments;
    private StringBuilder buffer;
    private int depth = 0;

    public MutableDivideState() {
        this.segments = new ArrayList<>(new ArrayList<>());
        this.buffer = new StringBuilder();
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
}
