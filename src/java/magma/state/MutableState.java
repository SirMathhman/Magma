package magma.state;

import java.util.ArrayList;
import java.util.List;

public class MutableState implements State {
    private final List<String> segments;
    private StringBuilder buffer;
    private int depth;

    public MutableState(List<String> segments, StringBuilder buffer, int depth) {
        this.buffer = buffer;
        this.segments = segments;
        this.depth = depth;
    }

    public MutableState() {
        this(new ArrayList<>(), new StringBuilder(), 0);
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

    @Override
    public boolean isLevel() {
        return this.depth == 0;
    }

    @Override
    public State enter() {
        this.depth++;
        return this;
    }

    @Override
    public State exit() {
        this.depth--;
        return this;
    }
}
