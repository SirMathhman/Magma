package magma;

import java.util.List;

public class State {
    private final List<String> segments;
    private StringBuilder buffer;

    public State(List<String> segments, StringBuilder buffer) {
        this.segments = segments;
        this.buffer = buffer;
    }

    public List<String> getSegments() {
        return this.segments;
    }

    public StringBuilder getBuffer() {
        return this.buffer;
    }

    public void setBuffer(StringBuilder buffer) {
        this.buffer = buffer;
    }

    public List<String> segments() {
        return this.segments;
    }
}
