package magma.app.compile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Splitter {
    static Stream<String> split(String input) {
        var state = new State();
        for (int i = 0; i < input.length(); i++) {
            state = splitAtChar(state, input.charAt(i));
        }

        return state.advance().stream();
    }

    static State splitAtChar(State state, char c) {
        var appended = state.append(c);
        if (c == ';' && appended.isLevel()) return appended.advance();
        if (c == '{') return appended.enter();
        if (c == '}') return appended.exit();
        return appended;
    }

    private record State(List<String> segments, StringBuilder buffer, int depth) {
        private State() {
            this(new ArrayList<>(), new StringBuilder(), 0);
        }

        private State advance() {
            if (buffer.isEmpty()) return this;

            var copy = new ArrayList<>(segments);
            copy.add(buffer.toString());
            return new State(copy, new StringBuilder(), depth);
        }

        private State append(char c) {
            return new State(segments, buffer.append(c), depth);
        }

        public Stream<String> stream() {
            return segments.stream();
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