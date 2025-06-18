package magma.app.compile.rule.divide;

import magma.api.collect.fold.Foldable;
import magma.api.collect.list.ListLike;
import magma.api.collect.list.Lists;

public class MutableDivideState implements DivideState {
    private ListLike<String> segments;
    private StringBuilder buffer;
    private int depth;

    public MutableDivideState(ListLike<String> segments, StringBuilder buffer, int depth) {
        this.buffer = buffer;
        this.segments = segments;
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
        this.segments = this.segments.add(this.buffer.toString());
        this.buffer = new StringBuilder();
        return this;
    }

    @Override
    public Foldable<String> segments() {
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
