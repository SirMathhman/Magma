package magma;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Main {
    public interface Result<T, X> {
        <R> R match(Function<T, R> whenOk, Function<X, R> whenErr);
    }

    public interface IOError {
        String display();
    }

    public interface List_<T> {
        List_<T> add(T element);

        List_<T> addAll(List_<T> others);

        Iterator<T> iter();

        boolean isEmpty();

        T getFirst();

        int size();

        List_<T> slice(int startInclusive, int endExclusive);

        Optional<Tuple<T, List_<T>>> popFirst();

        Optional<T> peekFirst();
    }

    public interface Path_ {
        Path_ resolveSibling(String sibling);

        List_<String> asList();

        Path_ resolveChild(String child);
    }

    public interface Iterator<T> {
        <R> R foldWithInitial(R initial, BiFunction<R, T, R> folder);

        void forEach(Consumer<T> consumer);

        <R> Iterator<R> map(Function<T, R> mapper);

        Iterator<T> filter(Predicate<T> predicate);

        Optional<T> next();

        Iterator<T> concat(Iterator<T> other);

        <C> C collect(Collector<T, C> collector);

        boolean allMatch(Predicate<T> predicate);

        <R> Optional<R> foldWithMapper(Function<T, R> mapper, BiFunction<R, T, R> folder);
    }

    public interface Collector<T, C> {
        C createInitial();

        C fold(C current, T element);
    }

    private interface Head<T> {
        Optional<T> next();
    }

    public record HeadedIterator<T>(Head<T> head) implements Iterator<T> {
        @Override
        public <R> R foldWithInitial(R initial, BiFunction<R, T, R> folder) {
            R current = initial;
            while (true) {
                R finalCurrent = current;
                Optional<R> option = this.head.next().map(next -> folder.apply(finalCurrent, next));

                if (option.isPresent()) {
                    current = option.orElse(finalCurrent);
                }
                else {
                    return current;
                }
            }
        }

        @Override
        public void forEach(Consumer<T> consumer) {
            while (true) {
                Optional<T> next = this.head.next();
                if (next.isEmpty()) {
                    break;
                }
                next.ifPresent(consumer);
            }
        }

        @Override
        public <R> Iterator<R> map(Function<T, R> mapper) {
            return new HeadedIterator<>(() -> this.head.next().map(mapper));
        }

        @Override
        public Iterator<T> filter(Predicate<T> predicate) {
            return this.flatMap(value -> new HeadedIterator<>(predicate.test(value)
                    ? new SingleHead<>(value)
                    : new EmptyHead<T>()));
        }

        @Override
        public Optional<T> next() {
            return this.head.next();
        }

        @Override
        public Iterator<T> concat(Iterator<T> other) {
            return new HeadedIterator<>(() -> this.head.next().or(other::next));
        }

        @Override
        public <C> C collect(Collector<T, C> collector) {
            return this.foldWithInitial(collector.createInitial(), collector::fold);
        }

        @Override
        public boolean allMatch(Predicate<T> predicate) {
            return this.foldWithInitial(true, (aBoolean, t) -> aBoolean && predicate.test(t));
        }

        @Override
        public <R> Optional<R> foldWithMapper(Function<T, R> mapper, BiFunction<R, T, R> folder) {
            return this.head.next().map(mapper).map(next -> this.foldWithInitial(next, folder));
        }

        private <R> Iterator<R> flatMap(Function<T, Iterator<R>> mapper) {
            return this.map(mapper).foldWithInitial(Iterators.empty(), Iterator::concat);
        }
    }

    private static class EmptyHead<T> implements Head<T> {
        @Override
        public Optional<T> next() {
            return Optional.empty();
        }
    }

    private static class SingleHead<T> implements Head<T> {
        private final T value;
        private boolean retrieved = false;

        private SingleHead(T value) {
            this.value = value;
        }

        @Override
        public Optional<T> next() {
            if (this.retrieved) {
                return Optional.empty();
            }

            this.retrieved = true;
            return Optional.of(this.value);
        }
    }

    public record Err<T, X>(X error) implements Result<T, X> {
        @Override
        public <R> R match(Function<T, R> whenOk, Function<X, R> whenErr) {
            return whenErr.apply(this.error);
        }

    }

    public record Ok<T, X>(T value) implements Result<T, X> {
        @Override
        public <R> R match(Function<T, R> whenOk, Function<X, R> whenErr) {
            return whenOk.apply(this.value);
        }

    }

    private static class State {
        private final List_<Character> queue;
        private List_<String> segments;
        private StringBuilder buffer;
        private int depth;

        private State(List_<Character> queue, List_<String> segments, StringBuilder buffer, int depth) {
            this.queue = queue;
            this.segments = segments;
            this.buffer = buffer;
            this.depth = depth;
        }

        public State(List_<Character> queue) {
            this(queue, Lists.empty(), new StringBuilder(), 0);
        }

        private Optional<State> popAndAppend() {
            return this.pop().map(tuple -> tuple.right.append(tuple.left));
        }

        private State advance() {
            this.segments = this.segments.add(this.buffer.toString());
            this.buffer = new StringBuilder();
            return this;
        }

        private State append(char c) {
            this.buffer.append(c);
            return this;
        }

        private boolean isLevel() {
            return this.depth == 0;
        }

        private Optional<Tuple<Character, State>> pop() {
            return this.queue.popFirst().map(tuple -> {
                return new Tuple<>(tuple.left, new State(tuple.right, this.segments, this.buffer, this.depth));
            });
        }

        private boolean hasElements() {
            return !this.queue.isEmpty();
        }

        private State exit() {
            this.depth = this.depth - 1;
            return this;
        }

        private State enter() {
            this.depth = this.depth + 1;
            return this;
        }

        public List_<String> segments() {
            return this.segments;
        }

        public Optional<Character> peek() {
            return this.queue.peekFirst();
        }
    }

    public record Tuple<A, B>(A left, B right) {
    }

    private static class Iterators {
        public static <T> Iterator<T> fromArray(T[] array) {
            return new HeadedIterator<>(new RangeHead(array.length))
                    .map(index -> array[index]);
        }

        public static <T> Iterator<T> empty() {
            return new HeadedIterator<>(new EmptyHead<>());
        }

        public static <T> Iterator<T> from(T... array) {
            return new HeadedIterator<>(new RangeHead(array.length))
                    .map(index -> array[index]);
        }

        public static Iterator<Character> fromString(String input) {
            return fromStringWithIndices(input).map(tuple -> tuple.right);
        }

        public static Iterator<Tuple<Integer, Character>> fromStringWithIndices(String input) {
            return new HeadedIterator<>(new RangeHead(input.length())).map(index -> new Tuple<>(index, input.charAt(index)));
        }
    }

    public static class RangeHead implements Head<Integer> {
        private final int length;
        private int counter = 0;

        public RangeHead(int length) {
            this.length = length;
        }

        @Override
        public Optional<Integer> next() {
            if (this.counter < this.length) {
                int next = this.counter;
                this.counter++;
                return Optional.of(next);
            }

            return Optional.empty();
        }
    }

    public static class ListCollector<T> implements Collector<T, List_<T>> {
        @Override
        public List_<T> createInitial() {
            return Lists.empty();
        }

        @Override
        public List_<T> fold(List_<T> current, T element) {
            return current.add(element);
        }
    }

    private record Joiner(String delimiter) implements Collector<String, Optional<String>> {
        @Override
        public Optional<String> createInitial() {
            return Optional.empty();
        }

        @Override
        public Optional<String> fold(Optional<String> current, String element) {
            return Optional.of(current.map(inner -> inner + this.delimiter + element).orElse(element));
        }
    }

    private static final List_<String> imports = Lists.empty();
    private static final List_<String> structs = Lists.empty();
    private static final List_<String> globals = Lists.empty();
    private static final List_<String> methods = Lists.empty();
    private static int counter = 0;

    public static void main(String[] args) {
        Path_ source = Paths.get(".", "src", "java", "magma", "Main.java");
        Files.readString(source)
                .match(input -> compileAndWrite(source, input), Optional::of)
                .ifPresent(error -> System.err.println(error.display()));
    }

    private static Optional<IOError> compileAndWrite(Path_ source, String input) {
        Path_ target = source.resolveSibling("main.c");
        String output = compile(input);
        return Files.writeString(target, output);
    }

    private static String compile(String input) {
        List_<String> segments = divide(input, Main::divideStatementChar);
        return parseAll(segments, Main::compileRootSegment)
                .map(Main::mergeStatics)
                .map(compiled -> mergeAll(compiled, Main::mergeStatements))
                .or(() -> generatePlaceholder(input)).orElse("");
    }

    private static List_<String> mergeStatics(List_<String> list) {
        return Lists.<String>empty()
                .addAll(imports)
                .addAll(structs)
                .addAll(globals)
                .addAll(methods)
                .addAll(list);
    }

    private static Optional<String> compileStatements(String input, Function<String, Optional<String>> compiler) {
        return compileAndMerge(divide(input, Main::divideStatementChar), compiler, Main::mergeStatements);
    }

    private static Optional<String> compileAndMerge(List_<String> segments, Function<String, Optional<String>> compiler, BiFunction<StringBuilder, String, StringBuilder> merger) {
        return parseAll(segments, compiler).map(compiled -> mergeAll(compiled, merger));
    }

    private static String mergeAll(List_<String> compiled, BiFunction<StringBuilder, String, StringBuilder> merger) {
        return compiled.iter().foldWithInitial(new StringBuilder(), merger).toString();
    }

    private static Optional<List_<String>> parseAll(List_<String> segments, Function<String, Optional<String>> compiler) {
        return segments.iter().foldWithInitial(Optional.of(Lists.empty()),
                (maybeCompiled, segment) -> maybeCompiled.flatMap(
                        allCompiled -> compiler.apply(segment).map(allCompiled::add)));
    }

    private static StringBuilder mergeStatements(StringBuilder output, String compiled) {
        return output.append(compiled);
    }

    private static List_<String> divide(String input, BiFunction<State, Character, State> divider) {
        List_<Character> queue = Iterators.fromString(input)
                .collect(new ListCollector<>());

        State state = new State(queue);
        while (state.hasElements()) {
            Optional<Tuple<Character, State>> maybeNextTuple = state.pop();
            if (maybeNextTuple.isEmpty()) {
                break;
            }

            Tuple<Character, State> nextTuple = maybeNextTuple.orElse(new Tuple<>('\0', state));

            char c = nextTuple.left;
            state = nextTuple.right;

            State finalState = state;
            state = divideSingleQuotes(state, c)
                    .or(() -> divideDoubleQuotes(finalState, c))
                    .orElse(divider.apply(finalState, c));
        }

        return state.advance().segments();
    }

    private static Optional<State> divideSingleQuotes(State state, char c) {
        if (c != '\'') {
            return Optional.empty();
        }

        State appended = state.append(c);
        Optional<Tuple<Character, State>> maybeSlashTuple = appended.pop();
        if (maybeSlashTuple.isEmpty()) {
            return Optional.empty();
        }

        Tuple<Character, State> slashTuple = maybeSlashTuple.orElse(new Tuple<>('\0', appended));
        var withMaybeSlash = slashTuple.right.append(slashTuple.left);

        Optional<State> withSlash = slashTuple.left == '\\' ? withMaybeSlash.popAndAppend() : Optional.empty();
        return withSlash.flatMap(State::popAndAppend);
    }

    private static Optional<State> divideDoubleQuotes(State state, char c) {
        if (c != '\"') {
            return Optional.empty();
        }

        State current = state.append(c);
        while (true) {
            Optional<Tuple<Character, State>> maybeNextTuple = current.pop();
            if (maybeNextTuple.isEmpty()) {
                break;
            }

            Tuple<Character, State> nextTuple = maybeNextTuple.get();

            char next = nextTuple.left;
            current = nextTuple.right.append(next);

            if (next == '\\') {
                current = current.popAndAppend().orElse(current);
            }
            if (next == '"') {
                break;
            }
        }

        return Optional.of(current);
    }

    private static State divideStatementChar(State state, char c) {
        State appended = state.append(c);
        if (c == ';' && appended.isLevel()) {
            return appended.advance();
        }
        if (c == '}' && isShallow(appended)) {
            return appended.advance().exit();
        }
        if (c == '{' || c == '(') {
            return appended.enter();
        }
        if (c == '}' || c == ')') {
            return appended.exit();
        }
        return appended;
    }

    private static boolean isShallow(State state) {
        return state.depth == 1;
    }

    private static Optional<String> compileRootSegment(String input) {
        Optional<String> whitespace = compileWhitespace(input);
        if (whitespace.isPresent()) {
            return whitespace;
        }

        if (input.startsWith("package ")) {
            return Optional.of("");
        }

        String stripped = input.strip();
        if (stripped.startsWith("import ")) {
            String right = stripped.substring("import ".length());
            if (right.endsWith(";")) {
                String content = right.substring(0, right.length() - ";".length());
                String joined = String.join("/", content.split(Pattern.quote(".")));
                imports.add("#include \"./" + joined + "\"\n");
                return Optional.of("");
            }
        }

        Optional<String> maybeClass = compileToStruct(input, "class ", Lists.empty());
        if (maybeClass.isPresent()) {
            return maybeClass;
        }

        return generatePlaceholder(input);
    }

    private static Optional<String> compileToStruct(String input, String infix, List_<String> typeParams) {
        int classIndex = input.indexOf(infix);
        if (classIndex < 0) {
            return Optional.empty();
        }

        String afterKeyword = input.substring(classIndex + infix.length());
        int contentStart = afterKeyword.indexOf("{");
        if (contentStart >= 0) {
            String beforeContent = afterKeyword.substring(0, contentStart).strip();
            int implementsIndex = beforeContent.indexOf(" implements ");

            String beforeImplements;
            if (implementsIndex >= 0) {
                beforeImplements = beforeContent.substring(0, implementsIndex);
            }
            else {
                beforeImplements = beforeContent;
            }
            String strippedBeforeImplements = beforeImplements.strip();

            String withoutParams;
            if (strippedBeforeImplements.endsWith(")")) {
                String withoutEnd = strippedBeforeImplements.substring(0, strippedBeforeImplements.length() - ")".length());
                int paramStart = withoutEnd.indexOf("(");
                if (paramStart >= 0) {
                    withoutParams = withoutEnd.substring(0, paramStart).strip();
                }
                else {
                    withoutParams = strippedBeforeImplements;
                }
            }
            else {
                withoutParams = strippedBeforeImplements;
            }

            String strippedWithoutParams = withoutParams.strip();

            String name;
            if (strippedWithoutParams.endsWith(">")) {
                int genStart = strippedWithoutParams.indexOf("<");
                if (genStart >= 0) {
                    name = strippedWithoutParams.substring(0, genStart).strip();
                }
                else {
                    name = strippedWithoutParams;
                }
            }
            else {
                name = strippedWithoutParams;
            }

            if (isSymbol(name)) {
                String withEnd = afterKeyword.substring(contentStart + "{".length()).strip();
                if (withEnd.endsWith("}")) {
                    String inputContent = withEnd.substring(0, withEnd.length() - "}".length());
                    return compileStatements(inputContent, input1 -> compileClassMember(input1, typeParams)).map(outputContent -> {
                        structs.add("struct " + name + " {\n" + outputContent + "};\n");
                        return "";
                    });
                }
            }
        }
        return Optional.empty();
    }

    private static Optional<String> compileClassMember(String input, List_<String> typeParams) {
        return compileWhitespace(input)
                .or(() -> compileToStruct(input, "interface ", typeParams))
                .or(() -> compileToStruct(input, "record ", typeParams))
                .or(() -> compileToStruct(input, "class ", typeParams))
                .or(() -> compileGlobalInitialization(input, typeParams))
                .or(() -> compileDefinitionStatement(input))
                .or(() -> compileMethod(input, typeParams))
                .or(() -> generatePlaceholder(input));
    }

    private static Optional<String> compileDefinitionStatement(String input) {
        String stripped = input.strip();
        if (stripped.endsWith(";")) {
            String content = stripped.substring(0, stripped.length() - ";".length());
            return compileDefinition(content).map(result -> "\t" + result + ";\n");
        }
        return Optional.empty();
    }

    private static Optional<String> compileGlobalInitialization(String input, List_<String> typeParams) {
        return compileInitialization(input, typeParams, 0).map(generated -> {
            globals.add(generated + ";\n");
            return "";
        });
    }

    private static Optional<String> compileInitialization(String input, List_<String> typeParams, int depth) {
        if (!input.endsWith(";")) {
            return Optional.empty();
        }

        String withoutEnd = input.substring(0, input.length() - ";".length());
        int valueSeparator = withoutEnd.indexOf("=");
        if (valueSeparator < 0) {
            return Optional.empty();
        }

        String definition = withoutEnd.substring(0, valueSeparator).strip();
        String value = withoutEnd.substring(valueSeparator + "=".length()).strip();
        return compileDefinition(definition).flatMap(outputDefinition -> compileValue(value, typeParams, depth).map(outputValue -> outputDefinition + " = " + outputValue));
    }

    private static Optional<String> compileWhitespace(String input) {
        if (input.isBlank()) {
            return Optional.of("");
        }
        return Optional.empty();
    }

    private static Optional<String> compileMethod(String input, List_<String> typeParams) {
        int paramStart = input.indexOf("(");
        if (paramStart < 0) {
            return Optional.empty();
        }

        String inputDefinition = input.substring(0, paramStart).strip();
        String withParams = input.substring(paramStart + "(".length());

        return compileDefinition(inputDefinition).flatMap(outputDefinition -> {
            int paramEnd = withParams.indexOf(")");
            if (paramEnd < 0) {
                return Optional.empty();
            }

            String params = withParams.substring(0, paramEnd);
            return compileValues(params, Main::compileParameter)
                    .flatMap(outputParams -> assembleMethodBody(typeParams, outputDefinition, outputParams, withParams.substring(paramEnd + ")".length()).strip()));
        });
    }

    private static Optional<String> assembleMethodBody(
            List_<String> typeParams,
            String definition,
            String params,
            String body
    ) {
        String header = "\t".repeat(0) + definition + "(" + params + ")";
        if (body.startsWith("{") && body.endsWith("}")) {
            String inputContent = body.substring("{".length(), body.length() - "}".length());
            return compileStatements(inputContent, input1 -> compileStatementOrBlock(input1, typeParams, 1)).flatMap(outputContent -> {
                methods.add(header + " {" + outputContent + "\n}\n");
                return Optional.of("");
            });
        }

        return Optional.of("\t" + header + ";\n");
    }

    private static Optional<String> compileParameter(String definition) {
        return compileWhitespace(definition)
                .or(() -> compileDefinition(definition))
                .or(() -> generatePlaceholder(definition));
    }

    private static Optional<String> compileValues(String input, Function<String, Optional<String>> compiler) {
        List_<String> divided = divide(input, Main::divideValueChar);
        return compileValues(divided, compiler);
    }

    private static State divideValueChar(State state, char c) {
        if (c == '-') {
            if (state.peek().orElse('\0') == '>') {
                state.pop();
                return state.append('-').append('>');
            }
        }

        if (c == ',' && state.isLevel()) {
            return state.advance();
        }

        State appended = state.append(c);
        if (c == '<' || c == '(') {
            return appended.enter();
        }
        if (c == '>' || c == ')') {
            return appended.exit();
        }
        return appended;
    }

    private static Optional<String> compileValues(List_<String> params, Function<String, Optional<String>> compiler) {
        return compileAndMerge(params, compiler, Main::mergeValues);
    }

    private static Optional<String> compileStatementOrBlock(String input, List_<String> typeParams, int depth) {
        return compileWhitespace(input)
                .or(() -> compileKeywordStatement(input, depth, "continue"))
                .or(() -> compileKeywordStatement(input, depth, "break"))
                .or(() -> compileConditional(input, typeParams, "if ", depth))
                .or(() -> compileConditional(input, typeParams, "while ", depth))
                .or(() -> compileElse(input, typeParams, depth))
                .or(() -> compilePostOperator(input, typeParams, depth, "++"))
                .or(() -> compilePostOperator(input, typeParams, depth, "--"))
                .or(() -> compileReturn(input, typeParams, depth).map(result -> formatStatement(depth, result)))
                .or(() -> compileInitialization(input, typeParams, depth).map(result -> formatStatement(depth, result)))
                .or(() -> compileAssignment(input, typeParams, depth).map(result -> formatStatement(depth, result)))
                .or(() -> compileInvocationStatement(input, typeParams, depth).map(result -> formatStatement(depth, result)))
                .or(() -> compileDefinitionStatement(input))
                .or(() -> generatePlaceholder(input));
    }

    private static Optional<String> compilePostOperator(String input, List_<String> typeParams, int depth, String operator) {
        String stripped = input.strip();
        if (stripped.endsWith(operator + ";")) {
            String slice = stripped.substring(0, stripped.length() - (operator + ";").length());
            return compileValue(slice, typeParams, depth).map(value -> value + operator + ";");
        }
        else {
            return Optional.empty();
        }
    }

    private static Optional<String> compileElse(String input, List_<String> typeParams, int depth) {
        String stripped = input.strip();
        if (stripped.startsWith("else ")) {
            String withoutKeyword = stripped.substring("else ".length()).strip();
            if (withoutKeyword.startsWith("{") && withoutKeyword.endsWith("}")) {
                String indent = createIndent(depth);
                return compileStatements(withoutKeyword.substring(1, withoutKeyword.length() - 1),
                        statement -> compileStatementOrBlock(statement, typeParams, depth + 1))
                        .map(result -> indent + "else {" + result + indent + "}");
            }
            else {
                return compileStatementOrBlock(withoutKeyword, typeParams, depth).map(result -> "else " + result);
            }
        }

        return Optional.empty();
    }

    private static Optional<String> compileKeywordStatement(String input, int depth, String keyword) {
        if (input.strip().equals(keyword + ";")) {
            return Optional.of(formatStatement(depth, keyword));
        }
        else {
            return Optional.empty();
        }
    }

    private static String formatStatement(int depth, String value) {
        return createIndent(depth) + value + ";";
    }

    private static String createIndent(int depth) {
        return "\n" + "\t".repeat(depth);
    }

    private static Optional<String> compileConditional(String input, List_<String> typeParams, String prefix, int depth) {
        String stripped = input.strip();
        if (!stripped.startsWith(prefix)) {
            return Optional.empty();
        }

        String afterKeyword = stripped.substring(prefix.length()).strip();
        if (!afterKeyword.startsWith("(")) {
            return Optional.empty();
        }

        String withoutConditionStart = afterKeyword.substring(1);
        int conditionEnd = findConditionEnd(withoutConditionStart);

        if (conditionEnd < 0) {
            return Optional.empty();
        }
        String oldCondition = withoutConditionStart.substring(0, conditionEnd).strip();
        String withBraces = withoutConditionStart.substring(conditionEnd + ")".length()).strip();

        return compileValue(oldCondition, typeParams, depth).flatMap(newCondition -> {
            String withCondition = createIndent(depth) + prefix + "(" + newCondition + ")";

            if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
                String content = withBraces.substring(1, withBraces.length() - 1);
                return compileStatements(content, statement -> compileStatementOrBlock(statement, typeParams, depth + 1)).map(statements -> withCondition +
                        " {" + statements + "\n" +
                        "\t".repeat(depth) +
                        "}");
            }
            else {
                return compileStatementOrBlock(withBraces, typeParams, depth).map(result -> withCondition + " " + result);
            }
        });

    }

    private static int findConditionEnd(String input) {
        int conditionEnd = -1;
        int depth0 = 0;

        List_<Tuple<Integer, Character>> queue = Iterators.fromStringWithIndices(input).collect(new ListCollector<>());

        while (!queue.isEmpty()) {
            Tuple<Integer, Character> pair = queue.popFirst().orElseThrow().left;
            Integer i = pair.left;
            Character c = pair.right;

            if (c == '\'') {
                if (queue.popFirst().orElseThrow().left.right == '\\') {
                    queue.popFirst().orElseThrow();
                }

                queue.popFirst().orElseThrow();
                continue;
            }

            if (c == '"') {
                while (!queue.isEmpty()) {
                    Tuple<Integer, Character> next = queue.popFirst().orElseThrow().left;

                    if (next.right == '\\') {
                        queue.popFirst().orElseThrow();
                    }
                    if (next.right == '"') {
                        break;
                    }
                }

                continue;
            }

            if (c == ')' && depth0 == 0) {
                conditionEnd = i;
                break;
            }

            if (c == '(') {
                depth0++;
            }
            if (c == ')') {
                depth0--;
            }
        }
        return conditionEnd;
    }

    private static Optional<String> compileInvocationStatement(String input, List_<String> typeParams, int depth) {
        String stripped = input.strip();
        if (stripped.endsWith(";")) {
            String withoutEnd = stripped.substring(0, stripped.length() - ";".length());
            Optional<String> maybeInvocation = compileInvocation(withoutEnd, typeParams, depth);
            if (maybeInvocation.isPresent()) {
                return maybeInvocation;
            }
        }
        return Optional.empty();
    }

    private static Optional<String> compileAssignment(String input, List_<String> typeParams, int depth) {
        String stripped = input.strip();
        if (stripped.endsWith(";")) {
            String withoutEnd = stripped.substring(0, stripped.length() - ";".length());
            int valueSeparator = withoutEnd.indexOf("=");
            if (valueSeparator >= 0) {
                String destination = withoutEnd.substring(0, valueSeparator).strip();
                String source = withoutEnd.substring(valueSeparator + "=".length()).strip();
                return compileValue(destination, typeParams, depth).flatMap(newDest -> compileValue(source, typeParams, depth).map(newSource -> newDest + " = " + newSource));
            }
        }
        return Optional.empty();
    }

    private static Optional<String> compileReturn(String input, List_<String> typeParams, int depth) {
        String stripped = input.strip();
        if (stripped.endsWith(";")) {
            String withoutEnd = stripped.substring(0, stripped.length() - ";".length());
            if (withoutEnd.startsWith("return ")) {
                return compileValue(withoutEnd.substring("return ".length()), typeParams, depth).map(result -> "return " + result);
            }
        }

        return Optional.empty();
    }

    private static Optional<String> compileValue(String input, List_<String> typeParams, int depth) {
        String stripped = input.strip();
        if (stripped.startsWith("\"") && stripped.endsWith("\"")) {
            return Optional.of(stripped);
        }
        if (stripped.startsWith("'") && stripped.endsWith("'")) {
            return Optional.of(stripped);
        }

        if (isSymbol(stripped) || isNumber(stripped)) {
            return Optional.of(stripped);
        }

        if (stripped.startsWith("new ")) {
            String slice = stripped.substring("new ".length());
            int argsStart = slice.indexOf("(");
            if (argsStart >= 0) {
                String type = slice.substring(0, argsStart);
                String withEnd = slice.substring(argsStart + "(".length()).strip();
                if (withEnd.endsWith(")")) {
                    String argsString = withEnd.substring(0, withEnd.length() - ")".length());
                    return compileType(type, typeParams).flatMap(outputType -> compileArgs(argsString, typeParams, depth).map(value -> outputType + value));
                }
            }
        }

        if (stripped.startsWith("!")) {
            return compileValue(stripped.substring(1), typeParams, depth).map(result -> "!" + result);
        }

        Optional<String> value = compileLambda(stripped, typeParams, depth);
        if (value.isPresent()) {
            return value;
        }

        Optional<String> invocation = compileInvocation(input, typeParams, depth);
        if (invocation.isPresent()) {
            return invocation;
        }

        int methodIndex = stripped.lastIndexOf("::");
        if (methodIndex >= 0) {
            String type = stripped.substring(0, methodIndex).strip();
            String property = stripped.substring(methodIndex + "::".length()).strip();

            if (isSymbol(property)) {
                return compileType(type, typeParams).flatMap(compiled -> generateLambdaWithReturn(Lists.empty(), "\n\treturn " + compiled + "." + property + "()"));
            }
        }

        int separator = input.lastIndexOf(".");
        if (separator >= 0) {
            String object = input.substring(0, separator).strip();
            String property = input.substring(separator + ".".length()).strip();
            return compileValue(object, typeParams, depth).map(compiled -> compiled + "." + property);
        }

        return compileOperator(input, typeParams, depth, "||")
                .or(() -> compileOperator(input, typeParams, depth, "<"))
                .or(() -> compileOperator(input, typeParams, depth, "+"))
                .or(() -> compileOperator(input, typeParams, depth, ">="))
                .or(() -> compileOperator(input, typeParams, depth, "&&"))
                .or(() -> compileOperator(input, typeParams, depth, "=="))
                .or(() -> compileOperator(input, typeParams, depth, "!="))
                .or(() -> generatePlaceholder(input));
    }

    private static Optional<String> compileOperator(String input, List_<String> typeParams, int depth, String operator) {
        int operatorIndex = input.indexOf(operator);
        if (operatorIndex < 0) {
            return Optional.empty();
        }

        String left = input.substring(0, operatorIndex);
        String right = input.substring(operatorIndex + operator.length());

        return compileValue(left, typeParams, depth).flatMap(leftResult -> compileValue(right, typeParams, depth).map(rightResult -> leftResult + " " + operator + " " + rightResult));
    }

    private static Optional<String> compileLambda(String input, List_<String> typeParams, int depth) {
        int arrowIndex = input.indexOf("->");
        if (arrowIndex < 0) {
            return Optional.empty();
        }

        String beforeArrow = input.substring(0, arrowIndex).strip();
        List_<String> paramNames;
        if (isSymbol(beforeArrow)) {
            paramNames = Lists.of(beforeArrow);
        }
        else if (beforeArrow.startsWith("(") && beforeArrow.endsWith(")")) {
            String inner = beforeArrow.substring(1, beforeArrow.length() - 1);
            paramNames = Iterators.fromArray(inner.split(Pattern.quote(",")))
                    .map(String::strip)
                    .filter(value -> !value.isEmpty())
                    .collect(new ListCollector<>());
        }
        else {
            return Optional.empty();
        }

        String value = input.substring(arrowIndex + "->".length()).strip();
        if (value.startsWith("{") && value.endsWith("}")) {
            String slice = value.substring(1, value.length() - 1);
            return compileStatements(slice, statement -> compileStatementOrBlock(statement, typeParams, depth)).flatMap(result -> generateLambdaWithReturn(paramNames, result));
        }

        return compileValue(value, typeParams, depth).flatMap(newValue -> generateLambdaWithReturn(paramNames, "\n\treturn " + newValue + ";"));
    }

    private static Optional<String> generateLambdaWithReturn(List_<String> paramNames, String returnValue) {
        int current = counter;
        counter++;
        String lambdaName = "__lambda" + current + "__";

        String joined = paramNames.iter()
                .map(name -> "auto " + name)
                .collect(new Joiner(", "))
                .map(value -> "(" + value + ")")
                .orElse("");

        methods.add("auto " + lambdaName + joined + " {" + returnValue + "\n}\n");
        return Optional.of(lambdaName);
    }

    private static boolean isNumber(String input) {
        return IntStream.range(0, input.length()).allMatch(index -> {
            char c = input.charAt(index);
            return (index == 0 && c == '-') || Character.isDigit(c);
        });
    }

    private static Optional<String> compileInvocation(String input, List_<String> typeParams, int depth) {
        String stripped = input.strip();
        if (stripped.endsWith(")")) {
            String sliced = stripped.substring(0, stripped.length() - ")".length());

            int argsStart = findInvocationStart(sliced);

            if (argsStart >= 0) {
                String type = sliced.substring(0, argsStart);
                String withEnd = sliced.substring(argsStart + "(".length()).strip();
                return compileValue(type, typeParams, depth).flatMap(caller -> compileArgs(withEnd, typeParams, depth).map(value -> caller + value));
            }
        }
        return Optional.empty();
    }

    private static int findInvocationStart(String sliced) {
        int argsStart = -1;
        int depth0 = 0;
        int i = sliced.length() - 1;
        while (i >= 0) {
            char c = sliced.charAt(i);
            if (c == '(' && depth0 == 0) {
                argsStart = i;
                break;
            }

            if (c == ')') {
                depth0++;
            }
            if (c == '(') {
                depth0--;
            }
            i--;
        }
        return argsStart;
    }

    private static Optional<String> compileArgs(String argsString, List_<String> typeParams, int depth) {
        return compileValues(argsString, arg -> compileWhitespace(arg).or(() -> compileValue(arg, typeParams, depth))).map(args -> "(" + args + ")");
    }

    private static StringBuilder mergeValues(StringBuilder cache, String element) {
        if (cache.isEmpty()) {
            return cache.append(element);
        }
        return cache.append(", ").append(element);
    }

    private static Optional<String> compileDefinition(String definition) {
        String stripped = definition.strip();
        int nameSeparator = stripped.lastIndexOf(" ");
        if (nameSeparator < 0) {
            return Optional.empty();
        }

        String beforeName = stripped.substring(0, nameSeparator).strip();
        String name = stripped.substring(nameSeparator + " ".length()).strip();
        if (!isSymbol(name)) {
            return Optional.empty();
        }

        int typeSeparator = -1;
        int depth = 0;
        int i = beforeName.length() - 1;
        while (i >= 0) {
            char c = beforeName.charAt(i);
            if (c == ' ' && depth == 0) {
                typeSeparator = i;
                break;
            }
            else {
                if (c == '>') {
                    depth++;
                }
                if (c == '<') {
                    depth--;
                }
            }
            i--;
        }

        if (typeSeparator >= 0) {
            String beforeType = beforeName.substring(0, typeSeparator).strip();

            String beforeTypeParams = beforeType;
            List_<String> typeParams;
            if (beforeType.endsWith(">")) {
                String withoutEnd = beforeType.substring(0, beforeType.length() - ">".length());
                int typeParamStart = withoutEnd.indexOf("<");
                if (typeParamStart >= 0) {
                    beforeTypeParams = withoutEnd.substring(0, typeParamStart);
                    String substring = withoutEnd.substring(typeParamStart + 1);
                    typeParams = splitValues(substring);
                }
                else {
                    typeParams = Lists.empty();
                }
            }
            else {
                typeParams = Lists.empty();
            }

            String strippedBeforeTypeParams = beforeTypeParams.strip();

            String modifiersString;
            int annotationSeparator = strippedBeforeTypeParams.lastIndexOf("\n");
            if (annotationSeparator >= 0) {
                modifiersString = strippedBeforeTypeParams.substring(annotationSeparator + "\n".length());
            }
            else {
                modifiersString = strippedBeforeTypeParams;
            }

            boolean allSymbols = Iterators.fromArray(modifiersString.split(Pattern.quote(" ")))
                    .map(String::strip)
                    .filter(value -> !value.isEmpty())
                    .allMatch(Main::isSymbol);

            if (!allSymbols) {
                return Optional.empty();
            }

            String inputType = beforeName.substring(typeSeparator + " ".length());
            return compileType(inputType, typeParams).flatMap(outputType -> Optional.of(generateDefinition(typeParams, outputType, name)));
        }
        else {
            return compileType(beforeName, Lists.empty()).flatMap(outputType -> Optional.of(generateDefinition(Lists.empty(), outputType, name)));
        }
    }

    private static List_<String> splitValues(String substring) {
        String[] paramsArrays = substring.strip().split(Pattern.quote(","));
        return Iterators.from(paramsArrays)
                .map(String::strip)
                .filter(param -> !param.isEmpty())
                .collect(new ListCollector<>());
    }

    private static String generateDefinition(List_<String> maybeTypeParams, String type, String name) {
        return generateTypeParams(maybeTypeParams) + type + " " + name;
    }

    private static String generateTypeParams(List_<String> maybeTypeParams) {
        if (maybeTypeParams.isEmpty()) {
            return "";
        }
        return maybeTypeParams.iter()
                .collect(new Joiner(", "))
                .map(result -> "<" + result + "> ")
                .orElse("");
    }

    private static Optional<String> compileType(String input, List_<String> typeParams) {
        if (input.equals("void")) {
            return Optional.of("void");
        }

        if (input.equals("int") || input.equals("Integer") || input.equals("boolean") || input.equals("Boolean")) {
            return Optional.of("int");
        }

        if (input.equals("char") || input.equals("Character")) {
            return Optional.of("char");
        }

        if (input.endsWith("[]")) {
            return compileType(input.substring(0, input.length() - "[]".length()), typeParams)
                    .map(value -> value + "*");
        }

        String stripped = input.strip();
        if (isSymbol(stripped)) {
            if (Lists.contains(typeParams, stripped, String::equals)) {
                return Optional.of(stripped);
            }
            else {
                return Optional.of("struct " + stripped);
            }
        }

        if (stripped.endsWith(">")) {
            String slice = stripped.substring(0, stripped.length() - ">".length());
            int argsStart = slice.indexOf("<");
            if (argsStart >= 0) {
                String base = slice.substring(0, argsStart).strip();
                String params = slice.substring(argsStart + "<".length()).strip();
                return compileValues(params, type -> compileWhitespace(type).or(() -> compileType(type, typeParams)))
                        .map(compiled -> base + "<" + compiled + ">");
            }
        }

        return generatePlaceholder(input);
    }

    private static boolean isSymbol(String input) {
        if (input.isBlank()) {
            return false;
        }

        return IntStream.range(0, input.length()).allMatch(index -> {
            char c = input.charAt(index);
            return c == '_' || Character.isLetter(c) || (index != 0 && Character.isDigit(c));
        });
    }

    private static Optional<String> generatePlaceholder(String input) {
        return Optional.of("/* " + input + " */");
    }
}
