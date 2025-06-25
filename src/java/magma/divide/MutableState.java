package magma.divide;

import magma.list.ListLike;
import magma.list.Lists;

public class MutableState implements State {
    private ListLike<String> segments;
    private String buffer;
    private int depth;

    private MutableState(final ListLike<String> segments) {
        this.segments = segments;
        buffer = "";
        depth = 0;
    }

    public MutableState() {
        this(Lists.empty());
    }

    @Override
    public State append(final char c) {
        buffer = buffer + c;
        return this;
    }

    @Override
    public State advance() {
        segments = segments.add(buffer);
        buffer = "";
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

    @Override
    public boolean isShallow() {
        return 1 == depth;
    }
}
