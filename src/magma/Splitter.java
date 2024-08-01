package magma;

import java.util.ArrayList;
import java.util.List;

public class Splitter {
    public static final char STATEMENT_END = ';';
    public static final char BLOCK_START = '{';
    public static final char BLOCK_END = '}';

    public static List<String> splitRootMembers(String input) {
        var current = new State();
        var length = input.length();
        for (int i = 0; i < length; i++) {
            var c = input.charAt(i);
            current = splitAtChar(current, c);
        }

        return current.advance().segments;
    }

    static State splitAtChar(State current, char c) {
        var appended = current.append(c);
        if (c == STATEMENT_END && appended.isLevel()) return appended.advance();
        if (c == BLOCK_START) return appended.enter();
        if (c == BLOCK_END) return appended.exit();
        return appended;
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
            var copy = new ArrayList<String>(segments);
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
    }
}