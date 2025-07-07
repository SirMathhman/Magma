package magma;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class MutableDivideState implements DivideState {
    private final List<String> segments;
    private StringBuilder buffer;

    public MutableDivideState() {
        this.segments = new ArrayList<>();
        this.buffer = new StringBuilder();
    }

    @Override
    public Stream<String> stream() {
        return this.segments.stream();
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
}
