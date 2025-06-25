package magma;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class State implements MutableState {
    private final List<String> segments;
    private StringBuilder buffer;

    private State(final List<String> segments, final StringBuilder buffer) {
        this.segments = new ArrayList<>(segments);
        this.buffer = buffer;
    }

    State() {
        this(new ArrayList<>(), new StringBuilder());
    }

    @Override
    public MutableState append(final char c) {
        buffer.append(c);
        return this;
    }

    @Override
    public MutableState advance() {
        segments.add(buffer.toString());
        buffer = new StringBuilder();
        return this;
    }

    @Override
    public List<String> unwrap() {
        return Collections.unmodifiableList(segments);
    }
}
