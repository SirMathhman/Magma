package magma.app.compile;

import magma.api.list.Iterable;
import magma.api.list.List;
import magma.api.list.Lists;

public class MutableDivideState implements DivideState {
    private final List<String> segments;
    private StringBuilder buffer;
    private int depth;

    public MutableDivideState(List<String> segments, StringBuilder buffer, int depth) {
        this.segments = segments;
        this.buffer = buffer;
        this.depth = depth;
    }

    public MutableDivideState() {
        this(Lists.empty(), new StringBuilder(), 0);
    }

    @Override
    public DivideState append(char c) {
        this.buffer.append(c);
        return this;
    }

    @Override
    public DivideState advance() {
        this.segments.add(this.buffer.toString());
        this.buffer = new StringBuilder();
        return this;
    }

    @Override
    public Iterable<String> segments() {
        return this.segments;
    }

    @Override
    public boolean isLevel() {
        return this.depth == 0;
    }

    @Override
    public DivideState enter() {
        this.depth++;
        return this;
    }

    @Override
    public DivideState exit() {
        this.depth--;
        return this;
    }
}
