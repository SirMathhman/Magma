package magma.app.compile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class State {
    private final List<String> rootMembers;
    private final StringBuilder buffer;

    public State(List<String> rootMembers, StringBuilder buffer) {
        this.rootMembers = rootMembers;
        this.buffer = buffer;
    }

    public State() {
        this(Collections.emptyList(), new StringBuilder());
    }

    State advance() {
        var copy = new ArrayList<>(rootMembers);
        copy.add(buffer.toString());
        return new State(copy, new StringBuilder());
    }

    public Stream<String> stream() {
        return rootMembers.stream();
    }

    public State append(char c) {
        return new State(rootMembers, buffer.append(c));
    }
}
