package magma;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record MutableState(List<String> segments, StringBuilder buffer, int depth) implements State {
    public MutableState() {
        this(new ArrayList<>(), new StringBuilder(), 0);
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

    @Override
    public boolean isLevel() {
        return 0 == this.depth;
    }

    @Override
    public State exit() {
        return new MutableState(this.segments, this.buffer, this.depth - 1);
    }

    @Override
    public boolean isShallow() {
        return 1 == this.depth;
    }

    @Override
    public State enter() {
        return new MutableState(this.segments, this.buffer, this.depth + 1);
    }
}