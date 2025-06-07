package magma.compile;

import magma.util.*;
public class DivideState {
    public List<String> segments;
    private StringBuilder buffer;
    private int depth;

    public DivideState(List<String> segments, StringBuilder buffer, int depth) {
        this.segments = segments;
        this.buffer = buffer;
        this.depth = depth;
    }

    public DivideState() {
        this(Lists.empty(), new StringBuilder(), 0);
    }

    public DivideState append(char c) {
        buffer.append(c);
        return this;
    }

    public DivideState enter() {
        this.depth = depth + 1;
        return this;
    }

    public DivideState exit() {
        this.depth = depth - 1;
        return this;
    }

    public boolean isShallow() {
        return depth == 1;
    }

    public DivideState advance() {
        segments = segments.add(buffer.toString());
        this.buffer = new StringBuilder();
        return this;
    }

    public boolean isLevel() {
        return depth == 0;
    }
}
