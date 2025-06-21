package magma;

import java.util.ArrayList;
import java.util.List;

public record State(List<String> segments, StringBuilder buffer) {
    public State() {
        this(new ArrayList<>(), new StringBuilder());
    }

    State append(final char c) {
        this.buffer.append(c);
        return this;
    }

    State advance() {
        this.segments.add(this.buffer.toString());
        this.buffer.setLength(0);
        return this;
    }
}