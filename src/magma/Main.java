package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            var source = Paths.get(".", "src", "magma", "Main.java");
            var input = Files.readString(source);
            var output = compile(input);
            Files.writeString(Paths.get(".", "src", "magma", "Main.mgs"), output);
        } catch (IOException | CompilationException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static String compile(String input) throws CompilationException {
        var segments = split(input);
        var output = new StringBuilder();
        for (var segment : segments) {
            output.append(compileRootMember(segment.strip()));
        }

        return output.toString();
    }

    private static List<String> split(String input) {
        var length = input.length();
        var current = new State();
        for (var i = 0; i < length; i++) {
            var c = input.charAt(i);
            current = splitAtChar(current, c);
        }
        return current.advance().segments;
    }

    private static State splitAtChar(State state, char c) {
        var appended = state.append(c);
        if (c == ';' && state.isLevel()) return appended.advance();
        if (c == '{') return appended.enter();
        if (c == '}') return appended.exit();
        return appended;
    }

    private static String compileRootMember(String input) throws CompilationException {
        if (input.startsWith("package ")) return "";
        if (input.startsWith("import ")) return input;

        var index = input.indexOf("class ");
        if(index != -1) {
            var after = input.substring(index + "class ".length());

            var contentStart = after.indexOf('{');
            var name = after.substring(0, contentStart).strip();

            return "class def " + name + "() => {}";
        }

        throw new CompilationException("Invalid root member", input);
    }

    private static class State {
        private final List<String> segments;
        private final StringBuilder buffer;
        private final int depth;

        private State(StringBuilder buffer, List<String> segments, int depth) {
            this.buffer = buffer;
            this.segments = segments;
            this.depth = depth;
        }

        public State() {
            this(new StringBuilder(), Collections.emptyList(), 0);
        }

        private State advance() {
            var copy = new ArrayList<>(segments);
            copy.add(buffer.toString());
            return new State(new StringBuilder(), copy, depth);
        }

        public State append(char c) {
            return new State(buffer.append(c), segments, depth);
        }

        public State enter() {
            return new State(buffer, segments, depth + 1);
        }

        public State exit() {
            return new State(buffer, segments, depth - 1);
        }

        public boolean isLevel() {
            return depth == 0;
        }
    }
}
