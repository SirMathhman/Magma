package magma;

public class MutableState implements State {
    private ListLike<String> segments;
    private StringBuilder buffer;
    private int depth;

    private MutableState(final ListLike<String> segments, final StringBuilder buffer, final int depth) {
        this.segments = segments;
        this.buffer = buffer;
        this.depth = depth;
    }

    MutableState() {
        this(Lists.empty(), new StringBuilder(), 0);
    }

    @Override
    public State append(final char c) {
        buffer.append(c);
        return this;
    }

    @Override
    public State advance() {
        segments = segments.add(buffer.toString());
        buffer = new StringBuilder();
        return this;
    }

    @Override
    public ListLike<String> unwrap() {
        return segments;
    }

    @Override
    public boolean isLevel() {
        return 0 == depth;
    }

    @Override
    public State enter() {
        depth++;
        return this;
    }

    @Override
    public State exit() {
        depth--;
        return this;
    }
}
