package magma;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;

public class MutableDivideState implements DivideState {
    private final Collection<String> segments = new ArrayList<>(new ArrayList<>());
    private StringBuilder buffer = new StringBuilder();
    private int depth = 0;

    @Override
    public DivideState append(final char c) {
        this.buffer.append(c);
        return this;
    }

    @Override
    public DivideState advance() {
        this.segments.add(this.buffer.toString());
        this.buffer = new StringBuilder();
        return this;
    }

    @Override
    public Stream<String> stream() {
        return this.segments.stream();
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
    public boolean isShallow() {
        return 1 == this.depth;
    }
}
