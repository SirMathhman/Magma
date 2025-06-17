package magma;

import java.util.ArrayList;
import java.util.List;

public class MutableState implements State {
    private final List<String> segments;
    private StringBuilder buffer;

    public MutableState(List<String> segments, StringBuilder buffer) {
        this.segments = segments;
        this.buffer = buffer;
    }

    public MutableState() {
        this(new ArrayList<>(), new StringBuilder());
    }

    @Override
    public State append(char c) {
        this.buffer.append(c);
        return this;
    }

    @Override
    public State advance() {
        this.segments.add(this.buffer.toString());
        this.buffer = new StringBuilder();
        return this;
    }

    @Override
    public List<String> segments() {
        return this.segments;
    }
}
