package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Main {
    private interface Parameter {
        String generate();
    }

    private interface Collector<T, C> {
        C createInitial();

        C fold(C current, T element);
    }

    private interface Head<T> {
        Optional<T> next();
    }

    private interface Iterator<T> {
        <R> Iterator<R> map(Function<T, R> mapper);

        <R> R fold(R initial, BiFunction<R, T, R> folder);

        <C> C collect(Collector<T, C> collector);

        <R> Iterator<R> flatMap(Function<T, Iterator<R>> mapper);

        Optional<T> next();
    }

    private interface List<T> {
        List<T> add(T element);

        Iterator<T> iter();

        List<T> addAll(List<T> elements);

        Optional<Tuple<List<T>, T>> popLast();

        boolean isEmpty();
    }

    private static final class Lists {
        public static <T> List<T> empty() {
            return new JavaList<>();
        }
    }

    private static class Iterators {
        public static <T> Iterator<T> fromOptional(Optional<T> optional) {
            return new HeadedIterator<>(optional
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
        public Optional<Integer> next() {
            if (counter >= length) {
                return Optional.empty();
            }

            final var value = counter;
            counter++;
            return Optional.of(value);
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
            return new HeadedIterator<>(new RangeHead(elements.size())).map(elements::get);
        }

        @Override
        public List<T> addAll(List<T> elements) {
            return elements.iter().<List<T>>fold(this, List::add);
        }

        @Override
        public Optional<Tuple<List<T>, T>> popLast() {
            if (elements.isEmpty()) {
                return Optional.empty();
            }

            final var last = elements.removeLast();
            return Optional.of(new Tuple<>(this, last));
        }

        @Override
        public boolean isEmpty() {
            return elements.isEmpty();
        }
    }

    private static class EmptyHead<T> implements Head<T> {
        @Override
        public Optional<T> next() {
            return Optional.empty();
        }
    }

    private static class SingleHead<T> implements Head<T> {
        private final T element;
        private boolean retrieved = false;

        public SingleHead(T element) {
            this.element = element;
        }

        @Override
        public Optional<T> next() {
            if (retrieved) {
                return Optional.empty();
            }

            retrieved = true;
            return Optional.of(element);
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
        public Optional<R> next() {
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
                    return Optional.empty();
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
        public Optional<T> next() {
            return head.next();
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
            Optional<String> beforeType,
            List<String> typeParams,
            String type,
            String name
    ) implements Parameter {
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
            return beforeType + name + joinedTypeParams + afterName + ": " + type;
        }
    }

    private record Placeholder(String input) implements Parameter {
        @Override
        public String generate() {
            return generatePlaceholder(input);
        }
    }

    private static class Whitespace implements Parameter {
        @Override
        public String generate() {
            return "";
        }
    }

    private static class Joiner implements Collector<String, Optional<String>> {
        private final String delimiter;

        public Joiner() {
            this("");
        }

        public Joiner(String delimiter) {
            this.delimiter = delimiter;
        }

        @Override
        public Optional<String> createInitial() {
            return Optional.empty();
        }

        @Override
        public Optional<String> fold(Optional<String> current, String element) {
            return Optional.of(current.map(inner -> inner + delimiter + element).orElse(element));
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
        return divide(input, folder)
                .iter()
                .map(mapper)
                .fold(new StringBuilder(), merger)
                .toString();
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

    private static Optional<String> compileRootStructure(String input) {
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

    private static Optional<Tuple<String, List<String>>> compileStructure(String input, String keyword, String targetInfix) {
        final var classIndex = input.indexOf(keyword);
        if (classIndex < 0) {
            return Optional.empty();
        }

        final var modifiersString = input.substring(0, classIndex);
        final var afterClass = input.substring(classIndex + keyword.length());
        final var contentStart = afterClass.indexOf("{");
        if (contentStart < 0) {
            return Optional.empty();
        }

        final var beforeContent = afterClass.substring(0, contentStart).strip();
        final var withEnd = afterClass.substring(contentStart + "{".length()).strip();
        if (!withEnd.endsWith("}")) {
            return Optional.empty();
        }

        final var inputContent = withEnd.substring(0, withEnd.length() - "}".length());
        final var folded = divide(inputContent, Main::foldStatements)
                .iter()
                .map(Main::compileClassSegment)
                .fold(new Tuple<>(new StringBuilder(), Lists.<String>empty()), (tuple, element) -> new Tuple<>(tuple.left.append(element.left), tuple.right.addAll(element.right)));

        final var output = folded.left.toString();
        final var structures = folded.right;

        final var modifiers = modifiersString.contains("public") ? "export " : "";

        final var generated = assembleStructure(beforeContent, modifiers, output, targetInfix);
        return Optional.of(new Tuple<>("", structures.add(generated)));
    }

    private static String assembleStructure(String beforeContent, String modifiers, String outputContent, String targetInfix) {
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
                            return generateStatement(content, 2);
                        })
                        .collect(new Joiner())
                        .orElse("");

                return generateClass(modifiers, name, generatedFields + "\n\tconstructor (" + outputParams + ") {" +
                        assignments +
                        "\n\t}" + outputContent, targetInfix);
            }
        }

        return generateClass(modifiers, beforeContent, outputContent, targetInfix);
    }

    private static Optional<Definition> retainDefinition(Parameter parameter) {
        if (parameter instanceof Definition definition) {
            return Optional.of(definition);
        }
        else {
            return Optional.empty();
        }
    }

    private static String generateStatement(String content, int depth) {
        return "\n" + "\t".repeat(depth) + content + ";";
    }

    private static String generateClass(String modifiers, String beforeContent, String outputContent, String targetInfix) {
        return modifiers + targetInfix + " " + beforeContent + " {" + outputContent + "\n}\n";
    }

    private static Tuple<String, List<String>> compileClassSegment(String input) {
        return compileWhitespace(input)
                .or(() -> compileStructure(input, "record ", "class"))
                .or(() -> compileStructure(input, "class ", "class"))
                .or(() -> compileStructure(input, "interface ", "interface"))
                .or(() -> compileField(input))
                .or(() -> compileMethod(input))
                .orElseGet(() -> new Tuple<>(generatePlaceholder(input), Lists.empty()));
    }

    private static Optional<Tuple<String, List<String>>> compileField(String input) {
        final var stripped = input.strip();
        if (!stripped.endsWith(";")) {
            return Optional.empty();
        }

        final var content = stripped.substring(0, stripped.length() - ";".length());
        return getStringListTuple(content);
    }

    private static Optional<Tuple<String, List<String>>> getStringListTuple(String content) {
        return compileSimpleDefinition(content).map(definition -> new Tuple<>("\n\t" + definition + ";", Lists.empty()));
    }

    private static Optional<String> compileSimpleDefinition(String content) {
        return parseDefinition(content).map(Definition::generate);
    }

    private static Optional<Tuple<String, List<String>>> compileWhitespace(String input) {
        return parseWhitespace(input).map(node -> new Tuple<>(node.generate(), Lists.empty()));
    }

    private static Optional<Whitespace> parseWhitespace(String input) {
        if (input.isBlank()) {
            return Optional.of(new Whitespace());
        }
        else {
            return Optional.empty();
        }
    }

    private static Optional<Tuple<String, List<String>>> compileMethod(String input) {
        final var paramStart = input.indexOf("(");
        if (paramStart >= 0) {
            final var inputDefinition = input.substring(0, paramStart);
            final var withParams = input.substring(paramStart + "(".length());
            final var paramEnd = withParams.indexOf(")");
            if (paramEnd >= 0) {
                final var inputParams = withParams.substring(0, paramEnd);
                final var inputAfterParams = withParams.substring(paramEnd + ")".length()).strip();

                final var maybeOutputDefinition = parseDefinition(inputDefinition);
                if (maybeOutputDefinition.isPresent()) {
                    final var outputDefinition = maybeOutputDefinition.get();
                    final var outputParams = compileParameters(inputParams);

                    final var outputAfterParams = inputAfterParams.equals(";")
                            ? ";"
                            : generatePlaceholder(inputAfterParams);

                    final var generated = "\n\t" + outputDefinition.generateWithAfterName("(" + outputParams + ")") + outputAfterParams;
                    return Optional.of(new Tuple<>(generated, Lists.empty()));
                }
            }
        }

        return Optional.empty();
    }

    private static String compileParameters(String input) {
        return compileValues(input, Main::compileParameter);
    }

    private static String compileValues(String input, Function<String, String> mapper) {
        return compileAll(input, mapper, Main::foldValues, Main::mergeValues);
    }

    private static String compileParameter(String input) {
        return parseParameter(input).generate();
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
                .or(() -> parseDefinition(input))
                .orElseGet(() -> new Placeholder(input));
    }

    private static Optional<Definition> parseDefinition(String input) {
        final var stripped = input.strip();
        final var nameSeparator = stripped.lastIndexOf(" ");
        if (nameSeparator < 0) {
            return Optional.empty();
        }

        final var beforeName = stripped.substring(0, nameSeparator).strip();
        final var name = stripped.substring(nameSeparator + " ".length()).strip();
        if (!isSymbol(name)) {
            return Optional.empty();
        }

        final var divisions = divide(beforeName, Main::foldTypeSeparator);
        final var maybePopped = divisions.popLast();
        if (maybePopped.isEmpty()) {
            return Optional.of(new Definition(Optional.empty(), Lists.empty(), compileType(beforeName), name));
        }

        final var popped = maybePopped.get();
        final var beforeTypeDivisions = popped.left;
        final var type = popped.right;
        final var compiledType = compileType(type);

        if (beforeTypeDivisions.isEmpty()) {
            return Optional.of(new Definition(Optional.empty(), Lists.empty(), compileType(type), name));
        }

        final var beforeType = beforeTypeDivisions.iter().collect(new Joiner(" ")).orElse("");
        return Optional.of(assembleDefinition(beforeType, compiledType, name));
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

    private static Definition assembleDefinition(String beforeType, String compiledType, String name) {
        if (beforeType.endsWith(">")) {
            final var withoutEnd = beforeType.substring(0, beforeType.length() - ">".length());
            final var typeParamStart = withoutEnd.indexOf("<");
            if (typeParamStart >= 0) {
                final var beforeTypeParams = withoutEnd.substring(0, typeParamStart);
                final var typeParamsString = withoutEnd.substring(typeParamStart + "<".length());
                final var typeParams = divide(typeParamsString, Main::foldValues)
                        .iter()
                        .map(String::strip)
                        .collect(new ListCollector<>());

                final var beforeTypeOptional = beforeTypeParams.isEmpty()
                        ? Optional.<String>empty()
                        : Optional.of(generatePlaceholder(beforeTypeParams));

                return new Definition(beforeTypeOptional, typeParams, compiledType, name);
            }
        }

        return new Definition(Optional.of(generatePlaceholder(beforeType)), Lists.empty(), compiledType, name);
    }

    private static String compileType(String input) {
        final var stripped = input.strip();
        if (stripped.equals("String")) {
            return "string";
        }

        if (stripped.endsWith(">")) {
            final var withoutEnd = stripped.substring(0, stripped.length() - ">".length());
            final var argumentsStart = withoutEnd.indexOf("<");
            if (argumentsStart >= 0) {
                final var base = withoutEnd.substring(0, argumentsStart).strip();
                final var inputArguments = withoutEnd.substring(argumentsStart + 1);
                final var outputArguments = compileValues(inputArguments, Main::compileType);
                return base + "<" + outputArguments + ">";
            }
        }

        if (isSymbol(stripped)) {
            return stripped;
        }

        return generatePlaceholder(input);
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