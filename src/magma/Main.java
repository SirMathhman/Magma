package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
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

    private interface Value extends Caller {
    }

    private interface Caller extends Generating {
    }

    private interface Type extends Generating {
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
            elements.add(element);
            return this;
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
    }

    private record Tuple<L, R>(L left, R right) {
    }

    private static class State {
        private List<String> segments;
        private StringBuilder buffer;
        private int depth;

        public State(List<String> segments, StringBuilder buffer, int depth) {
            this.segments = segments;
            this.buffer = buffer;
            this.depth = depth;
        }

        public State() {
            this(Lists.empty(), new StringBuilder(), 0);
        }

        private State append(char c) {
            buffer.append(c);
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

        private boolean isShallow() {
            return depth == 1;
        }

        private State advance() {
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
    }

    private record Placeholder(String input) implements Parameter, Value, Type {
        @Override
        public String generate() {
            return generatePlaceholder(input);
        }
    }

    private static class Whitespace implements Parameter, Generating {
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

    private record Stack(List<List<Definition>> frames) {
        private Stack() {
            this(Lists.of(Lists.empty()));
        }

        public Option<Type> resolveValue(String name) {
            return frames.iterReversed()
                    .map(frame -> resolveValueWithinFrame(name, frame))
                    .flatMap(Iterators::fromOptional)
                    .next()
                    .map(definition -> definition.type);
        }

        public Stack defineAll(List<Definition> definitions) {
            return new Stack(frames.mapLast(frame -> frame.addAll(definitions)));
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

    private static class TemplateType implements Type {
        private final String base;
        private final List<Type> elements;

        public TemplateType(String base, List<Type> elements) {
            this.base = base;
            this.elements = elements;
        }

        @Override
        public String generate() {
            final var outputArguments = generateNodes(elements);
            return base + "<" + outputArguments + ">";
        }
    }

    private static Option<Definition> resolveValueWithinFrame(String name, List<Definition> frame) {
        return frame.iter()
                .filter(definition -> definition.name.equals(name))
                .next();
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
        return compileStatements(input, Main::compileRootSegment);
    }

    private static String compileStatements(String input, Function<String, String> mapper) {
        return compileAll(input, mapper, Main::foldStatements, Main::mergeStatements);
    }

    private static String compileAll(String input, Function<String, String> mapper, BiFunction<State, Character, State> folder, BiFunction<StringBuilder, String, StringBuilder> merger) {
        return mergeAll(parseAll(input, folder, mapper), merger);
    }

    private static String mergeAll(List<String> elements, BiFunction<StringBuilder, String, StringBuilder> merger) {
        return elements.iter()
                .fold(new StringBuilder(), merger)
                .toString();
    }

    private static <T> List<T> parseAll(String input, BiFunction<State, Character, State> folder, Function<String, T> mapper) {
        return divide(input, folder)
                .iter()
                .map(mapper)
                .collect(new ListCollector<>());
    }

    private static StringBuilder mergeStatements(StringBuilder output, String compiled) {
        return output.append(compiled);
    }

    private static List<String> divide(String input, BiFunction<State, Character, State> folder) {
        State state = new State();
        final var length = input.length();
        var current = state;
        for (var i = 0; i < length; i++) {
            final var c = input.charAt(i);
            current = folder.apply(current, c);
        }

        return current.advance().segments;
    }

    private static State foldStatements(State current, char c) {
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

    private static String compileRootSegment(String input) {
        final var stripped = input.strip();
        if (stripped.startsWith("package ") || stripped.startsWith("import ")) {
            return "";
        }

        return compileRootStructure(input)
                .orElseGet(() -> generatePlaceholder(input));
    }

    private static Option<String> compileRootStructure(String input) {
        return compileStructure(input, "class ", "class").map(tuple -> {
            final var joined = join(tuple.right);
            return tuple.left + joined;
        });
    }

    private static String join(List<String> list) {
        return list.iter()
                .collect(new Joiner())
                .orElse("");
    }

    private static Option<Tuple<String, List<String>>> compileStructure(String input, String keyword, String targetInfix) {
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
        final var folded = divide(inputContent, Main::foldStatements)
                .iter()
                .map(Main::compileClassSegment)
                .fold(new Tuple<>(new StringBuilder(), Lists.<String>empty()), (tuple, element) -> new Tuple<>(tuple.left.append(element.left), tuple.right.addAll(element.right)));

        final var output = folded.left.toString();
        final var structures = folded.right;

        final var modifiers = modifiersString.contains("public") ? "export " : "";

        final var generated = assembleStructureWithImplements(targetInfix, beforeContent, modifiers, output);
        return new Some<>(new Tuple<>("", structures.add(generated)));
    }

    private static String assembleStructureWithImplements(String targetInfix, String beforeContent, String modifiers, String output) {
        final var implementsIndex = beforeContent.lastIndexOf(" implements ");
        if (implementsIndex >= 0) {
            final var beforeImplements = beforeContent.substring(0, implementsIndex);
            final var implementsString = beforeContent.substring(implementsIndex + " implements ".length());
            final var implementsTypes = parseValuesString(implementsString, Main::parseType);

            return assembleStructureWithParameters(beforeImplements, modifiers, output, targetInfix, implementsTypes);
        }

        return assembleStructureWithParameters(beforeContent, modifiers, output, targetInfix, Lists.empty());
    }

    private static String assembleStructureWithParameters(String beforeContent, String modifiers, String outputContent, String targetInfix, List<Type> implementsTypes) {
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

                final var output = fields.iter()
                        .map(Definition::generate)
                        .fold(new StringBuilder(), Main::mergeValues);

                final var outputParams = output.toString();
                final var generatedFields = fields.iter()
                        .map(Definition::generate)
                        .map(element -> "\n\t" + element + ";")
                        .collect(new Joiner())
                        .orElse("");

                final var assignments = fields.iter()
                        .map(field -> {
                            final var fieldName = field.name;
                            final var content = "this." + fieldName + " = " + fieldName;
                            return generateStatement(content);
                        })
                        .collect(new Joiner())
                        .orElse("");

                return generateClass(modifiers, name, generatedFields + "\n\tconstructor (" + outputParams + ") {" +
                        assignments +
                        "\n\t}" + outputContent, targetInfix, implementsTypes);
            }
        }

        return generateClass(modifiers, beforeContent, outputContent, targetInfix, implementsTypes);
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

    private static String generateClass(String modifiers, String beforeContent, String outputContent, String targetInfix, List<Type> implementsTypes) {
        final var joinedImplements = implementsTypes.isEmpty() ? "" : " implements " + generateNodes(implementsTypes);
        return modifiers + targetInfix + " " + beforeContent + joinedImplements + " {" + outputContent + "\n}\n";
    }

    private static String joinWithDelimiter(List<String> list, String delimiter) {
        return list.iter().collect(new Joiner(delimiter)).orElse("");
    }

    private static Tuple<String, List<String>> compileClassSegment(String input) {
        return compileWhitespaceWithStructures(input)
                .or(() -> compileStructure(input, "record ", "class"))
                .or(() -> compileStructure(input, "class ", "class"))
                .or(() -> compileStructure(input, "interface ", "interface"))
                .or(() -> compileField(input))
                .or(() -> compileMethod(input))
                .orElseGet(() -> new Tuple<>(generatePlaceholder(input), Lists.empty()));
    }

    private static Option<Tuple<String, List<String>>> compileField(String input) {
        final var stripped = input.strip();
        if (!stripped.endsWith(";")) {
            return new None<>();
        }

        final var content = stripped.substring(0, stripped.length() - ";".length());
        return getStringListTuple(content);
    }

    private static Option<Tuple<String, List<String>>> getStringListTuple(String content) {
        return compileSimpleDefinition(content).map(definition -> new Tuple<>("\n\t" + definition + ";", Lists.empty()));
    }

    private static Option<String> compileSimpleDefinition(String content) {
        return parseDefinition(content).map(Definition::generate);
    }

    private static Option<Tuple<String, List<String>>> compileWhitespaceWithStructures(String input) {
        return compileWhitespace(input).map(node -> new Tuple<>(node, Lists.empty()));
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

    private static Option<Tuple<String, List<String>>> compileMethod(String input) {
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

        final var outputParams = generateNodes(parameters);

        if (inputAfterParams.equals(";")) {
            return assembleMethod(outputDefinition, outputParams, ";");
        }

        if (!inputAfterParams.startsWith("{") || !inputAfterParams.endsWith("}")) {
            return new None<>();
        }

        final var content = inputAfterParams.substring(1, inputAfterParams.length() - 1);
        final Stack stack = new Stack().defineAll(parameters);
        final String outputAfterParams = compileStatements(content, input1 -> compileFunctionSegments(input1, stack));
        return assembleMethod(outputDefinition, outputParams, " {" + outputAfterParams + "\n\t}");
    }

    private static Some<Tuple<String, List<String>>> assembleMethod(Definition outputDefinition, String outputParams, String outputAfterParams) {
        final var header = outputDefinition.generateWithAfterName("(" + outputParams + ")");
        final var generated = "\n\t" + header + outputAfterParams;
        return new Some<>(new Tuple<>(generated, Lists.empty()));
    }

    private static String compileFunctionSegments(String input, Stack stack) {
        return compileWhitespace(input)
                .or(() -> compileFunctionStatement(input, stack))
                .orElseGet(() -> generatePlaceholder(input));
    }

    private static Option<String> compileFunctionStatement(String input, Stack stack) {
        final var stripped = input.strip();
        if (!stripped.endsWith(";")) {
            return new None<>();
        }

        final var withoutEnd = stripped.substring(0, stripped.length() - ";".length());
        return compileFunctionStatementValue(withoutEnd, stack).map(value -> "\n\t\t" + value + ";");
    }

    private static Option<String> compileFunctionStatementValue(String withoutEnd, Stack stack) {
        if (withoutEnd.startsWith("return ")) {
            final var value = withoutEnd.substring("return ".length());
            final var generated = parseValue(value, stack);
            return new Some<>("return " + generated.generate());
        }
        else {
            return new None<>();
        }
    }

    private static Value parseValue(String input, Stack stack) {
        final var stripped = input.strip();
        if (stripped.endsWith(")")) {
            final var withoutEnd = stripped.substring(0, stripped.length() - ")".length());
            final var argumentsStart = withoutEnd.indexOf("(");
            if (argumentsStart >= 0) {
                final var callerString = withoutEnd.substring(0, argumentsStart).strip();
                final var argumentsString = withoutEnd.substring(argumentsStart + "(".length());
                final var arguments = parseValuesString(argumentsString, input1 -> parseValue(input1, stack));
                final var caller = mapCaller(stack, callerString);
                return new Invocation(caller, arguments);
            }
        }

        final var separator = stripped.lastIndexOf(".");
        if (separator >= 0) {
            final var parentString = stripped.substring(0, separator);
            final var property = stripped.substring(separator + ".".length());
            final var parent = parseValue(parentString, stack);
            return new FieldAccess(parent, property);
        }

        if (isSymbol(stripped)) {
            return new Symbol(stripped);
        }

        return new Placeholder(input);
    }

    private static Caller mapCaller(Stack stack, String callerString) {
        final var caller = parseCaller(callerString, stack);

        if (caller instanceof FieldAccess access) {
            final var parent = access.parent;
            if (parent instanceof Symbol(String value)) {
                final var maybeType = stack.resolveValue(value);
                if (maybeType.isPresent()) {
                    final var type = maybeType.get();
                    if (type instanceof FunctionType functionType) {
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

    private static Caller parseCaller(String input, Stack stack) {
        final var stripped = input.strip();
        if (stripped.startsWith("new ")) {
            final var afterNew = stripped.substring("new ".length());
            final var type = parseType(afterNew);
            return new Construction(type);
        }

        return parseValue(stripped, stack);
    }

    private static StringBuilder mergeValues(StringBuilder cache, String element) {
        if (!cache.isEmpty()) {
            cache.append(", ");
        }
        return cache.append(element);
    }

    private static State foldValues(State state, char c) {
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

    private static State foldTypeSeparator(State state, char c) {
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
                final var elements = parseValuesString(inputArguments, Main::parseType);

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

        if (isSymbol(stripped)) {
            return new Symbol(stripped);
        }

        return new Placeholder(input);
    }

    private static <T> List<T> parseValuesString(String input, Function<String, T> mapper) {
        return parseAll(input, Main::foldValues, mapper);
    }

    private static boolean isSymbol(String input) {
        final var length = input.length();
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