package magma;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Main {
    private interface Path {
        Result readString();

        Option<IOError> write(String content);

        Path resolveSibling(String name);
    }

    private interface IOError {
        String display();
    }

    private interface Option<T> {
        <R> Option<R> map(Function<T, R> mapper);

        boolean isPresent();

        T get();

        T orElse(T other);

        void ifPresent(Consumer<T> ifPresent);

        T orElseGet(Supplier<T> supplier);

        Option<T> or(Supplier<Option<T>> other);

        <R> Option<R> flatMap(Function<T, Option<R>> mapper);

        boolean isEmpty();
    }

    private interface Collector<T, C> {
        C createInitial();

        C fold(C current, T element);
    }

    private interface Iterator<T> {
        <R> Iterator<R> map(Function<T, R> mapper);

        <C> C collect(Collector<T, C> collector);

        <R> R fold(R initial, BiFunction<R, T, R> folder);
    }

    private interface List<T> {
        List<T> addLast(T element);

        Iterator<T> iter();

        List<T> addAllLast(List<T> others);

        boolean containsElements();

        boolean contains(T element);

        Option<Tuple<List<T>, T>> popLast();

        T getLast();

        T getFirst();

        T get(int index);
    }

    private interface Head<T> {
        Option<T> next();
    }

    private interface Result {
        <R> R match(Function<String, R> whenOk, Function<IOError, R> whenErr);
    }

    private @interface Actual {
    }

    private interface Type extends Node {
        String generate();
    }

    private interface Node {
        String generate();
    }

    private interface CDefinition {
        String generate();
    }

    private static class None<T> implements Option<T> {
        @Override
        public <R> Option<R> map(Function<T, R> mapper) {
            return new None<>();
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
        public void ifPresent(Consumer<T> ifPresent) {
        }

        @Override
        public T orElseGet(Supplier<T> supplier) {
            return supplier.get();
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
        public boolean isEmpty() {
            return true;
        }
    }

    private record Some<T>(T value) implements Option<T> {
        @Override
        public <R> Option<R> map(Function<T, R> mapper) {
            return new Some<>(mapper.apply(this.value));
        }

        @Override
        public boolean isPresent() {
            return true;
        }

        @Override
        public T get() {
            return this.value;
        }

        @Override
        public T orElse(T other) {
            return this.value;
        }

        @Override
        public void ifPresent(Consumer<T> ifPresent) {
            ifPresent.accept(this.value);
        }

        @Override
        public T orElseGet(Supplier<T> supplier) {
            return this.value;
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
        public boolean isEmpty() {
            return false;
        }
    }

    private static class RangeHead implements Head<Integer> {
        private final int length;
        private int count;

        public RangeHead(int length) {
            this.length = length;
            this.count = 0;
        }

        @Override
        public Option<Integer> next() {
            if (this.count < this.length) {
                final var value = this.count;
                this.count++;
                return new Some<>(value);
            }
            else {
                return new None<>();
            }
        }
    }

    private record HeadedIterator<T>(Head<T> head) implements Iterator<T> {
        @Override
        public <R> Iterator<R> map(Function<T, R> mapper) {
            return new HeadedIterator<>(() -> this.head.next().map(mapper));
        }

        @Override
        public <C> C collect(Collector<T, C> collector) {
            return this.fold(collector.createInitial(), collector::fold);
        }

        @Override
        public <R> R fold(R initial, BiFunction<R, T, R> folder) {
            var current = initial;
            while (true) {
                R finalCurrent = current;
                final var folded = this.head.next().map(next -> folder.apply(finalCurrent, next));
                if (folded.isPresent()) {
                    current = folded.get();
                }
                else {
                    return current;
                }
            }
        }
    }

    @Actual
    private record JavaList<T>(java.util.List<T> elements) implements List<T> {
        public JavaList() {
            this(new ArrayList<>());
        }

        @Override
        public List<T> addLast(T element) {
            this.elements.add(element);
            return this;
        }

        @Override
        public Iterator<T> iter() {
            return new HeadedIterator<>(new RangeHead(this.elements.size())).map(this.elements::get);
        }

        @Override
        public List<T> addAllLast(List<T> others) {
            return others.iter().<List<T>>fold(this, List::addLast);
        }

        @Override
        public boolean containsElements() {
            return !this.elements.isEmpty();
        }

        @Override
        public boolean contains(T element) {
            return this.elements.contains(element);
        }

        @Override
        public Option<Tuple<List<T>, T>> popLast() {
            if (this.elements.isEmpty()) {
                return new None<>();
            }
            else {
                final var last = this.elements.removeLast();
                return new Some<>(new Tuple<>(this, last));
            }
        }

        @Override
        public T getLast() {
            return this.elements.getLast();
        }

        @Override
        public T getFirst() {
            return this.elements.getFirst();
        }

        @Override
        public T get(int index) {
            return this.elements.get(index);
        }
    }

    private static class Lists {
        @Actual
        public static <T> List<T> empty() {
            return new JavaList<>();
        }

        @Actual
        @SafeVarargs
        public static <T> List<T> of(T... elements) {
            return new JavaList<>(new ArrayList<>(Arrays.asList(elements)));
        }
    }

    private static class DivideState {
        private final String input;
        private final int index;
        private List<String> segments;
        private String buffer;
        private int depth;

        private DivideState(String input, List<String> segments, String buffer, int depth, int index) {
            this.input = input;
            this.index = index;
            this.segments = segments;
            this.buffer = buffer;
            this.depth = depth;
        }

        public DivideState(String input) {
            this(input, Lists.empty(), "", 0, 0);
        }

        private boolean isLevel() {
            return this.depth == 0;
        }

        private DivideState append(char c) {
            this.buffer = this.buffer + c;
            return this;
        }

        private DivideState advance() {
            this.segments = this.segments.addLast(this.buffer);
            this.buffer = "";
            return this;
        }

        private DivideState enter() {
            this.depth = this.depth + 1;
            return this;
        }

        private DivideState exit() {
            this.depth = this.depth - 1;
            return this;
        }

        public boolean isShallow() {
            return this.depth == 1;
        }

        public Option<Tuple<DivideState, Character>> pop() {
            if (this.index < this.input.length()) {
                final var c = this.input.charAt(this.index);
                final var next = new DivideState(this.input, this.segments, this.buffer, this.depth, this.index + 1);
                return new Some<>(new Tuple<>(next, c));
            }
            else {
                return new None<>();
            }
        }

        public Option<Character> peek() {
            if (this.index < this.input.length()) {
                return new Some<>(this.input.charAt(this.index));
            }

            return new None<>();
        }

        private Option<DivideState> append() {
            return this.pop().map(tuple -> tuple.left.append(tuple.right));
        }

        public Option<Tuple<DivideState, Character>> popAndAppendToTuple() {
            return this.pop().map(tuple -> new Tuple<>(tuple.left.append(tuple.right), tuple.right));
        }

        public Option<DivideState> popAndAppendToOption() {
            return this.popAndAppendToTuple().map(Tuple::left);
        }
    }

    private record Tuple<A, B>(A left, B right) {
    }

    private record Joiner(String delimiter) implements Collector<String, Option<String>> {
        public Joiner() {
            this("");
        }

        @Override
        public Option<String> createInitial() {
            return new None<>();
        }

        @Override
        public Option<String> fold(Option<String> current, String element) {
            return new Some<>(current.map(inner -> inner + this.delimiter + element).orElse(element));
        }
    }

    private record TupleCollector<A, AC, B, BC>(Collector<A, AC> leftCollector, Collector<B, BC> rightCollector)
            implements Collector<Tuple<A, B>, Tuple<AC, BC>> {
        @Override
        public Tuple<AC, BC> createInitial() {
            return new Tuple<>(this.leftCollector.createInitial(), this.rightCollector.createInitial());
        }

        @Override
        public Tuple<AC, BC> fold(Tuple<AC, BC> current, Tuple<A, B> element) {
            return new Tuple<>(this.leftCollector.fold(current.left, element.left), this.rightCollector.fold(current.right, element.right));
        }
    }

    private static class ListBulkCollector<T> implements Collector<List<T>, List<T>> {
        @Override
        public List<T> createInitial() {
            return Lists.empty();
        }

        @Override
        public List<T> fold(List<T> current, List<T> element) {
            return current.addAllLast(element);
        }
    }

    private record ClassDefinition(List<String> annotations, List<String> modifiers, String name,
                                   List<String> typeParameters) {
        private String generate() {
            return "struct " + this.name;
        }
    }

    private record JavaDefinition(
            List<String> annotations,
            List<String> modifiers,
            List<String> typeParameters,
            Type type,
            String name
    ) {
    }

    private static class ListCollector<T> implements Collector<T, List<T>> {
        @Override
        public List<T> createInitial() {
            return Lists.empty();
        }

        @Override
        public List<T> fold(List<T> current, T element) {
            return current.addLast(element);
        }
    }

    private record Ok(String value) implements Result {
        @Override
        public <R> R match(Function<String, R> whenOk, Function<IOError, R> whenErr) {
            return whenOk.apply(this.value);
        }
    }

    private record Err(IOError error) implements Result {
        @Override
        public <R> R match(Function<String, R> whenOk, Function<IOError, R> whenErr) {
            return whenErr.apply(this.error);
        }
    }

    @Actual
    private record JavaIOError(IOException exception) implements IOError {
        @Override
        public String display() {
            final var writer = new StringWriter();
            this.exception.printStackTrace(new PrintWriter(writer));
            return writer.toString();
        }
    }

    @Actual
    private record JavaPath(java.nio.file.Path path) implements Path {
        @Override
        public Path resolveSibling(String name) {
            return new JavaPath(this.path.resolveSibling(name));
        }

        @Override
        public Option<IOError> write(String content) {
            try {
                Files.writeString(this.path, content);
                return new None<>();
            } catch (IOException e) {
                return new Some<>(new JavaIOError(e));
            }
        }

        @Override
        public Result readString() {
            try {
                return new Ok(Files.readString(this.path));
            } catch (IOException e) {
                return new Err(new JavaIOError(e));
            }
        }
    }

    private static class Paths {
        @Actual
        public static Path get(String first, String... more) {
            return new JavaPath(java.nio.file.Paths.get(first, more));
        }
    }

    private record FunctionType(List<Type> argumentTypes, Type returnType) implements Type {
        @Override
        public String generate() {
            return this.generateWithName("");
        }

        public String generateWithName(String name) {
            final var joined = generateValueNodes(this.argumentTypes);
            return this.returnType.generate() + " (*" + name + ")(" + joined + ")";
        }
    }

    private record TemplateType(String base, List<Type> elements) implements Type {
        @Override
        public String generate() {
            final var outputArguments = generateValueNodes(this.elements);
            return this.base + "<" + outputArguments + ">";
        }
    }

    private record Placeholder(String input) implements Type {

        @Override
        public String generate() {
            return generatePlaceholder(this.input);
        }
    }

    private record StructType(String name) implements Type {
        @Override
        public String generate() {
            return "struct " + this.name;
        }
    }

    public record SimpleCDefinition(Type type, String name) implements CDefinition {
        @Override
        public String generate() {
            return this.type().generate() + " " + this.name();
        }
    }

    private static class CFunctionDefinition implements CDefinition {
        private final FunctionType type;
        private final JavaDefinition definition;

        public CFunctionDefinition(FunctionType type, JavaDefinition definition) {
            this.type = type;
            this.definition = definition;
        }

        @Override
        public String generate() {
            return this.type.generateWithName(this.definition.name);
        }
    }

    public static void main(String[] args) {
        final var source = Paths.get(".", "src", "magma", "Main.java");
        source.readString()
                .match(input -> compileAndWrite(input, source), Some::new)
                .ifPresent(error -> printErroneousLine(error.display()));
    }

    @Actual
    private static void printErroneousLine(String content) {
        System.err.println(content);
    }

    private static Option<IOError> compileAndWrite(String input, Path source) {
        final var target = source.resolveSibling("Main.c");
        final var string = compile(input);
        return target.write(string);
    }

    private static String compile(String input) {
        return compileStatements(input, Main::compileRootSegment);
    }

    private static String compileStatements(String input, Function<String, String> mapper) {
        return compileAll(input, Main::foldStatements, mapper, Main::mergeStatements);
    }

    private static String compileAll(String input, BiFunction<DivideState, Character, DivideState> folder, Function<String, String> mapper, BiFunction<String, String, String> merger) {
        return generateAll(merger, parseAll(input, folder, mapper));
    }

    private static String generateAll(BiFunction<String, String, String> merger, List<String> stringList) {
        return stringList.iter().fold("", merger);
    }

    private static <T> List<T> parseAll(String input, BiFunction<DivideState, Character, DivideState> folder, Function<String, T> mapper) {
        return mapAll(divide(input, folder), mapper);
    }

    private static <T> List<T> mapAll(List<String> elements, Function<String, T> mapper) {
        return elements.iter().map(mapper).collect(new ListCollector<>());
    }

    private static String mergeStatements(String buffer, String element) {
        return buffer + element;
    }

    private static List<String> divideStatements(String input) {
        return divide(input, Main::foldStatements);
    }

    private static List<String> divide(String input, BiFunction<DivideState, Character, DivideState> folder) {
        var current = new DivideState(input);

        while (true) {
            final var maybeNext = current.pop().map(tuple -> getObject(folder, tuple));
            if (maybeNext.isPresent()) {
                current = maybeNext.get();
            }
            else {
                break;
            }
        }

        return current.advance().segments;
    }

    private static DivideState getObject(BiFunction<DivideState, Character, DivideState> folder, Tuple<DivideState, Character> tuple) {
        final var currentState = tuple.left;
        final var c = tuple.right;

        return foldSingleQuotes(currentState, c).orElseGet(() -> folder.apply(currentState, c));
    }

    private static Option<DivideState> foldSingleQuotes(DivideState currentState, char c) {
        if (c != '\'') {
            return new None<>();
        }

        final var appended = currentState.append(c);
        return appended.popAndAppendToTuple()
                .flatMap(Main::foldEscaped)
                .flatMap(DivideState::popAndAppendToOption);
    }

    private static Option<DivideState> foldEscaped(Tuple<DivideState, Character> tuple) {
        if (tuple.right == '\\') {
            return tuple.left.popAndAppendToOption();
        }
        return new Some<>(tuple.left);
    }

    private static DivideState foldStatements(DivideState state, char c) {
        final var appended = state.append(c);
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

        return compileClass(input)
                .map(tuple -> {
                    final var joined = tuple.left
                            .iter()
                            .collect(new Joiner())
                            .orElse("");

                    return joined + tuple.right;
                })
                .orElseGet(() -> generatePlaceholder(input));
    }

    private static Option<Tuple<List<String>, String>> compileClass(String input) {
        final var contentStart = input.indexOf('{');
        if (contentStart >= 0) {
            final var beforeContent = input.substring(0, contentStart);
            final var withEnd = input.substring(contentStart + "{".length()).strip();
            if (withEnd.endsWith("}")) {
                final var maybeHeader = compileClassDefinition(beforeContent);
                if (maybeHeader.isPresent()) {
                    final var definition = maybeHeader.get();
                    final var others = compileClassWithDefinition(definition, withEnd);
                    return new Some<>(new Tuple<>(others, ""));
                }
            }
        }

        return new None<>();
    }

    private static List<String> compileClassWithDefinition(ClassDefinition definition, String withEnd) {
        if (definition.typeParameters.containsElements() || definition.annotations.contains("Actual")) {
            return Lists.empty();
        }

        final var inputContent = withEnd.substring(0, withEnd.length() - "}".length());

        final var segments = divideStatements(inputContent);

        final var tuple = segments.iter()
                .map(Main::compileClassSegment)
                .collect(new TupleCollector<>(new ListBulkCollector<>(), new Joiner()));

        final var others = tuple.left;
        final var output = tuple.right.orElse("");

        final var generatedHeader = definition.generate();
        final var generated = generatedHeader + " {" + output + "\n};\n";
        return others.addLast(generated);
    }

    private static Tuple<List<String>, String> compileClassSegment(String input) {
        return compileWhitespace(input).<Tuple<List<String>, String>>map(result -> new Tuple<>(Lists.empty(), result))
                .or(() -> compileField(input))
                .or(() -> compileClass(input))
                .or(() -> compileMethod(input))
                .orElseGet(() -> new Tuple<>(Lists.empty(), generatePlaceholder(input)));
    }

    private static Option<Tuple<List<String>, String>> compileMethod(String input) {
        final var paramStart = input.indexOf("(");
        if (paramStart >= 0) {
            final var beforeParams = input.substring(0, paramStart);
            final var withParams = input.substring(paramStart + "(".length());
            final var paramEnd = withParams.indexOf(")");
            if (paramEnd >= 0) {
                final var params = withParams.substring(0, paramEnd);
                final var withBraces = withParams.substring(paramEnd + ")".length()).strip();
                final var maybeDefinition = parseMethodDefinition(beforeParams);
                if (maybeDefinition.isPresent()) {
                    final var definition = maybeDefinition.get();
                    if (definition.typeParameters.containsElements()) {
                        return new Some<>(new Tuple<>(Lists.empty(), ""));
                    }

                    final var compiledParameters = compileValues(params, Main::compileParameter);
                    final var header = transformDefinition(definition).generate() + "(" + compiledParameters + ")";

                    if (withBraces.equals(";")) {
                        final var generated = header + ";";
                        return new Some<>(new Tuple<>(Lists.empty(), "\n\t" + generated));
                    }

                    if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
                        return new Some<>(compileMethodWithBody(definition, header, withBraces));
                    }

                    return new None<>();
                }
            }
        }

        return new None<>();
    }

    private static CDefinition transformDefinition(JavaDefinition definition) {
        if (definition.type instanceof FunctionType type) {
            return new CFunctionDefinition(type, definition);
        }

        return new SimpleCDefinition(definition.type, definition.name);
    }

    private static Tuple<List<String>, String> compileMethodWithBody(JavaDefinition definition, String header, String withBraces) {
        if (definition.annotations.contains("Actual")) {
            return new Tuple<>(Lists.of(header + ";\n"), "");
        }

        final var inputContent = withBraces.substring(1, withBraces.length() - 1).strip();
        final var outputContent = compileFunctionSegments(inputContent, 1);
        final var withinStructure = definition.modifiers.contains("static") ? "" : "\n\t" + header + ";";

        return new Tuple<>(Lists.of(header + " {" + outputContent + "\n}" + "\n"), withinStructure);
    }

    private static String compileFunctionSegments(String input, int depth) {
        return compileStatements(input, input1 -> compileFunctionSegment(input1, depth));
    }

    private static Option<JavaDefinition> parseMethodDefinition(String input) {
        return parseDefinition(input).or(() -> parseConstructor(input));
    }

    private static Option<JavaDefinition> parseConstructor(String input) {
        final var separator = input.lastIndexOf(" ");
        if (separator >= 0) {
            final var name = input.substring(separator + " ".length());
            return new Some<>(new JavaDefinition(Lists.empty(), Lists.of("static"), Lists.empty(), new StructType(name), "new"));
        }
        else {
            return new None<>();
        }
    }

    private static String compileValues(String input, Function<String, String> mapper) {
        return generateValues(parseValues(input, mapper));
    }

    private static String generateValues(List<String> elements) {
        return generateAll(Main::mergeValues, elements);
    }

    private static <T> List<T> parseValues(String input, Function<String, T> mapper) {
        return parseAll(input, Main::foldValues, mapper);
    }

    private static String compileFunctionSegment(String input, int depth) {
        return compileWhitespace(input)
                .or(() -> compileFunctionStatement(input, depth))
                .or(() -> compileBlock(input, depth))
                .orElseGet(() -> generatePlaceholder(input));
    }

    private static Option<String> compileBlock(String input, int depth) {
        final var stripped = input.strip();
        if (stripped.endsWith("}")) {
            final var withoutEnd = stripped.substring(0, stripped.length() - "}".length());
            final var contentStart = withoutEnd.indexOf("{");
            if (contentStart >= 0) {
                final var header = withoutEnd.substring(0, contentStart);
                final var inputContent = withoutEnd.substring(contentStart + "{".length());
                final var outputContent = compileFunctionSegments(inputContent, depth + 1);
                final var indent = "\n" + "\t".repeat(depth);
                return new Some<>(indent + compileBlockHeader(header) + " {" + outputContent + indent + "}");
            }
        }

        return new None<>();
    }

    private static String compileBlockHeader(String input) {
        final var stripped = input.strip();
        if (stripped.equals("else")) {
            return "else";
        }

        if (stripped.endsWith(")")) {
            final var withoutEnd = stripped.substring(0, stripped.length() - ")".length());
            final var conditionStart = withoutEnd.indexOf("(");
            if (conditionStart >= 0) {
                final var beforeCondition = withoutEnd.substring(0, conditionStart);
                final var conditionString = withoutEnd.substring(conditionStart + "(".length());
                final var compiled = compileValue(conditionString);
                final var strippedCompiled = beforeCondition.strip();
                final var beforeContent = switch (strippedCompiled) {
                    case "if", "while" -> strippedCompiled;
                    default -> generatePlaceholder(strippedCompiled);
                };
                return beforeContent + " (" + compiled + ")";
            }
        }

        return generatePlaceholder(stripped);
    }

    private static Option<String> compileFunctionStatement(String input, int depth) {
        final var stripped = input.strip();
        if (stripped.endsWith(";")) {
            final var withoutEnd = stripped.substring(0, stripped.length() - ";".length());
            return new Some<>("\n" + "\t".repeat(depth) + compileFunctionStatementValue(withoutEnd) + ";");
        }

        return new None<>();
    }

    private static String compileFunctionStatementValue(String input) {
        final var stripped = input.strip();
        if (stripped.equals("break")) {
            return "break";
        }

        if (stripped.startsWith("return ")) {
            final var value = stripped.substring("return ".length());
            return "return " + compileValue(value);
        }

        final var i = stripped.indexOf("=");
        if (i >= 0) {
            final var destinationString = stripped.substring(0, i);
            final var substring1 = stripped.substring(i + "=".length());
            final var destination = parseDefinition(destinationString).map(javaDefinition -> transformDefinition(javaDefinition).generate())
                    .orElseGet(() -> compileValue(destinationString));

            return destination + " = " + compileValue(substring1);
        }

        return compileInvokable(stripped).orElseGet(() -> generatePlaceholder(input));
    }

    private static Option<String> compileInvokable(String input) {
        final var stripped = input.strip();
        if (stripped.endsWith(")")) {
            final var withoutEnd = stripped.substring(0, stripped.length() - ")".length());

            final var divisions = divide(withoutEnd, Main::foldInvocationStart);
            return divisions.popLast().flatMap(tuple -> {
                final var joined = tuple.left.iter().collect(new Joiner()).orElse("");
                final var arguments = tuple.right;

                if (joined.endsWith("(")) {
                    final var oldCaller = joined.substring(0, joined.length() - 1);
                    final var newCaller = oldCaller.startsWith("new ")
                            ? compileConstruction(oldCaller)
                            : compileValue(oldCaller);

                    return new Some<>(newCaller + "(" + compileValues(arguments, Main::compileValue) + ")");
                }
                else {
                    return new None<>();
                }
            });
        }

        return new None<>();
    }

    private static DivideState foldInvocationStart(DivideState state, char c) {
        final var appended = state.append(c);
        if (c == '(') {
            final var entered = appended.enter();
            if (entered.isShallow()) {
                return entered.advance();
            }
            else {
                return entered;
            }
        }
        if (c == ')') {
            return appended.exit();
        }
        return appended;
    }

    private static String compileConstruction(String caller) {
        final var type = caller.substring("new ".length());
        return compileTypeOrPlaceholder(type);
    }

    private static String compileValue(String input) {
        return compileLambda(input)
                .or(() -> compileInvokable(input))
                .or(() -> compileAccess(input))
                .or(() -> compileOperator(input, "!="))
                .or(() -> compileOperator(input, "=="))
                .or(() -> compileOperator(input, "+"))
                .or(() -> compileOperator(input, "-"))
                .or(() -> compileOperator(input, "&&"))
                .or(() -> compileSymbol(input))
                .or(() -> compileNumber(input))
                .or(() -> compileChar(input))
                .or(() -> compileString(input))
                .orElseGet(() -> generatePlaceholder(input));
    }

    private static Option<String> compileChar(String input) {
        final var stripped = input.strip();
        if (stripped.startsWith("'") && stripped.endsWith("'")) {
            return new Some<>(stripped);
        }
        else {
            return new None<>();
        }
    }

    private static Option<String> compileLambda(String input) {
        final var arrowIndex = input.indexOf("->");
        if (arrowIndex >= 0) {
            final var left = input.substring(0, arrowIndex).strip();
            final var right = input.substring(arrowIndex + "->".length());

            if (isSymbol(left)) {
                return new Some<>(generatePlaceholder(input));
            }
        }

        return new None<>();
    }

    private static Option<String> compileString(String input) {
        final var stripped = input.strip();
        if (stripped.startsWith("\"") && stripped.endsWith("\"")) {
            return new Some<>(stripped);
        }
        else {
            return new None<>();
        }
    }

    private static Option<String> compileNumber(String input) {
        final var stripped = input.strip();
        if (isNumber(stripped)) {
            return new Some<>(stripped);
        }
        else {
            return new None<>();
        }
    }

    private static Option<String> compileSymbol(String input) {
        final var stripped = input.strip();
        if (isSymbol(stripped)) {
            return new Some<>(stripped);
        }
        else {
            return new None<>();
        }
    }

    private static Option<String> compileAccess(String input) {
        final var separator = input.lastIndexOf(".");
        if (separator >= 0) {
            final var substring = input.substring(0, separator);
            final var property = input.substring(separator + ".".length()).strip();
            if (isSymbol(property)) {
                return new Some<>(compileValue(substring) + "." + property);
            }
        }

        return new None<>();
    }

    private static Option<String> compileOperator(String input, String infix) {
        final var index = input.indexOf(infix);
        if (index >= 0) {
            final var leftString = input.substring(0, index);
            final var rightString = input.substring(index + infix.length());
            return new Some<>(compileValue(leftString) + " " + infix + " " + compileValue(rightString));
        }

        return new None<>();
    }

    private static boolean isNumber(String input) {
        for (int i = 0; i < input.length(); i++) {
            final var c = input.charAt(i);
            if (Character.isDigit(c)) {
                continue;
            }
            return false;
        }

        return true;
    }

    private static String mergeValues(String buffer, String element) {
        if (buffer.isEmpty()) {
            return element;
        }
        return buffer + ", " + element;
    }

    private static String compileParameter(String input) {
        return compileWhitespace(input)
                .or(() -> parseDefinition(input).map(javaDefinition -> transformDefinition(javaDefinition).generate()))
                .orElseGet(() -> generatePlaceholder(input));
    }

    private static Option<String> compileWhitespace(String input) {
        if (input.isBlank()) {
            return new Some<>("");
        }
        else {
            return new None<>();
        }
    }

    private static Option<Tuple<List<String>, String>> compileField(String input) {
        final var stripped = input.strip();
        if (stripped.endsWith(";")) {
            final var withoutEnd = stripped.substring(0, stripped.length() - ";".length());
            return parseDefinition(withoutEnd).map(javaDefinition -> transformDefinition(javaDefinition).generate()).map(generated -> new Tuple<>(Lists.empty(), "\n\t" + generated + ";"));
        }

        return new None<>();
    }

    private static Option<JavaDefinition> parseDefinition(String input) {
        final var stripped = input.strip();
        final var nameSeparator = stripped.lastIndexOf(" ");
        if (nameSeparator >= 0) {
            final var beforeName = stripped.substring(0, nameSeparator).strip();
            final var name = stripped.substring(nameSeparator + " ".length()).strip();

            if (isSymbol(name)) {
                return parseDefinitionWithType(beforeName, name);
            }
        }
        return new None<>();
    }

    private static boolean isSymbol(String input) {
        for (var i = 0; i < input.length(); i++) {
            final var c = input.charAt(i);
            if (Character.isLetter(c)) {
                continue;
            }
            return false;
        }
        return true;
    }

    private static Option<JavaDefinition> parseDefinitionWithType(String beforeName, String name) {
        final var maybeTuple = divide(beforeName, Main::foldTypeSeparator).popLast();
        if (maybeTuple.isEmpty()) {
            return parseType(beforeName).map(type -> new JavaDefinition(Lists.empty(), Lists.empty(), Lists.empty(), type, name));
        }

        final var tuple = maybeTuple.get();
        final var beforeType = tuple.left
                .iter()
                .collect(new Joiner(" "))
                .orElse("");

        final var type = tuple.right;

        return parseType(type).map(compiledType -> {
            return parseDefinitionWithTypeParameters(name, compiledType, beforeType);
        });
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

    private static JavaDefinition parseDefinitionWithTypeParameters(String name, Type compiledType, String input) {
        final var beforeType = input.strip();
        if (beforeType.endsWith(">")) {
            final var withoutEnd = beforeType.substring(0, beforeType.length() - ">".length());
            final var typeParametersStart = withoutEnd.indexOf("<");
            if (typeParametersStart >= 0) {
                final var beforeTypeParameters = withoutEnd.substring(0, typeParametersStart);
                final var typeParametersString = withoutEnd.substring(typeParametersStart + "<".length());
                final var typeParameters = parseTypeParameters(typeParametersString);
                return parseDefinitionWithModifiers(beforeTypeParameters, typeParameters, compiledType, name);
            }
        }

        return parseDefinitionWithModifiers(beforeType, Lists.empty(), compiledType, name);
    }

    private static JavaDefinition parseDefinitionWithModifiers(
            String beforeTypeParameters,
            List<String> typeParameters,
            Type type,
            String name
    ) {
        final var separator = beforeTypeParameters.lastIndexOf("\n");
        if (separator >= 0) {
            final var annotationsString = beforeTypeParameters.substring(0, separator);
            final var annotations = parseAnnotations(annotationsString);

            final var substring = beforeTypeParameters.substring(separator + "\n".length());

            final var modifiers = parseModifiers(substring);
            return new JavaDefinition(annotations, modifiers, typeParameters, type, name);
        }
        else {
            final var modifiers = parseModifiers(beforeTypeParameters);
            return new JavaDefinition(Lists.empty(), modifiers, typeParameters, type, name);
        }
    }

    private static List<String> parseAnnotations(String annotationsString) {
        return divide(annotationsString, foldByDelimiter('\n'))
                .iter()
                .map(String::strip)
                .map(value -> value.substring(1))
                .collect(new ListCollector<>());
    }

    private static List<String> parseModifiers(String beforeTypeParameters) {
        return parseAll(beforeTypeParameters, foldByDelimiter(' '), String::strip);
    }

    private static BiFunction<DivideState, Character, DivideState> foldByDelimiter(char delimiter) {
        return (state, c) -> {
            if (c == delimiter) {
                return state.advance();
            }
            return state.append(c);
        };
    }

    private static Option<Type> parseType(String input) {
        return compileTemplateType(input)
                .or(() -> compilePrimitiveType(input))
                .or(() -> compileSymbolType(input))
                .or(() -> compileArrayType(input).map(type -> type));
    }

    private static Option<TemplateType> compileArrayType(String input) {
        final var stripped = input.strip();
        if (stripped.endsWith("[]")) {
            final var slice = stripped.substring(0, stripped.length() - "[]".length());
            return parseType(slice).map(compiled -> new TemplateType("Array", Lists.of(compiled)));
        }
        return new None<>();
    }

    private static Option<Type> compileSymbolType(String input) {
        if (isSymbol(input.strip())) {
            return new Some<>(new StructType(input.strip()));
        }
        return new None<>();
    }

    private static Option<Type> compilePrimitiveType(String input) {
        return switch (input.strip()) {
            case "char", "Character" -> new Some<>(Primitive.Char);
            case "boolean", "Boolean", "int", "Integer" -> new Some<>(Primitive.Int);
            case "var" -> new Some<>(Primitive.Auto);
            case "void" -> new Some<>(Primitive.Void);
            case "String" -> new Some<>(new TemplateType("Array", Lists.of(Primitive.Char)));
            default -> new None<>();
        };
    }

    private static Option<Type> compileTemplateType(String input) {
        if (!input.strip().endsWith(">")) {
            return new None<>();
        }

        final var withoutEnd = input.strip().substring(0, input.strip().length() - ">".length());
        final var typeArgumentsStart = withoutEnd.indexOf("<");
        if (typeArgumentsStart < 0) {
            return new None<>();
        }

        final var base = withoutEnd.substring(0, typeArgumentsStart);
        final var arguments = withoutEnd.substring(typeArgumentsStart + "<".length());
        return new Some<>(assembleTemplateType(base, arguments));
    }

    private static Type assembleTemplateType(String base, String inputArguments) {
        final var elements = parseValues(inputArguments, Main::parseTypeOrPlaceholder);
        return switch (base) {
            case "Function" -> {
                final var first = elements.getFirst();
                final var last = elements.getLast();
                yield new FunctionType(Lists.of(first), last);
            }
            case "BiFunction" -> {
                final var arg0 = elements.getFirst();
                final var arg1 = elements.get(1);
                final var returnType = elements.getLast();
                yield new FunctionType(Lists.of(arg0, arg1), returnType);
            }
            default -> new TemplateType(base, elements);
        };
    }

    private static String compileTypeOrPlaceholder(String input) {
        return parseTypeOrPlaceholder(input).generate();
    }

    private static Type parseTypeOrPlaceholder(String input) {
        return parseType(input).orElseGet(() -> new Placeholder(input));
    }

    private static Option<ClassDefinition> compileClassDefinition(String input) {
        return compileClassDefinitionWithKeyword(input, "class ")
                .or(() -> compileClassDefinitionWithKeyword(input, "interface "))
                .or(() -> compileClassDefinitionWithKeyword(input, "record "));
    }

    private static Option<ClassDefinition> compileClassDefinitionWithKeyword(String input, String keyword) {
        final var classIndex = input.indexOf(keyword);
        if (classIndex < 0) {
            return new None<>();
        }

        final var beforeKeyword = input.substring(0, classIndex).strip();
        final var afterKeyword = input.substring(classIndex + keyword.length()).strip();
        return new Some<>(parseClassDefinitionWithParameters(beforeKeyword, afterKeyword));
    }

    private static ClassDefinition parseClassDefinitionWithParameters(String beforeKeyword, String afterKeyword) {
        if (afterKeyword.endsWith(")")) {
            final var withoutEnd = afterKeyword.substring(0, afterKeyword.length() - ")".length());
            final var paramStart = withoutEnd.indexOf("(");
            if (paramStart >= 0) {
                final var beforeParameters = withoutEnd.substring(0, paramStart);
                final var parameters = withoutEnd.substring(paramStart + "(".length());
                return parseClassDefinitionWithTypeParameters(beforeKeyword, beforeParameters);
            }
        }

        return parseClassDefinitionWithTypeParameters(beforeKeyword, afterKeyword);
    }

    private static ClassDefinition parseClassDefinitionWithTypeParameters(String beforeKeyword, String input) {
        final var stripped = input.strip();
        if (stripped.endsWith(">")) {
            final var withoutEnd = stripped.substring(0, stripped.length() - ">".length());
            final var typeParamsStart = withoutEnd.indexOf("<");
            if (typeParamsStart >= 0) {
                final var base = withoutEnd.substring(0, typeParamsStart);
                final var typeParametersString = withoutEnd.substring(typeParamsStart + "<".length());
                final var typeParameters = parseTypeParameters(typeParametersString);
                return parseClassDefinitionWithModifiers(beforeKeyword, base, typeParameters);
            }
        }

        return parseClassDefinitionWithModifiers(beforeKeyword, stripped, Lists.empty());
    }

    private static ClassDefinition parseClassDefinitionWithModifiers(String beforeKeyword, String base, List<String> typeParameters) {
        final var i = beforeKeyword.lastIndexOf("\n");
        if (i >= 0) {
            final var annotationsString = beforeKeyword.substring(0, i);
            final var modifiersString = beforeKeyword.substring(i + "\n".length());

            final var annotations = parseAnnotations(annotationsString);
            final var modifiers = parseModifiers(modifiersString);

            return new ClassDefinition(annotations, modifiers, base, typeParameters);
        }

        final var modifiers = parseModifiers(beforeKeyword);
        return new ClassDefinition(Lists.empty(), modifiers, base, typeParameters);
    }

    private static List<String> parseTypeParameters(String typeParameters) {
        return mapAll(divideValues(typeParameters), String::strip);
    }

    private static List<String> divideValues(String input) {
        return divide(input, Main::foldValues);
    }

    private static DivideState foldValues(DivideState state, char c) {
        if (c == ',' && state.isLevel()) {
            return state.advance();
        }

        final var appended = state.append(c);
        if (c == '-') {
            final var maybe = appended.peek();
            if (maybe instanceof Some(var peek) && peek == '>') {
                return appended.append().orElse(appended);
            }
        }

        if (c == '<' || c == '(') {
            return appended.enter();
        }
        if (c == '>' || c == ')') {
            return appended.exit();
        }
        return appended;
    }

    private static String generatePlaceholder(String input) {
        return "/*" + input
                .replace("/*", "start")
                .replace("*/", "end") + "*/";
    }

    private static <T extends Node> String generateValueNodes(List<T> nodes) {
        return nodes.iter()
                .map(Node::generate)
                .collect(new Joiner(", "))
                .orElse("");
    }

    private enum Primitive implements Type {
        Char("char"),
        Int("int"),
        Auto("auto"),
        Void("void");

        private final String value;

        Primitive(String value) {
            this.value = value;
        }

        @Override
        public String generate() {
            return this.value;
        }
    }
}
