package magma;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

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
    }

    public interface Error {
        String display();
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

    interface Rule extends Function<String, Main.Result<String, Main.CompileError>> {

    }

    public record ApplicationError(Error error) implements Error {
        @Override
        public String display() {
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

    private static class Iterators {
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

    private record CompileError(String message, String context, List_<CompileError> errors) implements Error {
        public CompileError(String message, String context) {
            this(message, context, Lists.empty());
        }

        @Override
        public String display() {
            String joiner = this.errors.iter()
                    .map(CompileError::display)
                    .map(value -> "\n" + value)
                    .collect(new Joiner(""))
                    .orElse("");

            return this.message + ": " + this.context + joiner;
        }
    }

    private record OrState(Option<String> maybeValue, List_<CompileError> errors) {
        public OrState() {
            this(new None<>(), Lists.empty());
        }

        public OrState withValue(String value) {
            return new OrState(new Some<>(value), this.errors);
        }

        public OrState withError(CompileError error) {
            return new OrState(this.maybeValue, this.errors.add(error));
        }

        public Result<String, List_<CompileError>> toResult() {
            return this.maybeValue.<Result<String, List_<CompileError>>>match(Ok::new, () -> new Err<String, List_<CompileError>>(this.errors));
        }
    }

    public static final List_<String> FUNCTIONAL_NAMESPACE = Lists.of("java", "util", "function");
    private static final List_<String> imports = Lists.empty();
    private static final List_<String> structs = Lists.empty();
    private static final List_<String> globals = Lists.empty();
    private static final List_<String> methods = Lists.empty();
    private static int counter = 0;

    public static void main(String[] args) {
        Path_ source = Paths.get(".", "src", "java", "magma", "Main.java");
        Files.readString(source)
                .mapErr(ApplicationError::new)
                .match(input -> compileAndWrite(source, input), Some::new)
                .ifPresent(error -> System.err.println(error.display()));
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
        return parseAll(segments, Main::compileRootSegment)
                .mapValue(Main::mergeStatics)
                .mapValue(compiled -> mergeAll(compiled, Main::mergeStatements));
    }

    private static Result<String, CompileError> compileRootSegment(String input0) {
        return compileOr(input0, listRules());
    }

    private static Result<String, CompileError> compileOr(String input, List_<Rule> rules) {
        return rules.iter()
                .foldWithInitial(new OrState(), (orState, rule) -> rule.apply(input).match(orState::withValue, orState::withError))
                .toResult()
                .mapErr(errors -> new CompileError("No valid combination present", input, errors));
    }

    private static List_<Rule> listRules() {
        return Lists.of(
                Main::compileWhitespace,
                Main::compilePackage,
                Main::getStringCompileErrorResult,
                (input) -> compileToStruct(input, "class ", Lists.empty())
        );
    }

    private static Result<String, CompileError> getStringCompileErrorResult(String input) {
        return wrap(compileImport(input));
    }

    private static Result<String, CompileError> compilePackage(String input) {
        if (input.startsWith("package ")) {
            return new Ok<>("");
        }
        return new Err<>(new CompileError("Prefix 'package ' not present", input));
    }

    private static List_<String> mergeStatics(List_<String> list) {
        return Lists.<String>empty()
                .addAll(imports)
                .addAll(structs)
                .addAll(globals)
                .addAll(methods)
                .addAll(list);
    }

    private static Result<String, CompileError> compileStatements(String input, Rule compiler) {
        return compileAndMerge(divide(input, new DecoratedDivider(Main::divideStatementChar)), compiler, Main::mergeStatements);
    }

    private static Result<String, CompileError> compileAndMerge(List_<String> segments, Rule compiler, BiFunction<StringBuilder, String, StringBuilder> merger) {
        return parseAll(segments, compiler).mapValue(compiled -> mergeAll(compiled, merger));
    }

    private static String mergeAll(List_<String> compiled, BiFunction<StringBuilder, String, StringBuilder> merger) {
        return compiled.iter().foldWithInitial(new StringBuilder(), merger).toString();
    }

    private static Result<List_<String>, CompileError> parseAll(List_<String> segments, Rule mapper) {
        return segments.iter().foldToResult(Lists.empty(),
                (current, element) -> mapper.apply(element).mapValue(current::add));
    }

    @Deprecated
    private static <T> Result<T, CompileError> wrap(Option<T> option) {
        return option.<Result<T, CompileError>>map(Ok::new).orElseGet(() -> new Err<>(new CompileError("No value present", "")));
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

    private static Option<String> compileImport(String input) {
        String stripped = input.strip();
        if (!stripped.startsWith("import ")) {
            return new None<>();
        }

        String right = stripped.substring("import ".length());
        if (!right.endsWith(";")) {
            return new None<>();
        }

        String content = right.substring(0, right.length() - ";".length());
        List_<String> split = divide(content, new DelimitedDivider('.'));
        if (split.size() >= 3 && Lists.equals(split.slice(0, 3), FUNCTIONAL_NAMESPACE, String::equals)) {
            return new Some<>("");
        }

        String joined = split.iter().collect(new Joiner("/")).orElse("");
        imports.add("#include \"./" + joined + "\"\n");
        return new Some<>("");
    }

    private static Result<String, CompileError> compileToStruct(String input, String infix, List_<String> typeParams) {
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

        String withoutParams = getString(withoutExtends);
        String strippedWithoutParams = withoutParams.strip();
        String name = getName(strippedWithoutParams);

        if (!isSymbol(name)) {
            return new Err<>(new CompileError("Not a symbol", name));
        }

        String withEnd = afterKeyword.substring(contentStart + "{".length()).strip();
        if (!withEnd.endsWith("}")) {
            return new Err<>(new CompileError("Suffix '}' not present", withEnd));
        }

        String inputContent = withEnd.substring(0, withEnd.length() - "}".length());
        return compileStatements(inputContent, s -> compileClassMember(s, typeParams)).mapValue(outputContent -> {
            structs.add("struct " + name + " {\n" + outputContent + "};\n");
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

    private static String getName(String strippedWithoutParams) {
        String name;
        if (strippedWithoutParams.endsWith(">")) {
            int genStart = strippedWithoutParams.indexOf("<");
            if (genStart >= 0) {
                name = strippedWithoutParams.substring(0, genStart).strip();
            }
            else {
                name = strippedWithoutParams;
            }
        }
        else {
            name = strippedWithoutParams;
        }
        return name;
    }

    private static String getString(String strippedBeforeImplements) {
        String withoutParams;
        if (strippedBeforeImplements.endsWith(")")) {
            String withoutEnd = strippedBeforeImplements.substring(0, strippedBeforeImplements.length() - ")".length());
            int paramStart = withoutEnd.indexOf("(");
            if (paramStart >= 0) {
                withoutParams = withoutEnd.substring(0, paramStart).strip();
            }
            else {
                withoutParams = strippedBeforeImplements;
            }
        }
        else {
            withoutParams = strippedBeforeImplements;
        }
        return withoutParams;
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

    private static Result<String, CompileError> compileClassMember(String input0, List_<String> typeParams) {
        return compileOr(input0, Lists.of(
                Main::compileWhitespace,
                (input) -> compileToStruct(input, "interface ", typeParams),
                (input) -> compileToStruct(input, "record ", typeParams),
                (input) -> compileToStruct(input, "class ", typeParams),
                (input) -> compileGlobalInitialization(typeParams, input),
                Main::compileDefinitionStatement,
                (input) -> compileMethod(typeParams, input)
        ));
    }

    private static Result<String, CompileError> compileMethod(List_<String> typeParams, String input) {
        return wrap(compileMethod0(input, typeParams));
    }

    private static Result<String, CompileError> compileDefinitionStatement(String input) {
        String stripped = input.strip();
        if (stripped.endsWith(";")) {
            String content = stripped.substring(0, stripped.length() - ";".length());
            return compileDefinition(content).mapValue(result -> "\t" + result + ";\n");
        }
        return createSuffixErr(input, ";");
    }

    private static Err<String, CompileError> createSuffixErr(String input, String suffix) {
        return new Err<>(new CompileError("Suffix '" + suffix + "' not present", input));
    }

    private static Result<String, CompileError> compileGlobalInitialization(List_<String> typeParams, String input) {
        return wrap(compileGlobalInitialization0(input, typeParams));
    }

    private static Option<String> compileGlobalInitialization0(String input, List_<String> typeParams) {
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
        return unwrap(compileDefinition(definition)).flatMap(outputDefinition -> unwrap(compileValue(value, typeParams, depth)).map(outputValue -> outputDefinition + " = " + outputValue));
    }

    private static Result<String, CompileError> compileWhitespace(String input) {
        if (input.isBlank()) {
            return new Ok<>("");
        }
        return new Err<>(new CompileError("Not blank", input));
    }

    private static Option<String> compileMethod0(String input, List_<String> typeParams) {
        int paramStart = input.indexOf("(");
        if (paramStart < 0) {
            return new None<>();
        }

        String inputDefinition = input.substring(0, paramStart).strip();
        String withParams = input.substring(paramStart + "(".length());

        return unwrap(compileDefinition(inputDefinition)).flatMap(outputDefinition -> {
            int paramEnd = withParams.indexOf(")");
            if (paramEnd < 0) {
                return new None<>();
            }

            String params = withParams.substring(0, paramEnd);
            return compileValues(params, Main::compileParameter).findValue()
                    .flatMap(outputParams -> assembleMethodBody(typeParams, outputDefinition, outputParams, withParams.substring(paramEnd + ")".length()).strip()));
        });
    }

    private static Option<String> assembleMethodBody(
            List_<String> typeParams,
            String definition,
            String params,
            String body
    ) {
        String header = "\t".repeat(0) + definition + "(" + params + ")";
        if (body.startsWith("{") && body.endsWith("}")) {
            String inputContent = body.substring("{".length(), body.length() - "}".length());
            return unwrap(compileStatements(inputContent, s -> wrap(compileStatementOrBlock(s, typeParams, 1)))).flatMap(outputContent -> {
                methods.add(header + " {" + outputContent + "\n}\n");
                return new Some<>("");
            });
        }

        return new Some<>("\t" + header + ";\n");
    }

    private static Result<String, CompileError> compileParameter(String definition) {
        return compileOr(definition, Lists.of(
                Main::compileWhitespace,
                Main::compileDefinition
        ));
    }

    private static Result<String, CompileError> compileValues(String input, Rule rule) {
        return compileValues(divide(input, new DecoratedDivider(Main::divideValueChar)), rule);
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

    private static Result<String, CompileError> compileValues(List_<String> segments, Rule rule) {
        return compileAndMerge(segments, rule, Main::mergeValues);
    }

    private static Option<String> compileStatementOrBlock(String input, List_<String> typeParams, int depth) {
        return unwrap(compileWhitespace(input))
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
                .or(() -> {
                    String stripped = input.strip();
                    if (stripped.endsWith(";")) {
                        String content = stripped.substring(0, stripped.length() - ";".length());
                        return unwrap(compileDefinition(content)).map(result -> "\t" + result + ";\n");
                    }
                    return new None<>();
                })
                .or(() -> unwrap(new Err<String, CompileError>(new CompileError("Invalid input: ", input))));
    }

    private static Option<String> compilePostOperator(String input, List_<String> typeParams, int depth, String operator) {
        String stripped = input.strip();
        if (stripped.endsWith(operator + ";")) {
            String slice = stripped.substring(0, stripped.length() - (operator + ";").length());
            return unwrap(compileValue(slice, typeParams, depth)).map(value -> value + operator + ";");
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
                return unwrap(compileStatements(withoutKeyword.substring(1, withoutKeyword.length() - 1), s -> wrap(compileStatementOrBlock(s, typeParams, depth + 1))))
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

        return unwrap(compileValue(oldCondition, typeParams, depth)).flatMap(newCondition -> {
            String withCondition = createIndent(depth) + prefix + "(" + newCondition + ")";

            if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
                String content = withBraces.substring(1, withBraces.length() - 1);
                Result<String, CompileError> stringCompileErrorResult = compileStatements(content, s -> wrap(compileStatementOrBlock(s, typeParams, depth + 1)));
                return stringCompileErrorResult.mapValue(statements -> withCondition +
                        " {" + statements + "\n" +
                        "\t".repeat(depth) +
                        "}").findValue();
            }
            else {
                return compileStatementOrBlock(withBraces, typeParams, depth).map(result -> withCondition + " " + result);
            }
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
                if (queue.popFirst().orElse(null).left.right == '\\') {
                    queue.popFirst().orElse(null);
                }

                queue.popFirst().orElse(null);
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
                return unwrap(compileValue(destination, typeParams, depth)).flatMap(newDest -> unwrap(compileValue(source, typeParams, depth)).map(newSource -> newDest + " = " + newSource));
            }
        }
        return new None<>();
    }

    private static Option<String> compileReturn(String input, List_<String> typeParams, int depth) {
        String stripped = input.strip();
        if (stripped.endsWith(";")) {
            String withoutEnd = stripped.substring(0, stripped.length() - ";".length());
            if (withoutEnd.startsWith("return ")) {
                return unwrap(compileValue(withoutEnd.substring("return ".length()), typeParams, depth)).map(result -> "return " + result);
            }
        }

        return new None<>();
    }

    private static Result<String, CompileError> compileValue(String input, List_<String> typeParams, int depth) {
        return wrap(getStringOption(input, typeParams, depth));
    }

    private static Option<String> getStringOption(String input, List_<String> typeParams, int depth) {
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
                    return unwrap(compileType(type, typeParams)).flatMap(outputType -> compileArgs(argsString, typeParams, depth).map(value -> outputType + value));
                }
            }
        }

        if (stripped.startsWith("!")) {
            return unwrap(compileValue(stripped.substring(1), typeParams, depth)).map(result -> "!" + result);
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
                return unwrap(compileType(type, typeParams)).flatMap(compiled -> generateLambdaWithReturn(Lists.empty(), "\n\treturn " + compiled + "." + property + "()"));
            }
        }

        int separator = input.lastIndexOf(".");
        if (separator >= 0) {
            String object = input.substring(0, separator).strip();
            String property = input.substring(separator + ".".length()).strip();
            return unwrap(compileValue(object, typeParams, depth)).map(compiled -> compiled + "." + property);
        }

        return compileOperator(input, typeParams, depth, "||")
                .or(() -> compileOperator(input, typeParams, depth, "<"))
                .or(() -> compileOperator(input, typeParams, depth, "+"))
                .or(() -> compileOperator(input, typeParams, depth, ">="))
                .or(() -> compileOperator(input, typeParams, depth, "&&"))
                .or(() -> compileOperator(input, typeParams, depth, "=="))
                .or(() -> compileOperator(input, typeParams, depth, "!="))
                .or(() -> unwrap(new Err<String, CompileError>(new CompileError("Invalid input: ", input))));
    }

    private static Option<String> compileOperator(String input, List_<String> typeParams, int depth, String operator) {
        int operatorIndex = input.indexOf(operator);
        if (operatorIndex < 0) {
            return new None<>();
        }

        String left = input.substring(0, operatorIndex);
        String right = input.substring(operatorIndex + operator.length());

        return unwrap(compileValue(left, typeParams, depth)).flatMap(leftResult -> unwrap(compileValue(right, typeParams, depth)).map(rightResult -> leftResult + " " + operator + " " + rightResult));
    }

    private static Option<String> compileLambda(String input, List_<String> typeParams, int depth) {
        int arrowIndex = input.indexOf("->");
        if (arrowIndex < 0) {
            return new None<>();
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
            return new None<>();
        }

        String value = input.substring(arrowIndex + "->".length()).strip();
        if (value.startsWith("{") && value.endsWith("}")) {
            String slice = value.substring(1, value.length() - 1);
            return unwrap(compileStatements(slice, s -> wrap(compileStatementOrBlock(s, typeParams, depth))))
                    .flatMap(result -> generateLambdaWithReturn(paramNames, result));
        }

        return unwrap(compileValue(value, typeParams, depth)).flatMap(newValue -> generateLambdaWithReturn(paramNames, "\n\treturn " + newValue + ";"));
    }

    private static Option<String> generateLambdaWithReturn(List_<String> paramNames, String returnValue) {
        int current = counter;
        counter++;
        String lambdaName = "__lambda" + current + "__";

        String joined = paramNames.iter()
                .map(name -> "auto " + name)
                .collect(new Joiner(", "))
                .map(value -> "(" + value + ")")
                .orElse("");

        methods.add("auto " + lambdaName + joined + " {" + returnValue + "\n}\n");
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
                return unwrap(compileValue(type, typeParams, depth)).flatMap(caller -> compileArgs(withEnd, typeParams, depth).map(value -> caller + value));
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
        return compileValues(argsString, arg -> compileOr(arg, Lists.of(
                Main::compileWhitespace,
                value -> compileValue(value, typeParams, depth)
        ))).findValue().map(args -> "(" + args + ")");
    }

    private static StringBuilder mergeValues(StringBuilder cache, String element) {
        if (cache.isEmpty()) {
            return cache.append(element);
        }
        return cache.append(", ").append(element);
    }

    private static Result<String, CompileError> compileDefinition(String definition) {
        return wrap(getStringOption(definition));
    }

    private static Option<String> getStringOption(String definition) {
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
                    typeParams = Lists.empty();
                }
            }
            else {
                typeParams = Lists.empty();
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

            boolean allSymbols = divide(modifiersString, new DelimitedDivider(' '))
                    .iter()
                    .map(String::strip)
                    .filter(value -> !value.isEmpty())
                    .allMatch(Main::isSymbol);

            if (!allSymbols) {
                return new None<>();
            }

            String inputType = beforeName.substring(typeSeparator + " ".length());
            return unwrap(compileType(inputType, typeParams)).flatMap(outputType -> new Some<>(generateDefinition(typeParams, outputType, name)));
        }
        else {
            return unwrap(compileType(beforeName, Lists.empty())).flatMap(outputType -> new Some<>(generateDefinition(Lists.empty(), outputType, name)));
        }
    }

    private static List_<String> splitValues(String substring) {
        String stripped = substring.strip();
        return divide(stripped, new DelimitedDivider(','))
                .iter()
                .map(String::strip)
                .filter(param -> !param.isEmpty())
                .collect(new ListCollector<>());
    }

    private static String generateDefinition(List_<String> maybeTypeParams, String type, String name) {
        return generateTypeParams(maybeTypeParams) + type + " " + name;
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

    private static Result<String, CompileError> compileType(String input, List_<String> typeParams) {
        return compileOr(input, Lists.of(
                value -> wrap(compilePrimitive(value)),
                value -> wrap(compileArray(value, typeParams)),
                value -> wrap(compileSymbol(value, typeParams)),
                value -> wrap(compileGeneric(value, typeParams))
        ));
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

    private static Option<String> compileGeneric(String input, List_<String> typeParams) {
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
        return compileValues(params, s -> getWrap(typeParams, s)).findValue()
                .map(compiled -> base + "<" + compiled + ">");
    }

    private static Result<String, CompileError> getWrap(List_<String> typeParams, String s) {
        return compileOr(s, Lists.of(
                Main::compileWhitespace,
                type -> compileType(type, typeParams)
        ));
    }

    private static Option<String> compileArray(String input, List_<String> typeParams) {
        if (input.endsWith("[]")) {
            return unwrap(compileType(input.substring(0, input.length() - "[]".length()), typeParams))
                    .map(value -> value + "*");
        }
        return new None<>();
    }

    private static Option<String> compileSymbol(String input, List_<String> typeParams) {
        if (!isSymbol(input.strip())) {
            return new None<>();
        }

        if (Lists.contains(typeParams, input.strip(), String::equals)) {
            return new Some<>(input.strip());
        }
        else {
            return new Some<>("struct " + input.strip());
        }
    }

    private static boolean isSymbol(String input) {
        if (input.isBlank()) {
            return false;
        }

        return Iterators.fromStringWithIndices(input).allMatch(tuple -> {
            Integer index = tuple.left;
            char c = tuple.right;
            return c == '_' || Character.isLetter(c) || (index != 0 && Character.isDigit(c));
        });
    }

    @Deprecated
    private static <T> Option<T> unwrap(Result<T, CompileError> result) {
        return result.match(Some::new, error -> {
            System.err.println(error.display());
            return new None<>();
        });
    }
}
