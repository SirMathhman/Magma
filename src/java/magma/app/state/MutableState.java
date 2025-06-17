package magma.app.state;

import java.util.ArrayList;
import java.util.List;

public class MutableState implements State {
    public final List<String> segments;
    private StringBuilder buffer;
    private int depth;

    public MutableState(List<String> segments, StringBuilder buffer, int depth) {
        this.segments = segments;
        this.buffer = buffer;
        this.depth = depth;
    }

    public MutableState() {
        this(new ArrayList<>(), new StringBuilder(), 0);
    }

    @Override
    public State enter() {
        this.depth = this.depth + 1;
        return this;
    }

    @Override
    public State exit() {
        this.depth = this.depth - 1;
        return this;
    }

    @Override
    public MutableState advance() {
        this.segments.add(this.buffer.toString());
        this.buffer = new StringBuilder();
        return this;
    }

    @Override
    public State append(char c) {
        this.buffer.append(c);
        return this;
    }

    @Override
    public boolean isLevel() {
        return this.depth == 0;
    }

    @Override
    public List<String> unwrap() {
        return this.segments;
    }
}
