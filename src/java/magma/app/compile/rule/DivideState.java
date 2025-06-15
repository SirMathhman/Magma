package magma.app.compile.rule;

import java.util.ArrayList;
import java.util.List;

public class DivideState {
    private final List<String> segments;
    private StringBuilder buffer;

    public DivideState(List<String> segments, StringBuilder buffer) {
        this.segments = segments;
        this.buffer = buffer;
    }

    public DivideState() {
        this(new ArrayList<>(), new StringBuilder());
    }

    public DivideState advance() {
        this.segments.add(this.buffer.toString());
        this.buffer = new StringBuilder();
        return this;
    }

    public DivideState append(char c) {
        this.buffer.append(c);
        return this;
    }

    public List<String> segments() {
        return this.segments;
    }
}
