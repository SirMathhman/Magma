package magma;

public record MutableState(ListLike<String> segments, StringBuilder buffer) implements State {
    public MutableState() {
        this(Lists.empty(), new StringBuilder());
    }

    @Override
    public State append(final char c) {
        this.buffer.append(c);
        return this;
    }

    @Override
    public State advance() {
        final var segment = this.buffer.toString();
        this.buffer.setLength(0);
        return new MutableState(this.segments.add(segment), this.buffer);
    }
}