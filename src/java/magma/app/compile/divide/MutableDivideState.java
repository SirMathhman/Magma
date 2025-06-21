package magma.app.compile.divide;

import magma.api.collect.list.ListLike;
import magma.api.collect.list.Lists;

public record MutableDivideState(ListLike<String> segments, StringBuilder buffer) implements DivideState {
    public MutableDivideState() {
        this(Lists.empty(), new StringBuilder());
    }

    @Override
    public DivideState append(final char c) {
        this.buffer.append(c);
        return this;
    }

    @Override
    public DivideState advance() {
        final var segment = this.buffer.toString();
        this.buffer.setLength(0);
        return new MutableDivideState(this.segments.add(segment), this.buffer);
    }
}