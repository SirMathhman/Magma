package magma;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

class State {
    public final List<String> segments;
    private final StringBuilder buffer;
    private final int depth;
    private final Deque<Character> queue;

    private State(Deque<Character> queue, StringBuilder buffer, List<String> segments, int depth) {
        this.queue = queue;
        this.buffer = buffer;
        this.segments = segments;
        this.depth = depth;
    }

    public State(Deque<Character> queue) {
        this(queue, new StringBuilder(), new ArrayList<String>(), 0);
    }

    Optional<Tuple<State, Character>> appendAndRetrieve() {
        return pop().map(popped -> new Tuple<>(append(popped), popped));
    }

    @Deprecated
    State append(char c) {
        return new State(queue, buffer.append(c), segments, depth);
    }

    State advance() {
        var copy = new ArrayList<String>(segments);
        copy.add(buffer.toString());
        return new State(queue, new StringBuilder(), copy, depth);
    }

    public boolean isShallow() {
        return depth == 1;
    }

    public boolean isLevel() {
        return depth == 0;
    }

    public State enter() {
        return new State(queue, buffer, segments, depth + 1);
    }

    public State exit() {
        return new State(queue, buffer, segments, depth - 1);
    }

    @Deprecated
    public Optional<Character> pop() {
        if (queue.isEmpty()) return Optional.empty();
        return Optional.of(queue.pop());
    }

    public Optional<State> append() {
        return pop().map(this::append);
    }
}
