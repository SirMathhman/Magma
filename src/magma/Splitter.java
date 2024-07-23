package magma;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Splitter {
    static List<String> split(String root) {
        var length = root.length();
        var queue = IntStream.range(0, length)
                .mapToObj(root::charAt)
                .collect(Collectors.toCollection(LinkedList::new));

        var state = new State(queue);
        while (true) {
            var appended = state.append();
            if (appended.isEmpty()) break;

            var tuple = appended.get();
            state = splitAtChar(tuple.left(), tuple.right());
        }

        return state.advance().segments.list();
    }

    private static State splitAtChar(State state, Character appended) {
        if (appended == '\'') return processSingleQuotes(state);
        if (appended == '\"') return processDoubleQuotes(state);

        if (appended == ';' && state.isLevel()) return state.advance();
        if (appended == '}' && state.isShallow()) return state.exit().advance();
        if (appended == '{') return state.enter();
        if (appended == '}') return state.exit();
        return state;
    }

    private static State processDoubleQuotes(State state) {
        var current = state;
        while (true) {
            var optional = current.append();
            if (optional.isEmpty()) return current;

            var tuple = optional.get();
            var nextState = tuple.left();
            var nextChar = tuple.right();

            if (nextChar == '\"') return current;
            if (nextChar == '\\') {
                current = nextState.appendAndDiscard().orElse(nextState);
            } else {
                current = nextState;
            }
        }
    }

    private static State processSingleQuotes(State state) {
        var escapeOptional = state.append();
        if (escapeOptional.isEmpty()) return state;

        var escape = escapeOptional.get();
        var escapeState = escape.left();
        var escapeChar = escape.right();

        var withEscape = escapeChar == '\\'
                ? escapeState.appendAndDiscard().orElse(escapeState)
                : escapeState;

        return withEscape.appendAndDiscard().orElse(withEscape);
    }

    private static class State {
        private final JavaList<String> segments;
        private final StringBuilder buffer;
        private final int depth;
        private final Deque<Character> queue;

        private State(Deque<Character> queue, StringBuilder buffer, JavaList<String> segments, int depth) {
            this.queue = queue;
            this.buffer = buffer;
            this.segments = segments;
            this.depth = depth;
        }

        public State(Deque<Character> queue) {
            this(queue, new StringBuilder(), new JavaList<>(), 0);
        }

        private State advance() {
            if (buffer.isEmpty()) return this;
            return new State(queue, new StringBuilder(), segments.add(buffer.toString()), depth);
        }

        public State append(char c) {
            return new State(queue, buffer.append(c), segments, depth);
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

        public boolean isShallow() {
            return depth == 1;
        }

        public Optional<Tuple<State, Character>> append() {
            if (queue.isEmpty()) return Optional.empty();
            var next = queue.pop();
            var appended = append(next);
            return Optional.of(new Tuple<>(appended, next));
        }

        public Optional<State> appendAndDiscard() {
            return append().map(Tuple::left);
        }
    }
}
