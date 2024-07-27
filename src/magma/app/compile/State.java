package magma.app.compile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public record State(List<String> segments, StringBuilder buffer, int depth) {
    public State() {
        this(Collections.emptyList(), new StringBuilder(), 0);
    }

    State advance() {
        var copy = new ArrayList<>(segments);
        copy.add(buffer.toString());
        return new State(copy, new StringBuilder(), depth);
    }

    public Stream<String> stream() {
        return segments.stream();
    }

    public State append(char c) {
        return new State(segments, buffer.append(c), depth);
    }

    public State enter() {
        return new State(segments, buffer, depth + 1);
    }

    public State exit() {
        return new State(segments, buffer, depth - 1);
    }

    public boolean isLevel() {
        return depth == 0;
    }
}
