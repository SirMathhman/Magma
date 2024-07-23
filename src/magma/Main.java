package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Main {

    public static final String CLASS_KEYWORD_WITH_SPACE = "class ";

    public static void main(String[] args) {
        try {
            var source = resolve("java");
            var input = Files.readString(source);
            var output = compile(input);
            Files.writeString(resolve("mgs"), output);
        } catch (IOException | CompileException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static Path resolve(String extension) {
        return Paths.get(".", "src", "magma", "Main." + extension);
    }

    private static String compile(String input) throws CompileException {
        var segments = split(input);

        var output = new StringBuilder();
        for (String segment : segments) {
            output.append(compileRootMember(segment.strip()));
        }

        return output.toString();
    }

    private static List<String> split(String input) {
        var current = new State();
        for (int i = 0; i < input.length(); i++) {
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

    private static String compileRootMember(String rootMember) throws CompileException {
        if (rootMember.startsWith("package ")) return "";
        if (rootMember.startsWith("import ")) return rootMember;

        return compileClass(rootMember).orElseThrow(() -> new CompileException("Unknown root member", rootMember));
    }

    private static Optional<String> compileClass(String rootMember) {
        var classIndex = rootMember.indexOf(CLASS_KEYWORD_WITH_SPACE);
        if (classIndex == -1) return Optional.empty();

        var contentStart = rootMember.indexOf('{');
        if (contentStart == -1) return Optional.empty();

        var name = rootMember.substring(classIndex + CLASS_KEYWORD_WITH_SPACE.length(), contentStart).strip();
        return Optional.of(CLASS_KEYWORD_WITH_SPACE + "def " + name + "() => {}");
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
            this(new StringBuilder(), new ArrayList<>(), 0);
        }

        private State append(char c) {
            return new State(buffer.append(c), segments, depth);
        }

        private State advance() {
            var copy = new ArrayList<>(segments);
            copy.add(buffer.toString());
            return new State(new StringBuilder(), copy, depth);
        }

        public boolean isLevel() {
            return depth == 0;
        }

        public State enter() {
            return new State(buffer, segments, depth + 1);
        }

        public State exit() {
            return new State(buffer, segments, depth - 1);
        }
    }
}
