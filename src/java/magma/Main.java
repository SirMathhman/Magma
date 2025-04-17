package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class Main {
    public interface List<T> {
        Stream<T> stream();

        List<T> add(T element);

        boolean isEmpty();

        Option<Tuple<T, List<T>>> pop();

        T last();

        List<T> setLast(T element);
    }

    public interface Stream<T> {
        Stream<T> concat(Stream<T> other);

        <C> C collect(Collector<T, C> collector);

        <R> R fold(R initial, BiFunction<R, T, R> folder);

        <R> Stream<R> map(Function<T, R> mapper);

        Option<T> next();
    }

    public interface Option<T> {

        <R> Option<R> map(Function<T, R> mapper);

        Option<T> or(Supplier<Option<T>> other);

        T orElse(T other);

        boolean isPresent();

        T orElseGet(Supplier<T> other);

        <R> Option<R> flatMap(Function<T, Option<R>> mapper);
    }

    public interface Collector<T, C> {
        C createInitial();

        C fold(C current, T element);
    }

    private interface Head<T> {
        Option<T> next();
    }

    record Some<T>(T value) implements Option<T> {
        @Override
        public <R> Option<R> map(Function<T, R> mapper) {
            return new Some<>(mapper.apply(this.value));
        }

        @Override
        public Option<T> or(Supplier<Option<T>> other) {
            return this;
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
        public T orElseGet(Supplier<T> other) {
            return this.value;
        }

        @Override
        public <R> Option<R> flatMap(Function<T, Option<R>> mapper) {
            return mapper.apply(this.value);
        }
    }

    static class None<T> implements Option<T> {
        @Override
        public <R> Option<R> map(Function<T, R> mapper) {
            return new None<>();
        }

        @Override
        public Option<T> or(Supplier<Option<T>> other) {
            return other.get();
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
        public T orElseGet(Supplier<T> other) {
            return other.get();
        }

        @Override
        public <R> Option<R> flatMap(Function<T, Option<R>> mapper) {
            return new None<>();
        }
    }

    private static class DivideState {
        private final List<String> segments;
        private final String buffer;
        private final int depth;
        private final List<Character> queue;

        private DivideState(List<Character> queue, List<String> segments, String buffer, int depth) {
            this.segments = segments;
            this.buffer = buffer;
            this.depth = depth;
            this.queue = queue;
        }

        public DivideState(List<Character> queue) {
            this(queue, Lists.empty(), "", 0);
        }

        private Option<DivideState> popAndAppend() {
            return this.pop().map(popped -> popped.right.append(popped.left));
        }

        private Stream<String> stream() {
            return this.segments.stream();
        }

        private DivideState advance() {
            return new DivideState(this.queue, this.segments.add(this.buffer), "", this.depth);
        }

        private DivideState append(char c) {
            return new DivideState(this.queue, this.segments, this.buffer + c, this.depth);
        }

        public boolean isLevel() {
            return this.depth == 0;
        }

        public DivideState enter() {
            return new DivideState(this.queue, this.segments, this.buffer, this.depth + 1);
        }

        public DivideState exit() {
            return new DivideState(this.queue, this.segments, this.buffer, this.depth - 1);
        }

        public boolean isShallow() {
            return this.depth == 1;
        }

        public Option<Tuple<Character, DivideState>> pop() {
            return this.queue.pop().map(tuple -> {
                return new Tuple<>(tuple.left, new DivideState(tuple.right, this.segments, this.buffer, this.depth));
            });
        }
    }

    public record Tuple<A, B>(A left, B right) {
    }

    private record CompilerState(List<String> structs, List<String> methods, List<List<String>> frames) {
        public CompilerState() {
            this(Lists.empty(), Lists.empty(), Lists.of(Lists.empty()));
        }

        public CompilerState addStruct(String element) {
            return new CompilerState(this.structs.add(element), this.methods, Lists.empty());
        }

        public CompilerState addMethod(String element) {
            return new CompilerState(this.structs, this.methods.add(element), Lists.empty());
        }

        public CompilerState defineType(String name) {
            return new CompilerState(this.structs, this.methods, this.frames.setLast(this.frames.last().add(name)));
        }

        public CompilerState enter() {
            return new CompilerState(this.structs, this.methods, this.frames.add(Lists.empty()));
        }

        public CompilerState exit() {
            return new CompilerState(this.structs, this.methods, this.frames.pop().map(Tuple::right).orElse(null));
        }
    }

    private static class Joiner implements Collector<String, Option<String>> {
        @Override
        public Option<String> createInitial() {
            return new None<>();
        }

        @Override
        public Option<String> fold(Option<String> current, String element) {
            return new Some<String>(current.map(inner -> inner + element).orElse(element));
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
                int value = this.counter;
                this.counter++;
                return new Some<Integer>(value);
            }
            else {
                return new None<>();
            }
        }
    }

    public record HeadedStream<T>(Head<T> head) implements Stream<T> {
        @Override
        public Stream<T> concat(Stream<T> other) {
            return new HeadedStream<>(() -> this.head.next().or(other::next));
        }

        @Override
        public <C> C collect(Collector<T, C> collector) {
            return this.fold(collector.createInitial(), collector::fold);
        }

        @Override
        public <R> R fold(R initial, BiFunction<R, T, R> folder) {
            R current = initial;
            while (true) {
                R finalCurrent = current;
                Option<R> result = this.next().map(next -> folder.apply(finalCurrent, next));
                if (result.isPresent()) {
                    current = result.orElse(null);
                }
                else {
                    return current;
                }
            }
        }

        @Override
        public <R> Stream<R> map(Function<T, R> mapper) {
            return new HeadedStream<>(() -> this.head.next().map(mapper));
        }

        @Override
        public Option<T> next() {
            return this.head.next();
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
            Path source = Paths.get(".", "src", "java", "magma", "Main.java");
            String input = Files.readString(source);
            Path target = source.resolveSibling("main.c");
            Files.writeString(target, compile(input));
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static String compile(String input) {
        Tuple<CompilerState, String> tuple = compileStatements(input, new CompilerState(), Main::compileRootSegment);
        CompilerState elements = tuple.left.addStruct(tuple.right);

        Stream<String> left = elements.structs.stream();
        String joined = left.concat(elements.methods.stream())
                .collect(new Joiner())
                .orElse("");

        return joined + "int main(){\n\treturn 0;\n}\n";
    }

    private static Tuple<CompilerState, String> compileStatements(String input, CompilerState structs, BiFunction<CompilerState, String, Tuple<CompilerState, String>> compiler) {
        return divideStatements(input).fold(new Tuple<>(structs, ""), (tuple, element) -> foldSegment(tuple, element, compiler));
    }

    private static Tuple<CompilerState, String> foldSegment(Tuple<CompilerState, String> tuple, String element, BiFunction<CompilerState, String, Tuple<CompilerState, String>> compiler) {
        CompilerState currentStructs = tuple.left;
        String currentOutput = tuple.right;

        Tuple<CompilerState, String> compiledStruct = compiler.apply(currentStructs, element);
        CompilerState compiledStructs = compiledStruct.left;
        String compiledElement = compiledStruct.right;

        return new Tuple<>(compiledStructs, currentOutput + compiledElement);
    }

    private static Stream<String> divideStatements(String input) {
        List<Character> queue = new HeadedStream<>(new RangeHead(input.length()))
                .map(input::charAt)
                .collect(new ListCollector<>());

        DivideState current = new DivideState(queue);
        while (true) {
            Option<Tuple<Character, DivideState>> maybeNext = current.pop();
            if (!maybeNext.isPresent()) {
                break;
            }

            Tuple<Character, DivideState> tuple = maybeNext.orElse(new Tuple<>('\0', current));
            char next = tuple.left;
            DivideState finalCurrent = tuple.right;
            current = divideSingleQuotes(finalCurrent, next)
                    .orElseGet(() -> divideStatementChar(finalCurrent, next));
        }
        return current.advance().stream();
    }

    private static Option<DivideState> divideSingleQuotes(DivideState current, char c) {
        if (c != '\'') {
            return new None<>();
        }

        return current.append(c).pop().flatMap(maybeSlashTuple -> {
            char maybeSlash = maybeSlashTuple.left;
            DivideState withMaybeSlash = maybeSlashTuple.right.append(maybeSlash);
            Option<DivideState> divideState = maybeSlash == '\\' ? withMaybeSlash.popAndAppend() : new None<>();
            return divideState.flatMap(DivideState::popAndAppend);
        });
    }

    private static DivideState divideStatementChar(DivideState divideState, char c) {
        DivideState appended = divideState.append(c);
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

    private static Tuple<CompilerState, String> compileRootSegment(CompilerState state, String input) {
        String stripped = input.strip();
        if (stripped.startsWith("package ")) {
            return new Tuple<>(state, "");
        }

        if (stripped.startsWith("import ")) {
            return new Tuple<>(state, "// #include <temp.h>\n");
        }

        return compileClass(state, stripped)
                .or(() -> compileWhitespace(state, stripped))
                .orElseGet(() -> generatePlaceholderToTuple(state, stripped));
    }

    private static Tuple<CompilerState, String> generatePlaceholderToTuple(CompilerState state, String stripped) {
        return new Tuple<>(state, generatePlaceholder(stripped));
    }

    private static String generatePlaceholder(String stripped) {
        return "/* " + stripped + " */";
    }

    private static Option<Tuple<CompilerState, String>> compileClass(CompilerState state, String stripped) {
        return compileToStruct(state, stripped, "class ");
    }

    private static Option<Tuple<CompilerState, String>> compileToStruct(CompilerState state, String input, String infix) {
        String stripped = input.strip();
        int classIndex = stripped.indexOf(infix);
        if (classIndex < 0) {
            return new None<>();
        }

        String afterKeyword = stripped.substring(classIndex + infix.length());
        int contentStart = afterKeyword.indexOf("{");
        if (contentStart < 0) {
            return new None<>();
        }

        String beforeContent = afterKeyword.substring(0, contentStart).strip();

        int implementsIndex = beforeContent.indexOf(" implements ");
        String withoutImplements = implementsIndex >= 0
                ? beforeContent.substring(0, implementsIndex).strip()
                : beforeContent.strip();

        int paramStart = withoutImplements.indexOf("(");
        String withoutParams = paramStart >= 0
                ? withoutImplements.substring(0, withoutImplements.indexOf("(")).strip()
                : withoutImplements.strip();

        int typeParamStart = withoutParams.indexOf("<");
        String withoutTypeParams = typeParamStart >= 0
                ? withoutParams.substring(0, typeParamStart).strip()
                : withoutParams.strip();

        String withEnd = afterKeyword.substring(contentStart + "{".length()).strip();
        if (!withEnd.endsWith("}")) {
            return new None<>();
        }

        String inputContent = withEnd.substring(0, withEnd.length() - "}".length());
        CompilerState defined = state.enter().defineType(withoutTypeParams);
        Tuple<CompilerState, String> outputTuple = compileStatements(inputContent, defined, Main::compileClassSegment);
        CompilerState outputStructs = outputTuple.left.exit();
        String outputContent = outputTuple.right;

        String generated = "struct %s {%s\n};\n".formatted(withoutTypeParams, outputContent);
        CompilerState withGenerated = outputStructs.addStruct(generated);
        return new Some<Tuple<CompilerState, String>>(new Tuple<CompilerState, String>(withGenerated, ""));
    }

    private static Tuple<CompilerState, String> compileClassSegment(CompilerState state, String input) {
        return compileWhitespace(state, input)
                .or(() -> compileClass(state, input))
                .or(() -> compileToStruct(state, input, "interface "))
                .or(() -> compileToStruct(state, input, "record "))
                .or(() -> compileMethod(state, input))
                .or(() -> compileStatement(state, input, Main::compileDefinition))
                .or(() -> compileStatement(state, input, Main::compileInitialization))
                .orElseGet(() -> generatePlaceholderToTuple(state, input.strip()));
    }

    private static Option<Tuple<CompilerState, String>> compileWhitespace(CompilerState state, String input) {
        String stripped = input.strip();
        if (stripped.isEmpty()) {
            return new Some<Tuple<CompilerState, String>>(new Tuple<CompilerState, String>(state, stripped));
        }
        else {
            return new None<>();
        }
    }

    private static Option<Tuple<CompilerState, String>> compileInitialization(CompilerState state, String input) {
        int valueSeparator = input.indexOf("=");
        if (valueSeparator >= 0) {
            String definition = input.substring(0, valueSeparator).strip();
            String value = input.substring(valueSeparator + "=".length()).strip();
            return compileDefinition(state, definition).map(outputDefinition -> {
                return new Tuple<>(outputDefinition.left, outputDefinition.right + " = " + compileValue(value));
            });
        }
        else {
            return new None<>();
        }
    }

    private static String compileValue(String value) {
        if (isNumber(value.strip())) {
            return value;
        }
        return generatePlaceholder(value);
    }

    private static boolean isNumber(String input) {
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (Character.isDigit(c)) {
                continue;
            }
            return false;
        }
        return true;
    }

    private static Option<Tuple<CompilerState, String>> compileMethod(CompilerState state, String input) {
        int paramStart = input.indexOf("(");
        if (paramStart >= 0) {
            String inputDefinition = input.substring(0, paramStart).strip();
            return compileDefinition(state, inputDefinition).flatMap(definitionTuple -> new Some<Tuple<CompilerState, String>>(new Tuple<CompilerState, String>(definitionTuple.left.addMethod(definitionTuple.right + "(){\n}\n"), "")));
        }
        else {
            return new None<>();
        }
    }

    private static Option<Tuple<CompilerState, String>> compileStatement(CompilerState state, String input, BiFunction<CompilerState, String, Option<Tuple<CompilerState, String>>> compileDefinition) {
        String stripped = input.strip();
        if (!stripped.endsWith(";")) {
            return new None<>();
        }

        String withoutEnd = stripped.substring(0, stripped.length() - ";".length());
        return compileDefinition.apply(state, withoutEnd).map(tuple -> new Tuple<>(tuple.left, "\n\t" + tuple.right + ";"));
    }

    private static Option<Tuple<CompilerState, String>> compileDefinition(CompilerState state, String input) {
        String stripped = input.strip();
        int nameSeparator = stripped.lastIndexOf(" ");
        if (nameSeparator < 0) {
            return new None<>();
        }

        String beforeName = stripped.substring(0, nameSeparator).strip();

        Option<String> outputBeforeString = compileTypeProperty(beforeName);

        String oldName = stripped.substring(nameSeparator + " ".length()).strip();
        if (isSymbol(oldName)) {
            String newName = oldName.equals("main") ? "__main__" : oldName;
            return outputBeforeString.map(type -> new Tuple<>(state, type + " " + newName));
        }
        else {
            return new None<>();
        }
    }

    private static Option<String> compileTypeProperty(String beforeName) {
        int typeSeparator = findTypeSeparator(beforeName);
        if (typeSeparator >= 0) {
            String beforeType = beforeName.substring(0, typeSeparator).strip();
            String type = beforeName.substring(typeSeparator + " ".length()).strip();
            return compileType(type).map(outputType -> generatePlaceholder(beforeType) + " " + outputType);
        }
        else {
            return compileType(beforeName);
        }
    }

    private static int findTypeSeparator(String beforeName) {
        int typeSeparator = -1;
        int depth = 0;
        for (int i = beforeName.length() - 1; i >= 0; i--) {
            char c = beforeName.charAt(i);
            if (c == ' ' && depth == 0) {
                typeSeparator = i;
                break;
            }
            if (c == '>') {
                depth++;
            }
            if (c == '<') {
                depth--;
            }
        }
        return typeSeparator;
    }

    private static boolean isSymbol(String input) {
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (Character.isLetter(c)) {
                continue;
            }
            return false;
        }

        return true;
    }

    private static Option<String> compileType(String input) {
        String stripped = input.strip();
        if (stripped.equals("public") || stripped.equals("private")) {
            return new None<>();
        }

        if (stripped.equals("int") || stripped.equals("boolean")) {
            return new Some<String>("int");
        }

        if (stripped.equals("void")) {
            return new Some<String>("void");
        }

        if (stripped.equals("char")) {
            return new Some<String>("char");
        }

        if (stripped.equals("String")) {
            return new Some<String>("char*");
        }

        int typeParamStart = stripped.indexOf("<");
        if (typeParamStart >= 0) {
            return new Some<String>("struct " + stripped.substring(0, typeParamStart).strip());
        }

        if (isSymbol(stripped)) {
            return new Some<String>("struct " + stripped);
        }

        return new Some<String>(generatePlaceholder(stripped));
    }
}
