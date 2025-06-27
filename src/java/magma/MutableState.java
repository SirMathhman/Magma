package magma;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MutableState implements State {
    private final List<String> segments;
    private StringBuilder buffer;

    public MutableState() {
        this.segments = new ArrayList<>();
        this.buffer = new StringBuilder();
    }

    @Override
    public State advance() {
        this.segments.add(this.buffer.toString());
        this.buffer = new StringBuilder();
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
