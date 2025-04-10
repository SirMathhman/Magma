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

        List_<T> sort(BiFunction<T, T, Integer> comparator);
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

    public record CompileError(String message, String context, List_<CompileError> errors) implements Error {
        public CompileError(String message, String context) {
            this(message, context, Lists.empty());
        }

        @Override
        public String display() {
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

    private record TypeRule(String type, Rule childRule) implements Rule {
        @Override
        public Result<String, CompileError> apply(String s) {
            return this.childRule.apply(s).mapErr(err -> new CompileError("Invalid type '" + this.type + "'", s, Lists.of(err)));
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

    private static class SymbolRule implements Rule {
        @Override
        public Result<String, CompileError> apply(String input) {
            if (isSymbol(input)) {
                return new Ok<>(input);
            }
            return new Err<>(new CompileError("Not a symbol", input));
        }
    }

    private record StripRule(Rule childRule) implements Rule {
        @Override
        public Result<String, CompileError> apply(String s) {
            return this.childRule.apply(s.strip());
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
        String stripped = input.strip();
        if (!stripped.startsWith("import ")) {
            return createPrefixRule(stripped, "import ");
        }

        String right = stripped.substring("import ".length());
        if (!right.endsWith(";")) {
            return createSuffixErr(right, ";");
        }

        String content = right.substring(0, right.length() - ";".length());
        List_<String> split = divide(content, new DelimitedDivider('.'));
        if (split.size() >= 3 && Lists.equals(split.slice(0, 3), FUNCTIONAL_NAMESPACE, String::equals)) {
            return new Ok<>("");
        }

        String joined = split.iter().collect(new Joiner("/")).orElse("");
        imports.add("#include \"./" + joined + "\"\n");
        return new Ok<>("");
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
        int paramStart = input.indexOf("(");
        if (paramStart < 0) {
            return createInfixErr(input, "(");
        }

        String inputDefinition = input.substring(0, paramStart).strip();
        String withParams = input.substring(paramStart + "(".length());

        return createDefinitionRule().apply(inputDefinition).flatMapValue(outputDefinition -> {
            int paramEnd = withParams.indexOf(")");
            if (paramEnd < 0) {
                return createInfixErr(withParams, ")");
            }

            String params = withParams.substring(0, paramEnd);
            return compileValues(params, Main::compileParameter)
                    .flatMapValue(outputParams -> assembleMethodBody(typeParams, outputDefinition, outputParams, withParams.substring(paramEnd + ")".length()).strip()));
        });
    }

    private static Result<String, CompileError> compileDefinitionStatement(String input) {
        String stripped = input.strip();
        if (stripped.endsWith(";")) {
            String content = stripped.substring(0, stripped.length() - ";".length());
            return createDefinitionRule().apply(content).mapValue(result -> "\t" + result + ";\n");
        }
        return createSuffixErr(input, ";");
    }

    private static Err<String, CompileError> createSuffixErr(String input, String suffix) {
        return new Err<>(new CompileError("Suffix '" + suffix + "' not present", input));
    }

    private static Result<String, CompileError> compileGlobalInitialization(List_<String> typeParams, String input) {
        return compileInitialization(input, typeParams, 0).mapValue(generated -> {
            globals.add(generated + ";\n");
            return "";
        });
    }

    private static Result<String, CompileError> compileInitialization(
            String input,
            List_<String> typeParams,
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
        return createDefinitionRule().apply(definition)
                .flatMapValue(outputDefinition -> compileValue(value, typeParams, depth).mapValue(outputValue -> outputDefinition + " = " + outputValue));
    }

    private static Result<String, CompileError> compileWhitespace(String input) {
        if (input.isBlank()) {
            return new Ok<>("");
        }
        return new Err<>(new CompileError("Not blank", input));
    }

    private static Result<String, CompileError> assembleMethodBody(
            List_<String> typeParams,
            String definition,
            String params,
            String body
    ) {
        String header = "\t".repeat(0) + definition + "(" + params + ")";
        if (body.startsWith("{") && body.endsWith("}")) {
            String inputContent = body.substring("{".length(), body.length() - "}".length());
            Result<String, CompileError> result = compileStatements(inputContent, s -> compileStatementOrBlock(s, typeParams, 1));
            return result.mapValue(outputContent -> {
                methods.add(header + " {" + outputContent + "\n}\n");
                return "";
            });
        }

        return new Ok<>("\t" + header + ";\n");
    }

    private static Result<String, CompileError> compileParameter(String definition) {
        return compileOr(definition, Lists.of(
                Main::compileWhitespace,
                definition1 -> createDefinitionRule().apply(definition1)
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

    private static Result<String, CompileError> compileStatementOrBlock(String input0, List_<String> typeParams, int depth) {
        return compileOr(input0, Lists.of(
                Main::compileWhitespace,
                input -> compileKeywordStatement(input, depth, "continue"),
                input -> compileKeywordStatement(input, depth, "break"),
                input -> compileConditional(input, typeParams, "if ", depth),
                input -> compileConditional(input, typeParams, "while ", depth),
                input -> getWrap(typeParams, depth, input),
                input -> compilePostOperator(input, typeParams, depth, "++"),
                input -> compilePostOperator(input, typeParams, depth, "--"),
                input -> compileReturn(input, typeParams, depth).mapValue(result -> formatStatement(depth, result)),
                input -> compileInitialization(input, typeParams, depth).mapValue(result -> formatStatement(depth, result)),
                input -> compileAssignment(input, typeParams, depth).mapValue(result -> formatStatement(depth, result)),
                input -> compileInvocationStatement(input, typeParams, depth).mapValue(result -> formatStatement(depth, result)),
                Main::compileDefinitionStatement)
        );
    }

    private static Result<String, CompileError> getWrap(List_<String> typeParams, int depth, String input) {
        String stripped = input.strip();
        if (!stripped.startsWith("else ")) {
            return createPrefixRule(stripped, "else ");
        }

        String withoutKeyword = stripped.substring("else ".length()).strip();
        if (withoutKeyword.startsWith("{") && withoutKeyword.endsWith("}")) {
            String indent = createIndent(depth);
            return compileStatements(withoutKeyword.substring(1, withoutKeyword.length() - 1), s -> compileStatementOrBlock(s, typeParams, depth + 1))
                    .mapValue(result -> indent + "else {" + result + indent + "}");
        }

        Result<String, CompileError> stringCompileErrorResult = compileStatementOrBlock(withoutKeyword, typeParams, depth);
        return stringCompileErrorResult.mapValue(result -> "else " + result);
    }

    private static Err<String, CompileError> createPrefixRule(String input, String prefix) {
        return new Err<>(new CompileError("Prefix '" + prefix + "' not present", input));
    }

    private static Result<String, CompileError> compilePostOperator(String input, List_<String> typeParams, int depth, String operator) {
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

    private static Result<String, CompileError> compileConditional(String input, List_<String> typeParams, String prefix, int depth) {
        String stripped = input.strip();
        if (!stripped.startsWith(prefix)) {
            return createPrefixRule(stripped, prefix);
        }

        String afterKeyword = stripped.substring(prefix.length()).strip();
        if (!afterKeyword.startsWith("(")) {
            return createPrefixRule(afterKeyword, "(");
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
            Result<String, CompileError> stringCompileErrorResult = compileStatements(content, s -> compileStatementOrBlock(s, typeParams, depth + 1));
            return stringCompileErrorResult.mapValue(statements -> withCondition +
                    " {" + statements + "\n" +
                    "\t".repeat(depth) +
                    "}");
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

    private static Result<String, CompileError> compileInvocationStatement(String input, List_<String> typeParams, int depth) {
        String stripped = input.strip();
        if (stripped.endsWith(";")) {
            String withoutEnd = stripped.substring(0, stripped.length() - ";".length());
            return compileInvocation(withoutEnd, typeParams, depth);
        }
        return createSuffixErr(stripped, ";");
    }

    private static Result<String, CompileError> compileAssignment(String input, List_<String> typeParams, int depth) {
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

    private static Result<String, CompileError> compileReturn(String input, List_<String> typeParams, int depth) {
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

    private static Result<String, CompileError> compileValue(String input0, List_<String> typeParams, int depth) {
        return compileOr(input0, Lists.of(
                Main::compileString,
                Main::compileChar,
                stripped -> createSymbolRule().apply(stripped),
                Main::compileNumber,
                input -> compileConstruction(input, typeParams, depth),
                input -> createNotRule(input, typeParams, depth),
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
        ));
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
        if (input.startsWith("'") && input.endsWith("'")) {
            return new Ok<>(input);
        }
        else {
            return new Err<>(new CompileError("Not a char", input));
        }
    }

    private static TypeRule createSymbolRule() {
        return new TypeRule("symbol", new StripRule(new SymbolRule()));
    }

    private static Result<String, CompileError> compileNumber(String stripped) {
        if (isNumber(stripped)) {
            return new Ok<>(stripped);
        }
        return new Err<>(new CompileError("Not a number", stripped));
    }

    private static Result<String, CompileError> compileConstruction(String stripped, List_<String> typeParams, int depth) {
        return new TypeRule("construction", new StripRule(input -> {
            if (!input.startsWith("new ")) {
                return createPrefixRule(input, "new ");
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
            return compileType(type, typeParams)
                    .flatMapValue(outputType -> compileArgs(argsString, typeParams, depth)
                            .mapValue(value -> outputType + value));
        })).apply(stripped);
    }

    private static Result<String, CompileError> createNotRule(String stripped, List_<String> typeParams, int depth) {
        if (stripped.startsWith("!")) {
            return compileValue(stripped.substring(1), typeParams, depth).mapValue(result -> "!" + result);
        }
        else {
            return createPrefixRule(stripped, "!");
        }
    }

    private static Result<String, CompileError> compileMethodAccess(String input, List_<String> typeParams) {
        int methodIndex = input.lastIndexOf("::");
        if (methodIndex >= 0) {
            String type = input.substring(0, methodIndex).strip();
            String property = input.substring(methodIndex + "::".length()).strip();

            if (isSymbol(property)) {
                return compileType(type, typeParams)
                        .flatMapValue(compiled -> generateLambdaWithReturn(Lists.empty(), "\n\treturn " + compiled + "." + property + "()"));
            }
        }
        return createInfixErr(input, "::");
    }

    private static Result<String, CompileError> compileDataAccess(String input, List_<String> typeParams, int depth) {
        int separator = input.lastIndexOf(".");
        if (separator >= 0) {
            String object = input.substring(0, separator).strip();
            String property = input.substring(separator + ".".length()).strip();
            return compileValue(object, typeParams, depth).mapValue(compiled -> compiled + "." + property);
        }
        return createInfixErr(input, ".");
    }

    private static Result<String, CompileError> compileOperator(String input, List_<String> typeParams, int depth, String operator) {
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

    private static Result<String, CompileError> compileLambda(String input, List_<String> typeParams, int depth) {
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
            return compileStatements(slice, s -> compileStatementOrBlock(s, typeParams, depth))
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

    private static Result<String, CompileError> compileInvocation(String input, List_<String> typeParams, int depth) {
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

    private static Result<String, CompileError> compileArgs(String argsString, List_<String> typeParams, int depth) {
        return compileValues(argsString, arg -> compileOr(arg, Lists.of(
                Main::compileWhitespace,
                value -> compileValue(value, typeParams, depth)
        ))).mapValue(args -> "(" + args + ")");
    }

    private static StringBuilder mergeValues(StringBuilder cache, String element) {
        if (cache.isEmpty()) {
            return cache.append(element);
        }
        return cache.append(", ").append(element);
    }

    private static TypeRule createDefinitionRule() {
        return new TypeRule("definition", Main::getCompileErrorResult);
    }

    private static Result<String, CompileError> getCompileErrorResult(String definition) {
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
                return new Err<>(new CompileError("Invalid modifiers", modifiersString));
            }

            String inputType = beforeName.substring(typeSeparator + " ".length());
            return compileType(inputType, typeParams).mapValue(outputType -> generateDefinition(typeParams, outputType, name));
        }
        else {
            return compileType(beforeName, Lists.empty())
                    .mapValue(outputType -> generateDefinition(Lists.empty(), outputType, name));
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
                Main::getWrap,
                value -> getWrapped(typeParams, value),
                value -> getStringCompileErrorResult(typeParams, value),
                value -> getCompileErrorResult(typeParams, value)
        ));
    }

    private static Result<String, CompileError> getCompileErrorResult(List_<String> typeParams, String value) {
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
        return compileValues(params, s -> getWrap(typeParams, s))
                .mapValue(compiled -> base + "<" + compiled + ">");
    }

    private static Result<String, CompileError> getStringCompileErrorResult(List_<String> typeParams, String value) {
        if (!isSymbol(value.strip())) {
            return new Err<>(new CompileError("Not a symbol", value.strip()));
        }

        if (Lists.contains(typeParams, value.strip(), String::equals)) {
            return new Ok<>(value.strip());
        }
        else {
            return new Ok<>("struct " + value.strip());
        }
    }

    private static Result<String, CompileError> getWrapped(List_<String> typeParams, String value) {
        if (value.endsWith("[]")) {
            return compileType(value.substring(0, value.length() - "[]".length()), typeParams).mapValue(value1 -> value1 + "*");
        }
        return createSuffixErr(value, "[]");
    }

    private static Result<String, CompileError> getWrap(String value) {
        if (value.equals("void")) {
            return new Ok<>("void");
        }

        if (value.equals("int") || value.equals("Integer") || value.equals("boolean") || value.equals("Boolean")) {
            return new Ok<>("int");
        }

        if (value.equals("char") || value.equals("Character")) {
            return new Ok<>("char");
        }

        return new Err<>(new CompileError("Not a primitive", value));
    }

    private static Result<String, CompileError> getWrap(List_<String> typeParams, String s) {
        return compileOr(s, Lists.of(
                Main::compileWhitespace,
                type -> compileType(type, typeParams)
        ));
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
}
