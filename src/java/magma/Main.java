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

        Option<T> filter(Predicate<T> predicate);

        <R> Option<Tuple<T, R>> and(Supplier<Option<R>> supplier);
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

        <R> Iterator<R> flatMap(Function<T, Iterator<R>> mapper);
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
    }

    public interface IOError {
        String display();
    }

    public interface Path_ {
        Path_ resolveSibling(String sibling);

        List_<String> listNames();
    }

    public interface Map_<K, V> {
        Map_<K, V> with(K key, V value);

        Option<V> find(K key);

        Iterator<K> iterKeys();
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
            this(queue, Impl.listEmpty(), new StringBuilder(), 0);
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

        @Override
        public Option<T> filter(Predicate<T> predicate) {
            return new None<>();
        }

        @Override
        public <R> Option<Tuple<T, R>> and(Supplier<Option<R>> supplier) {
            return new None<>();
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

        @Override
        public Option<T> filter(Predicate<T> predicate) {
            return predicate.test(this.value) ? this : new None<>();
        }

        @Override
        public <R> Option<Tuple<T, R>> and(Supplier<Option<R>> supplier) {
            return supplier.get().map(otherValue -> new Tuple<>(this.value, otherValue));
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

        @Override
        public <R> Iterator<R> flatMap(Function<T, Iterator<R>> mapper) {
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

        public static <T> Iterator<T> fromOption(Option<T> option) {
            return new HeadedIterator<>(option.<Head<T>>map(SingleHead::new).orElseGet(EmptyHead::new));
        }
    }

    private static class ListCollector<T> implements Collector<T, List_<T>> {
        @Override
        public List_<T> createInitial() {
            return Impl.listEmpty();
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

    private record Node(Option<String> type, Map_<String, String> strings, Map_<String, Node> nodes,
                        Map_<String, List_<Node>> nodeLists) {
        public Node() {
            this(new None<>(), Impl.mapEmpty(), Impl.mapEmpty(), Impl.mapEmpty());
        }

        public Node withString(String propertyKey, String propertyValue) {
            return new Node(this.type, this.strings.with(propertyKey, propertyValue), this.nodes, this.nodeLists);
        }

        public Node withNodeList(String propertyKey, List_<Node> propertyValues) {
            return new Node(this.type, this.strings, this.nodes, this.nodeLists.with(propertyKey, propertyValues));
        }

        public Option<List_<Node>> findNodeList(String propertyKey) {
            return this.nodeLists.find(propertyKey);
        }

        public Option<String> findString(String propertyKey) {
            return this.strings.find(propertyKey);
        }

        public Node withNode(String propertyKey, Node propertyValue) {
            return new Node(this.type, this.strings, this.nodes.with(propertyKey, propertyValue), this.nodeLists);
        }

        public Option<Node> findNode(String propertyKey) {
            return this.nodes.find(propertyKey);
        }

        public boolean is(String type) {
            return this.type.filter(inner -> inner.equals(type)).isPresent();
        }

        public Node retype(String type) {
            return new Node(new Some<>(type), this.strings, this.nodes, this.nodeLists);
        }

        public boolean equalsTo(Node other) {
            boolean hasSameType = Options.equalsTo(this.type, other.type, String::equals);
            boolean hasSameStrings = Maps.equalsTo(this.strings, other.strings, String::equals, String::equals);
            boolean hasSameNodes = Maps.equalsTo(this.nodes, other.nodes, String::equals, Node::equals);
            boolean hasSameNodeLists = Maps.equalsTo(this.nodeLists, other.nodeLists, String::equals, this::isABoolean);
            return hasSameType && hasSameStrings && hasSameNodes && hasSameNodeLists;
        }

        private boolean isABoolean(List_<Node> nodeList, List_<Node> nodeList2) {
            return Lists.equalsTo(nodeList, nodeList2, Node::equalsTo);
        }
    }

    private static class Lists {
        public static <T> boolean contains(
                List_<T> list,
                T element,
                BiFunction<T, T, Boolean> equator
        ) {
            return list.iter().anyMatch(child -> equator.apply(child, element));
        }

        public static <T> boolean equalsTo(List_<T> first, List_<T> second, BiFunction<T, T, Boolean> equator) {
            if (first.size() != second.size()) {
                return false;
            }

            return new HeadedIterator<>(new RangeHead(first.size())).allMatch(index -> {
                return equator.apply(first.get(index), second.get(index));
            });
        }
    }

    private static class Options {
        public static <T> boolean equalsTo(Option<T> first, Option<T> second, BiFunction<T, T, Boolean> equator) {
            if (first.isEmpty() && second.isEmpty()) {
                return true;
            }

            return first.and(() -> second)
                    .filter(tuple -> equator.apply(tuple.left, tuple.right))
                    .isPresent();
        }
    }

    private static class Maps {
        public static <K, V> boolean equalsTo(
                Map_<K, V> first,
                Map_<K, V> second,
                BiFunction<K, K, Boolean> keyEquator,
                BiFunction<V, V, Boolean> valueEquator
        ) {
            return first.iterKeys()
                    .concat(second.iterKeys())
                    .fold(Impl.<K>listEmpty(), (kList, key) -> foldUniquely(kList, key, keyEquator))
                    .iter()
                    .allMatch(key -> entryEqualsTo(key, first, second, valueEquator));
        }
    }

    private static final List_<String> imports = Impl.listEmpty();
    private static final List_<String> structs = Impl.listEmpty();
    private static final List_<String> globals = Impl.listEmpty();
    private static final List_<String> methods = Impl.listEmpty();
    private static List_<Node> expansions = Impl.listEmpty();
    private static int counter = 0;
    private static Map_<String, Function<Node, String>> generators = Impl.mapEmpty();

    private static <K, V> boolean entryEqualsTo(
            K key,
            Map_<K, V> first,
            Map_<K, V> second,
            BiFunction<V, V, Boolean> valueEquator
    ) {
        Option<V> firstOption = first.find(key);
        Option<V> secondOption = second.find(key);
        return Options.equalsTo(firstOption, secondOption, valueEquator);
    }

    private static <K> List_<K> foldUniquely(List_<K> kList, K key, BiFunction<K, K, Boolean> keyEquator) {
        if (Lists.contains(kList, key, keyEquator)) {
            return kList;
        }
        else {
            return kList.add(key);
        }
    }

    public static void main(String[] args) {
        Path_ source = Impl.get(".", "src", "java", "magma", "Main.java");
        Impl.readString(source)
                .match(input -> compileAndWrite(input, source), Some::new)
                .ifPresent(IOError::display);
    }

    private static Option<IOError> compileAndWrite(String input, Path_ source) {
        Path_ target = source.resolveSibling("main.c");
        String output = compile(input);
        return Impl.writeString(target, output);
    }

    private static String compile(String input) {
        List_<String> segments = divideAllStatements(input);
        return parseAll(segments, wrapDefaultFunction(Main::compileRootSegment))
                .map(list1 -> list1.iter().map(Main::unwrapDefault).collect(new ListCollector<>()))
                .map(list -> {
                    List_<String> collect = expansions.iter()
                            .map(expansion -> {
                                String comment = "// " + generateGeneric(expansion) + "\n";
                                String base = generators.find(expansion.findString("base").orElse(""))
                                        .map(nodeOptionFunction -> nodeOptionFunction.apply(expansion))
                                        .orElse("");

                                return comment + base;
                            })
                            .collect(new ListCollector<>());

                    return imports.addAll(structs)
                            .addAll(collect)
                            .addAll(globals)
                            .addAll(methods)
                            .addAll(list);
                })
                .map(compiled -> mergeAll(compiled, Main::mergeStatements))
                .or(() -> generatePlaceholder(input)).orElse("");
    }

    private static String mergeAllStatements(List_<Node> compiled) {
        return generateAll(compiled, Main::unwrapDefault, Main::mergeStatements);
    }

    private static Option<List_<Node>> parseAllStatements(String input, Function<String, Option<Node>> rule) {
        return parseAll(divideAllStatements(input), rule);
    }

    private static List_<String> divideAllStatements(String input) {
        return divide(input, Main::divideStatementChar);
    }

    private static String generateAll(List_<Node> compiled, Function<Node, String> generator, BiFunction<StringBuilder, String, StringBuilder> merger) {
        return mergeAll(compiled.iter()
                .map(generator)
                .collect(new ListCollector<>()), merger);
    }

    private static String mergeAll(List_<String> compiled, BiFunction<StringBuilder, String, StringBuilder> merger) {
        return compiled.iter().fold(new StringBuilder(), merger).toString();
    }

    private static Option<List_<Node>> parseAll(List_<String> segments, Function<String, Option<Node>> rule) {
        return segments.iter().<Option<List_<Node>>>fold(new Some<>(Impl.listEmpty()),
                (maybeCompiled, segment) -> maybeCompiled.flatMap(allCompiled -> rule.apply(segment).map(allCompiled::add)));
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

    private static Option<String> compileRootSegment(String input) {
        Option<String> whitespace = compileWhitespace(input);
        if (whitespace.isPresent()) {
            return whitespace;
        }

        if (input.startsWith("package ")) {
            return new Some<>("");
        }

        String stripped = input.strip();
        if (stripped.startsWith("import ")) {
            String right = stripped.substring("import ".length());
            if (right.endsWith(";")) {
                String content = right.substring(0, right.length() - ";".length());
                List_<String> split = splitByDelimiter(content, '.');
                if (split.size() >= 3 && Lists.equalsTo(split.slice(0, 3), Impl.listOf("java", "util", "function"), String::equals)) {
                    return new Some<>("");
                }

                String joined = split.iter().collect(new Joiner("/")).orElse("");
                imports.add("#include \"./" + joined + "\"\n");
                return new Some<>("");
            }
        }

        Option<String> maybeClass = compileToStruct(input, "class ", Impl.listEmpty());
        if (maybeClass.isPresent()) {
            return maybeClass;
        }

        return generatePlaceholder(input);
    }

    private static List_<String> splitByDelimiter(String content, char delimiter) {
        List_<String> segments = Impl.listEmpty();
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

    private static Option<String> compileToStruct(String input, String infix, List_<String> typeParams) {
        int classIndex = input.indexOf(infix);
        if (classIndex < 0) {
            return new None<>();
        }

        String afterKeyword = input.substring(classIndex + infix.length());
        int contentStart = afterKeyword.indexOf("{");
        if (contentStart < 0) {
            return new None<>();
        }
        String beforeContent = afterKeyword.substring(0, contentStart).strip();

        int implementsIndex = beforeContent.indexOf(" implements ");
        String beforeContent1 = implementsIndex >= 0
                ? beforeContent.substring(0, implementsIndex)
                : beforeContent;

        int paramStart = beforeContent1.indexOf("(");
        String withoutParams = paramStart >= 0
                ? beforeContent1.substring(0, paramStart)
                : beforeContent1;

        String strippedWithoutParams = withoutParams.strip();
        int typeParamStart = withoutParams.indexOf("<");
        String body = afterKeyword.substring(contentStart + "{".length());

        Node withBody = new Node().withString("body", body);

        if (typeParamStart >= 0) {
            String name = strippedWithoutParams.substring(0, typeParamStart).strip();

            Node withName = withBody.withString("name", name);
            generators = generators.with(name, (expansion) -> expand(input, typeParams, withName, expansion));

            return new Some<>("// " + withoutParams + "\n");
        }

        return generateStruct(typeParams, withBody.withString("name", strippedWithoutParams));
    }

    private static String expand(String input, List_<String> typeParams, Node withName, Node expansion) {
        String stringify = stringify(expansion);

        return generateStruct(typeParams, withName.withString("name", stringify))
                .or(() -> generatePlaceholder(input))
                .orElse("");
    }

    private static String stringify(Node expansion) {
        if (expansion.is("generic")) {
            String base = expansion.findString("base").orElse("");
            String typeParams = expansion.findNodeList("type-params")
                    .orElse(Impl.listEmpty())
                    .iter()
                    .filter(node -> !node.is("whitespace"))
                    .map(Main::stringify)
                    .collect(new Joiner("_"))
                    .orElse("");

            return base + "_" + typeParams;
        }
        else {
            return expansion.findString("value").orElse("");
        }
    }

    private static Option<String> generateStruct(List_<String> typeParams, Node node) {
        String name = node.findString("name").orElse("");
        String body = node.findString("body").orElse("");
        if (!isSymbol(name)) {
            return new None<>();
        }

        String withEnd = body.strip();
        if (!withEnd.endsWith("}")) {
            return new None<>();
        }

        String inputContent = withEnd.substring(0, withEnd.length() - "}".length());
        return parseAllStatements(inputContent, wrapDefaultFunction(input1 -> compileClassMember(input1, typeParams))).map(Main::mergeAllStatements).map(outputContent -> {
            structs.add("typedef struct {\n" + outputContent + "} " +
                    name +
                    ";\n");
            return "";
        });
    }

    private static Option<String> compileClassMember(String input, List_<String> typeParams) {
        return compileWhitespace(input)
                .or(() -> compileToStruct(input, "interface ", typeParams))
                .or(() -> compileToStruct(input, "record ", typeParams))
                .or(() -> compileToStruct(input, "class ", typeParams))
                .or(() -> compileGlobalInitialization(input, typeParams))
                .or(() -> compileDefinitionStatement(input))
                .or(() -> compileMethod(input, typeParams))
                .or(() -> generatePlaceholder(input));
    }

    private static Option<String> compileDefinitionStatement(String input) {
        String stripped = input.strip();
        if (stripped.endsWith(";")) {
            String content = stripped.substring(0, stripped.length() - ";".length());
            return parseDefinition(content).flatMap(Main::generateDefinition).map(result -> "\t" + result + ";\n");
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
        return parseDefinition(definition).flatMap(Main::generateDefinition).flatMap(outputDefinition -> {
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

    private static Option<String> compileMethod(String input, List_<String> typeParams) {
        int paramStart = input.indexOf("(");
        if (paramStart < 0) {
            return new None<>();
        }

        String inputDefinition = input.substring(0, paramStart).strip();
        String withParams = input.substring(paramStart + "(".length());

        return parseDefinition(inputDefinition).flatMap(outputDefinition -> {
            int paramEnd = withParams.indexOf(")");
            if (paramEnd < 0) {
                return new None<>();
            }

            String params = withParams.substring(0, paramEnd);
            String body = withParams.substring(paramEnd + ")".length()).strip();
            return parseAllValues(params, createParamRule())
                    .flatMap(outputParams -> getStringOption(typeParams, outputDefinition, outputParams, body));
        });
    }

    private static Function<String, Option<Node>> createParamRule() {
        return definition -> parseOr(definition, Impl.listOf(
                wrapDefaultFunction(Main::compileWhitespace),
                Main::parseDefinition
        ));
    }

    private static Option<String> getStringOption(List_<String> typeParams, Node definition, List_<Node> params, String body) {
        List_<Node> paramTypes = params.iter()
                .map(param -> param.findNode("type"))
                .flatMap(Iterators::fromOption)
                .collect(new ListCollector<>());

        String name = definition.findString("name").orElse("");
        Node returns = definition.findNode("type").orElse(new Node());
        Node functionalDefinition = new Node()
                .retype("functional-definition")
                .withString("name", name)
                .withNode("returns", returns)
                .withNodeList("params", paramTypes);

        return generateDefinition(definition).and(() -> generateDefinition(functionalDefinition)).flatMap(output -> {
            String asContent = output.left;
            String asType = output.right;

            String entry = "\t" + asType + ";\n";

            if (!body.startsWith("{") || !body.endsWith("}")) {
                return new Some<>(entry);
            }

            String inputContent = body.substring("{".length(), body.length() - "}".length());
            return parseAllStatements(inputContent, wrapDefaultFunction(input1 -> compileStatementOrBlock(input1, typeParams, 1))).map(Main::mergeAllStatements).flatMap(outputContent -> {
                methods.add("\t".repeat(0) + asContent + "(" + mergeAllValues(params, Main::unwrapDefault) + ")" + " {" + outputContent + "\n}\n");
                return new Some<>(entry);
            });
        });
    }

    private static Option<List_<Node>> parseAllValues(String input, Function<String, Option<Node>> rule) {
        return parseAll(divide(input, Main::divideValueChar), rule);
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

    private static String mergeAllValues(List_<Node> compiled, Function<Node, String> generator) {
        return generateAll(compiled, generator, Main::mergeValues);
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
                return parseAllStatements(withoutKeyword.substring(1, withoutKeyword.length() - 1), wrapDefaultFunction(statement -> compileStatementOrBlock(statement, typeParams, depth + 1))).map(Main::mergeAllStatements)
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
                return parseAllStatements(content, wrapDefaultFunction(statement -> compileStatementOrBlock(statement, typeParams, depth + 1))).map(Main::mergeAllStatements).map(statements -> {
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
                    return parseType(type, typeParams).map(Main::generateType).flatMap(outputType -> compileArgs(argsString, typeParams, depth).map(value -> outputType + value));
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
                return parseType(type, typeParams).map(Main::generateType).flatMap(compiled -> {
                    return generateLambdaWithReturn(Impl.listEmpty(), "\n\treturn " + compiled + "." + property + "()");
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
            return parseAllStatements(slice, wrapDefaultFunction(statement -> compileStatementOrBlock(statement, typeParams, depth))).map(Main::mergeAllStatements).flatMap(result -> {
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
        return parseAllValues(argsString, wrapDefaultFunction(arg -> {
            return compileWhitespace(arg).or(() -> compileValue(arg, typeParams, depth));
        })).map(compiled -> mergeAllValues(compiled, Main::unwrapDefault)).map(args -> {
            return "(" + args + ")";
        });
    }

    private static StringBuilder mergeValues(StringBuilder cache, String element) {
        if (cache.isEmpty()) {
            return cache.append(element);
        }
        return cache.append(", ").append(element);
    }

    private static Option<Node> parseDefinition(String definition) {
        String stripped = definition.strip();
        int nameSeparator = stripped.lastIndexOf(" ");
        if (nameSeparator < 0) {
            return new None<>();
        }

        String beforeName = stripped.substring(0, nameSeparator).strip();
        String name = stripped.substring(nameSeparator + " ".length()).strip();
        if (!isSymbol(name)) {
            return new None<>();
        }

        Node withName = new Node().withString("name", name);
        return parseDefinitionWithName(beforeName, withName);
    }

    private static Option<Node> parseDefinitionWithName(String beforeName, Node withName) {
        return findTypeSeparator(beforeName).map(typeSeparator -> {
            String beforeType = beforeName.substring(0, typeSeparator).strip();
            String type = beforeName.substring(typeSeparator + " ".length());
            return parseDefinitionWithTypeSeparator(withName, beforeType, type);
        }).orElseGet(() -> parseDefinitionTypeProperty(withName, beforeName, Impl.listEmpty()));
    }

    private static Option<Node> parseDefinitionWithTypeSeparator(Node withName, String beforeType, String type) {
        if (!beforeType.endsWith(">")) {
            return parseDefinitionWithNoTypeParams(withName, beforeType, type);
        }

        String withoutEnd = beforeType.substring(0, beforeType.length() - ">".length());
        int typeParamStart = withoutEnd.indexOf("<");
        if (typeParamStart < 0) {
            return parseDefinitionWithNoTypeParams(withName, beforeType, type);
        }

        String beforeTypeParams = withoutEnd.substring(0, typeParamStart);
        String substring = withoutEnd.substring(typeParamStart + 1);

        List_<String> typeParamsStrings = splitValues(substring);
        List_<Node> typeParamsNodes = typeParamsStrings.iter()
                .map(Main::wrapDefault)
                .collect(new ListCollector<>());

        boolean hasValidBeforeParams = validateLeft(beforeTypeParams);
        if (!hasValidBeforeParams) {
            return new None<>();
        }

        return parseDefinitionTypeProperty(withName, type, typeParamsStrings)
                .map(node -> node.withNodeList("type-params", typeParamsNodes));
    }

    private static Option<Node> parseDefinitionTypeProperty(Node withName, String type, List_<String> typeParams) {
        return parseType(type, typeParams)
                .map(outputType -> withName.withNode("type", outputType));
    }

    private static Option<Node> parseDefinitionWithNoTypeParams(Node withName, String beforeType, String type) {
        boolean hasValidBeforeParams = validateLeft(beforeType);
        List_<Node> typeParamsList = Impl.listEmpty();
        if (!hasValidBeforeParams) {
            return new None<>();
        }

        return parseDefinitionTypeProperty(withName, type, Impl.listEmpty()).map(node -> node.withNodeList("type-params", typeParamsList));
    }

    private static boolean validateLeft(String beforeTypeParams) {
        String strippedBeforeTypeParams = beforeTypeParams.strip();

        String modifiersString;
        int annotationSeparator = strippedBeforeTypeParams.lastIndexOf("\n");
        if (annotationSeparator >= 0) {
            modifiersString = strippedBeforeTypeParams.substring(annotationSeparator + "\n".length());
        }
        else {
            modifiersString = strippedBeforeTypeParams;
        }

        return splitByDelimiter(modifiersString, ' ')
                .iter()
                .map(String::strip)
                .filter(value -> !value.isEmpty())
                .allMatch(Main::isSymbol);
    }

    private static Option<String> generateDefinition(Node node) {
        if (node.is("functional-definition")) {
            String name = node.findString("name").orElse("");
            String returns = generateType(node.findNode("returns").orElse(new Node()));

            String params = node.findNodeList("params")
                    .orElseGet(Impl::listEmpty)
                    .iter()
                    .map(Main::generateType)
                    .collect(new Joiner(", "))
                    .orElse("");

            return new Some<>(returns + " (*" + name + ")(" + params + ")");
        }

        String typeParamsString = node.findNodeList("type-params")
                .orElseGet(Impl::listEmpty)
                .iter()
                .map(Main::unwrapDefault)
                .collect(new Joiner(", "))
                .map(inner -> "<" + inner + "> ")
                .orElse("");

        String type = node.findNode("type")
                .map(Main::generateType)
                .orElse("");

        String name = node.findString("name").orElse("name");
        return new Some<>(typeParamsString + type + " " + name);
    }

    private static String unwrapDefault(Node value) {
        return value.findString("value").orElse("");
    }

    private static Node wrapDefault(String typeParam) {
        return new Node().withString("value", typeParam);
    }

    private static Option<Integer> findTypeSeparator(String beforeName) {
        int depth = 0;
        int index = beforeName.length() - 1;
        while (index >= 0) {
            char c = beforeName.charAt(index);
            if (c == ' ' && depth == 0) {
                return new Some<>(index);
            }
            else {
                if (c == '>') {
                    depth++;
                }
                if (c == '<') {
                    depth--;
                }
            }
            index--;
        }
        return new None<>();
    }

    private static List_<String> splitValues(String substring) {
        return splitByDelimiter(substring.strip(), ',')
                .iter()
                .map(String::strip)
                .filter(param -> !param.isEmpty())
                .collect(new ListCollector<>());
    }

    private static String generateType(Node node) {
        if (node.is("generic")) {
            if (!Lists.contains(expansions, node, Node::equalsTo)) {
                List_<Node> params = node.findNodeList("type-params")
                        .orElse(Impl.listEmpty())
                        .iter()
                        .filter(param -> !param.is("whitespace"))
                        .collect(new ListCollector<>());

                if (!params.isEmpty()) {
                    expansions = expansions.add(node);
                }
            }
            return generateGeneric(node);
        }

        return unwrapDefault(node);
    }

    private static Option<Node> parseType(String input, List_<String> typeParams) {
        return parseOr(input, listTypeRules(typeParams));
    }

    private static Option<Node> parseOr(String input, List_<Function<String, Option<Node>>> rules) {
        return rules.iter()
                .map(function -> function.apply(input))
                .flatMap(Iterators::fromOption)
                .next();
    }

    private static List_<Function<String, Option<Node>>> listTypeRules(List_<String> typeParams) {
        return Impl.listOf(
                wrapDefaultFunction(Main::compilePrimitive),
                wrapDefaultFunction(input -> compileArray(input, typeParams)),
                wrapDefaultFunction(input -> compileSymbol(input, typeParams)),
                parseGeneric(typeParams)
        );
    }

    private static Function<String, Option<Node>> parseGeneric(List_<String> typeParams) {
        return input -> {
            String stripped = input.strip();
            if (!stripped.endsWith(">")) {
                return new None<>();
            }

            String slice = stripped.substring(0, stripped.length() - ">".length());
            int argsStart = slice.indexOf("<");
            if (argsStart < 0) {
                return new None<>();
            }

            String base = slice.substring(0, argsStart).strip();
            String params = slice.substring(argsStart + "<".length()).strip();

            Option<List_<Node>> listOption = parseAllValues(params, inner -> {
                return parseOr(inner, Impl.listOf(
                        parseWithType("whitespace", wrapDefaultFunction(Main::compileWhitespace)),
                        input0 -> parseType(input0, typeParams)
                ));
            });

            return listOption.map(compiled -> {
                return new Node()
                        .retype("generic")
                        .withNodeList("type-params", compiled).withString("base", base);
            });
        };
    }

    private static Function<String, Option<Node>> parseWithType(String type, Function<String, Option<Node>> mapper) {
        return input -> mapper.apply(input).map(value -> value.retype(type));
    }

    private static String generateGeneric(Node node) {
        return "struct " + stringify(node);
    }

    private static Function<String, Option<Node>> wrapDefaultFunction(Function<String, Option<String>> mapper) {
        return input -> mapper.apply(input).map(Main::wrapDefault);
    }

    private static Option<String> compilePrimitive(String input) {
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
    }

    private static Option<String> compileArray(String input, List_<String> typeParams) {
        if (input.endsWith("[]")) {
            return parseType(input.substring(0, input.length() - "[]".length()), typeParams).map(Main::generateType)
                    .map(value -> value + "*");
        }

        return new None<>();
    }

    private static Option<String> compileSymbol(String input, List_<String> typeParams) {
        String stripped = input.strip();
        if (!isSymbol(stripped)) {
            return new None<>();
        }

        if (Lists.contains(typeParams, stripped, String::equals)) {
            return new Some<>(stripped);
        }
        else {
            return new Some<>(stripped);
        }
    }

    private static boolean isSymbol(String input) {
        if (input.isBlank()) {
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
