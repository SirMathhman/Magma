package magma;

import magma.java.Strings;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static magma.java.Console.printlnErr;

public class Main {
    public interface Result<T, X> {
        <R> R match(Function<T, R> whenOk, Function<X, R> whenErr);

        <R> Result<T, R> mapErr(Function<X, R> mapper);

        <R> Result<R, X> flatMapValue(Function<T, Result<R, X>> mapper);

        <R> Result<R, X> mapValue(Function<T, R> mapper);

        Option<T> findValue();
    }

    public interface Option<T> {
        <R> Option<R> map(Function<T, R> mapper);

        boolean isPresent();

        T orElse(T other);

        boolean isEmpty();

        void ifPresent(Consumer<T> consumer);

        Option<T> or(Supplier<Option<T>> other);

        <R> Option<R> flatMap(Function<T, Option<R>> mapper);

        Tuple<Boolean, T> toTuple(T other);

        T orElseGet(Supplier<T> supplier);

        <R> R match(Function<T, R> whenPresent, Supplier<R> whenEmpty);

        <R> Option<Tuple<T, R>> and(Supplier<Option<R>> other);
    }

    public interface Error {
        String_ display();
    }

    public interface IOError extends Error {
    }

    public interface List_<T> {
        List_<T> add(T element);

        List_<T> addAll(List_<T> others);

        Iterator<T> iter();

        boolean isEmpty();

        int size();

        List_<T> slice(int startInclusive, int endExclusive);

        Option<Tuple<T, List_<T>>> popFirst();

        Option<T> peekFirst();

        T get(int index);

        List_<T> sort(BiFunction<T, T, Integer> comparator);

        T last();

        List_<T> set(int index, T element);
    }

    public interface Path_ {
        Path_ resolveSibling(String sibling);

        List_<String> asList();
    }

    public interface Iterator<T> {
        <R> R foldWithInitial(R initial, BiFunction<R, T, R> folder);

        void forEach(Consumer<T> consumer);

        <R> Iterator<R> map(Function<T, R> mapper);

        Iterator<T> filter(Predicate<T> predicate);

        Option<T> next();

        Iterator<T> concat(Iterator<T> other);

        <C> C collect(Collector<T, C> collector);

        boolean allMatch(Predicate<T> predicate);

        <R> Option<R> foldWithMapper(Function<T, R> mapper, BiFunction<R, T, R> folder);

        <R, X> Result<R, X> foldToResult(R initial, BiFunction<R, T, Result<R, X>> mapper);

        boolean anyMatch(Predicate<T> filter);
    }

    public interface Collector<T, C> {
        C createInitial();

        C fold(C current, T element);
    }

    private interface Head<T> {
        Option<T> next();
    }

    public interface Divider {
        State fold(State state, char c);
    }

    public interface Map_<K, V> {
        Map_<K, V> with(K propertyKey, V propertyValue);

        Option<V> find(K propertyKey);

        Map_<K, V> withAll(Map_<K, V> other);

        Main.Iterator<Main.Tuple<K, V>> iter();

        Iterator<K> keys();
    }

    interface Rule extends Function<String, Result<Node, CompileError>> {
    }

    public record String_(char[] array) {
    }

    public record ApplicationError(Error error) implements Error {
        @Override
        public String_ display() {
            return this.error.display();
        }
    }

