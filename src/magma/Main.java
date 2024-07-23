package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        var compiledSegments = compileRootMembers(segments);
        var builder = generateRoot(compiledSegments);
        return builder.toString();
    }

    private static StringBuilder generateRoot(List<String> compiledSegments) {
        var builder = new StringBuilder();
        for (int i = 0; i < compiledSegments.size(); i++) {
            var segment = compiledSegments.get(i);
            var prefix = i == 0 ? "" : "\n";
            builder.append(prefix).append(segment);
        }
        return builder;
    }

    private static List<String> compileRootMembers(List<String> segments) throws CompileException {
        var compiledSegments = new ArrayList<String>();
        for (String segment : segments) {
            var compiled = compileRootMember(segment.strip());
            if (!compiled.isEmpty()) compiledSegments.add(compiled);
        }
        return compiledSegments;
    }

    private static List<String> split(String root) {
        var length = root.length();
        var queue = IntStream.range(0, length)
                .mapToObj(root::charAt)
                .collect(Collectors.toCollection(LinkedList::new));

        var state = new State(queue);
        while (true) {
            var appended = state.append();
            if (appended.isEmpty()) break;

            var tuple = appended.get();
            state = splitAtChar(tuple.left(), tuple.right());
        }

        return state.advance().segments.list();
    }

    private static State splitAtChar(State state, Character right) {
        if (right == '\'') return processSingleQuotes(state);


        if (right == ';' && state.isLevel()) return state.advance();
        if (right == '}' && state.isShallow()) return state.exit().advance();
        if (right == '{') return state.enter();
        if (right == '}') return state.exit();
        return state;
    }

    private static State processSingleQuotes(State state) {
        var escapeOptional = state.append();
        if (escapeOptional.isEmpty()) return state;

        var escape = escapeOptional.get();
        var escapeState = escape.left();
        var escapeChar = escape.right();

        var withEscape = escapeChar == '\\'
                ? escapeState.appendAndDiscard().orElse(escapeState)
                : escapeState;

        return withEscape.appendAndDiscard().orElse(withEscape);
    }

    private static String compileRootMember(String segment) throws CompileException {
        if (segment.startsWith("package ")) return "";
        if (segment.startsWith("import ")) return segment;

        return compileClass(segment)
                .orElseGet(() -> new Err<>(new CompileException("Unknown root member", segment)))
                .unwrap();
    }

    private static Optional<Result<String, CompileException>> compileClass(String rootMember) {
        var classIndex = rootMember.indexOf("class ");
        if (classIndex == -1) return Optional.empty();

        var oldModifiers = rootMember.substring(0, classIndex);
        var newModifiers = oldModifiers.equals("public ") ? "export " : "";

        var afterClass = rootMember.substring(classIndex + "class ".length());
        var contentStart = afterClass.indexOf('{');
        if (contentStart == -1) return Optional.empty();

        var name = afterClass.substring(0, contentStart).strip();
        var afterContentStart = afterClass.substring(contentStart + 1).strip();
        if (!afterContentStart.endsWith("}")) return Optional.empty();

        var content = afterContentStart.substring(0, afterContentStart.length() - 1);

        var oldClassMembers = split(content);
        Result<JavaList<String>, CompileException> newClassMembers = new Ok<>(new JavaList<>());
        for (String oldClassMember : oldClassMembers) {
            newClassMembers = newClassMembers
                    .and(() -> compileClassMember(oldClassMember))
                    .mapValue(tuple -> tuple.left().add(tuple.right()));
        }

        return Optional.of(newClassMembers.mapValue(value -> newModifiers + "class def " + name + "() => {" + String.join("", value.list()) + "}"));
    }

    private static Result<String, CompileException> compileClassMember(String classMember) {
        return compileMethod(classMember).orElseGet(() -> {
            return new Err<>(new CompileException("Invalid class member", classMember));
        });
    }

    private static Optional<Result<String, CompileException>> compileMethod(String classMember) {
        var separator = classMember.indexOf('(');
        if (separator == -1) return Optional.empty();
        var before = classMember.substring(0, separator).strip();
        var space = before.lastIndexOf(" ");
        var name = before.substring(space + 1).strip();
        return Optional.of(new Ok<>("def " + name + "() => {}"));
    }

    private static Path resolve(String extension) {
        return Paths.get(".", "src", "magma", "Main." + extension);
    }

    private static class State {
        private final JavaList<String> segments;
        private final StringBuilder buffer;
        private final int depth;
        private final Deque<Character> queue;

        private State(Deque<Character> queue, StringBuilder buffer, JavaList<String> segments, int depth) {
            this.queue = queue;
            this.buffer = buffer;
            this.segments = segments;
            this.depth = depth;
        }

        public State(Deque<Character> queue) {
            this(queue, new StringBuilder(), new JavaList<>(), 0);
        }

        private State advance() {
            if (buffer.isEmpty()) return this;
            return new State(queue, new StringBuilder(), segments.add(buffer.toString()), depth);
        }

        public State append(char c) {
            return new State(queue, buffer.append(c), segments, depth);
        }

        public boolean isLevel() {
            return depth == 0;
        }

        public State enter() {
            return new State(queue, buffer, segments, depth + 1);
        }

        public State exit() {
            return new State(queue, buffer, segments, depth - 1);
        }

        public boolean isShallow() {
            return depth == 1;
        }

        public Optional<Tuple<State, Character>> append() {
            if (queue.isEmpty()) return Optional.empty();
            var next = queue.pop();
            var appended = append(next);
            return Optional.of(new Tuple<>(appended, next));
        }

        public Optional<State> appendAndDiscard() {
            return append().map(Tuple::left);
        }
    }
}
