package magma;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class MutableDivideState implements DivideState {
    private final List<String> segments;
    private StringBuilder buffer;

    MutableDivideState() {
        this.segments = new ArrayList<>(new ArrayList<String>());
        this.buffer = new StringBuilder();
    }

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
}
