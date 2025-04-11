package magma;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Main {
    public interface Option<T> {
        <R> Option<R> map(Function<T, R> mapper);

        T orElse(T other);

        boolean isPresent();

        boolean isEmpty();

        void ifPresent(Consumer<T> consumer);

        Option<T> or(Supplier<Option<T>> supplier);

        <R> Option<R> flatMap(Function<T, Option<R>> mapper);

        T orElseGet(Supplier<T> other);
    }

    public interface List_<T> {
        List_<T> add(T element);

        List_<T> addAll(List_<T> elements);

        Iterator<T> iter();

        Option<Tuple<T, List_<T>>> popFirst();

        T pop();

        boolean isEmpty();

        T peek();

        int size();

        List_<T> slice(int startInclusive, int endExclusive);

        T get(int index);

        List_<T> sort(BiFunction<T, T, Integer> sorter);
    }

    public interface Iterator<T> {
        <R> R fold(R initial, BiFunction<R, T, R> folder);

        <R> Iterator<R> map(Function<T, R> mapper);

        <C> C collect(Collector<T, C> collector);

        boolean anyMatch(Predicate<T> predicate);

        void forEach(Consumer<T> consumer);

        Iterator<T> filter(Predicate<T> predicate);

        boolean allMatch(Predicate<T> predicate);

        Iterator<T> concat(Iterator<T> other);

        Option<T> next();
    }

    public interface Head<T> {
        Option<T> next();
    }

    public interface Collector<T, C> {
        C createInitial();

        C fold(C current, T element);
    }

    public interface Result<T, X> {
        <R> R match(Function<T, R> whenOk, Function<X, R> whenErr);

        <R> Result<R, X> flatMapValue(Function<T, Result<R, X>> mapper);

        <R> Result<R, X> mapValue(Function<T, R> mapper);

        Option<T> findValue();

        <R> Result<T, R> mapErr(Function<X, R> mapper);
    }

    public interface IOError extends Error {
        String display();
    }

    public interface Path_ {
        Path_ resolveSibling(String sibling);

        List_<String> listNames();
    }

    private interface Error {
        String display();
    }

    public record Err<T, X>(X error) implements Result<T, X> {
        @Override
        public <R> R match(Function<T, R> whenOk, Function<X, R> whenErr) {
            return whenErr.apply(this.error);
        }

        @Override
        public <R> Result<R, X> flatMapValue(Function<T, Result<R, X>> mapper) {
            return new Err<>(this.error);
        }

        @Override
        public <R> Result<R, X> mapValue(Function<T, R> mapper) {
            return new Err<>(this.error);
        }

        @Override
        public Option<T> findValue() {
            return new None<>();
        }

        @Override
        public <R> Result<T, R> mapErr(Function<X, R> mapper) {
            return new Err<>(mapper.apply(this.error));
        }
    }

    public record Ok<T, X>(T value) implements Result<T, X> {
        @Override
        public <R> R match(Function<T, R> whenOk, Function<X, R> whenErr) {
            return whenOk.apply(this.value);
        }

        @Override
        public <R> Result<R, X> flatMapValue(Function<T, Result<R, X>> mapper) {
            return mapper.apply(this.value);
        }

        @Override
        public <R> Result<R, X> mapValue(Function<T, R> mapper) {
            return new Ok<>(mapper.apply(this.value));
        }

        @Override
        public Option<T> findValue() {
            return new Some<>(this.value);
        }

        @Override
        public <R> Result<T, R> mapErr(Function<X, R> mapper) {
            return new Ok<>(this.value);
        }
    }

    private static class State {
        private final List_<Character> queue;
        private final List_<String> segments;
        private StringBuilder buffer;
        private int depth;

        private State(List_<Character> queue, List_<String> segments, StringBuilder buffer, int depth) {
            this.queue = queue;
            this.segments = segments;
            this.buffer = buffer;
            this.depth = depth;
        }

        public State(List_<Character> queue) {
            this(queue, Impl.emptyList(), new StringBuilder(), 0);
        }

        private State advance() {
            this.segments.add(this.buffer.toString());
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

        private char pop() {
            return this.queue.pop();
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

        public char peek() {
            return this.queue.peek();
        }
    }

    public record Tuple<A, B>(A left, B right) {
    }

    public static class None<T> implements Option<T> {
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
        public boolean isEmpty() {
            return true;
        }

        @Override
        public void ifPresent(Consumer<T> consumer) {
        }

        @Override
        public Option<T> or(Supplier<Option<T>> supplier) {
            return supplier.get();
        }

        @Override
        public <R> Option<R> flatMap(Function<T, Option<R>> mapper) {
            return new None<>();
        }

        @Override
        public T orElseGet(Supplier<T> other) {
            return other.get();
        }
    }

    public record Some<T>(T value) implements Option<T> {
        @Override
        public <R> Option<R> map(Function<T, R> mapper) {
            return new Some<>(mapper.apply(this.value));
        }

        @Override
        public T orElse(T other) {
            return this.value;
        }

        @Override
        public boolean isPresent() {
            return true;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public void ifPresent(Consumer<T> consumer) {
            consumer.accept(this.value);
        }

        @Override
        public Option<T> or(Supplier<Option<T>> supplier) {
            return this;
        }

        @Override
        public <R> Option<R> flatMap(Function<T, Option<R>> mapper) {
            return mapper.apply(this.value);
        }

        @Override
        public T orElseGet(Supplier<T> other) {
            return this.value;
        }
    }

    private record Joiner(String delimiter) implements Collector<String, Option<String>> {
        @Override
        public Option<String> createInitial() {
            return new None<>();
        }

        @Override
        public Option<String> fold(Option<String> current, String element) {
            return new Some<>(current.map(inner -> inner + this.delimiter + element).orElse(element));
        }
    }

    static final class RangeHead implements Head<Integer> {
        private final int length;
        private int counter = 0;

        public RangeHead(int length) {
            this.length = length;
        }

        @Override
        public Option<Integer> next() {
            if (this.counter >= this.length) {
                return new None<>();
            }

            int value = this.counter;
            this.counter++;
            return new Some<>(value);
        }
    }

    record HeadedIterator<T>(Head<T> head) implements Iterator<T> {
        @Override
        public <R> R fold(R initial, BiFunction<R, T, R> folder) {
            R current = initial;
            while (true) {
                R finalCurrent = current;
                Option<R> maybeCurrent = this.head.next().map(next -> folder.apply(finalCurrent, next));
                if (maybeCurrent.isPresent()) {
                    current = maybeCurrent.orElse(null);
                }
                else {
                    return current;
                }
            }
        }

        @Override
        public <R> Iterator<R> map(Function<T, R> mapper) {
            return new HeadedIterator<>(() -> this.head.next().map(mapper));
        }

        @Override
        public <C> C collect(Collector<T, C> collector) {
            return this.fold(collector.createInitial(), collector::fold);
        }

        @Override
        public boolean anyMatch(Predicate<T> predicate) {
            return this.fold(false, (aBoolean, t) -> aBoolean || predicate.test(t));
        }

        @Override
        public void forEach(Consumer<T> consumer) {
            while (true) {
                Option<T> next = this.head.next();
                if (next.isEmpty()) {
                    break;
                }
                next.ifPresent(consumer);
            }
        }

        @Override
        public Iterator<T> filter(Predicate<T> predicate) {
            return this.flatMap(value -> new HeadedIterator<>(predicate.test(value)
                    ? new SingleHead<>(value)
                    : new EmptyHead<>()));
        }

        @Override
        public boolean allMatch(Predicate<T> predicate) {
            return this.fold(true, (aBoolean, t) -> aBoolean && predicate.test(t));
        }

        @Override
        public Iterator<T> concat(Iterator<T> other) {
            return new HeadedIterator<>(() -> this.head.next().or(other::next));
        }

        @Override
        public Option<T> next() {
            return this.head.next();
        }

        private <R> Iterator<R> flatMap(Function<T, Iterator<R>> mapper) {
            return this.map(mapper).fold(Iterators.empty(), Iterator::concat);
        }
    }

    private static class EmptyHead<T> implements Head<T> {
        @Override
        public Option<T> next() {
            return new None<>();
        }
    }

    private static class Iterators {
        public static <T> Iterator<T> empty() {
            return new HeadedIterator<>(new EmptyHead<>());
        }

        public static Iterator<Character> fromString(String string) {
            return fromStringWithIndices(string).map(Tuple::right);
        }

        public static Iterator<Tuple<Integer, Character>> fromStringWithIndices(String string) {
            return new HeadedIterator<>(new RangeHead(string.length()))
                    .map(index -> new Tuple<>(index, string.charAt(index)));
        }
    }

    private static class ListCollector<T> implements Collector<T, List_<T>> {
        @Override
        public List_<T> createInitial() {
            return Impl.emptyList();
        }

        @Override
        public List_<T> fold(List_<T> current, T element) {
            return current.add(element);
        }
    }

    private static class SingleHead<T> implements Head<T> {
        private final T value;
        private boolean retrieved = false;

        public SingleHead(T value) {
            this.value = value;
        }

        @Override
        public Option<T> next() {
            if (this.retrieved) {
                return new None<>();
            }

            this.retrieved = true;
            return new Some<>(this.value);
        }
    }

    private static class Max implements Collector<Integer, Option<Integer>> {
        @Override
        public Option<Integer> createInitial() {
            return new None<>();
        }

        @Override
        public Option<Integer> fold(Option<Integer> current, Integer element) {
            return new Some<>(current.map(inner -> inner > element ? inner : element).orElse(element));
        }
    }

    record CompileError(String message, String context, List_<CompileError> errors) implements Error {
        CompileError(String message, String context) {
            this(message, context, Impl.emptyList());
        }

        @Override
        public String display() {
            return this.format(0);
        }

        private String format(int depth) {
            String joined = this.errors.sort((first, second) -> first.computeMaxDepth() - second.computeMaxDepth())
                    .iter()
                    .map(compileError -> compileError.format(depth + 1))
                    .map(display -> "\n" + "\t".repeat(depth + 1) + display)
                    .collect(new Joiner(""))
                    .orElse("");

            return this.message + ": " + this.context + joined;
        }

        private int computeMaxDepth() {
            return 1 + this.errors.iter()
                    .map(CompileError::computeMaxDepth)
                    .collect(new Max())
                    .orElse(0);
        }
    }

    private record ApplicationError(Error error) implements Error {
        @Override
        public String display() {
            return this.error.display();
        }
    }

    private record OrState(Option<String> maybeValue, List_<CompileError> errors) {
        public OrState() {
            this(new None<>(), Impl.emptyList());
        }

        public OrState withValue(String value) {
            return new OrState(new Some<>(value), this.errors);
        }

        public OrState withError(CompileError error) {
            return new OrState(this.maybeValue, this.errors.add(error));
        }

        public Result<String, List_<CompileError>> toResult() {
            return this.maybeValue.<Result<String, List_<CompileError>>>map(Ok::new)
                    .orElseGet(() -> new Err<>(this.errors));
        }
    }

    private static final List_<String> imports = Impl.emptyList();
    private static final List_<String> structs = Impl.emptyList();
    private static final List_<String> globals = Impl.emptyList();
    private static final List_<String> methods = Impl.emptyList();
    private static int counter = 0;

    public static void main(String[] args) {
        Path_ source = Impl.get(".", "src", "java", "magma", "Main.java");
        Impl.readString(source)
                .mapErr(ApplicationError::new)
                .match(input -> compileAndWrite(input, source), Some::new)
                .ifPresent(error -> System.err.println(error.display()));
    }

    private static Option<ApplicationError> compileAndWrite(String input, Path_ source) {
        Path_ target = source.resolveSibling("main.c");
        return compile(input).mapErr(ApplicationError::new).match(output -> {
            return Impl.writeString(target, output).map(ApplicationError::new);
        }, Some::new);
    }

    private static Result<String, CompileError> compile(String input) {
        return parseAll(divide(input, Main::divideStatementChar), createRootSegmentCompiler())
                .mapValue(Main::assembleChildren)
                .mapValue(compiled -> mergeAll(compiled, Main::mergeStatements));
    }

    private static List_<String> assembleChildren(List_<String> rootChildren) {
        return Impl.<String>emptyList()
                .addAll(imports)
                .addAll(structs)
                .addAll(globals)
                .addAll(methods)
                .addAll(rootChildren);
    }

    private static Function<String, Result<String, CompileError>> createRootSegmentCompiler() {
        return createOrRule(Impl.listOf(
                createTypeRule("whitespace", createWhitespaceRule()),
                createTypeRule("package", wrap(Main::compilePackage)),
                createTypeRule("import", wrap(Main::compileImport)),
                createClassRule(Impl.emptyList())
        ));
    }

    private static Function<String, Result<String, CompileError>> createTypeRule(String type, Function<String, Result<String, CompileError>> childRule) {
        return input -> childRule.apply(input).mapErr(err -> {
            String format = "Cannot assign type to '%s'";
            String message = format.formatted(type);
            return new CompileError(message, input, Impl.listOf(err));
        });
    }

    private static Function<String, Result<String, CompileError>> createWhitespaceRule() {
        return input -> {
            if (input.isBlank()) {
                return new Ok<>("");
            }
            return new Err<>(new CompileError("Not blank", input));
        };
    }

    private static Function<String, Result<String, CompileError>> createOrRule(List_<Function<String, Result<String, CompileError>>> aClass) {
        return input -> aClass.iter()
                .fold(new OrState(), (state, rule) -> rule.apply(input).match(state::withValue, state::withError))
                .toResult()
                .mapErr(children -> new CompileError("No valid combination", input, children));
    }

    private static Option<String> compileImport(String input) {
        String stripped = input.strip();
        if (stripped.startsWith("import ")) {
            String right = stripped.substring("import ".length());
            if (right.endsWith(";")) {
                String content = right.substring(0, right.length() - ";".length());
                List_<String> split = splitByDelimiter(content, '.');
                if (split.size() >= 3 && Impl.equalsList(split.slice(0, 3), Impl.listOf("java", "util", "function"), String::equals)) {
                    return new Some<>("");
                }

                String joined = split.iter().collect(new Joiner("/")).orElse("");
                imports.add("#include \"./" + joined + "\"\n");
                return new Some<>("");
            }
        }
        return new None<>();
    }

    private static Option<String> compilePackage(String input) {
        if (input.startsWith("package ")) {
            return new Some<>("");
        }
        return new None<>();
    }

    private static Result<String, CompileError> compileStatements(String input, Function<String, Result<String, CompileError>> compiler) {
        return compileAndMerge(divide(input, Main::divideStatementChar), compiler, Main::mergeStatements);
    }

    private static Result<String, CompileError> compileAndMerge(List_<String> segments, Function<String, Result<String, CompileError>> compiler, BiFunction<StringBuilder, String, StringBuilder> merger) {
        return parseAll(segments, compiler).mapValue(compiled -> mergeAll(compiled, merger));
    }

    private static String mergeAll(List_<String> compiled, BiFunction<StringBuilder, String, StringBuilder> merger) {
        return compiled.iter().fold(new StringBuilder(), merger).toString();
    }

    private static Result<List_<String>, CompileError> parseAll(List_<String> segments, Function<String, Result<String, CompileError>> compiler) {
        return segments.iter()
                .<Result<List_<String>, CompileError>>fold(new Ok<>(Impl.emptyList()),
                        (maybeCompiled, segment) -> maybeCompiled.flatMapValue(allCompiled -> compiler.apply(segment).mapValue(allCompiled::add)));
    }

    private static Function<String, Result<String, CompileError>> wrap(Function<String, Option<String>> compiler) {
        return input -> compiler.apply(input)
                .<Result<String, CompileError>>map(Ok::new)
                .orElseGet(() -> new Err<>(new CompileError("Invalid value", input)));
    }

    private static StringBuilder mergeStatements(StringBuilder output, String compiled) {
        return output.append(compiled);
    }

    private static List_<String> divide(String input, BiFunction<State, Character, State> divider) {
        List_<Character> queue = Iterators.fromString(input).collect(new ListCollector<>());

        State state = new State(queue);
        while (state.hasElements()) {
            char c = state.pop();

            if (c == '\'') {
                state.append(c);
                char maybeSlash = state.pop();
                state.append(maybeSlash);

                if (maybeSlash == '\\') {
                    state.append(state.pop());
                }
                state.append(state.pop());
                continue;
            }

            if (c == '\"') {
                state.append(c);

                while (state.hasElements()) {
                    char next = state.pop();
                    state.append(next);

                    if (next == '\\') {
                        state.append(state.pop());
                    }
                    if (next == '"') {
                        break;
                    }
                }

                continue;
            }

            state = divider.apply(state, c);
        }

        return state.advance().segments();
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

    private static List_<String> splitByDelimiter(String content, char delimiter) {
        List_<String> segments = Impl.emptyList();
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            if (c == delimiter) {
                segments = segments.add(buffer.toString());
                buffer = new StringBuilder();
            }
            else {
                buffer.append(c);
            }
        }

        return segments.add(buffer.toString());
    }

    private static Function<String, Result<String, CompileError>> createCompileToStructRule(String type, String infix, List_<String> typeParams) {
        return createTypeRule(type, input -> {
            int classIndex = input.indexOf(infix);
            if (classIndex < 0) {
                return createInfixErr(input, infix);
            }

            String afterKeyword = input.substring(classIndex + infix.length());
            int contentStart = afterKeyword.indexOf("{");
            if (contentStart < 0) {
                return createInfixErr(afterKeyword, "{");
            }

            String beforeContent = afterKeyword.substring(0, contentStart).strip();

            int implementsIndex = beforeContent.indexOf(" implements ");
            String withoutImplements = implementsIndex >= 0
                    ? beforeContent.substring(0, implementsIndex)
                    : beforeContent;

            int extendsIndex = withoutImplements.indexOf(" extends ");
            String withoutExtends = extendsIndex >= 0
                    ? withoutImplements.substring(0, extendsIndex)
                    : withoutImplements;

            int paramStart = withoutExtends.indexOf("(");
            String withoutParams = paramStart >= 0
                    ? withoutExtends.substring(0, paramStart).strip()
                    : withoutExtends;

            int typeParamsStart = withoutParams.indexOf("<");
            if (typeParamsStart >= 0) {
                return new Ok<>("");
            }

            if (!isSymbol(withoutParams)) {
                return new Err<>(new CompileError("Not a symbol", withoutParams));
            }

            String withEnd = afterKeyword.substring(contentStart + "{".length()).strip();
            if (!withEnd.endsWith("}")) {
                return new Err<>(new CompileError("Suffix '}' not present", withEnd));
            }

            String inputContent = withEnd.substring(0, withEnd.length() - "}".length());
            return compileStatements(inputContent, input1 -> createClassMemberRule(typeParams).apply(input1)).mapValue(outputContent -> {
                structs.add("struct " + withoutParams + " {" + outputContent + "\n};\n");
                return "";
            });
        });
    }

    private static Err<String, CompileError> createInfixErr(String input, String infix) {
        return new Err<>(new CompileError("Infix '" + infix + "' not present", input));
    }

    private static Function<String, Result<String, CompileError>> createClassMemberRule(List_<String> typeParams) {
        return createOrRule(Impl.listOf(
                createWhitespaceRule(),
                createCompileToStructRule("interface", "interface ", typeParams),
                createCompileToStructRule("record", "record ", typeParams),
                createClassRule(typeParams),
                wrap(input -> compileGlobalInitialization(input, typeParams)),
                wrap(Main::compileDefinitionStatement),
                createMethodRule(typeParams)
        ));
    }

    private static Function<String, Result<String, CompileError>> createMethodRule(List_<String> typeParams) {
        return input -> {
            int paramStart = input.indexOf("(");
            if (paramStart < 0) {
                return createInfixErr(input, "(");
            }

            String inputDefinition = input.substring(0, paramStart).strip();
            String withParams = input.substring(paramStart + "(".length());

            return createDefinitionRule().apply(inputDefinition).flatMapValue(outputDefinition -> {
                int paramEnd = withParams.indexOf(")");
                if (paramEnd < 0) {
                    return createInfixErr(withParams, ")");
                }

                String params = withParams.substring(0, paramEnd);
                return compileValues(params, createParameterRule()).flatMapValue(outputParams -> {
                    String substring = withParams.substring(paramEnd + ")".length());
                    return assembleMethodBody(typeParams, outputDefinition, outputParams, substring.strip());
                });
            });
        };
    }

    private static Function<String, Result<String, CompileError>> createClassRule(List_<String> typeParams) {
        return createCompileToStructRule("class", "class ", typeParams);
    }

    private static Option<String> compileDefinitionStatement(String input) {
        String stripped = input.strip();
        if (stripped.endsWith(";")) {
            String content = stripped.substring(0, stripped.length() - ";".length());
            return createDefinitionRule().apply(content).findValue().map(result -> "\n\t" + result + ";");
        }
        return new None<>();
    }

    private static Option<String> compileGlobalInitialization(String input, List_<String> typeParams) {
        return compileInitialization(input, typeParams, 0).map(generated -> {
            globals.add(generated + ";\n");
            return "";
        });
    }

    private static Option<String> compileInitialization(String input, List_<String> typeParams, int depth) {
        if (!input.endsWith(";")) {
            return new None<>();
        }

        String withoutEnd = input.substring(0, input.length() - ";".length());
        int valueSeparator = withoutEnd.indexOf("=");
        if (valueSeparator < 0) {
            return new None<>();
        }

        String definition = withoutEnd.substring(0, valueSeparator).strip();
        String value = withoutEnd.substring(valueSeparator + "=".length()).strip();
        return createDefinitionRule().apply(definition).findValue().flatMap(outputDefinition -> {
            return compileValue(value, typeParams, depth).map(outputValue -> {
                return outputDefinition + " = " + outputValue;
            });
        });
    }

    private static Option<String> compileWhitespace(String input) {
        if (input.isBlank()) {
            return new Some<>("");
        }
        return new None<>();
    }

    private static Result<String, CompileError> assembleMethodBody(
            List_<String> typeParams,
            String definition,
            String params,
            String body
    ) {
        String header = "\t".repeat(0) + definition + "(" + params + ")";
        if (body.startsWith("{") && body.endsWith("}")) {
            String inputContent = body.substring("{".length(), body.length() - "}".length());
            return compileStatements(inputContent, wrap(input1 -> compileStatementOrBlock(input1, typeParams, 1))).flatMapValue(outputContent -> {
                methods.add(header + " {" + outputContent + "\n}\n");
                return new Ok<>("");
            });
        }

        return new Ok<>("\n\t" + header + ";");
    }

    private static Function<String, Result<String, CompileError>> createParameterRule() {
        return createOrRule(Impl.listOf(
                wrap(Main::compileWhitespace),
                createDefinitionRule()
        ));
    }

    private static Result<String, CompileError> compileValues(String input, Function<String, Result<String, CompileError>> compiler) {
        return compileValues(divide(input, Main::divideValueChar), compiler);
    }

    private static State divideValueChar(State state, char c) {
        if (c == '-') {
            if (state.peek() == '>') {
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

    private static Result<String, CompileError> compileValues(List_<String> params, Function<String, Result<String, CompileError>> compiler) {
        return compileAndMerge(params, compiler, Main::mergeValues);
    }

    private static Option<String> compileStatementOrBlock(String input, List_<String> typeParams, int depth) {
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

    private static Option<String> compilePostOperator(String input, List_<String> typeParams, int depth, String operator) {
        String stripped = input.strip();
        if (stripped.endsWith(operator + ";")) {
            String slice = stripped.substring(0, stripped.length() - (operator + ";").length());
            return compileValue(slice, typeParams, depth).map(value -> value + operator + ";");
        }
        else {
            return new None<>();
        }
    }

    private static Option<String> compileElse(String input, List_<String> typeParams, int depth) {
        String stripped = input.strip();
        if (stripped.startsWith("else ")) {
            String withoutKeyword = stripped.substring("else ".length()).strip();
            if (withoutKeyword.startsWith("{") && withoutKeyword.endsWith("}")) {
                String indent = createIndent(depth);
                return compileStatements(withoutKeyword.substring(1, withoutKeyword.length() - 1), wrap(statement -> compileStatementOrBlock(statement, typeParams, depth + 1))).findValue()
                        .map(result -> indent + "else {" + result + indent + "}");
            }
            else {
                return compileStatementOrBlock(withoutKeyword, typeParams, depth).map(result -> "else " + result);
            }
        }

        return new None<>();
    }

    private static Option<String> compileKeywordStatement(String input, int depth, String keyword) {
        if (input.strip().equals(keyword + ";")) {
            return new Some<>(formatStatement(depth, keyword));
        }
        else {
            return new None<>();
        }
    }

    private static String formatStatement(int depth, String value) {
        return createIndent(depth) + value + ";";
    }

    private static String createIndent(int depth) {
        return "\n" + "\t".repeat(depth);
    }

    private static Option<String> compileConditional(String input, List_<String> typeParams, String prefix, int depth) {
        String stripped = input.strip();
        if (!stripped.startsWith(prefix)) {
            return new None<>();
        }

        String afterKeyword = stripped.substring(prefix.length()).strip();
        if (!afterKeyword.startsWith("(")) {
            return new None<>();
        }

        String withoutConditionStart = afterKeyword.substring(1);
        int conditionEnd = findConditionEnd(withoutConditionStart);

        if (conditionEnd < 0) {
            return new None<>();
        }
        String oldCondition = withoutConditionStart.substring(0, conditionEnd).strip();
        String withBraces = withoutConditionStart.substring(conditionEnd + ")".length()).strip();

        return compileValue(oldCondition, typeParams, depth).flatMap(newCondition -> {
            String withCondition = createIndent(depth) + prefix + "(" + newCondition + ")";

            if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
                String content = withBraces.substring(1, withBraces.length() - 1);
                return compileStatements(content, wrap(statement -> compileStatementOrBlock(statement, typeParams, depth + 1))).findValue().map(statements -> {
                    return withCondition +
                            " {" + statements + "\n" +
                            "\t".repeat(depth) +
                            "}";
                });
            }
            else {
                return compileStatementOrBlock(withBraces, typeParams, depth).map(result -> {
                    return withCondition + " " + result;
                });
            }
        });

    }

    private static int findConditionEnd(String input) {
        int conditionEnd = -1;
        int depth0 = 0;

        List_<Tuple<Integer, Character>> queue = Iterators.fromStringWithIndices(input).collect(new ListCollector<>());

        while (!queue.isEmpty()) {
            Tuple<Integer, Character> pair = queue.pop();
            Integer i = pair.left;
            Character c = pair.right;

            if (c == '\'') {
                if (queue.pop().right == '\\') {
                    queue.pop();
                }

                queue.pop();
                continue;
            }

            if (c == '"') {
                while (!queue.isEmpty()) {
                    Tuple<Integer, Character> next = queue.pop();

                    if (next.right == '\\') {
                        queue.pop();
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

    private static Option<String> compileInvocationStatement(String input, List_<String> typeParams, int depth) {
        String stripped = input.strip();
        if (stripped.endsWith(";")) {
            String withoutEnd = stripped.substring(0, stripped.length() - ";".length());
            Option<String> maybeInvocation = compileInvocation(withoutEnd, typeParams, depth);
            if (maybeInvocation.isPresent()) {
                return maybeInvocation;
            }
        }
        return new None<>();
    }

    private static Option<String> compileAssignment(String input, List_<String> typeParams, int depth) {
        String stripped = input.strip();
        if (stripped.endsWith(";")) {
            String withoutEnd = stripped.substring(0, stripped.length() - ";".length());
            int valueSeparator = withoutEnd.indexOf("=");
            if (valueSeparator >= 0) {
                String destination = withoutEnd.substring(0, valueSeparator).strip();
                String source = withoutEnd.substring(valueSeparator + "=".length()).strip();
                return compileValue(destination, typeParams, depth).flatMap(newDest -> {
                    return compileValue(source, typeParams, depth).map(newSource -> {
                        return newDest + " = " + newSource;
                    });
                });
            }
        }
        return new None<>();
    }

    private static Option<String> compileReturn(String input, List_<String> typeParams, int depth) {
        String stripped = input.strip();
        if (stripped.endsWith(";")) {
            String withoutEnd = stripped.substring(0, stripped.length() - ";".length());
            if (withoutEnd.startsWith("return ")) {
                return compileValue(withoutEnd.substring("return ".length()), typeParams, depth).map(result -> "return " + result);
            }
        }

        return new None<>();
    }

    private static Option<String> compileValue(String input, List_<String> typeParams, int depth) {
        String stripped = input.strip();
        if (stripped.startsWith("\"") && stripped.endsWith("\"")) {
            return new Some<>(stripped);
        }
        if (stripped.startsWith("'") && stripped.endsWith("'")) {
            return new Some<>(stripped);
        }

        if (isSymbol(stripped) || isNumber(stripped)) {
            return new Some<>(stripped);
        }

        if (stripped.startsWith("new ")) {
            String slice = stripped.substring("new ".length());
            int argsStart = slice.indexOf("(");
            if (argsStart >= 0) {
                String type = slice.substring(0, argsStart);
                String withEnd = slice.substring(argsStart + "(".length()).strip();
                if (withEnd.endsWith(")")) {
                    String argsString = withEnd.substring(0, withEnd.length() - ")".length());
                    return createTypeRule(typeParams).apply(type).findValue().flatMap(outputType -> compileArgs(argsString, typeParams, depth).map(value -> outputType + value));
                }
            }
        }

        if (stripped.startsWith("!")) {
            return compileValue(stripped.substring(1), typeParams, depth).map(result -> "!" + result);
        }

        Option<String> value = compileLambda(stripped, typeParams, depth);
        if (value.isPresent()) {
            return value;
        }

        Option<String> invocation = compileInvocation(input, typeParams, depth);
        if (invocation.isPresent()) {
            return invocation;
        }

        int methodIndex = stripped.lastIndexOf("::");
        if (methodIndex >= 0) {
            String type = stripped.substring(0, methodIndex).strip();
            String property = stripped.substring(methodIndex + "::".length()).strip();

            if (isSymbol(property)) {
                return createTypeRule(typeParams).apply(type).findValue().flatMap(compiled -> {
                    return generateLambdaWithReturn(Impl.emptyList(), "\n\treturn " + compiled + "." + property + "()");
                });
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

    private static Option<String> compileOperator(String input, List_<String> typeParams, int depth, String operator) {
        int operatorIndex = input.indexOf(operator);
        if (operatorIndex < 0) {
            return new None<>();
        }

        String left = input.substring(0, operatorIndex);
        String right = input.substring(operatorIndex + operator.length());

        return compileValue(left, typeParams, depth).flatMap(leftResult -> {
            return compileValue(right, typeParams, depth).map(rightResult -> {
                return leftResult + " " + operator + " " + rightResult;
            });
        });
    }

    private static Option<String> compileLambda(String input, List_<String> typeParams, int depth) {
        int arrowIndex = input.indexOf("->");
        if (arrowIndex < 0) {
            return new None<>();
        }

        String beforeArrow = input.substring(0, arrowIndex).strip();
        List_<String> paramNames;
        if (isSymbol(beforeArrow)) {
            paramNames = Impl.listOf(beforeArrow);
        }
        else if (beforeArrow.startsWith("(") && beforeArrow.endsWith(")")) {
            String inner = beforeArrow.substring(1, beforeArrow.length() - 1);
            paramNames = splitByDelimiter(inner, ',')
                    .iter()
                    .map(String::strip)
                    .filter(value -> !value.isEmpty())
                    .collect(new ListCollector<>());
        }
        else {
            return new None<>();
        }

        String value = input.substring(arrowIndex + "->".length()).strip();
        if (value.startsWith("{") && value.endsWith("}")) {
            String slice = value.substring(1, value.length() - 1);
            return compileStatements(slice, wrap(statement -> compileStatementOrBlock(statement, typeParams, depth))).findValue().flatMap(result -> {
                return generateLambdaWithReturn(paramNames, result);
            });
        }

        return compileValue(value, typeParams, depth).flatMap(newValue -> {
            return generateLambdaWithReturn(paramNames, "\n\treturn " + newValue + ";");
        });
    }

    private static Option<String> generateLambdaWithReturn(List_<String> paramNames, String returnValue) {
        int current = counter;
        counter++;
        String lambdaName = "__lambda" + current + "__";

        String joinedLambdaParams = paramNames.iter()
                .map(name -> "auto " + name)
                .collect(new Joiner(", "))
                .orElse("");

        methods.add("auto " + lambdaName + "(" + joinedLambdaParams + ")" + " {" + returnValue + "\n}\n");
        return new Some<>(lambdaName);
    }

    private static boolean isNumber(String input) {
        return Iterators.fromStringWithIndices(input).allMatch(tuple -> {
            int index = tuple.left;
            char c = tuple.right;
            return (index == 0 && c == '-') || Character.isDigit(c);
        });
    }

    private static Option<String> compileInvocation(String input, List_<String> typeParams, int depth) {
        String stripped = input.strip();
        if (stripped.endsWith(")")) {
            String sliced = stripped.substring(0, stripped.length() - ")".length());

            int argsStart = findInvocationStart(sliced);

            if (argsStart >= 0) {
                String type = sliced.substring(0, argsStart);
                String withEnd = sliced.substring(argsStart + "(".length()).strip();
                return compileValue(type, typeParams, depth).flatMap(caller -> {
                    return compileArgs(withEnd, typeParams, depth).map(value -> caller + value);
                });
            }
        }
        return new None<>();
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

    private static Option<String> compileArgs(String argsString, List_<String> typeParams, int depth) {
        return compileValues(argsString, wrap(arg -> {
            return compileWhitespace(arg).or(() -> compileValue(arg, typeParams, depth));
        })).findValue().map(args -> {
            return "(" + args + ")";
        });
    }

    private static StringBuilder mergeValues(StringBuilder cache, String element) {
        if (cache.isEmpty()) {
            return cache.append(element);
        }
        return cache.append(", ").append(element);
    }

    private static Function<String, Result<String, CompileError>> createDefinitionRule() {
        return createTypeRule("definition", input -> {
            String stripped = input.strip();
            int nameSeparator = stripped.lastIndexOf(" ");
            if (nameSeparator < 0) {
                if (isSymbol(stripped)) {
                    return new Ok<>(stripped + " new");
                }

                return createInfixErr(stripped, " ");
            }

            String beforeName = stripped.substring(0, nameSeparator).strip();
            String name = stripped.substring(nameSeparator + " ".length()).strip();
            if (!isSymbol(name)) {
                return new Err<>(new CompileError("Not a symbol", name));
            }

            int typeSeparator = findTypeSeparator(beforeName);
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
                        typeParams = Impl.emptyList();
                    }
                }
                else {
                    typeParams = Impl.emptyList();
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

                boolean allSymbols = splitByDelimiter(modifiersString, ' ')
                        .iter()
                        .map(String::strip)
                        .filter(value -> !value.isEmpty())
                        .allMatch(Main::isSymbol);

                if (!allSymbols) {
                    return new Err<>(new CompileError("Not all modifiers are strings", modifiersString));
                }

                String inputType = beforeName.substring(typeSeparator + " ".length());
                return createTypeRule(typeParams)
                        .apply(inputType)
                        .flatMapValue(outputType -> new Ok<String, CompileError>(generateDefinition(typeParams, outputType, name)));
            }
            else {
                return createTypeRule(Impl.emptyList())
                        .apply(beforeName)
                        .flatMapValue(outputType -> new Ok<String, CompileError>(generateDefinition(Impl.emptyList(), outputType, name)));
            }
        });
    }

    private static int findTypeSeparator(String beforeName) {
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
        return typeSeparator;
    }

    private static List_<String> splitValues(String substring) {
        return splitByDelimiter(substring.strip(), ',')
                .iter()
                .map(String::strip)
                .filter(param -> !param.isEmpty())
                .collect(new ListCollector<>());
    }

    private static String generateDefinition(List_<String> maybeTypeParams, String type, String name) {
        String typeParamsString = maybeTypeParams.iter()
                .collect(new Joiner(", "))
                .map(inner -> "<" + inner + "> ")
                .orElse("");

        return typeParamsString + type + " " + name;
    }

    private static Function<String, Result<String, CompileError>> createTypeRule(List_<String> typeParams) {
        return createOrRule(Impl.listOf(
                createPrimitiveRule(),
                createArrayRule(typeParams),
                createSymbolRule(typeParams),
                createGenericRule(typeParams)
        ));
    }

    private static Function<String, Result<String, CompileError>> createGenericRule(List_<String> typeParams) {
        return wrap(input -> compileGeneric(input, typeParams));
    }

    private static Function<String, Result<String, CompileError>> createSymbolRule(List_<String> typeParams) {
        return wrap(input -> compileSymbol(input, typeParams));
    }

    private static Function<String, Result<String, CompileError>> createArrayRule(List_<String> typeParams) {
        return wrap(input -> compileArray(input, typeParams));
    }

    private static Function<String, Result<String, CompileError>> createPrimitiveRule() {
        return wrap(input -> {
            if (input.equals("void")) {
                return new Some<>("void");
            }

            if (input.equals("int") || input.equals("Integer") || input.equals("boolean") || input.equals("Boolean")) {
                return new Some<>("int");
            }

            if (input.equals("char") || input.equals("Character")) {
                return new Some<>("char");
            }

            return new None<>();
        });
    }

    private static Option<String> compileArray(String input, List_<String> typeParams) {
        if (input.endsWith("[]")) {
            return createTypeRule(typeParams).apply(input.substring(0, input.length() - "[]".length())).findValue().map(value -> value + "*");
        }
        return new None<>();
    }

    private static Option<String> compileSymbol(String input, List_<String> typeParams) {
        if (isSymbol(input.strip())) {
            return Impl.contains(typeParams, input.strip(), String::equals) ? new Some<>(input.strip()) : new Some<>("struct " + input.strip());
        }
        return new None<>();
    }

    private static Option<String> compileGeneric(String stripped, List_<String> typeParams) {
        if (stripped.endsWith(">")) {
            String slice = stripped.substring(0, stripped.length() - ">".length());
            int argsStart = slice.indexOf("<");
            if (argsStart >= 0) {
                String base = slice.substring(0, argsStart).strip();
                String params = slice.substring(argsStart + "<".length()).strip();
                return compileValues(params, wrap(type -> {
                    return compileWhitespace(type).or(() -> createTypeRule(typeParams).apply(type).findValue());
                })).findValue().map(compiled -> {
                    return base + "_" + compiled;
                });
            }
        }

        return new None<>();
    }

    private static boolean isSymbol(String input) {
        if (input.isBlank()) {
            return false;
        }

        if (input.equals("record")) {
            return false;
        }

        return Iterators.fromStringWithIndices(input).allMatch(tuple -> {
            int index = tuple.left;
            char c = tuple.right;
            return c == '_' || Character.isLetter(c) || (index != 0 && Character.isDigit(c));
        });
    }

    private static Option<String> generatePlaceholder(String input) {
        return new Some<>("/* " + input + " */");
    }
}