    public record HeadedIterator<T>(Head<T> head) implements Iterator<T> {
        @Override
        public <R> R foldWithInitial(R initial, BiFunction<R, T, R> folder) {
            R current = initial;
            while (true) {
                R finalCurrent = current;
                Option<R> option = this.head.next().map(next -> folder.apply(finalCurrent, next));

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
                Option<T> next = this.head.next();
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
        public Option<T> next() {
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
        public <R> Option<R> foldWithMapper(Function<T, R> mapper, BiFunction<R, T, R> folder) {
            return this.head.next().map(mapper).map(next -> this.foldWithInitial(next, folder));
        }

        @Override
        public <R, X> Result<R, X> foldToResult(R initial, BiFunction<R, T, Result<R, X>> mapper) {
            return this.<Result<R, X>>foldWithInitial(new Ok<>(initial),
                    (result, t) -> result.flatMapValue(
                            current -> mapper.apply(current, t)));
        }

        @Override
        public boolean anyMatch(Predicate<T> filter) {
            return this.foldWithInitial(false, (aBoolean, t) -> aBoolean || filter.test(t));
        }

        private <R> Iterator<R> flatMap(Function<T, Iterator<R>> mapper) {
            return this.map(mapper).foldWithInitial(Iterators.empty(), Iterator::concat);
        }
    }

    public static final class None<T> implements Option<T> {
        @Override
        public <R> Option<R> map(Function<T, R> mapper) {
            return new None<>();
        }

        @Override
        public boolean isPresent() {
            return false;
        }

        @Override
        public T orElse(T other) {
            return other;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public void ifPresent(Consumer<T> consumer) {
        }

        @Override
        public Option<T> or(Supplier<Option<T>> other) {
            return other.get();
        }

        @Override
        public <R> Option<R> flatMap(Function<T, Option<R>> mapper) {
            return new None<>();
        }

        @Override
        public Tuple<Boolean, T> toTuple(T other) {
            return new Tuple<>(false, other);
        }

        @Override
        public T orElseGet(Supplier<T> supplier) {
            return supplier.get();
        }

        @Override
        public <R> R match(Function<T, R> whenPresent, Supplier<R> whenEmpty) {
            return whenEmpty.get();
        }

        @Override
        public <R> Option<Tuple<T, R>> and(Supplier<Option<R>> other) {
            return new None<>();
        }
    }

    private static class EmptyHead<T> implements Head<T> {
        @Override
        public Option<T> next() {
            return new None<>();
        }
    }

    private static class SingleHead<T> implements Head<T> {
        private final T value;
        private boolean retrieved = false;

        private SingleHead(T value) {
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

    public record Some<T>(T value) implements Option<T> {
        @Override
        public <R> Option<R> map(Function<T, R> mapper) {
            return new Some<>(mapper.apply(this.value));
        }

        @Override
        public boolean isPresent() {
            return true;
        }

        @Override
        public T orElse(T other) {
            return this.value;
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
        public Option<T> or(Supplier<Option<T>> other) {
            return this;
        }

        @Override
        public <R> Option<R> flatMap(Function<T, Option<R>> mapper) {
            return mapper.apply(this.value);
        }

        @Override
        public Tuple<Boolean, T> toTuple(T other) {
            return new Tuple<>(true, this.value);
        }

        @Override
        public T orElseGet(Supplier<T> supplier) {
            return this.value;
        }

        @Override
        public <R> R match(Function<T, R> whenPresent, Supplier<R> whenEmpty) {
            return whenPresent.apply(this.value);
        }

        @Override
        public <R> Option<Tuple<T, R>> and(Supplier<Option<R>> other) {
            return other.get().map(otherValue -> new Tuple<>(this.value, otherValue));
        }
    }

    public record Err<T, X>(X error) implements Result<T, X> {
        @Override
        public <R> R match(Function<T, R> whenOk, Function<X, R> whenErr) {
            return whenErr.apply(this.error);
        }

        @Override
        public <R> Result<T, R> mapErr(Function<X, R> mapper) {
            return new Err<>(mapper.apply(this.error));
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
    }

    public record Ok<T, X>(T value) implements Result<T, X> {
        @Override
        public <R> R match(Function<T, R> whenOk, Function<X, R> whenErr) {
            return whenOk.apply(this.value);
        }

        @Override
        public <R> Result<T, R> mapErr(Function<X, R> mapper) {
            return new Ok<>(this.value);
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

    }

    public static class State {
        private final List_<Character> queue;
        private final List_<String> segments;
        private final StringBuilder buffer;
        private final int depth;

        private State(List_<Character> queue, List_<String> segments, StringBuilder buffer, int depth) {
            this.queue = queue;
            this.segments = segments;
            this.buffer = buffer;
            this.depth = depth;
        }

        public State(List_<Character> queue) {
            this(queue, Lists.empty(), new StringBuilder(), 0);
        }

        private Option<State> popAndAppend() {
            return this.pop().map(tuple -> tuple.right.append(tuple.left));
        }

        private State advance() {
            return new State(this.queue, this.segments.add(this.buffer.toString()), new StringBuilder(), this.depth);
        }

        private State append(char c) {
            return new State(this.queue, this.segments, this.buffer.append(c), this.depth);
        }

        private boolean isLevel() {
            return this.depth == 0;
        }

        private Option<Tuple<Character, State>> pop() {
            return this.queue.popFirst().map(tuple -> new Tuple<>(tuple.left, new State(tuple.right, this.segments, this.buffer, this.depth)));
        }

        private boolean hasElements() {
            return !this.queue.isEmpty();
        }

        private State exit() {
            return new State(this.queue, this.segments, this.buffer, this.depth - 1);
        }

        private State enter() {
            return new State(this.queue, this.segments, this.buffer, this.depth + 1);
        }

        public List_<String> segments() {
            return this.segments;
        }

        public Option<Character> peek() {
            return this.queue.peekFirst();
        }
    }

    public record Tuple<A, B>(A left, B right) {
    }

    public static class Iterators {
        public static <T> Iterator<T> empty() {
            return new HeadedIterator<>(new EmptyHead<>());
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
        public Option<Integer> next() {
            if (this.counter < this.length) {
                int next = this.counter;
                this.counter++;
                return new Some<>(next);
            }

            return new None<>();
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

    public record DecoratedDivider(Divider divider) implements Divider {
        private static Option<State> divideSingleQuotes(State state, char c) {
            if (c != '\'') {
                return new None<>();
            }

            State appended = state.append(c);
            Option<Tuple<Character, State>> maybeSlashTuple = appended.pop();
            if (maybeSlashTuple.isEmpty()) {
                return new None<>();
            }

            Tuple<Character, State> slashTuple = maybeSlashTuple.orElse(new Tuple<>('\0', appended));
            var withMaybeSlash = slashTuple.right.append(slashTuple.left);

            Option<State> withSlash = slashTuple.left == '\\' ? withMaybeSlash.popAndAppend() : new Some<>(withMaybeSlash);
            return withSlash.flatMap(State::popAndAppend);
        }

        private static Option<State> divideDoubleQuotes(State state, char c) {
            if (c != '\"') {
                return new None<>();
            }

            State current = state.append(c);
            while (true) {
                Tuple<Boolean, State> maybeNextTuple = current.pop()
                        .flatMap(DecoratedDivider::foldWithinDoubleQuotes)
                        .toTuple(current);

                if (maybeNextTuple.left) {
                    current = maybeNextTuple.right;
                }
                else {
                    return new Some<>(current);
                }
            }
        }

        private static Option<State> foldWithinDoubleQuotes(Tuple<Character, State> tuple) {
            char next = tuple.left;
            State state = tuple.right;

            State appended = state.append(next);
            if (next == '\\') {
                return appended.popAndAppend();
            }
            if (next == '"') {
                return new None<>();
            }
            return new Some<>(appended);
        }

        @Override
        public State fold(State state, char c) {
            return divideSingleQuotes(state, c)
                    .or(() -> divideDoubleQuotes(state, c))
                    .orElseGet(() -> this.divider().fold(state, c));
        }
    }

    private record DelimitedDivider(char delimiter) implements Divider {
        @Override
        public State fold(State state, char c) {
            if (c == this.delimiter) {
                return state.advance();
            }
            return state.append(c);
        }
    }

    public static class Options {
        public static <V> boolean equalsTo(Option<V> first, Option<V> second, BiFunction<V, V, Boolean> equator) {
            if (first.isEmpty() && second.isEmpty()) {
                return true;
            }

            return first.and(() -> second)
                    .map(tuple -> equator.apply(tuple.left, tuple.right))
                    .orElse(false);
        }
    }

    public record CompileError(String message, String context, List_<CompileError> errors) implements Error {
        public CompileError(String message, String context) {
            this(message, context, Lists.empty());
        }

        private String display0() {
            return this.format(0);
        }

        private String format(int depth) {
            List_<CompileError> sorted = this.errors.sort((first, second) -> {
                return first.maxDepth() - second.maxDepth();
            });

            String joiner = sorted.iter()
                    .map(compileError -> compileError.format(depth + 1))
                    .map(value -> "\n" + "\t".repeat(depth) + value)
                    .collect(new Joiner(""))
                    .orElse("");

            return this.message + ": " + this.context + joiner;
        }

        private int maxDepth() {
            return 1 + this.errors.iter()
                    .map(CompileError::maxDepth)
                    .collect(new Max())
                    .orElse(0);
        }

        @Override
        public String_ display() {
            return Strings.fromNativeString(this.display0());
        }
    }

    private record OrState<T>(Option<T> maybeValue, List_<CompileError> errors) {
        public OrState() {
            this(new None<>(), Lists.empty());
        }

        public OrState<T> withValue(T value) {
            if (this.maybeValue.isPresent()) {
                return this;
            }
            return new OrState<T>(new Some<>(value), this.errors);
        }

        public OrState<T> withError(CompileError error) {
            return new OrState<T>(this.maybeValue, this.errors.add(error));
        }

        public Result<T, List_<CompileError>> toResult() {
            return this.maybeValue.<Result<T, List_<CompileError>>>match(Ok::new, () -> new Err<T, List_<CompileError>>(this.errors));
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

    static class ParseState {
        private List_<List_<String>> frames;

        public ParseState(List_<List_<String>> frames) {
            this.frames = frames;
        }

        public ParseState() {
            this(Lists.<List_<String>>empty().add(Lists.empty()));
        }

        public ParseState enter() {
            this.frames = this.frames.add(Lists.empty());
            return this;
        }

        public ParseState defineAll(List_<String> typeParams) {
            List_<String> newLast = this.frames.last().addAll(typeParams);
            this.frames = this.frames.set(this.frames.size() - 1, newLast);
            return this;
        }

        public boolean isDefined(String typeParam) {
            return this.frames.iter().anyMatch(frame -> Lists.contains(frame, typeParam, String::equals));
        }
    }

    private static final class Node {
        private final Map_<String, String> strings;
        private final Map_<String, List_<Node>> nodeLists;

        public Node() {
            this(Maps.empty(), Maps.empty());
        }

        public Node(Map_<String, String> strings, Map_<String, List_<Node>> nodeLists) {
            this.strings = strings;
            this.nodeLists = nodeLists;
        }

        @Override
        public String toString() {
            return this.strings.toString() + this.nodeLists.toString();
        }

        private Node withString(String propertyKey, String propertyValue) {
            return new Node(this.strings.with(propertyKey, propertyValue), this.nodeLists);
        }

        public Option<String> findString(String propertyKey) {
            return this.strings.find(propertyKey);
        }

        public Node withNodeList(String propertyKey, List_<Node> propertyValues) {
            return new Node(this.strings, this.nodeLists.with(propertyKey, propertyValues));
        }

        public Option<List_<Node>> findNodeList(String propertyKey) {
            return this.nodeLists.find(propertyKey);
        }

        public Node merge(Node other) {
            return new Node(this.strings.withAll(other.strings), this.nodeLists.withAll(other.nodeLists));
        }

        public boolean equalsTo(Node node) {
            return this.hasSameStrings(node) && this.hasSameNodeLists(node);
        }

        private boolean hasSameStrings(Node node) {
            return Maps.equalsTo(this.strings, node.strings, String::equals);
        }

        private boolean hasSameNodeLists(Node node) {
            return Maps.equalsTo(this.nodeLists, node.nodeLists, new BiFunction<List_<Node>, List_<Node>, Boolean>() {
                @Override
                public Boolean apply(List_<Node> nodeList, List_<Node> nodeList2) {
                    return Lists.equalsTo(nodeList, nodeList2, Node::equalsTo);
                }
            });
        }
    }

    public static final List_<String> FUNCTIONAL_NAMESPACE = Lists.of("java", "util", "function");
    private static final List_<String> imports = Lists.empty();
    private static final List_<String> structs = Lists.empty();
    private static final List_<String> globals = Lists.empty();
    private static final List_<String> methods = Lists.empty();
    private static List_<Node> expansions = Lists.empty();
    private static int counter = 0;

    private static Result<String, CompileError> getRecord(String input) {
        if (isSymbol(input)) {
            return new Ok<>(input);
        }
        return new Err<>(new CompileError("Not a symbol", input));
    }

    private static Result<String, CompileError> compileWithType(String type, String input, Function<String, Result<String, CompileError>> childRule) {
        return childRule.apply(input).mapErr(err -> new CompileError("Invalid type '" + type + "'", input, Lists.of(err)));
    }

    private static Result<String, CompileError> compileStrip(String s, Function<String, Result<String, CompileError>> childRule) {
        return childRule.apply(s.strip());
    }

    private static Result<Node, CompileError> compileOr(String input, List_<Rule> rules) {
        return rules.iter()
                .foldWithInitial(new OrState<Node>(), (orState, compiler) -> compiler.apply(input).match(orState::withValue, orState::withError))
                .toResult()
                .mapErr(errors -> new CompileError("No valid combination present", input, errors));
    }

    public static void main(String[] args) {
        Path_ source = Paths.get(".", "src", "java", "magma", "Main.java");
        Files.readString(source)
                .mapErr(ApplicationError::new)
                .match(input -> compileAndWrite(source, input), Some::new)
                .ifPresent(error -> printlnErr(error.display()));
    }

    private static Option<ApplicationError> compileAndWrite(Path_ source, String input) {
        Path_ target = source.resolveSibling("main.c");
        return compile(input)
                .mapErr(ApplicationError::new)
                .match(output -> writeWrapped(output, target), Some::new);
    }

    private static Option<ApplicationError> writeWrapped(String output, Path_ target) {
        return Files.writeString(target, output).map(ApplicationError::new);
    }

    private static Result<String, CompileError> compile(String input) {
        List_<String> segments = divide(input, new DecoratedDivider(Main::divideStatementChar));
        return parseAll(segments, wrapDefault(input0 -> compileRootSegment(input0))).mapValue(list -> {
                    return list.iter()
                            .map(Main::unwrapDefault)
                            .collect(new ListCollector<>());
                })
                .mapValue(Main::mergeStatics)
                .mapValue(compiled -> mergeAll(compiled, Main::mergeStatements));
    }

    private static Result<String, CompileError> compileRootSegment(String input0) {
        ParseState state = new ParseState();
        List_<Rule> rules = Lists.<Function<String, Result<String, CompileError>>>of(
                Main::compileWhitespace,
                Main::compilePackage,
                Main::compileImport,
                (input) -> compileToStruct(input, "class ", state)
        ).iter()
                .map(Main::wrapDefault)
                .collect(new ListCollector<>());

        return compileOr(input0, rules).mapValue(Main::unwrapDefault);
    }

    private static Result<String, CompileError> compileImport(String input) {
        String stripped = input.strip();
        if (!stripped.startsWith("import ")) {
            return createPrefixError(stripped, "import ");
        }

        String right = stripped.substring("import ".length());
        if (!right.endsWith(";")) {
            return createSuffixErr(right, ";");
        }

        String content = right.substring(0, right.length() - ";".length());
        List_<String> split = divide(content, new DelimitedDivider('.'));
        if (split.size() >= 3 && Lists.equalsTo(split.slice(0, 3), FUNCTIONAL_NAMESPACE, String::equals)) {
            return new Ok<>("");
        }

        String joined = split.iter().collect(new Joiner("/")).orElse("");
        imports.add("#include \"../" + joined + ".h\"\n");
        return new Ok<>("");
    }

    private static Result<String, CompileError> compilePackage(String input) {
        if (input.startsWith("package ")) {
            return new Ok<>("");
        }
        return new Err<>(new CompileError("Prefix 'package ' not present", input));
    }

    private static List_<String> mergeStatics(List_<String> list) {
        List_<String> stringList = Lists.<String>empty()
                .addAll(imports)
                .addAll(structs);

        List_<String> folded = expansions.iter().foldWithInitial(stringList,
                (typeParams, node) -> typeParams.add("// " + generateGeneric(node) + "\n"));

        return stringList.addAll(globals)
                .addAll(methods)
                .addAll(folded);
    }

    private static Result<List_<Node>, CompileError> parseAllStatements(String input, Rule rule) {
        return parseAll(divide(input, new DecoratedDivider(Main::divideStatementChar)), rule);
    }

    private static String generateAll(BiFunction<StringBuilder, String, StringBuilder> merger, List_<Node> list) {
        return mergeAll(list.iter()
                .map(Main::unwrapDefault)
                .collect(new ListCollector<>()), merger);
    }

    private static String mergeAll(List_<String> compiled, BiFunction<StringBuilder, String, StringBuilder> merger) {
        return compiled.iter().foldWithInitial(new StringBuilder(), merger).toString();
    }

    private static Result<List_<Node>, CompileError> parseAll(List_<String> segments, Rule rule) {
        return segments.iter().foldToResult(Lists.empty(),
                (current, element) -> rule.apply(element).mapValue(current::add));
    }

    private static String unwrapDefault(Node node) {
        return node.findString("value").orElse("");
    }

    private static Rule wrapDefault(Function<String, Result<String, CompileError>> mapper) {
        return s -> mapper.apply(s).mapValue(value -> new Node().withString("value", value));
    }

    private static StringBuilder mergeStatements(StringBuilder output, String compiled) {
        return output.append(compiled);
    }

    public static List_<String> divide(String input, Divider divider) {
        List_<Character> queue = Iterators.fromString(input)
                .collect(new ListCollector<>());

        State state = new State(queue);
        while (state.hasElements()) {
            Option<Tuple<Character, State>> maybeNextTuple = state.pop();
            if (maybeNextTuple.isEmpty()) {
                break;
            }

            Tuple<Character, State> nextTuple = maybeNextTuple.orElse(new Tuple<>('\0', state));
            state = divider.fold(nextTuple.right, nextTuple.left);
        }

        return state.advance().segments();
    }

    public static State divideStatementChar(State state, char c) {
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

    private static Result<String, CompileError> compileToStruct(String input, String infix, ParseState typeParams) {
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
        String beforeImplements = getBeforeImplements(beforeContent);
        String strippedBeforeImplements = beforeImplements.strip();

        String withoutExtends = removeExtends(strippedBeforeImplements);

        String withoutParams = removeParameters(withoutExtends);
        String strippedWithoutParams = withoutParams.strip();
        return getCompileErrorResult(typeParams, strippedWithoutParams, afterKeyword, contentStart);
    }

    private static Result<String, CompileError> getCompileErrorResult(ParseState typeParams, String strippedWithoutParams, String afterKeyword, int contentStart) {
        if (strippedWithoutParams.endsWith(">")) {
            String strippedWithoutParams1 = strippedWithoutParams.substring(0, strippedWithoutParams.length() - ">".length());
            int genStart = strippedWithoutParams1.indexOf("<");
            if (genStart < 0) {
                return generateStruct(typeParams, strippedWithoutParams1, afterKeyword, contentStart, Lists.empty());
            }
            else {
                String name = strippedWithoutParams1.substring(0, genStart).strip();
                String substring = strippedWithoutParams1.substring(genStart + 1);
                List_<String> moreTypeParams = divide(substring, new DelimitedDivider(','))
                        .iter()
                        .map(String::strip)
                        .collect(new ListCollector<>());

                return generateStruct(typeParams.enter().defineAll(moreTypeParams), name, afterKeyword, contentStart, moreTypeParams);
            }
        }
        else {
            return generateStruct(typeParams, strippedWithoutParams, afterKeyword, contentStart, Lists.empty());
        }

    }

    private static Result<String, CompileError> generateStruct(ParseState typeParams, String name, String afterKeyword, int contentStart, List_<String> moreTypeParams) {
        if (!isSymbol(name)) {
            return new Err<>(new CompileError("Not a symbol", name));
        }

        String withEnd = afterKeyword.substring(contentStart + "{".length()).strip();
        if (!withEnd.endsWith("}")) {
            return new Err<>(new CompileError("Suffix '}' not present", withEnd));
        }

        String inputContent = withEnd.substring(0, withEnd.length() - "}".length());
        return parseAllStatements(inputContent, wrapDefault(s -> compileClassMember(s, typeParams))).mapValue(list -> generateAll(Main::mergeStatements, list)).mapValue(outputContent -> {
            String typeParameters = moreTypeParams.iter()
                    .collect(new Joiner(", "))
                    .map(inner -> "<" + inner + ">")
                    .orElse("");

            String element = "struct " + name + typeParameters + " {\n" + outputContent + "};\n";
            String element1 = typeParameters.isEmpty() ? element : "/*" + element + "*/";
            structs.add(element1);
            return "";
        });
    }

    private static String removeExtends(String strippedBeforeImplements) {
        int extendsIndex = strippedBeforeImplements.indexOf(" extends ");
        String strippedBeforeImplements1 = extendsIndex >= 0
                ? strippedBeforeImplements.substring(0, extendsIndex)
                : strippedBeforeImplements;

        return strippedBeforeImplements1.strip();
    }

    private static String getBeforeImplements(String beforeContent) {
        return getString(beforeContent.indexOf(" implements "), beforeContent);
    }

    private static Err<String, CompileError> createInfixErr(String input, String infix) {
        return new Err<>(new CompileError("Infix '" + infix + "' not present", input));
    }

    private static String removeParameters(String strippedBeforeImplements) {
        if (!strippedBeforeImplements.endsWith(")")) {
            return strippedBeforeImplements;
        }
        String withoutEnd = strippedBeforeImplements.substring(0, strippedBeforeImplements.length() - ")".length());

        int paramStart = withoutEnd.indexOf("(");
        if (paramStart < 0) {
            return strippedBeforeImplements;
        }

        return withoutEnd.substring(0, paramStart).strip();
    }

    private static String getString(int implementsIndex, String beforeContent) {
        String beforeImplements;
        if (implementsIndex >= 0) {
            beforeImplements = beforeContent.substring(0, implementsIndex);
        }
        else {
            beforeImplements = beforeContent;
        }
        return beforeImplements;
    }

    private static Result<String, CompileError> compileClassMember(String input0, ParseState typeParams) {
        List_<Rule> rules = Lists.<Function<String, Result<String, CompileError>>>of(
                Main::compileWhitespace,
                (input) -> compileToStruct(input, "interface ", typeParams),
                (input) -> compileToStruct(input, "record ", typeParams),
                (input) -> compileToStruct(input, "class ", typeParams),
                (input) -> compileGlobalInitialization(typeParams, input),
                input1 -> compileDefinitionStatement(input1, typeParams),
                (input) -> compileMethod(typeParams, input)
        ).iter()
                .map(Main::wrapDefault)
                .collect(new ListCollector<>());

        return compileOr(input0, rules).mapValue(Main::unwrapDefault);
    }

    private static Result<String, CompileError> compileMethod(ParseState state, String input) {
        int paramStart = input.indexOf("(");
        if (paramStart < 0) {
            return createInfixErr(input, "(");
        }

        String inputDefinition = input.substring(0, paramStart).strip();
        String withParams = input.substring(paramStart + "(".length());

        return compileDefinition(state, inputDefinition).flatMapValue(outputDefinition -> {
            int paramEnd = withParams.indexOf(")");
            if (paramEnd < 0) {
                return createInfixErr(withParams, ")");
            }

            String params = withParams.substring(0, paramEnd);
            return compileValues(params, definition -> compileParameter(definition, state))
                    .flatMapValue(outputParams -> assembleMethodBody(state, outputDefinition, outputParams, withParams.substring(paramEnd + ")".length()).strip()));
        });
    }

    private static Result<String, CompileError> compileDefinitionStatement(String input, ParseState typeParams) {
        String stripped = input.strip();
        if (stripped.endsWith(";")) {
            String content = stripped.substring(0, stripped.length() - ";".length());
            return compileDefinition(typeParams, content).mapValue(result -> "\t" + result + ";\n");
        }
        return createSuffixErr(input, ";");
    }

    private static <T> Result<T, CompileError> createSuffixErr(String input, String suffix) {
        return new Err<>(new CompileError("Suffix '" + suffix + "' not present", input));
    }

    private static Result<String, CompileError> compileGlobalInitialization(ParseState typeParams, String input) {
        return compileInitialization(input, typeParams, 0).mapValue(generated -> {
            globals.add(generated + ";\n");
            return "";
        });
    }

    private static Result<String, CompileError> compileInitialization(
            String input,
            ParseState state,
            int depth
    ) {
        if (!input.endsWith(";")) {
            return createSuffixErr(input, ";");
        }

        String withoutEnd = input.substring(0, input.length() - ";".length());
        int valueSeparator = withoutEnd.indexOf("=");
        if (valueSeparator < 0) {
            return createInfixErr(withoutEnd, "=");
        }

        String definition = withoutEnd.substring(0, valueSeparator).strip();
        String value = withoutEnd.substring(valueSeparator + "=".length()).strip();
        return compileDefinition(state, definition)
                .flatMapValue(outputDefinition -> compileValue(value, state, depth).mapValue(outputValue -> outputDefinition + " = " + outputValue));
    }

    private static Result<String, CompileError> compileWhitespace(String input) {
        if (input.isBlank()) {
            return new Ok<>("");
        }
        return new Err<>(new CompileError("Not blank", input));
    }

    private static Result<String, CompileError> assembleMethodBody(
            ParseState typeParams,
            String definition,
            String params,
            String body
    ) {
        String header = "\t".repeat(0) + definition + "(" + params + ")";
        if (body.startsWith("{") && body.endsWith("}")) {
            String inputContent = body.substring("{".length(), body.length() - "}".length());
            Result<String, CompileError> result = parseAllStatements(inputContent, wrapDefault(s -> compileStatementOrBlock(s, typeParams, 1))).mapValue(list -> generateAll(Main::mergeStatements, list));
            return result.mapValue(outputContent -> {
                methods.add(header + " {" + outputContent + "\n}\n");
                return "";
            });
        }

        return new Ok<>("\t" + header + ";\n");
    }

    private static Result<String, CompileError> compileParameter(String definition, ParseState state) {
        List_<Rule> rules = Lists.<Function<String, Result<String, CompileError>>>of(
                Main::compileWhitespace,
                definition1 -> compileDefinition(state, definition1)
        ).iter()
                .map(Main::wrapDefault)
                .collect(new ListCollector<>());

        return compileOr(definition, rules).mapValue(Main::unwrapDefault);
    }

    private static Result<String, CompileError> compileValues(String input, Function<String, Result<String, CompileError>> compiler) {
        List_<String> segments = divideValues(input);
        return parseAll(segments, wrapDefault(compiler)).mapValue(Main::generateValues);
    }

    private static List_<String> divideValues(String input) {
        return divide(input, new DecoratedDivider(Main::divideValueChar));
    }

    private static State divideValueChar(State state, char c) {
        if (c == '-') {
            if (state.peek().orElse('\0') == '>') {
                return state.append('-').popAndAppend().orElse(state);
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

    private static String generateValues(List_<Node> list) {
        return generateAll(Main::mergeValues, list);
    }

    private static Result<String, CompileError> compileStatementOrBlock(String input0, ParseState typeParams, int depth) {
        List_<Rule> rules = Lists.<Function<String, Result<String, CompileError>>>of(
                Main::compileWhitespace,
                input -> compileKeywordStatement(input, depth, "continue"),
                input -> compileKeywordStatement(input, depth, "break"),
                input -> compileConditional("if", "if ", input, typeParams, depth),
                input -> compileConditional("while", "while ", input, typeParams, depth),
                input -> getWrap(typeParams, depth, input),
                input -> compilePostOperator(input, typeParams, depth, "++"),
                input -> compilePostOperator(input, typeParams, depth, "--"),
                input -> compileReturn(input, typeParams, depth).mapValue(result -> formatStatement(depth, result)),
                input -> compileInitialization(input, typeParams, depth).mapValue(result -> formatStatement(depth, result)),
                input -> compileAssignment(input, typeParams, depth).mapValue(result -> formatStatement(depth, result)),
                input -> compileInvocationStatement(input, typeParams, depth).mapValue(result -> formatStatement(depth, result)),
                input1 -> compileDefinitionStatement(input1, typeParams)).iter()
                .map(Main::wrapDefault)
                .collect(new ListCollector<>());

        return compileOr(input0, rules).mapValue(Main::unwrapDefault);
    }

    private static Result<String, CompileError> getWrap(ParseState typeParams, int depth, String input) {
        String stripped = input.strip();
        if (!stripped.startsWith("else ")) {
            return createPrefixError(stripped, "else ");
        }

        String withoutKeyword = stripped.substring("else ".length()).strip();
        if (withoutKeyword.startsWith("{") && withoutKeyword.endsWith("}")) {
            String indent = createIndent(depth);
            return parseAllStatements(withoutKeyword.substring(1, withoutKeyword.length() - 1), wrapDefault(s -> compileStatementOrBlock(s, typeParams, depth + 1))).mapValue(list -> generateAll(Main::mergeStatements, list))
                    .mapValue(result -> indent + "else {" + result + indent + "}");
        }

        Result<String, CompileError> stringCompileErrorResult = compileStatementOrBlock(withoutKeyword, typeParams, depth);
        return stringCompileErrorResult.mapValue(result -> "else " + result);
    }

    private static Result<String, CompileError> createPrefixError(String input, String prefix) {
        return new Err<>(new CompileError("Prefix '" + prefix + "' not present", input));
    }

    private static Result<String, CompileError> compilePostOperator(String input, ParseState typeParams, int depth, String operator) {
        String stripped = input.strip();
        if (stripped.endsWith(operator + ";")) {
            String slice = stripped.substring(0, stripped.length() - (operator + ";").length());
            return compileValue(slice, typeParams, depth).mapValue(value -> value + operator + ";");
        }
        else {
            return createSuffixErr(stripped, operator + ";");
        }
    }

    private static Result<String, CompileError> compileKeywordStatement(String input, int depth, String keyword) {
        if (input.strip().equals(keyword + ";")) {
            return new Ok<>(formatStatement(depth, keyword));
        }
        else {
            return createSuffixErr(input.strip(), keyword + ";");
        }
    }

    private static String formatStatement(int depth, String value) {
        return createIndent(depth) + value + ";";
    }

    private static String createIndent(int depth) {
        return "\n" + "\t".repeat(depth);
    }

    private static Result<String, CompileError> compileConditional(String type, String prefix, String input0, ParseState typeParams, int depth) {
        return compileWithType(type, input0, input -> {
            String stripped = input.strip();
            if (!stripped.startsWith(prefix)) {
                return createPrefixError(stripped, prefix);
            }

            String afterKeyword = stripped.substring(prefix.length()).strip();
            if (!afterKeyword.startsWith("(")) {
                return createPrefixError(afterKeyword, "(");
            }

            String withoutConditionStart = afterKeyword.substring(1);
            int conditionEnd = findConditionEnd(withoutConditionStart);
            if (conditionEnd < 0) {
                return createInfixErr(withoutConditionStart, ")");
            }

            String oldCondition = withoutConditionStart.substring(0, conditionEnd).strip();
            String withBraces = withoutConditionStart.substring(conditionEnd + ")".length()).strip();

            return compileValue(oldCondition, typeParams, depth).flatMapValue(newCondition -> {
                String withCondition = createIndent(depth) + prefix + "(" + newCondition + ")";

                if (!withBraces.startsWith("{") || !withBraces.endsWith("}")) {
                    return compileStatementOrBlock(withBraces, typeParams, depth).mapValue(result -> withCondition + " " + result);
                }

                String content = withBraces.substring(1, withBraces.length() - 1);
                Result<String, CompileError> stringCompileErrorResult = parseAllStatements(content, wrapDefault(s -> compileStatementOrBlock(s, typeParams, depth + 1))).mapValue(list -> generateAll(Main::mergeStatements, list));
                return stringCompileErrorResult.mapValue(statements -> withCondition +
                        " {" + statements + "\n" +
                        "\t".repeat(depth) +
                        "}");
            });
        });
    }

    private static int findConditionEnd(String input) {
        int conditionEnd = -1;
        int depth0 = 0;

        List_<Tuple<Integer, Character>> queue = Iterators.fromStringWithIndices(input).collect(new ListCollector<>());

        while (!queue.isEmpty()) {
            Tuple<Tuple<Integer, Character>, List_<Tuple<Integer, Character>>> tupleListTuple = queue.popFirst().orElse(null);
            Tuple<Integer, Character> pair = tupleListTuple.left;
            queue = tupleListTuple.right;

            Integer i = pair.left;
            Character c = pair.right;

            if (c == '\'') {
                Tuple<Tuple<Integer, Character>, List_<Tuple<Integer, Character>>> tupleListTuple1 = queue.popFirst().orElse(null);
                queue = tupleListTuple1.right;

                Character next = tupleListTuple1.left.right;
                if (next == '\\') {
                    queue = queue.popFirst().orElse(null).right;
                }

                queue = queue.popFirst().orElse(null).right;
                continue;
            }

            if (c == '"') {
                while (!queue.isEmpty()) {
                    Tuple<Tuple<Integer, Character>, List_<Tuple<Integer, Character>>> tupleListTuple1 = queue.popFirst().orElse(null);
                    Tuple<Integer, Character> next = tupleListTuple1.left;
                    queue = tupleListTuple1.right;

                    if (next.right == '\\') {
                        queue.popFirst().orElse(null);
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

    private static Result<String, CompileError> compileInvocationStatement(String input, ParseState typeParams, int depth) {
        String stripped = input.strip();
        if (stripped.endsWith(";")) {
            String withoutEnd = stripped.substring(0, stripped.length() - ";".length());
            return compileInvocation(withoutEnd, typeParams, depth);
        }
        return createSuffixErr(stripped, ";");
    }

    private static Result<String, CompileError> compileAssignment(String input, ParseState typeParams, int depth) {
        String stripped = input.strip();
        if (!stripped.endsWith(";")) {
            return createSuffixErr(stripped, ";");
        }

        String withoutEnd = stripped.substring(0, stripped.length() - ";".length());
        int valueSeparator = withoutEnd.indexOf("=");
        if (valueSeparator < 0) {
            return createInfixErr(stripped, "=");
        }

        String destination = withoutEnd.substring(0, valueSeparator).strip();
        String source = withoutEnd.substring(valueSeparator + "=".length()).strip();
        return compileValue(destination, typeParams, depth)
                .flatMapValue(newDest -> compileValue(source, typeParams, depth)
                        .mapValue(newSource -> newDest + " = " + newSource));
    }

    private static Result<String, CompileError> compileReturn(String input, ParseState typeParams, int depth) {
        String stripped = input.strip();
        if (stripped.endsWith(";")) {
            String withoutEnd = stripped.substring(0, stripped.length() - ";".length());
            if (withoutEnd.startsWith("return ")) {
                return compileValue(withoutEnd.substring("return ".length()), typeParams, depth)
                        .mapValue(result -> "return " + result);
            }
        }

        return createSuffixErr(stripped, ";");
    }

    private static Result<String, CompileError> compileValue(String input0, ParseState typeParams, int depth) {
        List_<Function<String, Result<String, CompileError>>> rules = Lists.of(
                Main::compileBoolean,
                Main::compileString,
                Main::compileChar,
                stripped -> compileWithType("symbol", stripped, s1 -> compileStrip(s1, Main::getRecord)),
                stripped1 -> compileWithType("number", stripped1, s1 -> compileStrip(s1, s2 -> {
                    if (isNumber(s2)) {
                        return new Ok<>(s2);
                    }
                    return new Err<>(new CompileError("Not a number", s2));
                })),
                input -> compileConstruction(input, typeParams, depth),
                input -> compileNot(input, typeParams, depth),
                input -> compileLambda(input, typeParams, depth),
                input -> compileInvocation(input, typeParams, depth),
                input -> compileMethodAccess(input, typeParams),
                input -> compileDataAccess(input, typeParams, depth),
                (input) -> compileOperator(input, typeParams, depth, "||"),
                (input) -> compileOperator(input, typeParams, depth, "<"),
                (input) -> compileOperator(input, typeParams, depth, "+"),
                (input) -> compileOperator(input, typeParams, depth, ">="),
                (input) -> compileOperator(input, typeParams, depth, "&&"),
                (input) -> compileOperator(input, typeParams, depth, "=="),
                (input) -> compileOperator(input, typeParams, depth, "!=")
        );
        return ((Function<String, Result<String, CompileError>>) s -> {
            List_<Rule> rules1 = rules.iter()
                    .map(Main::wrapDefault)
                    .collect(new ListCollector<>());

            return compileOr(s, rules1).mapValue(Main::unwrapDefault);
        }).apply(input0);
    }

    private static Result<String, CompileError> compileBoolean(String input) {
        if (input.equals("true")) {
            return new Ok<>("1");
        }
        if (input.equals("false")) {
            return new Ok<>("0");
        }
        return new Err<>(new CompileError("Not a boolean", input));
    }

    private static Result<String, CompileError> compileString(String input) {
        if (input.startsWith("\"") && input.endsWith("\"")) {
            return new Ok<>(input);
        }
        else {
            return new Err<>(new CompileError("Not a string", input));
        }
    }

    private static Result<String, CompileError> compileChar(String input) {
        String stripped = input.strip();
        if (stripped.startsWith("'") && stripped.endsWith("'")) {
            return new Ok<>(stripped);
        }
        else {
            return new Err<>(new CompileError("Not a char", stripped));
        }
    }

    private static Result<String, CompileError> compileConstruction(String stripped, ParseState typeParams, int depth) {
        return compileWithType("construction", stripped, new Function<String, Result<String, CompileError>>() {
            @Override
            public Result<String, CompileError> apply(String s1) {
                return compileStrip(s1, input -> {
                    if (!input.startsWith("new ")) {
                        return createPrefixError(input, "new ");
                    }

                    String slice = input.substring("new ".length());
                    int argsStart = slice.indexOf("(");
                    if (argsStart < 0) {
                        return createInfixErr(slice, "(");
                    }

                    String type = slice.substring(0, argsStart);
                    String withEnd = slice.substring(argsStart + "(".length()).strip();
                    if (!withEnd.endsWith(")")) {
                        return createSuffixErr(withEnd, ")");
                    }

                    String argsString = withEnd.substring(0, withEnd.length() - ")".length());
                    return parseAnyType(type, typeParams).mapValue(Main::generateAnyType)
                            .flatMapValue(outputType -> compileArgs(argsString, typeParams, depth)
                                    .mapValue(value -> outputType + value));
                });
            }
        });
    }

    private static Result<String, CompileError> compileNot(String stripped, ParseState typeParams, int depth) {
        if (stripped.startsWith("!")) {
            return compileValue(stripped.substring(1), typeParams, depth).mapValue(result -> "!" + result);
        }
        else {
            return createPrefixError(stripped, "!");
        }
    }

    private static Result<String, CompileError> compileMethodAccess(String input, ParseState typeParams) {
        int methodIndex = input.lastIndexOf("::");
        if (methodIndex >= 0) {
            String type = input.substring(0, methodIndex).strip();
            String property = input.substring(methodIndex + "::".length()).strip();

            if (isSymbol(property)) {
                return parseAnyType(type, typeParams).mapValue(Main::generateAnyType)
                        .flatMapValue(compiled -> generateLambdaWithReturn(Lists.empty(), "\n\treturn " + compiled + "." + property + "()"));
            }
        }
        return createInfixErr(input, "::");
    }

    private static Result<String, CompileError> compileDataAccess(String input, ParseState typeParams, int depth) {
        int separator = input.lastIndexOf(".");
        if (separator >= 0) {
            String object = input.substring(0, separator).strip();
            String property = input.substring(separator + ".".length()).strip();
            return compileValue(object, typeParams, depth).mapValue(compiled -> compiled + "." + property);
        }
        return createInfixErr(input, ".");
    }

    private static Result<String, CompileError> compileOperator(String input, ParseState typeParams, int depth, String operator) {
        int operatorIndex = input.indexOf(operator);
        if (operatorIndex < 0) {
            return createInfixErr(input, operator);
        }

        String left = input.substring(0, operatorIndex);
        String right = input.substring(operatorIndex + operator.length());

        return compileValue(left, typeParams, depth)
                .flatMapValue(leftResult -> compileValue(right, typeParams, depth)
                        .mapValue(rightResult -> leftResult + " " + operator + " " + rightResult));
    }

    private static Result<String, CompileError> compileLambda(String input, ParseState typeParams, int depth) {
        int arrowIndex = input.indexOf("->");
        if (arrowIndex < 0) {
            return createInfixErr(input, "->");
        }

        String beforeArrow = input.substring(0, arrowIndex).strip();
        List_<String> paramNames;
        if (isSymbol(beforeArrow)) {
            paramNames = Lists.of(beforeArrow);
        }
        else if (beforeArrow.startsWith("(") && beforeArrow.endsWith(")")) {
            String inner = beforeArrow.substring(1, beforeArrow.length() - 1);
            paramNames = divide(inner, new DelimitedDivider('.'))
                    .iter()
                    .map(String::strip)
                    .filter(value -> !value.isEmpty())
                    .collect(new ListCollector<>());
        }
        else {
            return new Err<>(new CompileError("No params found", beforeArrow));
        }

        String value = input.substring(arrowIndex + "->".length()).strip();
        if (value.startsWith("{") && value.endsWith("}")) {
            String slice = value.substring(1, value.length() - 1);
            return parseAllStatements(slice, wrapDefault(s -> compileStatementOrBlock(s, typeParams, depth))).mapValue(list -> generateAll(Main::mergeStatements, list))
                    .flatMapValue(result -> generateLambdaWithReturn(paramNames, result));
        }

        return compileValue(value, typeParams, depth).flatMapValue(newValue -> generateLambdaWithReturn(paramNames, "\n\treturn " + newValue + ";"));
    }

    private static Result<String, CompileError> generateLambdaWithReturn(List_<String> paramNames, String returnValue) {
        int current = counter;
        counter++;
        String lambdaName = "__lambda" + current + "__";

        String joined = paramNames.iter()
                .map(name -> "auto " + name)
                .collect(new Joiner(", "))
                .map(value -> "(" + value + ")")
                .orElse("");

        methods.add("auto " + lambdaName + joined + " {" + returnValue + "\n}\n");
        return new Ok<>(lambdaName);
    }

    private static boolean isNumber(String input) {
        return Iterators.fromStringWithIndices(input).allMatch(tuple -> {
            int index = tuple.left;
            char c = tuple.right;
            return (index == 0 && c == '-') || Character.isDigit(c);
        });
    }

    private static Result<String, CompileError> compileInvocation(String input, ParseState typeParams, int depth) {
        String stripped = input.strip();
        if (!stripped.endsWith(")")) {
            return createSuffixErr(stripped, ")");
        }

        String sliced = stripped.substring(0, stripped.length() - ")".length());

        int argsStart = findInvocationStart(sliced);
        if (argsStart < 0) {
            return createInfixErr(stripped, "(");
        }

        String type = sliced.substring(0, argsStart);
        String withEnd = sliced.substring(argsStart + "(".length()).strip();
        return compileValue(type, typeParams, depth)
                .flatMapValue(caller -> compileArgs(withEnd, typeParams, depth).mapValue(value -> caller + value));
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

    private static Result<String, CompileError> compileArgs(String argsString, ParseState typeParams, int depth) {
        return compileValues(argsString, arg -> {
            List_<Function<String, Result<String, CompileError>>> rules = Lists.of(
                    Main::compileWhitespace,
                    value -> compileValue(value, typeParams, depth)
            );
            return ((Function<String, Result<String, CompileError>>) s -> {
                List_<Rule> rules1 = rules.iter()
                        .map(Main::wrapDefault)
                        .collect(new ListCollector<>());

                return compileOr(s, rules1).mapValue(Main::unwrapDefault);
            }).apply(arg);
        }).mapValue(args -> "(" + args + ")");
    }

    private static StringBuilder mergeValues(StringBuilder cache, String element) {
        if (cache.isEmpty()) {
            return cache.append(element);
        }
        return cache.append(", ").append(element);
    }

    private static Result<String, CompileError> compileDefinition(ParseState state, String s) {
        return compileWithType("definition", s, definition -> {
            String stripped = definition.strip();
            int nameSeparator = stripped.lastIndexOf(" ");
            if (nameSeparator < 0) {
                return createInfixErr(stripped, " ");
            }

            String beforeName = stripped.substring(0, nameSeparator).strip();
            String name = stripped.substring(nameSeparator + " ".length()).strip();
            if (!isSymbol(name)) {
                return new Err<>(new CompileError("Not a symbol", name));
            }

            return findTypeSeparator(beforeName).match(typeSeparator -> {
                return compileBeforeDefinitionName(state, new Node()
                        .withString("name", name), beforeName.substring(0, typeSeparator).strip(), beforeName.substring(typeSeparator + " ".length()));
            }, () -> {
                return parseAnyType(beforeName, state).mapValue(Main::generateAnyType).mapValue(outputType -> {
                    return generateDefinition(new Node()
                            .withNodeList("type-params", Lists.empty())
                            .withString("name", name)
                            .withString("type", outputType));
                });
            });
        });
    }

    private static Result<String, CompileError> compileBeforeDefinitionName(
            ParseState state,
            Node node,
            String beforeType,
            String typeString
    ) {
        if (beforeType.endsWith(">")) {
            String withoutEnd = beforeType.substring(0, beforeType.length() - ">".length());
            int typeParamStart = withoutEnd.indexOf("<");
            if (typeParamStart >= 0) {
                String more = withoutEnd.substring(0, typeParamStart);
                String substring = withoutEnd.substring(typeParamStart + 1);
                List_<String> typeParams = splitValues(substring);
                ParseState state1 = state.enter().defineAll(typeParams);
                String modifiers = removeAnnotations(more.strip());

                if (!validateModifiers(modifiers)) {
                    return new Err<>(new CompileError("Invalid modifiers", modifiers));
                }

                Result<Node, CompileError> withType = parseTypeProperty(state1, typeString)
                        .mapValue(node::merge);

                return withType
                        .flatMapValue(outputType -> parseTypeParams(typeParams).mapValue(outputType::merge))
                        .mapValue(Main::generateDefinition);
            }
        }

        String modifiers = removeAnnotations(beforeType.strip());
        List_<String> typeParams = Lists.empty();
        if (!validateModifiers(modifiers)) {
            return new Err<>(new CompileError("Invalid modifiers", modifiers));
        }

        return parseTypeProperty(state, typeString)
                .mapValue(node::merge)
                .flatMapValue(outputType -> parseTypeParams(typeParams).mapValue(outputType::merge))
                .mapValue(Main::generateDefinition);
    }

    private static Result<Node, CompileError> parseTypeParams(List_<String> typeParams) {
        final List_<Node> typeParamList = typeParams.iter()
                .map(value -> new Node().withString("value", value))
                .collect(new ListCollector<>());

        return new Ok<>(new Node().withNodeList("type-params", typeParamList));
    }

    private static Result<Node, CompileError> parseTypeProperty(ParseState state, String inputType) {
        return parseAnyType(inputType, state).mapValue(Main::generateAnyType)
                .flatMapValue(outputType -> parseString("type", outputType));
    }

    private static Result<Node, CompileError> parseString(String propertyKey, String input) {
        return new Ok<>(new Node().withString(propertyKey, input));
    }

    private static boolean validateModifiers(String modifiersString) {
        return divide(modifiersString, new DelimitedDivider(' '))
                .iter()
                .map(String::strip)
                .filter(value -> !value.isEmpty())
                .allMatch(Main::isSymbol);
    }

    private static String removeAnnotations(String maybeWithAnnotations) {
        int annotationSeparator = maybeWithAnnotations.lastIndexOf("\n");
        if (annotationSeparator >= 0) {
            return maybeWithAnnotations.substring(annotationSeparator + "\n".length());
        }
        return maybeWithAnnotations;
    }

    private static Option<Integer> findTypeSeparator(String beforeName) {
        int depth = 0;
        int i = beforeName.length() - 1;
        while (i >= 0) {
            char c = beforeName.charAt(i);
            if (c == ' ' && depth == 0) {
                return new Some<>(i);
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

        return new None<>();
    }

    private static List_<String> splitValues(String substring) {
        String stripped = substring.strip();
        return divide(stripped, new DelimitedDivider(','))
                .iter()
                .map(String::strip)
                .filter(param -> !param.isEmpty())
                .collect(new ListCollector<>());
    }

    private static String generateDefinition(Node node) {
        String typeParamString = generateTypeParams(node);
        String type = node.findString("type").orElse("");
        String name = node.findString("name").orElse("");
        return typeParamString + type + " " + name;
    }

    private static String generateTypeParams(Node node) {
        return generateTypeParams(node.findNodeList("type-params")
                .orElse(Lists.empty())
                .iter()
                .map(typeParam -> typeParam.findString("value").orElse(""))
                .collect(new ListCollector<>()));
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

    private static String generateAnyType(Node result) {
        return result.findString("value").orElse("");
    }

    private static Result<Node, CompileError> parseAnyType(String input, ParseState typeParams) {
        List_<Function<String, Result<String, CompileError>>> rules = Lists.of(
                value1 -> parsePrimitive(value1).flatMapValue(Main::generatePrimitive),
                value -> parseArray(typeParams, value).flatMapValue(Main::generateReference),
                value -> getStringCompileErrorResult(typeParams, value),
                value -> compileGeneric(typeParams, value)
        );
        return ((Function<String, Result<String, CompileError>>) s -> {
            List_<Rule> rules1 = rules.iter()
                    .map(Main::wrapDefault)
                    .collect(new ListCollector<>());

            return compileOr(s, rules1).mapValue(Main::unwrapDefault);
        }).apply(input).mapValue(result -> new Node().withString("value", result));
    }

    private static Result<String, CompileError> compileGeneric(ParseState typeParams, String value) {
        String stripped = value.strip();
        if (!stripped.endsWith(">")) {
            return createSuffixErr(stripped, ">");
        }

        String slice = stripped.substring(0, stripped.length() - ">".length());
        int argsStart = slice.indexOf("<");
        if (argsStart < 0) {
            return createInfixErr(slice, "<");
        }

        String base = slice.substring(0, argsStart).strip();
        String params = slice.substring(argsStart + "<".length()).strip();

        List_<Function<String, Result<String, CompileError>>> rules = Lists.of(
                Main::compileWhitespace,
                type -> parseAnyType(type, typeParams).mapValue(Main::generateAnyType)
        );

        List_<String> divided = divideValues(params);
        return parseAll(divided, wrapDefault(s -> {
            List_<Rule> rules1 = rules.iter()
                    .map(Main::wrapDefault)
                    .collect(new ListCollector<>());

            return compileOr(s, rules1).mapValue(Main::unwrapDefault);
        })).mapValue(list -> {
            return list.iter()
                    .map(Main::unwrapDefault)
                    .collect(new ListCollector<>());
        }).mapValue(parsed -> {
            if (base.equals("Function")) {
                String first = parsed.get(0);
                String second = parsed.get(1);

                return second + " (*)(" + first + ")";
            }

            List_<Node> arguments = parsed.iter()
                    .map(argument -> new Node().withString("value", argument))
                    .collect(new ListCollector<>());

            Node element = new Node()
                    .withString("base", base)
                    .withNodeList("arguments", arguments);

            if (!Lists.contains(expansions, element, Node::equalsTo)) {
                expansions = expansions.add(element);
            }
            return generateGeneric(element);
        });
    }

    private static String generateGeneric(Node element) {
        List_<String> parsed = element.findNodeList("arguments")
                .orElse(Lists.empty())
                .iter()
                .map(node -> node.findString("value").orElse(""))
                .collect(new ListCollector<>());

        String base = element.findString("base").orElse("");
        return base + "<" + mergeAll(parsed, Main::mergeValues) + ">";
    }

    private static Result<String, CompileError> getStringCompileErrorResult(ParseState state, String value) {
        if (!isSymbol(value.strip())) {
            return new Err<>(new CompileError("Not a symbol", value.strip()));
        }

        if (state.isDefined(value.strip())) {
            return new Ok<>(value.strip());
        }
        else {
            return new Ok<>("struct " + value.strip());
        }
    }

    private static Result<String, CompileError> generateReference(Node value1) {
        return new Ok<>(generateAnyType(value1) + "*");
    }

    private static Result<Node, CompileError> parseArray(ParseState typeParams, String value) {
        if (value.endsWith("[]")) {
            String slice = value.substring(0, value.length() - "[]".length());
            return parseAnyType(slice, typeParams);
        }
        return createSuffixErr(value, "[]");
    }

    private static Ok<String, CompileError> generatePrimitive(Node node) {
        return new Ok<>(node.findString("value").orElse(""));
    }

    private static Result<Node, CompileError> parsePrimitive(String input) {
        return getRecord1(input).mapValue(value -> new Node().withString("value", value));
    }

    private static Result<String, CompileError> getRecord1(String value) {
        String stripped = value.strip();
        if (stripped.equals("void")) {
            return new Ok<>("void");
        }

        if (stripped.equals("int") || stripped.equals("Integer") || stripped.equals("boolean") || stripped.equals("Boolean")) {
            return new Ok<>("int");
        }

        if (stripped.equals("char") || stripped.equals("Character")) {
            return new Ok<>("char");
        }

        return new Err<>(new CompileError("Not a primitive", stripped));
    }

    private static boolean isSymbol(String input) {
        if (input.isBlank()) {
            return false;
        }

        if (input.equals("record")) {
            return false;
        }

        return Iterators.fromStringWithIndices(input).allMatch(tuple -> {
            Integer index = tuple.left;
            char c = tuple.right;
            return c == '_' || Character.isLetter(c) || (index != 0 && Character.isDigit(c));
        });
    }
}
