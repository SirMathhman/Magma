package magma;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Main {
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

        boolean isEmpty();

        boolean contains(T element);

        Option<Tuple<List<T>, T>> popLast();
    }

    private interface Head<T> {
        Option<T> next();
    }

    private interface Result {
        <R> R match(Function<String, R> whenOk, Function<IOError, R> whenErr);
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
        public boolean isEmpty() {
            return this.elements.isEmpty();
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
    }

    private static class Lists {
        public static <T> List<T> empty() {
            return new JavaList<>();
        }

        @SafeVarargs
        public static <T> List<T> of(T... elements) {
            return new JavaList<>(new ArrayList<>(Arrays.asList(elements)));
        }
    }

    private static class State {
        private List<String> segments;
        private String buffer;
        private int depth;

        private State(List<String> segments, String buffer, int depth) {
            this.segments = segments;
            this.buffer = buffer;
            this.depth = depth;
        }

        public State() {
            this(Lists.empty(), "", 0);
        }

        private boolean isLevel() {
            return this.depth == 0;
        }

        private State append(char c) {
            this.buffer = this.buffer + c;
            return this;
        }

        private State advance() {
            this.segments = this.segments.addLast(this.buffer);
            this.buffer = "";
            return this;
        }

        private State enter() {
            this.depth = this.depth + 1;
            return this;
        }

        private State exit() {
            this.depth = this.depth - 1;
            return this;
        }

        public boolean isShallow() {
            return this.depth == 1;
        }
    }

    private record Tuple<A, B>(A left, B right) {
    }

    private static class Joiner implements Collector<String, Option<String>> {
        @Override
        public Option<String> createInitial() {
            return new None<>();
        }

        @Override
        public Option<String> fold(Option<String> current, String element) {
            return new Some<>(current.map(inner -> inner + element).orElse(element));
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

    private record ClassDefinition(String beforeKeyword, String name, List<String> typeParameters) {
        private String generate() {
            return generatePlaceholder(this.beforeKeyword) + "struct " + this.name;
        }
    }

    private record JavaDefinition(
            Option<String> maybeBefore,
            List<String> modifiers,
            List<String> typeParameters,
            String type,
            String name
    ) {
        private String generate() {
            final var beforeType = this.maybeBefore.map(Main::generatePlaceholder)
                    .map(inner -> inner + " ")
                    .orElse("");

            return beforeType + this.type + " " + this.name;
        }
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

    private record JavaIOError(IOException exception) implements IOError {
        @Override
        public String display() {
            final var writer = new StringWriter();
            this.exception.printStackTrace(new PrintWriter(writer));
            return writer.toString();
        }
    }

    public static void main(String[] args) {
        final var source = Paths.get(".", "src", "magma", "Main.java");
        readString(source)
                .match(input -> compileAndWrite(input, source), Some::new)
                .ifPresent(error -> System.err.println(error.display()));
    }

    private static Option<IOError> compileAndWrite(String input, Path source) {
        final var target = source.resolveSibling("Main.c");
        final var string = compile(input);
        return writeString(target, string);
    }

    private static Option<IOError> writeString(Path target, String string) {
        try {
            Files.writeString(target, string);
            return new None<>();
        } catch (IOException e) {
            return new Some<>(new JavaIOError(e));
        }
    }

    private static Result readString(Path source) {
        try {
            return new Ok(Files.readString(source));
        } catch (IOException e) {
            return new Err(new JavaIOError(e));
        }
    }

    private static String compile(String input) {
        return compileStatements(input, Main::compileRootSegment);
    }

    private static String compileStatements(String input, Function<String, String> mapper) {
        return compileAll(input, Main::foldStatements, mapper, Main::mergeStatements);
    }

    private static String compileAll(String input, BiFunction<State, Character, State> folder, Function<String, String> mapper, BiFunction<String, String, String> merger) {
        return divide(input, folder)
                .iter()
                .map(mapper)
                .fold("", merger);
    }

    private static String mergeStatements(String buffer, String element) {
        return buffer + element;
    }

    private static List<String> divideStatements(String input) {
        return divide(input, Main::foldStatements);
    }

    private static List<String> divide(String input, BiFunction<State, Character, State> folder) {
        var current = new State();
        for (var i = 0; i < input.length(); i++) {
            final var c = input.charAt(i);
            current = folder.apply(current, c);
        }

        return current.advance().segments;
    }

    private static State foldStatements(State state, char c) {
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
        if (definition.typeParameters.isEmpty()) {
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

        return Lists.empty();
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
                    if (!definition.typeParameters.isEmpty()) {
                        return new Some<>(new Tuple<>(Lists.empty(), ""));
                    }

                    final var compiledParameters = compileValues(params, Main::compileParameter);
                    final var header = definition.generate() + "(" + compiledParameters + ")";

                    if (withBraces.equals(";")) {
                        final var generated = header + ";";
                        return new Some<>(new Tuple<>(Lists.empty(), "\n\t" + generated));
                    }

                    if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
                        final var inputContent = withBraces.substring(1, withBraces.length() - 1).strip();
                        final var outputContent = compileStatements(inputContent, Main::compileFunctionSegment);
                        final var withinStructure = definition.modifiers.contains("static") ? "" : "\n\t" + header + ";";

                        return new Some<>(new Tuple<>(Lists.of(header + " {" +
                                outputContent +
                                "\n}" + "\n"), withinStructure));
                    }

                    return new None<>();
                }
            }
        }

        return new None<>();
    }

    private static Option<JavaDefinition> parseMethodDefinition(String input) {
        return parseDefinition(input).or(() -> parseConstructor(input));
    }

    private static Option<JavaDefinition> parseConstructor(String input) {
        final var separator = input.lastIndexOf(" ");
        if (separator >= 0) {
            final var name = input.substring(separator + " ".length());
            return new Some<>(new JavaDefinition(new None<>(), Lists.of("static"), Lists.empty(), name, "new"));
        }
        else {
            return new None<>();
        }
    }

    private static String compileValues(String input, Function<String, String> mapper) {
        return compileAll(input, Main::foldValues, mapper, Main::mergeValues);
    }

    private static String compileFunctionSegment(String input) {
        return compileWhitespace(input)
                .or(() -> compileFunctionStatement(input))
                .orElseGet(() -> generatePlaceholder(input));
    }

    private static Option<String> compileFunctionStatement(String input) {
        final var stripped = input.strip();
        if (stripped.endsWith(";")) {
            final var withoutEnd = stripped.substring(0, stripped.length() - ";".length());
            return new Some<>("\n\t" + compileFunctionStatementValue(withoutEnd) + ";");
        }

        return new None<>();
    }

    private static String compileFunctionStatementValue(String input) {
        final var stripped = input.strip();
        if (stripped.startsWith("return ")) {
            final var value = stripped.substring("return ".length());
            return "return " + compileValue(value);
        }

        final var i = stripped.indexOf("=");
        if (i >= 0) {
            final var destinationString = stripped.substring(0, i);
            final var substring1 = stripped.substring(i + "=".length());
            final var destination = parseDefinition(destinationString).map(JavaDefinition::generate)
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

    private static State foldInvocationStart(State state, char c) {
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
        return compileType(type).orElseGet(() -> generatePlaceholder(type));
    }

    private static String compileValue(String input) {
        return compileInvokable(input)
                .or(() -> compileAccess(input))
                .or(() -> compileOperator(input, "=="))
                .or(() -> compileOperator(input, "+"))
                .or(() -> compileOperator(input, "-"))
                .or(() -> compileSymbol(input))
                .or(() -> compileNumber(input))
                .or(() -> compileString(input))
                .orElseGet(() -> generatePlaceholder(input));
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
                .or(() -> parseDefinition(input).map(JavaDefinition::generate))
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
            return parseDefinition(withoutEnd).map(JavaDefinition::generate).map(generated -> new Tuple<>(Lists.empty(), "\n\t" + generated + ";"));
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
                return parseDefinitionWithBeforeType(beforeName, name);
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

    private static Option<JavaDefinition> parseDefinitionWithBeforeType(String beforeName, String name) {
        final var typeSeparator = beforeName.lastIndexOf(" ");
        if (typeSeparator < 0) {
            return compileType(beforeName).map(type -> new JavaDefinition(new None<>(), Lists.empty(), Lists.empty(), type, name));
        }

        final var type = beforeName.substring(typeSeparator + " ".length());
        return compileType(type).map(compiledType -> {
            final var beforeType = beforeName.substring(0, typeSeparator).strip();
            if (beforeType.endsWith(">")) {
                final var withoutEnd = beforeType.substring(0, beforeType.length() - ">".length());
                final var typeParametersStart = withoutEnd.indexOf("<");
                if (typeParametersStart >= 0) {
                    final var beforeTypeParameters = withoutEnd.substring(0, typeParametersStart);
                    final var typeParametersString = withoutEnd.substring(typeParametersStart + "<".length());
                    final var typeParameters = parseTypeParameters(typeParametersString);
                    return getJavaDefinition(beforeTypeParameters, typeParameters, compiledType, name);
                }
            }

            return getJavaDefinition(beforeType, Lists.empty(), compiledType, name);
        });
    }

    private static JavaDefinition getJavaDefinition(String beforeTypeParameters, List<String> typeParameters, String type, String name) {
        final var modifiers = divide(beforeTypeParameters, Main::foldModifiers)
                .iter()
                .map(String::strip)
                .collect(new ListCollector<>());

        return new JavaDefinition(new Some<>(beforeTypeParameters), modifiers, typeParameters, type, name);
    }

    private static State foldModifiers(State state, Character c) {
        if (c == ' ') {
            return state.advance();
        }
        return state.append(c);
    }

    private static Option<String> compileType(String input) {
        final var stripped = input.strip();
        switch (stripped) {
            case "private", "public" -> {
                return new None<>();
            }
            case "char" -> {
                return new Some<>("char");
            }
            case "boolean", "int" -> {
                return new Some<>("int");
            }
            case "String" -> {
                return new Some<>("Array<char>");
            }
            case "var" -> {
                return new Some<>("auto");
            }
            case "void" -> {
                return new Some<>("void");
            }
        }

        if (isSymbol(stripped)) {
            return new Some<>("struct " + stripped);
        }

        if (stripped.endsWith("[]")) {
            final var slice = stripped.substring(0, stripped.length() - "[]".length());
            return compileType(slice).map(compiled -> "Array<" + compiled + ">");
        }

        return new Some<>(generatePlaceholder(input));
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
                final var typeParameters = withoutEnd.substring(typeParamsStart + "<".length());
                return new ClassDefinition(beforeKeyword, base, parseTypeParameters(typeParameters));
            }
        }

        return new ClassDefinition(beforeKeyword, stripped, Lists.empty());
    }

    private static List<String> parseTypeParameters(String typeParameters) {
        return divideValues(typeParameters)
                .iter()
                .map(String::strip)
                .collect(new ListCollector<>());
    }

    private static List<String> divideValues(String input) {
        return divide(input, Main::foldValues);
    }

    private static State foldValues(State state, char c) {
        if (c == ',') {
            return state.advance();
        }
        return state.append(c);
    }

    private static String generatePlaceholder(String input) {
        return "/*" + input
                .replace("/*", "start")
                .replace("*/", "end") + "*/";
    }
}
