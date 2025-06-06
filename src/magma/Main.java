package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Main {
    private interface Option<T> {
        <R> Option<R> map(Function<T, R> mapper);

        T orElseGet(Supplier<T> other);

        boolean isPresent();

        T get();

        T orElse(T other);

        Option<T> or(Supplier<Option<T>> other);

        boolean isEmpty();

        <R> Option<Tuple<T, R>> and(Supplier<Option<R>> other);
    }

    private interface Parameter {
    }

    private interface Collector<T, C> {
        C createInitial();

        C fold(C current, T element);
    }

    private interface Head<T> {
        Option<T> next();
    }

    private interface Iterator<T> {
        <R> Iterator<R> map(Function<T, R> mapper);

        <R> R fold(R initial, BiFunction<R, T, R> folder);

        <C> C collect(Collector<T, C> collector);

        <R> Iterator<R> flatMap(Function<T, Iterator<R>> mapper);

        Option<T> next();

        Iterator<T> filter(Predicate<T> predicate);

        <R> Iterator<Tuple<T, R>> zip(Iterator<R> other);
    }

    private interface List<T> {
        List<T> add(T element);

        Iterator<T> iter();

        List<T> addAll(List<T> elements);

        Option<Tuple<List<T>, T>> popLast();

        boolean isEmpty();

        T get(int index);

        Iterator<Tuple<Integer, T>> iterWithIndex();

        Iterator<T> iterReversed();

        List<T> mapLast(Function<T, T> mapper);
    }

    private interface Generating {
        String generate();
    }

    private interface Value extends Caller, ValueArgument {
    }

    private interface Caller extends Generating {
    }

    private interface Map<K, V> {
        Map<K, V> putAll(Map<K, V> other);

        Iterator<Tuple<K, V>> iter();

        Map<K, V> putTuple(Tuple<K, V> kvTuple);

        Map<K, V> put(K key, V value);

        Option<V> get(K key);
    }

    private interface Type extends Generating, TypeArgument {
        default Map<String, Type> extract(Type actual) {
            return Maps.empty();
        }

        default Type resolve(Map<String, Type> resolved) {
            return this;
        }
    }

    private interface ValueArgument {
    }

    private interface TypeArgument {
    }

    private record Some<T>(T value) implements Option<T> {
        @Override
        public <R> Option<R> map(Function<T, R> mapper) {
            return new Some<>(mapper.apply(value));
        }

        @Override
        public T orElseGet(Supplier<T> other) {
            return value;
        }

        @Override
        public boolean isPresent() {
            return true;
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public T orElse(T other) {
            return value;
        }

        @Override
        public Option<T> or(Supplier<Option<T>> other) {
            return this;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public <R> Option<Tuple<T, R>> and(Supplier<Option<R>> other) {
            return other.get().map(otherValue -> new Tuple<>(value, otherValue));
        }
    }

    private static final class None<T> implements Option<T> {
        @Override
        public <R> Option<R> map(Function<T, R> mapper) {
            return new None<>();
        }

        @Override
        public T orElseGet(Supplier<T> other) {
            return other.get();
        }

        @Override
        public boolean isPresent() {
            return false;
        }

        @Override
        public T get() {
            return null;
        }

        @Override
        public T orElse(T other) {
            return other;
        }

        @Override
        public Option<T> or(Supplier<Option<T>> other) {
            return other.get();
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public <R> Option<Tuple<T, R>> and(Supplier<Option<R>> other) {
            return new None<>();
        }
    }

    private static final class Lists {
        public static <T> List<T> empty() {
            return new JavaList<>();
        }

        @SafeVarargs
        public static <T> List<T> of(T... elements) {
            return new JavaList<>(new ArrayList<>(Arrays.asList(elements)));
        }
    }

    private static class Iterators {
        public static <T> Iterator<T> fromOptional(Option<T> option) {
            return new HeadedIterator<>(option
                    .<Head<T>>map(SingleHead::new)
                    .orElseGet(EmptyHead::new));
        }

    }

    private static class RangeHead implements Head<Integer> {
        private final int length;
        private int counter = 0;

        public RangeHead(int length) {
            this.length = length;
        }

        @Override
        public Option<Integer> next() {
            if (counter >= length) {
                return new None<>();
            }

            final var value = counter;
            counter++;
            return new Some<>(value);
        }
    }

    private record JavaList<T>(java.util.List<T> elements) implements List<T> {
        public JavaList() {
            this(new ArrayList<>());
        }

        @Override
        public List<T> add(T element) {
            final var copy = new ArrayList<T>(elements);
            copy.add(element);
            return new JavaList<>(copy);
        }

        @Override
        public Iterator<T> iter() {
            return createIteratorFromSize().map(elements::get);
        }

        private Iterator<Integer> createIteratorFromSize() {
            return new HeadedIterator<>(new RangeHead(elements.size()));
        }

        @Override
        public List<T> addAll(List<T> elements) {
            return elements.iter().<List<T>>fold(this, List::add);
        }

        @Override
        public Option<Tuple<List<T>, T>> popLast() {
            if (elements.isEmpty()) {
                return new None<>();
            }

            final var last = elements.removeLast();
            return new Some<>(new Tuple<>(this, last));
        }

        @Override
        public boolean isEmpty() {
            return elements.isEmpty();
        }

        @Override
        public T get(int index) {
            return elements.get(index);
        }

        @Override
        public Iterator<Tuple<Integer, T>> iterWithIndex() {
            return createIteratorFromSize().map(index -> new Tuple<>(index, elements.get(index)));
        }

        @Override
        public Iterator<T> iterReversed() {
            return createIteratorFromSize()
                    .map(index -> elements.size() - index - 1)
                    .map(elements::get);
        }

        @Override
        public List<T> mapLast(Function<T, T> mapper) {
            if (elements.isEmpty()) {
                return this;
            }

            final var mapped = mapper.apply(elements.getLast());
            elements.set(elements.size() - 1, mapped);
            return this;
        }

    }

    private static class EmptyHead<T> implements Head<T> {
        @Override
        public Option<T> next() {
            return new None<>();
        }
    }

    private static class SingleHead<T> implements Head<T> {
        private final T element;
        private boolean retrieved = false;

        public SingleHead(T element) {
            this.element = element;
        }

        @Override
        public Option<T> next() {
            if (retrieved) {
                return new None<>();
            }

            retrieved = true;
            return new Some<>(element);
        }
    }

    private static class FlatMapHead<T, R> implements Head<R> {
        private final Head<T> head;
        private final Function<T, Iterator<R>> mapper;
        private Iterator<R> current;

        public FlatMapHead(Iterator<R> initial, Head<T> head, Function<T, Iterator<R>> mapper) {
            this.current = initial;
            this.head = head;
            this.mapper = mapper;
        }

        @Override
        public Option<R> next() {
            while (true) {
                final var maybeNext = current.next();
                if (maybeNext.isPresent()) {
                    return maybeNext;
                }

                final var maybeNextIter = head.next().map(mapper);
                if (maybeNextIter.isPresent()) {
                    current = maybeNextIter.get();
                }
                else {
                    return new None<>();
                }
            }
        }
    }

    private record HeadedIterator<T>(Head<T> head) implements Iterator<T> {
        @Override
        public <R> Iterator<R> map(Function<T, R> mapper) {
            return new HeadedIterator<>(() -> head.next().map(mapper));
        }

        @Override
        public <R> R fold(R initial, BiFunction<R, T, R> folder) {
            var current = initial;
            while (true) {
                R finalCurrent = current;
                final var maybeNext = head.next().map(next -> folder.apply(finalCurrent, next));
                if (maybeNext.isPresent()) {
                    current = maybeNext.get();
                }
                else {
                    return current;
                }
            }
        }

        @Override
        public <C> C collect(Collector<T, C> collector) {
            return fold(collector.createInitial(), collector::fold);
        }

        @Override
        public <R> Iterator<R> flatMap(Function<T, Iterator<R>> mapper) {
            final var head = this.head.next()
                    .map(mapper)
                    .<Head<R>>map(initial -> new FlatMapHead<>(initial, this.head, mapper))
                    .orElseGet(EmptyHead::new);

            return new HeadedIterator<>(head);
        }

        @Override
        public Option<T> next() {
            return head.next();
        }

        @Override
        public Iterator<T> filter(Predicate<T> predicate) {
            return flatMap(element -> {
                final var isValid = predicate.test(element);
                final var head = isValid ? new SingleHead<>(element) : new EmptyHead<T>();
                return new HeadedIterator<>(head);
            });
        }

        @Override
        public <R> Iterator<Tuple<T, R>> zip(Iterator<R> other) {
            return new HeadedIterator<>(() -> head.next().and(() -> other.next()));
        }
    }

    private record Tuple<L, R>(L left, R right) {
    }

    private static class DivideState {
        private List<String> segments;
        private StringBuilder buffer;
        private int depth;

        public DivideState(List<String> segments, StringBuilder buffer, int depth) {
            this.segments = segments;
            this.buffer = buffer;
            this.depth = depth;
        }

        public DivideState() {
            this(Lists.empty(), new StringBuilder(), 0);
        }

        private DivideState append(char c) {
            buffer.append(c);
            return this;
        }

        private DivideState enter() {
            this.depth = depth + 1;
            return this;
        }

        private DivideState exit() {
            this.depth = depth - 1;
            return this;
        }

        private boolean isShallow() {
            return depth == 1;
        }

        private DivideState advance() {
            segments = segments.add(buffer.toString());
            this.buffer = new StringBuilder();
            return this;
        }

        private boolean isLevel() {
            return depth == 0;
        }
    }

    private record Definition(
            Option<String> beforeType,
            List<String> typeParams,
            Type type,
            String name
    ) implements Parameter, Generating {
        public Definition(Type type, String name) {
            this(new None<>(), Lists.empty(), type, name);
        }

        @Override
        public String generate() {
            return generateWithAfterName("");
        }

        public String generateWithAfterName(String afterName) {
            final var joinedTypeParams = typeParams.iter()
                    .collect(new Joiner(", "))
                    .map(value -> "<" + value + ">")
                    .orElse("");

            final var beforeType = this.beforeType.map(inner -> inner + " ").orElse("");
            return beforeType + name + joinedTypeParams + afterName + ": " + type.generate();
        }

        public Definition mapType(Function<Type, Type> mapper) {
            return new Definition(beforeType, typeParams, mapper.apply(type), name);
        }
    }

    private record Placeholder(String input) implements Parameter, Value, Type {
        @Override
        public String generate() {
            return generatePlaceholder(input);
        }
    }

    private static class Whitespace implements Parameter, Generating, ValueArgument, TypeArgument {
        @Override
        public String generate() {
            return "";
        }
    }

    private static class Joiner implements Collector<String, Option<String>> {
        private final String delimiter;

        public Joiner() {
            this("");
        }

        public Joiner(String delimiter) {
            this.delimiter = delimiter;
        }

        @Override
        public Option<String> createInitial() {
            return new None<>();
        }

        @Override
        public Option<String> fold(Option<String> current, String element) {
            return new Some<>(current.map(inner -> inner + delimiter + element).orElse(element));
        }
    }

    private static class ListCollector<T> implements Collector<T, List<T>> {
        @Override
        public List<T> createInitial() {
            return Lists.empty();
        }

        @Override
        public List<T> fold(List<T> current, T element) {
            return current.add(element);
        }
    }

    private record Construction(Type type) implements Caller {
        @Override
        public String generate() {
            return "new " + type.generate();
        }
    }

    private record Invocation(Caller caller, List<Value> arguments) implements Value {
        @Override
        public String generate() {
            return caller.generate() + "(" + generateNodes(arguments) + ")";
        }
    }

    private record FieldAccess(Value parent, String property) implements Value {
        @Override
        public String generate() {
            return parent.generate() + "." + property;
        }
    }

    private record Symbol(String input) implements Value, Type {
        @Override
        public String generate() {
            return input;
        }

    }

    private record StructureType(String name, Map<String, Definition> definitions) implements Type {
        @Override
        public String generate() {
            return "?";
        }

        public boolean isNamed(String name) {
            return this.name.equals(name);
        }

        public Option<Definition> findField(String name) {
            return definitions.get(name);
        }
    }

    private record Frame(List<Definition> definitions, List<StructureType> structureTypes) {
        public Frame() {
            this(Lists.empty(), Lists.empty());
        }

        private Option<Definition> resolveValue(String name) {
            return definitions.iter()
                    .filter(definition -> definition.name.equals(name))
                    .next();
        }

        public Frame defineAllValues(List<Definition> definitions) {
            return new Frame(this.definitions.addAll(definitions), structureTypes);
        }

        public Option<StructureType> resolveType(String name) {
            return structureTypes.iter()
                    .filter(type -> type.isNamed(name))
                    .next();
        }

        public Frame defineStructureType(StructureType structureType) {
            return new Frame(definitions, structureTypes.add(structureType));
        }

        public Frame defineValue(Definition definition) {
            return new Frame(definitions.add(definition), structureTypes);
        }
    }

    private static final class CompileState {
        private final Stack stack;
        public List<String> structures;

        private CompileState(Stack stack, List<String> structures) {
            this.stack = stack;
            this.structures = structures;
        }

        private CompileState() {
            this(new Stack(Lists.of(new Frame())), Lists.empty());
        }

        public CompileState defineAll(List<Definition> definitions) {
            return mapLast(frame -> frame.defineAllValues(definitions));
        }

        public CompileState defineStructureType(StructureType structureType) {
            return mapLast(last -> last.defineStructureType(structureType));
        }

        private CompileState mapLast(Function<Frame, Frame> mapper) {
            return new CompileState(new Stack(stack.frames().mapLast(mapper)), structures);
        }

        public CompileState addStructure(String structure) {
            return new CompileState(stack, structures.add(structure));
        }

        public CompileState defineValue(Definition definition) {
            return new CompileState(stack.define(definition), structures);
        }

        public CompileState enter() {
            return new CompileState(stack.enter(), structures);
        }

        public Option<Tuple<CompileState, List<Definition>>> exit() {
            return stack.exit().map(stack -> {
                return new Tuple<>(new CompileState(stack.left, structures), stack.right);
            });
        }
    }

    private static class StringType implements Type {
        @Override
        public String generate() {
            return "string";
        }
    }

    private static class FunctionType implements Type {
        private final List<Type> parameterTypes;
        private final Type returnType;

        public FunctionType(List<Type> parameterTypes, Type returnType) {
            this.parameterTypes = parameterTypes;
            this.returnType = returnType;
        }

        @Override
        public String generate() {
            final var parameters = parameterTypes.iterWithIndex()
                    .map(entry -> "param" + entry.left + " : " + entry.right.generate())
                    .collect(new Joiner(", "))
                    .orElse("");

            return "(" + parameters + ") => " + returnType.generate();
        }
    }

    private record TemplateType(String base, List<Type> arguments) implements Type {
        @Override
        public String generate() {
            final var outputArguments = generateNodes(arguments);
            return base + "<" + outputArguments + ">";
        }
    }

    private record JavaMap<K, V>(java.util.Map<K, V> map) implements Map<K, V> {
        public JavaMap() {
            this(new HashMap<>());
        }

        @Override
        public Map<K, V> putAll(Map<K, V> other) {
            return other.iter().<Map<K, V>>fold(this, Map::putTuple);
        }

        @Override
        public Iterator<Tuple<K, V>> iter() {
            return new JavaList<>(new ArrayList<>(map.entrySet()))
                    .iter()
                    .map(entry -> new Tuple<>(entry.getKey(), entry.getValue()));
        }

        @Override
        public Map<K, V> putTuple(Tuple<K, V> tuple) {
            map.put(tuple.left, tuple.right);
            return this;
        }

        @Override
        public Map<K, V> put(K key, V value) {
            map.put(key, value);
            return this;
        }

        @Override
        public Option<V> get(K key) {
            if (map.containsKey(key)) {
                return new Some<>(map.get(key));
            }
            return new None<>();
        }
    }

    private static class Maps {
        public static <K, V> Map<K, V> empty() {
            return new JavaMap<>();
        }
    }

    private record Stack(List<Frame> frames) {
        public Option<StructureType> resolveType(String name) {
            return frames().iterReversed()
                    .map(frame -> frame.resolveType(name))
                    .flatMap(Iterators::fromOptional)
                    .next();
        }

        public Option<Type> resolveValue(String name) {
            return frames().iterReversed()
                    .map(frame -> frame.resolveValue(name))
                    .flatMap(Iterators::fromOptional)
                    .next()
                    .map(definition -> definition.type);
        }

        public Stack define(Definition definition) {
            return new Stack(frames.mapLast(last -> last.defineValue(definition)));
        }

        public Stack enter() {
            return new Stack(frames.add(new Frame()));
        }

        public Option<Tuple<Stack, List<Definition>>> exit() {
            return frames.popLast().map(tuple -> {
                return new Tuple<>(new Stack(tuple.left), tuple.right.definitions);
            });
        }
    }

    private static class MapCollector<K, V> implements Collector<Tuple<K, V>, Map<K, V>> {
        @Override
        public Map<K, V> createInitial() {
            return Maps.empty();
        }

        @Override
        public Map<K, V> fold(Map<K, V> current, Tuple<K, V> element) {
            return current.putTuple(element);
        }
    }

    private record StructureRefType(String name) implements Type {
        @Override
        public String generate() {
            return name;
        }
    }

    public static void main(String[] args) {
        try {
            final var source = Paths.get(".", "src", "magma", "Main.java");
            final var target = source.resolveSibling("Main.ts");

            final var input = Files.readString(source);
            final var output = compile(input);
            Files.writeString(target, output);
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static String compile(String input) {
        return compileStatements(input, input1 -> compileRootSegment(input1, new CompileState()));
    }

    private static String compileStatements(String input, Function<String, String> mapper) {
        return compileAll(input, mapper, Main::foldStatements, Main::mergeStatements);
    }

    private static String compileAll(String input, Function<String, String> mapper, BiFunction<DivideState, Character, DivideState> folder, BiFunction<StringBuilder, String, StringBuilder> merger) {
        return mergeAll(parseAll(input, folder, mapper), merger);
    }

    private static String mergeAll(List<String> elements, BiFunction<StringBuilder, String, StringBuilder> merger) {
        return elements.iter()
                .fold(new StringBuilder(), merger)
                .toString();
    }

    private static <T> List<T> parseAll(String input, BiFunction<DivideState, Character, DivideState> folder, Function<String, T> mapper) {
        return divide(input, folder)
                .iter()
                .map(mapper)
                .collect(new ListCollector<>());
    }

    private static StringBuilder mergeStatements(StringBuilder output, String compiled) {
        return output.append(compiled);
    }

    private static List<String> divide(String input, BiFunction<DivideState, Character, DivideState> folder) {
        DivideState state = new DivideState();
        final var length = input.length();
        var current = state;
        for (var i = 0; i < length; i++) {
            final var c = input.charAt(i);
            current = folder.apply(current, c);
        }

        return current.advance().segments;
    }

    private static DivideState foldStatements(DivideState current, char c) {
        final var appended = current.append(c);
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

    private static String compileRootSegment(String input, CompileState state) {
        final var stripped = input.strip();
        if (stripped.startsWith("package ") || stripped.startsWith("import ")) {
            return "";
        }

        return compileRootStructure(input, state)
                .orElseGet(() -> generatePlaceholder(input));
    }

    private static Option<String> compileRootStructure(String input, CompileState state) {
        return compileStructure(input, "class ", "class", state).map(tuple -> {
            final var joined = join(tuple.right.structures);
            return tuple.left + joined;
        });
    }

    private static String join(List<String> list) {
        return list.iter()
                .collect(new Joiner())
                .orElse("");
    }

    private static Option<Tuple<String, CompileState>> compileStructure(String input, String keyword, String targetInfix, CompileState state) {
        final var classIndex = input.indexOf(keyword);
        if (classIndex < 0) {
            return new None<>();
        }

        final var modifiersString = input.substring(0, classIndex);
        final var afterClass = input.substring(classIndex + keyword.length());
        final var contentStart = afterClass.indexOf("{");
        if (contentStart < 0) {
            return new None<>();
        }

        final var beforeContent = afterClass.substring(0, contentStart).strip();
        final var withEnd = afterClass.substring(contentStart + "{".length()).strip();
        if (!withEnd.endsWith("}")) {
            return new None<>();
        }

        final var inputContent = withEnd.substring(0, withEnd.length() - "}".length());
        final var modifiers = modifiersString.contains("public") ? "export " : "";

        return assembleStructureWithImplements(targetInfix, state, inputContent, beforeContent, modifiers);
    }

    private static Option<Tuple<String, CompileState>> assembleStructureWithImplements(String targetInfix, CompileState state, String inputContent, String beforeContent, String modifiers) {
        final var implementsIndex = beforeContent.lastIndexOf(" implements ");
        if (implementsIndex >= 0) {
            final var beforeImplements = beforeContent.substring(0, implementsIndex);
            final var implementsString = beforeContent.substring(implementsIndex + " implements ".length());
            final var implementsTypes = parseValuesString(implementsString, Main::parseType);

            return assembleStructureWithParameters(targetInfix, state, inputContent, modifiers, beforeImplements, implementsTypes);
        }
        else {
            return assembleStructureWithParameters(targetInfix, state, inputContent, modifiers, beforeContent, Lists.empty());
        }
    }

    private static Option<Tuple<String, CompileState>> assembleStructureWithParameters(String targetInfix, CompileState state, String inputContent, String modifiers, String beforeContent, List<Type> implementsTypes) {
        if (beforeContent.endsWith(")")) {
            final var withoutParamEnd = beforeContent.substring(0, beforeContent.length() - ")".length());
            final var paramStart = withoutParamEnd.indexOf("(");
            if (paramStart >= 0) {
                final var name = withoutParamEnd.substring(0, paramStart).strip();
                final var inputParams = withoutParamEnd.substring(paramStart + "(".length());
                final var fields = divide(inputParams, Main::foldValues)
                        .iter()
                        .map(Main::parseParameter)
                        .map(Main::retainDefinition)
                        .flatMap(Iterators::fromOptional)
                        .collect(new ListCollector<>());

                if (!fields.isEmpty()) {
                    return getTupleOption(targetInfix, state, inputContent, modifiers, implementsTypes, new Some<>(fields), name);
                }
            }
        }

        return getTupleOption(targetInfix, state, inputContent, modifiers, implementsTypes, new None<>(), beforeContent);
    }

    private static Option<Tuple<String, CompileState>> getTupleOption(
            String targetInfix,
            CompileState state,
            String inputContent,
            String modifiers,
            List<Type> implementsTypes,
            Option<List<Definition>> maybeFields,
            String name) {
        final String beforeBody;
        if (maybeFields.isPresent()) {
            final var fields = maybeFields.get();
            final var output1 = fields.iter()
                    .map(Definition::generate)
                    .fold(new StringBuilder(), Main::mergeValues);

            final var outputParams = output1.toString();
            final var generatedFields = fields.iter()
                    .map(Definition::generate)
                    .map(element -> "\n\t" + element + ";")
                    .collect(new Joiner())
                    .orElse("");

            final var assignments = joinConstructorAssignments(fields);

            beforeBody = generatedFields + "\n\tconstructor (" + outputParams + ") {" + assignments + "\n\t}";
        }
        else {
            beforeBody = "";
        }

        final var stripped = name.strip();
        if (stripped.endsWith(">")) {
            final var typeParamsStart = stripped.indexOf("<");
            if (typeParamsStart >= 0) {
                final var name1 = stripped.substring(0, typeParamsStart);
                return assembleStructure(targetInfix, state, inputContent, modifiers, implementsTypes, name1, beforeBody, maybeFields);
            }
        }

        return assembleStructure(targetInfix, state, inputContent, modifiers, implementsTypes, name, beforeBody, maybeFields);
    }

    private static Option<Tuple<String, CompileState>> assembleStructure(String targetInfix, CompileState state, String inputContent, String modifiers, List<Type> implementsTypes, String name, String beforeBody, Option<List<Definition>> maybeFields) {
        final var strippedName = name.strip();
        if (!isSymbol(strippedName)) {
            return new None<>();
        }

        final var classSegmentsTuple = joinClassSegments(inputContent, state.enter());
        final var classSegmentsOutput = classSegmentsTuple.left.toString();
        final var classSegmentsState = classSegmentsTuple.right;

        final var maybeExited = classSegmentsState.exit();
        if (maybeExited.isPresent()) {
            final var exited = maybeExited.get();
            final var right = exited.right
                    .iter()
                    .map(definition -> new Tuple<>(definition.name, definition))
                    .collect(new MapCollector<>());

            final var maybeWithConstructorType = maybeFields.map(fields -> {
                final var constructorTypes = fields.iter()
                        .map(Definition::type)
                        .collect(new ListCollector<>());

                return right.put("new", new Definition(new FunctionType(constructorTypes, new StructureRefType(name)), "new"));
            }).orElse(right);

            final var structureType = new StructureType(strippedName, maybeWithConstructorType);
            final var defined = exited.left.defineStructureType(structureType);
            final var outputContent = beforeBody + classSegmentsOutput;
            final var joinedImplements = implementsTypes.isEmpty() ? "" : " implements " + generateNodes(implementsTypes);
            var generated = modifiers + targetInfix + " " + strippedName + joinedImplements + " {" + outputContent + "\n}\n";

            return new Some<>(new Tuple<>("", defined.addStructure(generated)));
        }
        else {
            return new None<>();
        }
    }

    private static String joinConstructorAssignments(List<Definition> fields) {
        return fields.iter()
                .map(field -> {
                    final var fieldName = field.name;
                    final var content = "this." + fieldName + " = " + fieldName;
                    return generateStatement(content);
                })
                .collect(new Joiner())
                .orElse("");
    }

    private static Tuple<StringBuilder, CompileState> joinClassSegments(String inputContent, CompileState state) {
        return divide(inputContent, Main::foldStatements)
                .iter()
                .fold(new Tuple<>(new StringBuilder(), state), (tuple, element) -> {
                    final var compiled = compileClassSegment(element, tuple.right);
                    final var append = tuple.left.append(compiled.left);
                    return new Tuple<>(append, compiled.right);
                });
    }

    private static Option<Definition> retainDefinition(Parameter parameter) {
        if (parameter instanceof Definition definition) {
            return new Some<>(definition);
        }
        else {
            return new None<>();
        }
    }

    private static String generateStatement(String content) {
        return "\n" + "\t".repeat(2) + content + ";";
    }

    private static String joinWithDelimiter(List<String> list, String delimiter) {
        return list.iter().collect(new Joiner(delimiter)).orElse("");
    }

    private static Tuple<String, CompileState> compileClassSegment(String input, CompileState state) {
        return compileWhitespaceWithState(input, state)
                .or(() -> compileStructure(input, "record ", "class", state))
                .or(() -> compileStructure(input, "class ", "class", state))
                .or(() -> compileStructure(input, "interface ", "interface", state))
                .or(() -> compileField(input, state))
                .or(() -> compileMethod(input, state))
                .orElseGet(() -> new Tuple<>(generatePlaceholder(input), state));
    }

    private static Option<Tuple<String, CompileState>> compileField(String input, CompileState state) {
        final var stripped = input.strip();
        if (!stripped.endsWith(";")) {
            return new None<>();
        }

        final var content = stripped.substring(0, stripped.length() - ";".length());
        return compileSimpleDefinition(content).map(definition -> new Tuple<String, CompileState>("\n\t" + definition + ";", state));
    }

    private static Option<String> compileSimpleDefinition(String content) {
        return parseDefinition(content).map(Definition::generate);
    }

    private static Option<Tuple<String, CompileState>> compileWhitespaceWithState(String input, CompileState state) {
        return compileWhitespace(input).map(node -> new Tuple<>(node, state));
    }

    private static Option<String> compileWhitespace(String input) {
        return parseWhitespace(input).map(Whitespace::generate);
    }

    private static Option<Whitespace> parseWhitespace(String input) {
        if (input.isBlank()) {
            return new Some<>(new Whitespace());
        }
        else {
            return new None<>();
        }
    }

    private static Option<Tuple<String, CompileState>> compileMethod(String input, CompileState state) {
        final var paramStart = input.indexOf("(");
        if (paramStart < 0) {
            return new None<>();
        }

        final var inputDefinition = input.substring(0, paramStart);
        final var withParams = input.substring(paramStart + "(".length());
        final var paramEnd = withParams.indexOf(")");
        if (paramEnd < 0) {
            return new None<>();
        }

        final var inputParams = withParams.substring(0, paramEnd);
        final var inputAfterParams = withParams.substring(paramEnd + ")".length()).strip();

        final var maybeOutputDefinition = parseDefinition(inputDefinition);
        if (!maybeOutputDefinition.isPresent()) {
            return new None<>();
        }

        final var outputDefinition = maybeOutputDefinition.get();
        final var parameters = parseAll(inputParams, Main::foldValues, Main::parseParameter)
                .iter()
                .map(Main::retainDefinition)
                .flatMap(Iterators::fromOptional)
                .collect(new ListCollector<>());

        final var paramTypes = parameters.iter()
                .map(Definition::type)
                .collect(new ListCollector<>());

        final var outputParams = generateNodes(parameters);
        if (inputAfterParams.equals(";")) {
            return assembleMethod(outputDefinition, outputParams, ";", state, paramTypes);
        }

        if (!inputAfterParams.startsWith("{") || !inputAfterParams.endsWith("}")) {
            return new None<>();
        }

        final var content = inputAfterParams.substring(1, inputAfterParams.length() - 1);
        final CompileState defined = state.defineAll(parameters);
        final String outputAfterParams = compileStatements(content, input1 -> compileFunctionSegments(input1, defined));
        return assembleMethod(outputDefinition, outputParams, " {" + outputAfterParams + "\n\t}", state, paramTypes);
    }

    private static Some<Tuple<String, CompileState>> assembleMethod(Definition outputDefinition, String outputParams, String outputAfterParams, CompileState state, List<Type> paramTypes) {
        final var header = outputDefinition.generateWithAfterName("(" + outputParams + ")");
        final var generated = "\n\t" + header + outputAfterParams;
        return new Some<>(new Tuple<>(generated, state.defineValue(outputDefinition.mapType(type -> {
            return new FunctionType(paramTypes, type);
        }))));
    }

    private static String compileFunctionSegments(String input, CompileState state) {
        return compileWhitespace(input)
                .or(() -> compileFunctionStatement(input, state))
                .orElseGet(() -> generatePlaceholder(input));
    }

    private static Option<String> compileFunctionStatement(String input, CompileState state) {
        final var stripped = input.strip();
        if (!stripped.endsWith(";")) {
            return new None<>();
        }

        final var withoutEnd = stripped.substring(0, stripped.length() - ";".length());
        return compileFunctionStatementValue(withoutEnd, state).map(value -> "\n\t\t" + value + ";");
    }

    private static Option<String> compileFunctionStatementValue(String withoutEnd, CompileState state) {
        if (withoutEnd.startsWith("return ")) {
            final var value = withoutEnd.substring("return ".length());
            final var generated = parseValue(value, state);
            return new Some<>("return " + generated.generate());
        }
        else {
            return new None<>();
        }
    }

    private static Value parseValue(String input, CompileState state) {
        return parseInvocation(input, state).<Value>map(value -> value)
                .or(() -> parseAccess(input, state).map(value -> value))
                .or(() -> parseSymbol(input).map(value -> value))
                .orElseGet(() -> new Placeholder(input));
    }

    private static Option<FieldAccess> parseAccess(String input, CompileState state) {
        var stripped = input.strip();
        final var separator = stripped.lastIndexOf(".");
        if (separator >= 0) {
            final var parentString = stripped.substring(0, separator);
            final var property = stripped.substring(separator + ".".length());
            final var parent = parseValue(parentString, state);
            return new Some<>(new FieldAccess(parent, property));
        }

        return new None<>();
    }

    private static Option<Invocation> parseInvocation(String input, CompileState state) {
        var stripped = input.strip();
        if (stripped.endsWith(")")) {
            final var withoutEnd = stripped.substring(0, stripped.length() - ")".length());
            final var argumentsStart = withoutEnd.indexOf("(");
            if (argumentsStart >= 0) {
                final var callerString = withoutEnd.substring(0, argumentsStart).strip();
                final var argumentsString = withoutEnd.substring(argumentsStart + "(".length());
                final var arguments = parseValuesString(argumentsString, input1 -> parseArgument(input1, state))
                        .iter()
                        .map(Main::retainValue)
                        .flatMap(Iterators::fromOptional)
                        .collect(new ListCollector<>());

                final var caller = mapCaller(state, callerString);
                return new Some<>(parseAfterInvocation(state, caller, arguments));
            }
        }

        return new None<>();
    }

    private static Invocation parseAfterInvocation(CompileState state, Caller caller, List<Value> arguments) {
        if (caller instanceof Construction(var type)) {
            if (type instanceof TemplateType(String base, List<Type> templateArgumentTypes)) {
                if (templateArgumentTypes.isEmpty()) {
                    final var maybeStructureType = state.stack.resolveType(base);
                    if (maybeStructureType.isPresent()) {
                        final var structureType = maybeStructureType.get();
                        final var maybeConstructorDefinition = structureType.findField("new");
                        if (maybeConstructorDefinition.isPresent()) {
                            final var constructorDefinition = maybeConstructorDefinition.get();
                            final var constructorDefinitionType = constructorDefinition.type;
                            if (constructorDefinitionType instanceof FunctionType functionalConstructorDefinition) {
                                final var constructorArgumentTypes = functionalConstructorDefinition.parameterTypes;

                                final var argumentTypes = arguments.iter()
                                        .map(argument -> resolveValue(argument, state))
                                        .flatMap(Iterators::fromOptional)
                                        .collect(new ListCollector<>());

                                final var resolved = constructorArgumentTypes.iter()
                                        .zip(argumentTypes.iter())
                                        .map(pair -> pair.left.extract(pair.right))
                                        .fold(Maps.<String, Type>empty(), Map::putAll);

                                final var actualArgumentTypes = argumentTypes.iter()
                                        .map(argument -> argument.resolve(resolved))
                                        .collect(new ListCollector<>());

                                final var actualTemplateType = new TemplateType(base, actualArgumentTypes);
                                return new Invocation(new Construction(actualTemplateType), arguments);
                            }
                        }
                    }
                }
            }
        }
        return new Invocation(caller, arguments);
    }

    private static Option<Type> resolveValue(Value argument, CompileState state) {
        return new None<>();
    }

    private static Option<Value> retainValue(ValueArgument argument) {
        if (argument instanceof Value value) {
            return new Some<>(value);
        }
        else {
            return new None<>();
        }
    }

    private static ValueArgument parseArgument(String input, CompileState state) {
        return parseWhitespace(input).<ValueArgument>map(value -> value)
                .orElseGet(() -> parseValue(input, state));
    }

    private static Option<Symbol> parseSymbol(String input) {
        final var stripped = input.strip();
        if (isSymbol(stripped)) {
            return new Some<>(new Symbol(stripped));
        }
        return new None<>();
    }

    private static Caller mapCaller(CompileState state, String callerString) {
        final var caller = parseCaller(callerString, state);

        if (caller instanceof FieldAccess access) {
            final var parent = access.parent;
            if (parent instanceof Symbol(String value)) {
                final var maybeType = state.stack.resolveValue(value);
                if (maybeType.isPresent()) {
                    final var type = maybeType.get();
                    if (type instanceof FunctionType) {
                        return parent;
                    }
                }
            }
        }

        return caller;
    }

    private static <T extends Generating> String generateNodes(List<T> arguments) {
        final var generated = arguments.iter()
                .map(Generating::generate)
                .collect(new ListCollector<>());

        return mergeAll(generated, Main::mergeValues);
    }

    private static Caller parseCaller(String input, CompileState state) {
        final var stripped = input.strip();
        if (stripped.startsWith("new ")) {
            final var afterNew = stripped.substring("new ".length());
            final var type = parseType(afterNew);
            return new Construction(type);
        }

        return parseValue(stripped, state);
    }

    private static StringBuilder mergeValues(StringBuilder cache, String element) {
        if (!cache.isEmpty()) {
            cache.append(", ");
        }
        return cache.append(element);
    }

    private static DivideState foldValues(DivideState state, char c) {
        if (c == ',' && state.isLevel()) {
            return state.advance();
        }

        final var appended = state.append(c);
        if (c == '<') {
            return appended.enter();
        }
        if (c == '>') {
            return appended.exit();
        }
        return appended;
    }

    private static Parameter parseParameter(String input) {
        return parseWhitespace(input).<Parameter>map(parameter -> parameter)
                .or(() -> parseDefinition(input).map(parameter -> parameter))
                .orElseGet(() -> new Placeholder(input));
    }

    private static Option<Definition> parseDefinition(String input) {
        final var stripped = input.strip();
        final var nameSeparator = stripped.lastIndexOf(" ");
        if (nameSeparator < 0) {
            return new None<>();
        }

        final var beforeName = stripped.substring(0, nameSeparator).strip();
        final var name = stripped.substring(nameSeparator + " ".length()).strip();
        if (!isSymbol(name)) {
            return new None<>();
        }

        final var divisions = divide(beforeName, Main::foldTypeSeparator);
        final var maybePopped = divisions.popLast();
        if (maybePopped.isEmpty()) {
            return new Some<>(new Definition(new None<>(), Lists.empty(), parseType(beforeName), name));
        }

        final var popped = maybePopped.get();
        final var beforeTypeDivisions = popped.left;
        final var type = popped.right;
        final var compiledType = parseType(type);

        if (beforeTypeDivisions.isEmpty()) {
            return new Some<>(new Definition(new None<>(), Lists.empty(), parseType(type), name));
        }

        final var beforeType = joinWithDelimiter(beforeTypeDivisions, " ");
        return new Some<>(assembleDefinition(beforeType, compiledType, name));
    }

    private static DivideState foldTypeSeparator(DivideState state, char c) {
        if (c == ' ' && state.isLevel()) {
            return state.advance();
        }

        final var appended = state.append(c);
        if (c == '<') {
            return appended.enter();
        }
        if (c == '>') {
            return appended.exit();
        }
        return appended;
    }

    private static Definition assembleDefinition(String beforeType, Type compiledType, String name) {
        if (beforeType.endsWith(">")) {
            final var withoutEnd = beforeType.substring(0, beforeType.length() - ">".length());
            final var typeParamStart = withoutEnd.indexOf("<");
            if (typeParamStart >= 0) {
                final var beforeTypeParams = withoutEnd.substring(0, typeParamStart);
                final var typeParamsString = withoutEnd.substring(typeParamStart + "<".length());
                final var typeParams = parseValuesString(typeParamsString, String::strip);

                final Option<String> beforeTypeOptional;
                beforeTypeOptional = beforeTypeParams.isEmpty() ? new None<>() : new Some<>(generatePlaceholder(beforeTypeParams));

                return new Definition(beforeTypeOptional, typeParams, compiledType, name);
            }
        }

        return new Definition(new Some<>(generatePlaceholder(beforeType)), Lists.empty(), compiledType, name);
    }

    private static Type parseType(String input) {
        final var stripped = input.strip();
        if (stripped.equals("String")) {
            return new StringType();
        }

        if (stripped.endsWith(">")) {
            final var withoutEnd = stripped.substring(0, stripped.length() - ">".length());
            final var argumentsStart = withoutEnd.indexOf("<");
            if (argumentsStart >= 0) {
                final var base = withoutEnd.substring(0, argumentsStart).strip();
                final var inputArguments = withoutEnd.substring(argumentsStart + 1);
                final var elements = parseValuesString(inputArguments, Main::parseTypeArgument)
                        .iter()
                        .map(Main::retainType)
                        .flatMap(Iterators::fromOptional)
                        .collect(new ListCollector<>());

                if (base.equals("Supplier")) {
                    List<Type> parameterTypes = Lists.empty();
                    return new FunctionType(parameterTypes, elements.get(0));
                }

                if (base.equals("Function")) {
                    List<Type> parameterTypes = Lists.of(elements.get(0));
                    return new FunctionType(parameterTypes, elements.get(1));
                }

                if (base.equals("BiFunction")) {
                    List<Type> parameterTypes = Lists.of(elements.get(0), elements.get(1));
                    return new FunctionType(parameterTypes, elements.get(2));
                }

                return new TemplateType(base, elements);
            }
        }

        return parseSymbol(stripped).<Type>map(value -> value)
                .orElseGet(() -> new Placeholder(input));
    }

    private static Option<Type> retainType(TypeArgument argument) {
        if (argument instanceof Type type) {
            return new Some<>(type);
        }
        else {
            return new None<>();
        }
    }

    private static TypeArgument parseTypeArgument(String input) {
        return parseWhitespace(input)
                .<TypeArgument>map(whitespace -> whitespace)
                .orElseGet(() -> parseType(input));
    }

    private static <T> List<T> parseValuesString(String input, Function<String, T> mapper) {
        return parseAll(input, Main::foldValues, mapper);
    }

    private static boolean isSymbol(String input) {
        final var length = input.length();
        if (length == 0) {
            return false;
        }

        for (var i = 0; i < length; i++) {
            final var c = input.charAt(i);
            if (!Character.isLetter(c)) {
                return false;
            }
        }
        return true;
    }

    private static String generatePlaceholder(String input) {
        final var replaced = input
                .replace("/*", "start")
                .replace("*/", "end");

        return "/*" + replaced + "*/";
    }
}