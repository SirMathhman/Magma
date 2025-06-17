package magma.app.rule.divide;

import java.util.ArrayList;
import java.util.List;

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
        this(new ArrayList<>(), new StringBuilder(), 0);
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
    public List<String> segments() {
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
