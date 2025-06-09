package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class Main {
    private static class State {
        private final List<String> segments;
        private StringBuilder buffer;
        private int depth;

        private State(List<String> segments, StringBuilder buffer, int depth) {
            this.segments = segments;
            this.buffer = buffer;
            this.depth = depth;
        }

        public State() {
            this(new ArrayList<>(), new StringBuilder(), 0);
        }

        private boolean isLevel() {
            return depth == 0;
        }

        private State append(char c) {
            buffer.append(c);
            return this;
        }

        private State advance() {
            segments.add(buffer.toString());
            this.buffer = new StringBuilder();
            return this;
        }

        private State enter() {
            this.depth = depth + 1;
            return this;
        }

        private State exit() {
            this.depth = depth - 1;
            return this;
        }
    }

    public static void main(String[] args) {
        try {
            final var source = Paths.get(".", "src", "magma", "Main.java");
            final var input = Files.readString(source);
            final var target = source.resolveSibling("Main.c");
            final var string = compile(input);
            Files.writeString(target, string);
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static String compile(String input) {
        return compileStatements(input, Main::compileRootSegment);
    }

    private static String compileStatements(String input, Function<String, String> mapper) {
        final var segments = divide(input);
        final var output = new StringBuilder();
        for (var segment : segments) {
            output.append(mapper.apply(segment));
        }

        return output.toString();
    }

    private static List<String> divide(String input) {
        var current = new State();
        for (var i = 0; i < input.length(); i++) {
            final var c = input.charAt(i);
            current = fold(current, c);
        }

        return current.advance().segments;
    }

    private static State fold(State state, char c) {
        final var appended = state.append(c);
        if (c == ';' && appended.isLevel()) {
            return appended.advance();
        }
        if (c == '{') {
            return appended.enter();
        }
        if (c == '}') {
            return appended.exit();
        }
        return appended;
    }

    private static String compileRootSegment(String input) {
        final var stripped = input.strip();
        if (stripped.startsWith("package ") || stripped.startsWith("import ")) {
            return "";
        }

        return compileClass(input).orElseGet(() -> generatePlaceholder(input));
    }

    private static Optional<String> compileClass(String input) {
        final var contentStart = input.indexOf('{');
        if (contentStart >= 0) {
            final var beforeContent = input.substring(0, contentStart);
            final var withEnd = input.substring(contentStart + "{".length()).strip();
            if (withEnd.endsWith("}")) {
                final var header = compileClassDefinition(beforeContent);
                final var inputContent = withEnd.substring(0, withEnd.length() - "}".length());
                final var outputContent = compileStatements(inputContent, Main::compileClassSegment);
                return Optional.of(header + "{" + outputContent + "\n};\n");
            }
        }

        return Optional.empty();
    }

    private static String compileClassSegment(String input) {
        return compileClass(input).orElseGet(() -> generatePlaceholder(input));
    }

    private static String compileClassDefinition(String input) {
        final var classIndex = input.indexOf("class ");
        if (classIndex >= 0) {
            final var beforeKeyword = input.substring(0, classIndex).strip();
            final var afterKeyword = input.substring(classIndex + "class ".length());
            return generatePlaceholder(beforeKeyword) + "struct " + afterKeyword;
        }

        return generatePlaceholder(input);
    }

    private static String generatePlaceholder(String input) {
        return "/*" + input
                .replace("/*", "start")
                .replace("*/", "end") + "*/";
    }
}
