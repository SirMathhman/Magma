package magma.divide;

import magma.list.ListLike;
import magma.list.Lists;

public class MutableState implements State {
    private ListLike<String> segments;
    private String buffer;
    private int depth;

    private MutableState(final ListLike<String> segments) {
        this.segments = segments;
        this.buffer = "";
        this.depth = 0;
    }

    public MutableState() {
        this(Lists.empty());
    }

    @Override
    public State append(final char c) {
        this.buffer = this.buffer + c;
        return this;
    }

    @Override
    public State advance() {
        this.segments = this.segments.add(this.buffer);
        this.buffer = "";
        return this;
    }

    @Override
    public ListLike<String> unwrap() {
        return this.segments;
    }

    @Override
    public boolean isLevel() {
        return 0 == this.depth;
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

    @Override
    public boolean isShallow() {
        return 1 == this.depth;
    }
}
