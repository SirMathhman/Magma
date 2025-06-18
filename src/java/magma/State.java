package magma;

import java.util.List;

public class State {
    private final List<String> segments;
    private StringBuilder buffer;

    public State(StringBuilder buffer, List<String> segments) {
        this.buffer = buffer;
        this.segments = segments;
    }

    State append(char c) {
        this.buffer.append(c);
        return this;
    }

    State advance() {
        this.segments.add(this.buffer.toString());
        this.buffer = new StringBuilder();
        return this;
    }

    public List<String> segments() {
        return this.segments;
    }
}
