package magma.app;

import java.util.ArrayList;
import java.util.List;

public record MutableState(List<String> segments, StringBuilder buffer) implements State {
    public MutableState() {
        this(new ArrayList<>(), new StringBuilder());
    }

    @Override
    public State append(final char c) {
        this.buffer.append(c);
        return this;
    }

    @Override
    public State advance() {
        final var segment = this.buffer.toString();
        this.segments.add(segment);
        this.buffer.setLength(0);
        return this;
    }
}