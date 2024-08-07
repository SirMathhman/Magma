package magma.app.compile;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MemberSplitter implements Splitter {
    public static final char STATEMENT_END = ';';
    public static final char BLOCK_START = '{';
    public static final char BLOCK_END = '}';

    static State splitAtChar(State current, char c, LinkedList<Character> queue) {
        var appended = current.append(c);
        if (c == '/' && appended.isLevel()) {
            if (queue.isEmpty()) return appended;
            var next = queue.peek();
            if (next == '/') {
                var withComment = current.append(queue.pop());
                while (!queue.isEmpty()) {
                    var afterComment = queue.pop();
                    if (afterComment == '\n') {
                        withComment = withComment.advance();
                    } else {
                        withComment = withComment.append(afterComment);
                    }
                }

                return withComment;
            }
        }

        if (c == '\"') {
            var withString = appended;
            while (!queue.isEmpty()) {
                var next = queue.pop();
                withString = withString.append(next);
                if (next == '\"') {
                    break;
                }
            }
            return withString;
        }

        if (c == STATEMENT_END && appended.isLevel()) return appended.advance();
        if (c == BLOCK_END && appended.isShallow()) return appended.exit().advance();
        if (c == BLOCK_START || c == '(') return appended.enter();
        if (c == BLOCK_END || c == ')') return appended.exit();
        return appended;
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

        var current = new State();
        while (!queue.isEmpty()) {
            var c = queue.pop();
            current = splitAtChar(current, c, queue);
        }

        return current.advance().segments;
    }

    private static class State {
        private final List<String> segments;
        private final StringBuilder buffer;
        private final int depth;

        private State() {
            this(new ArrayList<>(), new StringBuilder(), 0);
        }

        private State(List<String> segments, StringBuilder buffer, int depth) {
            this.segments = segments;
            this.buffer = buffer;
            this.depth = depth;
        }

        private State advance() {
            if (buffer.isEmpty()) return this;
            var copy = new ArrayList<>(segments);
            copy.add(buffer.toString());
            return new State(copy, new StringBuilder(), depth);
        }

        public State append(char c) {
            return new State(segments, buffer.append(c), depth);
        }

        public boolean isLevel() {
            return depth == 0;
        }

        public State enter() {
            return new State(segments, buffer, depth + 1);
        }

        public State exit() {
            return new State(segments, buffer, depth - 1);
        }

        public boolean isShallow() {
            return depth == 1;
        }
    }
}