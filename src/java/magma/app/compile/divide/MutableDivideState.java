package magma.app.compile.divide;

import jvm.list.JVMLists;
import magma.api.list.ListLike;

public class MutableDivideState implements DivideState {
    public ListLike<String> segments;
    private StringBuilder buffer;
    private int depth;

    public MutableDivideState(ListLike<String> segments, StringBuilder buffer, int depth) {
        this.segments = segments;
        this.buffer = buffer;
        this.depth = depth;
    }

    public MutableDivideState() {
        this(JVMLists.empty(), new StringBuilder(), 0);
    }

    @Override
    public DivideState enter() {
        this.depth = this.depth + 1;
        return this;
    }

    @Override
    public DivideState exit() {
        this.depth = this.depth - 1;
        return this;
    }

    @Override
    public DivideState advance() {
        this.segments = this.segments.add(this.buffer.toString());
        this.buffer = new StringBuilder();
        return this;
    }

    @Override
    public DivideState append(char c) {
        this.buffer.append(c);
        return this;
    }

    @Override
    public boolean isLevel() {
        return this.depth == 0;
    }

    @Override
    public ListLike<String> unwrap() {
        return this.segments;
    }
}
