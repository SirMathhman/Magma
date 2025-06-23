package magma.app.compile.divide;

import magma.api.list.ListLike;

public class MutableDivideState implements DivideState {
    private ListLike<String> segments;
    private StringBuilder buffer;
    private int depth;

    public MutableDivideState(final ListLike<String> segments, final StringBuilder buffer, final int depth) {
        this.segments = segments;
        this.buffer = buffer;
        this.depth = depth;
    }

    @Override
    public DivideState append(final char c) {
        buffer.append(c);
        return this;
    }

    @Override
    public boolean isLevel() {
        return 0 == depth;
    }

    @Override
    public DivideState exit() {
        depth = depth - 1;
        return this;
    }

    @Override
    public DivideState enter() {
        depth = depth + 1;
        return this;
    }

    @Override
    public DivideState advance() {
        segments = segments.add(buffer.toString());
        buffer = new StringBuilder();
        return this;
    }

    @Override
    public ListLike<String> toList() {
        return segments;
    }
}
