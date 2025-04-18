package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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

        private State exit() {
            this.depth = this.depth - 1;
            return this;
        }

        private State enter() {
            this.depth = this.depth + 1;
            return this;
        }

        private State advance() {
            this.segments().add(this.buffer.toString());
            this.buffer = new StringBuilder();
            return this;
        }

        private boolean isShallow() {
            return this.depth == 1;
        }

        private State append(char c) {
            this.buffer.append(c);
            return this;
        }

        private boolean isLevel() {
            return this.depth == 0;
        }

        public List<String> segments() {
            return this.segments;
        }
    }

    public static void main(String[] args) {
        try {
            Path path = Paths.get(".", "src", "java", "magma", "Main.java");
            String input = Files.readString(path);

            Path target = path.resolveSibling("main.c");
            Files.writeString(target, compile(input));
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static String compile(String input) {
        return compileStatements(input, Main::compileRootSegment);
    }

    private static String compileStatements(String input, Function<String, String> compiler) {
        List<String> segments = divide(input);

        StringBuilder output = new StringBuilder();
        for (String segment : segments) {
            output.append(compiler.apply(segment));
        }

        return output.toString();
    }

    private static List<String> divide(String input) {
        State current = new State();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            current = foldStatementChar(current, c);
        }

        return current.advance().segments;
    }

    private static State foldStatementChar(State state, char c) {
        State appended = state.append(c);
        if (c == ';' && appended.isLevel()) {
            return appended.advance();
        }
        if (c == '}' && appended.isShallow()) {
            return appended.advance().exit();
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
        if (input.startsWith("package ")) {
            return "";
        }
        if (input.strip().startsWith("import ")) {
            return "// #include <temp.h>\n";
        }

        return compileClass(input).orElseGet(() -> generatePlaceholder(input));

    }

    private static Optional<String> compileClass(String input) {
        int classIndex = input.indexOf("class ");
        if (classIndex >= 0) {
            String modifiers = input.substring(0, classIndex).strip();
            String afterKeyword = input.substring(classIndex + "class ".length());
            int contentStart = afterKeyword.indexOf("{");
            if (contentStart >= 0) {
                String name = afterKeyword.substring(0, contentStart).strip();
                String withEnd = afterKeyword.substring(contentStart + "{".length()).strip();
                if (withEnd.endsWith("}")) {
                    String inputContent = generatePlaceholder(withEnd.substring(0, withEnd.length() - "}".length()));
                    String content = compileStatements(inputContent, Main::compileClassSegment);
                    return Optional.of(generatePlaceholder(modifiers) + " struct " +
                            name +
                            " {" + content + "};");
                }
            }
        }
        return Optional.empty();
    }

    private static String compileClassSegment(String classSegment) {
        return compileClass(classSegment).orElseGet(() -> generatePlaceholder(classSegment));
    }

    private static String generatePlaceholder(String input) {
        return "/* " + input + " */";
    }
}
