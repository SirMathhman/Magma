package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            var source = resolve("java");
            var input = Files.readString(source);
            Files.writeString(resolve("mgs"), compileRoot(input));
        } catch (IOException | CompilationException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static String compileRoot(String input) throws CompilationException {
        var segments = split(input);

        var output = new StringBuilder();
        for (String segment : segments) {
            output.append(compileRootMember(segment.strip()));
        }

        return output.toString();
    }

    private static List<String> split(String input) {
        var state = new State();
        var length = input.length();
        for (int i = 0; i < length; i++) {
            var c = input.charAt(i);
            state = splitChar(state, c);
        }
        return state.advance().segments;
    }

    private static State splitChar(State state, char c) {
        var appended = state.append(c);
        return c == ';' ? appended.advance() : appended;
    }

    private static String compileRootMember(String input) throws CompilationException {
        if (input.startsWith("package ")) return "";
        throw new CompilationException("Invalid input", input);
    }

    private static Path resolve(String extension) {
        return Paths.get(".", "src", "magma", "Main." + extension);
    }

    private record State(List<String> segments, StringBuilder buffer) {
        public State() {
            this(Collections.emptyList(), new StringBuilder());
        }

        public State append(char c) {
            return new State(segments, buffer.append(c));
        }

        public State advance() {
            var copy = new ArrayList<>(segments);
            copy.add(buffer.toString());
            return new State(copy, new StringBuilder());
        }
    }
}
