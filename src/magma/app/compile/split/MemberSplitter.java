package magma.app.compile.split;

import magma.api.Tuple;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MemberSplitter implements Splitter {
    private static State splitAtChar(Tuple<Character, State> tuple) {
        var current = tuple.right();
        var c = tuple.left();

        return tryComment(current, c)
                .or(() -> trySingleQuotes(current, c))
                .or(() -> tryDoubleQuotes(current, c))
                .or(() -> tryStatementEnd(current, c))
                .or(() -> tryBlockEnd(current, c))
                .or(() -> tryBlockEnter(current, c))
                .or(() -> tryBlockExit(current, c))
                .orElse(tuple.right());
    }

    private static Optional<State> tryBlockEnd(State current, char c) {
        return c == '}' && current.isShallow() ? Optional.of(current.exit().advance()) : Optional.empty();
    }

    private static Optional<State> tryBlockExit(State current, char c) {
        return c == '}' || c == ')' ? Optional.of(current.exit()) : Optional.empty();
    }

    private static Optional<State> tryBlockEnter(State current, char c) {
        return c == '{' || c == '(' ? Optional.of(current.enter()) : Optional.empty();
    }

    private static Optional<State> tryStatementEnd(State current, char c) {
        return c == ';' && current.isLevel() ? Optional.of(current.advance()) : Optional.empty();
    }

    private static Optional<State> tryDoubleQuotes(State state, char c) {
        if (c != '\"') return Optional.empty();

        var current = state;
        while (true) {
            var optional = current.append();
            if (optional.isEmpty()) break;

            var next = optional.get();
            var nextChar = next.left();
            if (nextChar == '\"') break;

            if (nextChar == '\\') {
                current = next.right().appendAndDiscard().orElse(next.right());
            } else {
                current = next.right();
            }
        }

        return Optional.of(current);
    }

    private static Optional<State> trySingleQuotes(State state, char c) {
        if (c != '\'') return Optional.empty();

        return state.append().flatMap(appended -> {
            if (appended.left() == '\\') {
                return appended.right().appendAndDiscard();
            } else {
                return Optional.of(appended.right());
            }
        }).flatMap(State::appendAndDiscard);
    }

    private static Optional<State> tryComment(State state, char c) {
        if (c != '/') return Optional.empty();

        return state.peek().flatMap(peeked -> {
            if (peeked != '/') return Optional.empty();

            return state.appendAndDiscard().map(appended -> {
                var current = appended;
                while (true) {
                    var maybeNext = current.append();
                    if (maybeNext.isEmpty()) break;

                    var next = maybeNext.get();
                    current = next.right();
                    if (next.left() == '\n') break;
                }

                return current;
            });
        });
    }

    @Override
    public String computeDelimiter() {
        return "";
    }

    @Override
    public List<String> split(String input) {
        var queue = IntStream.range(0, input.length())
                .mapToObj(input::charAt)
                .collect(Collectors.toCollection(LinkedList::new));

        var current = new State(queue);
        while (true) {
            var maybeTuple = current.append();
            if (maybeTuple.isEmpty()) break;

            var tuple = maybeTuple.get();
            current = splitAtChar(tuple);
        }

        return current.advance().segments;
    }

    private static class State {
        private final List<String> segments;
        private final StringBuilder buffer;
        private final int depth;
        private final Deque<Character> queue;

        private State(Deque<Character> queue) {
            this(new ArrayList<>(), new StringBuilder(), 0, queue);
        }

        private State(List<String> segments, StringBuilder buffer, int depth, Deque<Character> queue) {
            this.segments = segments;
            this.buffer = buffer;
            this.depth = depth;
            this.queue = queue;
        }

        private State advance() {
            if (buffer.isEmpty()) return this;
            var copy = new ArrayList<>(segments);
            copy.add(buffer.toString());
            return new State(copy, new StringBuilder(), depth, queue);
        }

        public State append(char c) {
            return new State(segments, buffer.append(c), depth, queue);
        }

        public boolean isLevel() {
            return depth == 0;
        }

        public State enter() {
            return new State(segments, buffer, depth + 1, queue);
        }

        public State exit() {
            return new State(segments, buffer, depth - 1, queue);
        }

        public boolean isShallow() {
            return depth == 1;
        }

        public Optional<Tuple<Character, State>> append() {
            if (queue.isEmpty()) return Optional.empty();

            var next = queue.pop();
            var appended = buffer.append(next);
            var state = new State(segments, appended, depth, queue);
            return Optional.of(new Tuple<>(next, state));
        }

        public Optional<Character> peek() {
            if (queue.isEmpty()) return Optional.empty();
            return Optional.of(queue.peekFirst());
        }

        public Optional<State> appendAndDiscard() {
            return append().map(Tuple::right);
        }

        public boolean isEmpty() {
            return queue.isEmpty();
        }
    }
}