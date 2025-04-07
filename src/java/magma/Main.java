package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Main {


    private sealed interface Result<T, X> permits Ok, Err {
        <R> R match(Function<T, R> whenOk, Function<X, R> whenErr);
    }

    private sealed interface Option<T> permits Some, None {
        void ifPresent(Consumer<T> ifPresent);

        <R> Option<R> flatMap(Function<T, Option<R>> mapper);

        <R> Option<R> map(Function<T, R> mapper);

        T orElse(T other);

        boolean isPresent();

        Tuple<Boolean, T> toTuple(T other);

        T orElseGet(Supplier<T> other);

        Option<T> or(Supplier<Option<T>> other);
    }

    private interface List_<T> {
        List_<T> add(T element);

        List_<T> addAll(List_<T> elements);

        void forEach(Consumer<T> consumer);

        Stream_<T> stream();

        T popFirst();

        boolean isEmpty();
    }

    private interface Stream_<T> {
        <R> Stream_<R> map(Function<T, R> mapper);

        <R> R foldWithInitial(R initial, BiFunction<R, T, R> folder);

        <C> C collect(Collector<T, C> collector);

        <R> Option<R> foldToOption(R initial, BiFunction<R, T, Option<R>> folder);
    }

    private interface Collector<T, C> {
        C createInitial();

        C fold(C current, T element);
    }

    private interface Head<T> {
        Option<T> next();
    }

    private record Tuple<A, B>(A left, B right) {
    }

    private record Ok<T, X>(T value) implements Result<T, X> {
        @Override
        public <R> R match(Function<T, R> whenOk, Function<X, R> whenErr) {
            return whenOk.apply(value);
        }
    }

    private record Err<T, X>(X error) implements Result<T, X> {
        @Override
        public <R> R match(Function<T, R> whenOk, Function<X, R> whenErr) {
            return whenErr.apply(error);
        }
    }

    private static final class RangeHead implements Head<Integer> {
        private final int size;
        private int counter = 0;

        private RangeHead(int size) {
            this.size = size;
        }

        @Override
        public Option<Integer> next() {
            if (counter < size) {
                int value = counter;
                counter++;
                return new Some<>(value);
            } else {
                return new None<>();
            }
        }
    }

    private record HeadedStream<T>(Head<T> head) implements Stream_<T> {
        @Override
        public <R> Stream_<R> map(Function<T, R> mapper) {
            return new HeadedStream<>(() -> head.next().map(mapper));
        }

        @Override
        public <R> R foldWithInitial(R initial, BiFunction<R, T, R> folder) {
            R current = initial;
            while (true) {
                R finalCurrent = current;
                Tuple<Boolean, R> tuple = head.next()
                        .map(inner -> folder.apply(finalCurrent, inner))
                        .toTuple(current);

                if (tuple.left) {
                    current = tuple.right;
                } else {
                    return current;
                }
            }
        }

        @Override
        public <C> C collect(Collector<T, C> collector) {
            return foldWithInitial(collector.createInitial(), collector::fold);
        }

        @Override
        public <R> Option<R> foldToOption(R initial, BiFunction<R, T, Option<R>> folder) {
            return this.<Option<R>>foldWithInitial(new Some<>(initial), (rOption, t) -> rOption.flatMap(current -> folder.apply(current, t)));
        }
    }

    private record JavaList<T>(List<T> inner) implements List_<T> {
        public JavaList() {
            this(new ArrayList<>());
        }

        @Override
        public List_<T> add(T element) {
            inner.add(element);
            return this;
        }

        @Override
        public List_<T> addAll(List_<T> elements) {
            elements.forEach(this::add);
            return this;
        }

        @Override
        public void forEach(Consumer<T> consumer) {
            inner.forEach(consumer);
        }

        @Override
        public Stream_<T> stream() {
            return new HeadedStream<>(new RangeHead(inner.size())).map(inner::get);
        }

        @Override
        public T popFirst() {
            return inner.removeFirst();
        }

        @Override
        public boolean isEmpty() {
            return inner.isEmpty();
        }
    }

    private static class Lists {
        public static <T> List_<T> empty() {
            return new JavaList<>();
        }
    }

    private static class State {
        private final List_<Character> queue;
        private final List_<String> segments;
        private StringBuilder buffer;
        private int depth;

        private State(List_<Character> queue) {
            this(queue, Lists.empty(), new StringBuilder(), 0);
        }

        private State(List_<Character> queue, List_<String> segments, StringBuilder buffer, int depth) {
            this.queue = queue;
            this.segments = segments;
            this.buffer = buffer;
            this.depth = depth;
        }

        private State popAndAppend() {
            return append(pop());
        }

        private boolean hasNext() {
            return !queue.isEmpty();
        }

        private State enter() {
            this.depth = depth + 1;
            return this;
        }

        private State exit() {
            this.depth = depth - 1;
            return this;
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

        private boolean isLevel() {
            return depth == 0;
        }

        private char pop() {
            return queue.popFirst();
        }

        private boolean isShallow() {
            return depth == 1;
        }

        public List_<String> segments() {
            return segments;
        }
    }

    private record Some<T>(T value) implements Option<T> {
        @Override
        public void ifPresent(Consumer<T> ifPresent) {
            ifPresent.accept(value);
        }

        @Override
        public <R> Option<R> flatMap(Function<T, Option<R>> mapper) {
            return mapper.apply(value);
        }

        @Override
        public <R> Option<R> map(Function<T, R> mapper) {
            return new Some<>(mapper.apply(value));
        }

        @Override
        public T orElse(T other) {
            return value;
        }

        @Override
        public boolean isPresent() {
            return true;
        }

        @Override
        public Tuple<Boolean, T> toTuple(T other) {
            return new Tuple<>(true, value);
        }

        @Override
        public T orElseGet(Supplier<T> other) {
            return value;
        }

        @Override
        public Option<T> or(Supplier<Option<T>> other) {
            return this;
        }
    }

    private static final class None<T> implements Option<T> {
        @Override
        public void ifPresent(Consumer<T> ifPresent) {
        }

        @Override
        public <R> Option<R> flatMap(Function<T, Option<R>> mapper) {
            return new None<>();
        }

        @Override
        public <R> Option<R> map(Function<T, R> mapper) {
            return new None<>();
        }

        @Override
        public T orElse(T other) {
            return other;
        }

        @Override
        public boolean isPresent() {
            return false;
        }

        @Override
        public Tuple<Boolean, T> toTuple(T other) {
            return new Tuple<>(false, other);
        }

        @Override
        public T orElseGet(Supplier<T> other) {
            return other.get();
        }

        @Override
        public Option<T> or(Supplier<Option<T>> other) {
            return other.get();
        }
    }

    private static class Streams {
        public static Stream_<Character> from(String value) {
            return new HeadedStream<>(new RangeHead(value.length())).map(value::charAt);
        }
    }

    private static class ListCollector<T> implements Collector<T, List_<T>> {
        @Override
        public List_<T> createInitial() {
            return Lists.empty();
        }

        @Override
        public List_<T> fold(List_<T> current, T element) {
            return current.add(element);
        }
    }

    private static final List_<String> imports = Lists.empty();
    private static final List_<String> structs = Lists.empty();
    private static final List_<String> functions = Lists.empty();
    private static List_<String> globals = Lists.empty();
    private static int lambdaCounter = 0;

    public static void main(String[] args) {
        Path source = Paths.get(".", "src", "java", "magma", "Main.java");
        readString(source)
                .match(input -> runWithInput(source, input), Some::new)
                .ifPresent(Throwable::printStackTrace);
    }

    private static Option<IOException> runWithInput(Path source, String input) {
        String output = compile(input) + "int main(){\n\t__main__();\n\treturn 0;\n}\n";

        Path target = source.resolveSibling("main.c");
        return writeString(target, output);
    }

    private static Option<IOException> writeString(Path target, String output) {
        try {
            Files.writeString(target, output);
            return new None<>();
        } catch (IOException e) {
            return new Some<>(e);
        }
    }

    private static Result<String, IOException> readString(Path source) {
        try {
            return new Ok<>(Files.readString(source));
        } catch (IOException e) {
            return new Err<>(e);
        }
    }

    private static String compile(String input) {
        List_<String> segments = divideAll(input, Main::divideStatementChar);
        return parseAll(segments, Main::compileRootSegment)
                .map(compiled -> {
                    compiled.addAll(imports);
                    compiled.addAll(structs);
                    compiled.addAll(globals);
                    compiled.addAll(functions);
                    return compiled;
                })
                .map(compiled -> mergeAll(compiled, Main::mergeStatements))
                .orElse("");
    }

    private static Option<String> compileStatements(String input, Function<String, Option<String>> compiler) {
        return compileAll(divideAll(input, Main::divideStatementChar), compiler, Main::mergeStatements);
    }

    private static Option<String> compileAll(
            List_<String> segments,
            Function<String, Option<String>> compiler,
            BiFunction<StringBuilder, String, StringBuilder> merger
    ) {
        return parseAll(segments, compiler).map(compiled -> mergeAll(compiled, merger));
    }

    private static String mergeAll(List_<String> compiled, BiFunction<StringBuilder, String, StringBuilder> merger) {
        return compiled.stream().foldWithInitial(new StringBuilder(), merger).toString();
    }

    private static Option<List_<String>> parseAll(List_<String> segments, Function<String, Option<String>> compiler) {
        return segments.stream().foldToOption(Lists.empty(), (compiled, segment) -> compiler.apply(segment).map(compiled::add));
    }

    private static StringBuilder mergeStatements(StringBuilder output, String str) {
        return output.append(str);
    }

    private static List_<String> divideAll(String input, BiFunction<State, Character, State> divider) {
        List_<Character> queue = Streams.from(input).collect(new ListCollector<>());

        State current = new State(queue);
        while (current.hasNext()) {
            char c = current.pop();

            State finalCurrent = current;
            current = divideSingleQuotes(finalCurrent, c)
                    .or(() -> divideDoubleQuotes(finalCurrent, c))
                    .orElseGet(() -> divider.apply(finalCurrent, c));
        }

        return current.advance().segments();
    }

    private static Option<State> divideDoubleQuotes(State state, char c) {
        if (c != '"') return new None<>();

        State current = state.append(c);
        while (current.hasNext()) {
            char popped = current.pop();
            current = current.append(popped);

            if (popped == '\\') current = current.popAndAppend();
            if (popped == '"') break;
        }

        return new Some<>(current);
    }

    private static Option<State> divideSingleQuotes(State current, char c) {
        if (c != '\'') return new None<>();

        State appended = current.append(c);
        char maybeEscape = current.pop();
        State withNext = appended.append(maybeEscape);
        State appended1 = maybeEscape == '\\' ? withNext.popAndAppend() : withNext;

        return new Some<>(appended1.popAndAppend());
    }

    private static State divideStatementChar(State state, char c) {
        State appended = state.append(c);
        if (c == ';' && appended.isLevel()) return appended.advance();
        if (c == '}' && appended.isShallow()) return appended.advance().exit();
        if (c == '{') return appended.enter();
        if (c == '}') return appended.exit();
        return appended;
    }

    private static Option<String> compileRootSegment(String input) {
        String stripped = input.strip();
        if (stripped.isEmpty()) return new Some<>("");
        if (input.startsWith("package ")) return new Some<>("");

        if (stripped.startsWith("import ")) {
            String value = "#include <temp.h>\n";
            imports.add(value);
            return new Some<>("");
        }

        Option<String> maybeClass = compileTypedBlock(input, "class ");
        if (maybeClass.isPresent()) return maybeClass;

        return new Some<>(invalidate("root segment", input));
    }

    private static Option<String> compileTypedBlock(String input, String keyword) {
        int classIndex = input.indexOf(keyword);
        if (classIndex < 0) return new None<>();

        String modifiers = input.substring(0, classIndex).strip();
        String right = input.substring(classIndex + keyword.length());
        int contentStart = right.indexOf("{");
        if (contentStart < 0) return new None<>();

        String name = right.substring(0, contentStart).strip();
        String body = right.substring(contentStart + "{".length()).strip();
        if (!body.endsWith("}")) return new None<>();

        String inputContent = body.substring(0, body.length() - "}".length());
        return compileStatements(inputContent, Main::compileClassSegment)
                .map(outputContent -> generateStruct(modifiers, name, outputContent));
    }

    private static String generateStruct(String modifiers, String name, String content) {
        String modifiersString = modifiers.isEmpty() ? "" : generatePlaceholder(modifiers) + " ";
        String generated = modifiersString + "struct " + name + " {" +
                content +
                "\n};\n";
        structs.add(generated);
        return "";
    }

    private static String invalidate(String type, String input) {
        System.err.println("Invalid " + type + ": " + input);
        return generatePlaceholder(input);
    }

    private static Option<String> compileClassSegment(String input) {
        if (input.isBlank()) return new Some<>("");

        Option<String> maybeInterface = compileTypedBlock(input, "interface ");
        if (maybeInterface.isPresent()) return maybeInterface;

        int recordIndex = input.indexOf("record ");
        if (recordIndex >= 0) {
            return new Some<>(generateStruct("", "Temp", ""));
        }

        Option<String> maybeMethod = compileMethod(input);
        if (maybeMethod.isPresent()) return maybeMethod;

        Option<String> maybeAssignment = compileAssignment(input);
        if (maybeAssignment.isPresent()) {
            globals = maybeAssignment.map(value -> value + ";\n")
                    .map(globals::add)
                    .orElse(globals);

            return new Some<>("");
        }

        return new Some<>(invalidate("class segment", input));
    }

    private static Option<String> compileMethod(String input) {
        int paramStart = input.indexOf("(");
        if (paramStart < 0) return new None<>();

        String header = input.substring(0, paramStart).strip();
        String withParams = input.substring(paramStart + "(".length());
        int paramEnd = withParams.indexOf(")");
        if (paramEnd < 0) return new None<>();

        String paramString = withParams.substring(0, paramEnd);
        String withBody = withParams.substring(paramEnd + ")".length()).strip();

        return compileValues(paramString, Main::compileDefinition).flatMap(outputParams -> {
            return compileDefinition(header).flatMap(definition -> {
                String string = generateInvokable(definition, outputParams);

                if (!withBody.startsWith("{") || !withBody.endsWith("}"))
                    return new Some<>(generateStatement(string));

                return compileStatements(withBody.substring(1, withBody.length() - 1), Main::compileStatement).map(statement -> {
                    return addFunction(statement, string);
                });
            });
        });
    }

    private static String addFunction(String content, String string) {
        String function = string + "{" + content + "\n}\n";
        functions.add(function);
        return "";
    }

    private static String generateInvokable(String definition, String params) {
        return definition + "(" + params + ")";
    }

    private static Option<String> compileValues(String input, Function<String, Option<String>> compiler) {
        return compileAll(divideAll(input, Main::divideValueChar), compiler, Main::mergeValues);
    }

    private static State divideValueChar(State state, Character c) {
        if (c == ',' && state.isLevel()) return state.advance();

        State appended = state.append(c);
        if (c == '(' || c == '<') return appended.enter();
        if (c == ')' || c == '>') return appended.exit();
        return appended;
    }

    private static StringBuilder mergeValues(StringBuilder buffer, String element) {
        if (buffer.isEmpty()) return buffer.append(element);
        return buffer.append(", ").append(element);
    }

    private static Option<String> compileStatement(String input) {
        String stripped = input.strip();
        if (stripped.isEmpty()) return new Some<>("");

        if (stripped.endsWith(";")) {
            String withoutEnd = stripped.substring(0, stripped.length() - ";".length());

            Option<String> maybeAssignment = compileAssignment(withoutEnd);
            if (maybeAssignment.isPresent()) return maybeAssignment;

            Option<String> maybeInvocation = compileInvocation(withoutEnd);
            if (maybeInvocation.isPresent()) return maybeInvocation.map(Main::generateStatement);
        }

        return new Some<>(invalidate("statement", input));
    }

    private static Option<String> compileAssignment(String input) {
        int separator = input.indexOf("=");
        if (separator < 0) return new None<>();

        String inputDefinition = input.substring(0, separator);
        String inputValue = input.substring(separator + "=".length());
        return compileDefinition(inputDefinition).flatMap(outputDefinition -> {
            return compileValue(inputValue).map(outputValue -> {
                return generateStatement(outputDefinition + " = " + outputValue);
            });
        });
    }

    private static String generateStatement(String value) {
        return "\n\t" + value + ";";
    }

    private static Option<String> compileValue(String input) {
        String stripped = input.strip();
        if (stripped.startsWith("\"") && stripped.endsWith("\"")) return new Some<>(stripped);

        int arrowIndex = stripped.indexOf("->");
        if (arrowIndex >= 0) {
            String paramName = stripped.substring(0, arrowIndex).strip();
            String inputValue = stripped.substring(arrowIndex + "->".length());
            if (isSymbol(paramName)) {
                return compileValue(inputValue).map(outputValue -> generateLambda(paramName, outputValue));
            }
        }

        Option<String> maybeInvocation = compileInvocation(stripped);
        if (maybeInvocation.isPresent()) return maybeInvocation;

        int dataSeparator = stripped.lastIndexOf(".");
        if (dataSeparator >= 0) {
            String object = stripped.substring(0, dataSeparator);
            String property = stripped.substring(dataSeparator + ".".length());

            return compileValue(object).map(newObject -> {
                return newObject + "." + property;
            });
        }

        int methodSeparator = stripped.lastIndexOf("::");
        if (methodSeparator >= 0) {
            String object = stripped.substring(0, methodSeparator);
            String property = stripped.substring(methodSeparator + "::".length());

            return compileValue(object).map(newObject -> {
                String caller = newObject + "." + property;
                String paramName = newObject.toLowerCase();
                return generateLambda(paramName, generateInvocation(caller, paramName));
            });
        }

        if (isSymbol(stripped)) {
            return new Some<>(stripped);
        }

        return new Some<>(invalidate("value", input));
    }

    private static String generateLambda(String paramName, String lambdaValue) {
        String lambda = "__lambda" + lambdaCounter + "__";
        lambdaCounter++;

        String definition = generateDefinition("", "auto", lambda);
        String param = generateDefinition("", "auto", paramName);
        addFunction("\n\treturn " + lambdaValue + ";", generateInvokable(definition, param));

        return lambda;
    }

    private static Option<String> compileInvocation(String stripped) {
        if (!stripped.endsWith(")")) return new None<>();
        String withoutEnd = stripped.substring(0, stripped.length() - ")".length());

        int argsStart = -1;
        int depth = 0;
        for (int i = withoutEnd.length() - 1; i >= 0; i--) {
            char c = withoutEnd.charAt(i);
            if (c == '(' && depth == 0) {
                argsStart = i;
                break;
            } else {
                if (c == ')') depth++;
                if (c == '(') depth--;
            }
        }

        if (argsStart < 0) return new None<>();

        String inputCaller = withoutEnd.substring(0, argsStart);
        String inputArguments = withoutEnd.substring(argsStart + 1);
        return compileValues(inputArguments, Main::compileValue).flatMap(outputValues -> {
            return compileValue(inputCaller).map(outputCaller -> {
                return generateInvocation(outputCaller, outputValues);
            });
        });
    }

    private static String generateInvocation(String caller, String arguments) {
        return caller + "(" + arguments + ")";
    }

    private static Option<String> compileDefinition(String input) {
        String stripped = input.strip();
        int nameSeparator = stripped.lastIndexOf(" ");
        if (nameSeparator >= 0) {
            String beforeName = stripped.substring(0, nameSeparator);

            int typeSeparator = -1;
            int depth = 0;
            for (int i = 0; i < beforeName.length(); i++) {
                char c = beforeName.charAt(i);
                if(c == ' ' && depth == 0) {
                    typeSeparator = i;
                    break;
                } else {
                    if(c == '>') depth++;
                    if(c == '<') depth--;
                }
            }

            String modifiers;
            String type;
            if (typeSeparator >= 0) {
                modifiers = generatePlaceholder(beforeName.substring(0, typeSeparator));
                type = beforeName.substring(typeSeparator + 1);
            } else {
                modifiers = "";
                type = beforeName;
            }

            String name = stripped.substring(nameSeparator + " ".length());
            return new Some<>(generateDefinition(modifiers, compileType(type), name));
        }

        return new Some<>(invalidate("definition", stripped));
    }

    private static String generateDefinition(String modifiers, String type, String name) {
        return modifiers + type + " " + name;
    }

    private static String compileType(String type) {
        String stripped = type.strip();
        if (stripped.equals("void")) return "void";
        if (stripped.endsWith("[]")) return compileType(stripped.substring(0, stripped.length() - "[]".length())) + "*";

        if (isSymbol(stripped)) return "struct " + stripped;
        if (stripped.endsWith(">")) {
            return "Generic";
        }

        return invalidate("type", stripped);
    }

    private static boolean isSymbol(String input) {
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (!Character.isLetter(c)) {
                return false;
            }
        }
        return true;
    }

    private static String generatePlaceholder(String input) {
        return "/* " + input + " */";
    }
}