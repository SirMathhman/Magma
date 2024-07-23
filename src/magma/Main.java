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

        var rootMembers = new ArrayList<String>();
        for (String segment : segments) {
            rootMembers.add(compileRootMember(segment.strip()));
        }

        var modified = new ArrayList<String>();
        for (String segment : rootMembers) {
            if (segment.isEmpty()) continue;
            modified.add(segment);
        }

        return generate(modified);
    }

    private static String generate(List<String> modified) {
        var output = new StringBuilder();
        for (int i = 0; i < modified.size(); i++) {
            var segment = modified.get(i);
            var prefix = i == 0 ? "" : "\n";
            output.append(prefix).append(segment);
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
        if (c == '}' && state.isShallow()) return appended.advance();
        if (c == '{') return appended.enter();
        if (c == '}') return appended.exit();
        return appended;
    }

    private static String compileRootMember(String rootMember) throws CompileException {
        if (rootMember.startsWith("package ")) return "";
        if (rootMember.startsWith("import ")) return rootMember;

        return compileClass(rootMember).orElseThrow(() -> new CompileException("Unknown root member", rootMember));
    }

    private static Optional<String> compileClass(String rootMember) throws CompileException {
        var classIndex = rootMember.indexOf(CLASS_KEYWORD_WITH_SPACE);
        if (classIndex == -1) return Optional.empty();

        var oldModifiers = rootMember.substring(0, classIndex);
        var newModifiers = oldModifiers.equals("public ") ? "export " : "";

        var right = rootMember.substring(classIndex + CLASS_KEYWORD_WITH_SPACE.length());

        var contentStart = right.indexOf('{');
        if (contentStart == -1) return Optional.empty();

        var name = right.substring(0, contentStart).strip();
        var contentAndEnd = right.substring(contentStart + 1).strip();
        if (!contentAndEnd.endsWith("}")) return Optional.empty();

        var content = contentAndEnd.substring(0, contentAndEnd.length() - 1);
        var inputClassMembers = split(content);
        var outputClassMembers = new ArrayList<String>();
        for (var inputClassMember : inputClassMembers) {
            outputClassMembers.add(compileClassMember(inputClassMember.strip()));
        }

        var generated = generate(outputClassMembers);
        return Optional.of(newModifiers + CLASS_KEYWORD_WITH_SPACE + "def " + name + "() => {" + generated + "}");
    }

    private static String compileClassMember(String classMember) throws CompileException {
        return compileDeclaration(classMember)
                .orElseGet(() -> new Err<>(new CompileException("Invalid class member", classMember)))
                .unwrap();
    }

    private static Optional<Result<String, CompileException>> compileDeclaration(String classMember) {
        var separator = classMember.indexOf('=');
        if (separator == -1) return Optional.empty();

        var definition = classMember.substring(0, separator).strip();
        var last = definition.lastIndexOf(' ');
        var name = definition.substring(last + 1).strip();

        var valueAndEnd = classMember.substring(separator + 1).strip();
        if (!valueAndEnd.endsWith(";")) return Optional.empty();

        var value = valueAndEnd.substring(0, valueAndEnd.length() - 1).strip();
        return Optional.of(compileValue(value).mapValue(compiledValue -> "let " + name + " = " + compiledValue + ";"));
    }

    private static Result<String, CompileException> compileValue(String value) {
        if (value.startsWith("\"") && value.endsWith("\"")) return new Ok<>(value);
        return new Err<>(new CompileException("Unknown value", value));
    }

    private interface Result<T, E extends Exception> {
        <R> Result<R, E> mapValue(Function<T, R> mapper);

        T unwrap() throws E;
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

        public boolean isShallow() {
            return depth == 1;
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

    private record Ok<T, E extends Exception>(T value) implements Result<T, E> {
        @Override
        public <R> Result<R, E> mapValue(Function<T, R> mapper) {
            return new Ok<>(mapper.apply(value));
        }

        @Override
        public T unwrap() throws E {
            return value;
        }
    }

    private record Err<T, E extends Exception>(E value) implements Result<T, E> {
        @Override
        public <R> Result<R, E> mapValue(Function<T, R> mapper) {
            return new Err<>(value);
        }

        @Override
        public T unwrap() throws E {
            throw value;
        }
    }
}
