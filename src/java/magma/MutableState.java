package magma;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record MutableState(List<String> segments, StringBuilder buffer) implements State {
    public MutableState() {
        this(new ArrayList<>(), new StringBuilder());
    }

    @Override
    public State advance() {
        this.segments.add(this.buffer.toString());
        this.buffer.setLength(0);
        return this;
    }

    @Override
    public State append(final char c) {
        this.buffer.append(c);
        return this;
    }

    @Override
    public List<String> unwrap() {
        return Collections.unmodifiableList(this.segments);
    }
}