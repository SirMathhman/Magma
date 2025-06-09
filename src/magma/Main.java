package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Main {
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
    }

    private interface Head<T> {
        Optional<T> next();
    }

    private static class RangeHead implements Head<Integer> {
        private final int length;
        private int count;

        public RangeHead(int length) {
            this.length = length;
            this.count = 0;
        }

        @Override
        public Optional<Integer> next() {
            if (this.count < this.length) {
                final var value = this.count;
                this.count++;
                return Optional.of(value);
            }
            else {
                return Optional.empty();
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
    }

    private static class Lists {
        public static <T> List<T> empty() {
            return new JavaList<T>();
        }

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

    private static class Joiner implements Collector<String, Optional<String>> {
        @Override
        public Optional<String> createInitial() {
            return Optional.empty();
        }

        @Override
        public Optional<String> fold(Optional<String> current, String element) {
            return Optional.of(current.map(inner -> inner + element).orElse(element));
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

    private record JavaDefinition(Optional<String> maybeBefore, List<String> typeParameters, String type, String name) {
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

    public static void main(String[] args) {
        try {
            final var source = Paths.get(".", "src", "magma", "Main.java");
            final var input = Files.readString(source);
            final var target = source.resolveSibling("Main.c");
            final var string = compile(input);
            Files.writeString(target, string);
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
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


    private static Optional<Tuple<List<String>, String>> compileClass(String input) {
        final var contentStart = input.indexOf('{');
        if (contentStart >= 0) {
            final var beforeContent = input.substring(0, contentStart);
            final var withEnd = input.substring(contentStart + "{".length()).strip();
            if (withEnd.endsWith("}")) {
                final var maybeHeader = compileClassDefinition(beforeContent);
                if (maybeHeader.isPresent()) {
                    final var definition = maybeHeader.get();
                    final var others = compileClassWithDefinition(definition, withEnd);
                    return Optional.of(new Tuple<>(others, ""));
                }
            }
        }

        return Optional.empty();
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

    private static Optional<Tuple<List<String>, String>> compileMethod(String input) {
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
                        return Optional.of(new Tuple<>(Lists.empty(), ""));
                    }

                    final var compiledParameters = compileValues(params, Main::compileParameter);
                    final var header = definition.generate() + "(" + compiledParameters + ")";

                    if (withBraces.equals(";")) {
                        final var generated = header + ";";
                        return Optional.of(new Tuple<>(Lists.of(generated + "\n"), "\n\t" + generated));
                    }

                    if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
                        final var inputContent = withBraces.substring(1, withBraces.length() - 1).strip();
                        final var outputContent = compileStatements(inputContent, Main::compileFunctionSegment);
                        return Optional.of(new Tuple<>(Lists.of(header + " {" +
                                outputContent +
                                "\n}" + "\n"), "\n\t" + header + ";"));
                    }

                    return Optional.empty();
                }
            }
        }

        return Optional.empty();
    }

    private static Optional<JavaDefinition> parseMethodDefinition(String input) {
        return parseDefinition(input).or(() -> parseConstructor(input));
    }

    private static Optional<JavaDefinition> parseConstructor(String input) {
        final var i = input.lastIndexOf(" ");
        if (i >= 0) {
            final var name = input.substring(i + " ".length());
            return Optional.of(new JavaDefinition(Optional.empty(), Lists.empty(), name, "new"));
        }
        else {
            return Optional.empty();
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

    private static Optional<String> compileFunctionStatement(String input) {
        final var stripped = input.strip();
        if (stripped.endsWith(";")) {
            final var withoutEnd = stripped.substring(0, stripped.length() - ";".length());
            return Optional.of("\n\t" + compileFunctionStatementValue(withoutEnd) + ";");
        }

        return Optional.empty();
    }

    private static String compileFunctionStatementValue(String input) {
        final var stripped = input.strip();
        if (stripped.startsWith("return ")) {
            final var value = stripped.substring("return ".length());
            return "return " + compileValue(value);
        }

        final var i = stripped.indexOf("=");
        if (i >= 0) {
            final var substring = stripped.substring(0, i);
            final var substring1 = stripped.substring(i + "=".length());
            return compileValue(substring) + " = " + compileValue(substring1);
        }

        return compileInvokable(stripped).orElseGet(() -> generatePlaceholder(input));
    }

    private static Optional<String> compileInvokable(String input) {
        final var stripped = input.strip();
        if (stripped.endsWith(")")) {
            final var withoutEnd = stripped.substring(0, stripped.length() - ")".length());
            final var argumentsStart = withoutEnd.indexOf("(");
            if (argumentsStart >= 0) {
                final var arguments = withoutEnd.substring(argumentsStart + "(".length());

                final var oldCaller = withoutEnd.substring(0, argumentsStart);
                final var newCaller = oldCaller.startsWith("new ")
                        ? compileConstruction(oldCaller)
                        : compileValue(oldCaller);

                return Optional.of(newCaller + "(" + compileValues(arguments, Main::compileValue) + ")");
            }
        }

        return Optional.empty();
    }

    private static String compileConstruction(String caller) {
        final var type = caller.substring("new ".length());
        return compileType(type).orElseGet(() -> generatePlaceholder(type));
    }

    private static String compileValue(String input) {
        return compileInvokable(input)
                .or(() -> compileOperator(input, "=="))
                .or(() -> compileOperator(input, "+"))
                .or(() -> compileOperator(input, "-"))
                .or(() -> compileAccess(input))
                .or(() -> compileSymbol(input))
                .or(() -> compileNumber(input))
                .orElseGet(() -> generatePlaceholder(input));
    }

    private static Optional<String> compileNumber(String input) {
        final var stripped = input.strip();
        if (isNumber(stripped)) {
            return Optional.of(stripped);
        }
        else {
            return Optional.empty();
        }
    }

    private static Optional<String> compileSymbol(String input) {
        final var stripped = input.strip();
        if (isSymbol(stripped)) {
            return Optional.of(stripped);
        }
        else {
            return Optional.empty();
        }
    }

    private static Optional<String> compileAccess(String input) {
        final var separator = input.lastIndexOf(".");
        if (separator >= 0) {
            final var substring = input.substring(0, separator);
            final var property = input.substring(separator + ".".length()).strip();
            if (isSymbol(property)) {
                return Optional.of(compileValue(substring) + "." + property);
            }
        }

        return Optional.empty();
    }

    private static Optional<String> compileOperator(String input, String infix) {
        final var index = input.indexOf(infix);
        if (index >= 0) {
            final var leftString = input.substring(0, index);
            final var rightString = input.substring(index + infix.length());
            return Optional.of(compileValue(leftString) + " " + infix + " " + compileValue(rightString));
        }

        return Optional.empty();
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

    private static Optional<String> compileWhitespace(String input) {
        if (input.isBlank()) {
            return Optional.of("");
        }
        else {
            return Optional.empty();
        }
    }

    private static Optional<Tuple<List<String>, String>> compileField(String input) {
        final var stripped = input.strip();
        if (stripped.endsWith(";")) {
            final var withoutEnd = stripped.substring(0, stripped.length() - ";".length());
            return parseDefinition(withoutEnd).map(JavaDefinition::generate).map(generated -> {
                return new Tuple<>(Lists.empty(), "\n\t" + generated + ";");
            });
        }

        return Optional.empty();
    }

    private static Optional<JavaDefinition> parseDefinition(String input) {
        final var nameSeparator = input.lastIndexOf(" ");
        if (nameSeparator >= 0) {
            final var beforeName = input.substring(0, nameSeparator).strip();
            final var name = input.substring(nameSeparator + " ".length()).strip();

            if (isSymbol(name)) {
                return parseDefinitionWithBeforeType(beforeName, name);
            }
        }
        return Optional.empty();
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

    private static Optional<JavaDefinition> parseDefinitionWithBeforeType(String beforeName, String name) {
        final var typeSeparator = beforeName.lastIndexOf(" ");
        if (typeSeparator < 0) {
            return compileType(beforeName).map(type -> {
                return new JavaDefinition(Optional.empty(), Lists.empty(), type, name);
            });
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
                    return new JavaDefinition(Optional.of(beforeTypeParameters), typeParameters, compiledType, name);
                }
            }

            return new JavaDefinition(Optional.of(beforeType), Lists.empty(), compiledType, name);
        });
    }

    private static Optional<String> compileType(String input) {
        final var stripped = input.strip();
        if (stripped.equals("private") || stripped.equals("public")) {
            return Optional.empty();
        }

        if (stripped.equals("char")) {
            return Optional.of("char");
        }

        if (stripped.equals("boolean") || stripped.equals("int")) {
            return Optional.of("int");
        }

        if (stripped.equals("String")) {
            return Optional.of(generatePlaceholder("Slice<char>"));
        }

        if (isSymbol(stripped)) {
            return Optional.of("struct " + stripped);
        }

        return Optional.of(generatePlaceholder(input));
    }

    private static Optional<ClassDefinition> compileClassDefinition(String input) {
        return compileClassDefinitionWithKeyword(input, "class ")
                .or(() -> compileClassDefinitionWithKeyword(input, "interface "))
                .or(() -> compileClassDefinitionWithKeyword(input, "record "));
    }

    private static Optional<ClassDefinition> compileClassDefinitionWithKeyword(String input, String keyword) {
        final var classIndex = input.indexOf(keyword);
        if (classIndex < 0) {
            return Optional.empty();
        }

        final var beforeKeyword = input.substring(0, classIndex).strip();
        final var afterKeyword = input.substring(classIndex + keyword.length()).strip();
        return Optional.of(parseClassDefinitionWithParameters(beforeKeyword, afterKeyword));
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
