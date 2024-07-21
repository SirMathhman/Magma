package magma.app.compile;

import java.util.ArrayList;
import java.util.List;

public class Splitter {
    public static final char STATEMENT_END = ';';
    public static final char BLOCK_START = '{';
    public static final char BLOCK_END = '}';

    static List<String> split(String input) {
        var state = new State();
        for (int i = 0; i < input.length(); i++) {
            state = splitAtChar(state, input.charAt(i));
        }

        return state.advance().segments;
    }

    static State splitAtChar(State state, char c) {
        var appended = state.append(c);
        if (c == STATEMENT_END && state.isLevel()) return appended.advance();
        if (c == BLOCK_START) return appended.enter();
        if (c == BLOCK_END) return appended.exit();
        return appended;
    }

    private record State(List<String> segments, StringBuilder buffer, int depth) {
        private State() {
            this(new ArrayList<>(), new StringBuilder(), 0);
        }

        private State append(char c) {
            return new State(this.segments, this.buffer.append(c), depth);
        }

        private State advance() {
            var copy = new ArrayList<>(segments);
            copy.add(buffer.toString());
            return new State(copy, new StringBuilder(), depth);
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
}