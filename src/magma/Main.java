package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        try {
            var source = resolve("java");
            var input = Files.readString(source);
            var output = compile(input);
            var target = resolve("mgs");
            Files.writeString(target, output);
        } catch (IOException | CompileException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static String compile(String root) throws CompileException {
        var segments = split(root);
        var builder = new StringBuilder();
        for (var segment : segments) {
            builder.append(compileRootMember(segment.strip()));
        }

        return builder.toString();
    }

    private static ArrayList<String> split(String root) {
        var state = new State();
        var length = root.length();
        for (int i = 0; i < length; i++) {
            var c = root.charAt(i);
            state = processChar(c, state);
        }

        return state.advance().segments;
    }

    private static State processChar(char c, State state) {
        var appended = state.append(c);
        return c == ';' ? appended.advance() : appended;
    }

    private static String compileRootMember(String segment) throws CompileException {
        if (segment.startsWith("package ")) return "";
        if (segment.startsWith("import ")) return segment;
        throw new CompileException("Unknown root member", segment);
    }

    private static Path resolve(String extension) {
        return Paths.get(".", "src", "magma", "Main." + extension);
    }

    private static class State {
        private final ArrayList<String> segments;
        private final StringBuilder buffer;

        private State(StringBuilder buffer, ArrayList<String> segments) {
            this.buffer = buffer;
            this.segments = segments;
        }

        public State() {
            this(new StringBuilder(), new ArrayList<>());
        }

        private State advance() {
            if (buffer.isEmpty()) return this;
            var copy = new ArrayList<>(segments);
            copy.add(buffer.toString());
            return new State(new StringBuilder(), copy);
        }

        public State append(char c) {
            return new State(buffer.append(c), segments);
        }
    }
}
