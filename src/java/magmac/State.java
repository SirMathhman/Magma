package magmac;

import java.util.ArrayList;
import java.util.List;

public class State {
    private final List<String> segments;
    private int depth;
    private StringBuilder buffer;

    public State(List<String> segments, StringBuilder buffer) {
        this.segments = segments;
        this.buffer = buffer;
        this.depth = 0;
    }

    public State() {
        this(new ArrayList<>(), new StringBuilder());
    }

    State advance() {
        this.segments().add(this.buffer.toString());
        this.buffer = new StringBuilder();
        return this;
    }

    State append(char c) {
        this.buffer.append(c);
        return this;
    }

    public List<String> segments() {
        return this.segments;
    }

    public boolean isLevel() {
        return 0 == this.depth;
    }

    public State enter() {
        this.depth++;
        return this;
    }

    public State exit() {
        this.depth--;
        return this;
    }
}
