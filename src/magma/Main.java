package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {
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
            output.append(compileRootMember(segment));
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
        if (c == ';') {
            return appended.advance();
        } else {
            return appended;
        }
    }

    private static String compileRootMember(String rootMember) throws CompileException {
        throw new CompileException("Unknown root member", rootMember);
    }

    private static class State {
        private final List<String> segments;
        private final StringBuilder buffer;

        private State(StringBuilder buffer, List<String> segments) {
            this.buffer = buffer;
            this.segments = segments;
        }

        public State() {
            this(new StringBuilder(), new ArrayList<>());
        }

        private State append(char c) {
            return new State(buffer.append(c), segments);
        }

        private State advance() {
            var copy = new ArrayList<>(segments);
            copy.add(buffer.toString());
            return new State(new StringBuilder(), copy);
        }
    }
}
