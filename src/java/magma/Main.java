package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    private interface Iter<Value> {
        <Return> Iter<Return> map(Function<Value, Return> mapper);

        <Collect> Collect collect(Collector<Value, ?, Collect> joining);

        <Collect> Collect fold(Collect collect, BiFunction<Collect, Value, Collect> folder);
    }

    private interface List<Value> {
        Iter<Value> stream();

        List<Value> add(Value element);

        List<Value> addAll(List<Value> elements);
    }

    private interface DivideState {
        Iter<String> stream();

        DivideState advance();

        DivideState append(char c);

        boolean isLevel();

        DivideState enter();

        DivideState exit();

        Optional<Tuple<DivideState, Character>> pop();

        Optional<Tuple<DivideState, Character>> popAndAppendToTuple();

        Optional<DivideState> popAndAppendToOptional();

        boolean isShallow();
    }

    private interface ClassSegment {
        String generate();
    }

    private interface RootSegment {
        String generate();
    }

    private static class Lists {
        @SafeVarargs
        static <Value> List<Value> of(final Value... elements) {
            return new JavaList<>(Arrays.asList(elements));
        }

        static <Value> List<Value> empty() {
            return new JavaList<>();
        }
    }

    private record JavaList<Value>(java.util.List<Value> list) implements List<Value> {
        private JavaList() {
            this(new ArrayList<>());
        }

        @Override
        public Iter<Value> stream() {
            return new JavaIter<>(this.list.stream());
        }

        @Override
        public List<Value> add(final Value element) {
            this.list.add(element);
            return this;
        }

        @Override
        public List<Value> addAll(final List<Value> elements) {
            return elements.stream().<List<Value>>fold(this, List::add);
        }
    }

    private record JavaIter<Value>(Stream<Value> stream) implements Iter<Value> {
        @Override
        public <R> Iter<R> map(final Function<Value, R> mapper) {
            return new JavaIter<>(this.stream.map(mapper));
        }

        @Override
        public <C> C collect(final Collector<Value, ?, C> collector) {
            return this.stream.collect(collector);
        }

        @Override
        public <Collect> Collect fold(final Collect collect, final BiFunction<Collect, Value, Collect> folder) {
            return this.stream.reduce(collect, folder, (_, next) -> next);
        }
    }

    private static class MutableDivideState implements DivideState {

        private final Collection<String> segments = new ArrayList<>();
        private final CharSequence input;
        private int depth = 0;
        private StringBuilder buffer = new StringBuilder();
        private int index = 0;

        private MutableDivideState(final CharSequence input) {
            this.input = input;
        }

        @Override
        public Iter<String> stream() {
            return new JavaIter<>(this.segments.stream());
        }

        @Override
        public DivideState advance() {
            this.segments.add(this.buffer.toString());
            this.buffer = new StringBuilder();
            return this;
        }

        @Override
        public DivideState append(final char c) {
            this.buffer.append(c);
            return this;
        }

        @Override
        public boolean isLevel() {
            return 0 == this.depth;
        }

        @Override
        public DivideState enter() {
            this.depth++;
            return this;
        }

        @Override
        public DivideState exit() {
            this.depth--;
            return this;
        }

        @Override
        public Optional<Tuple<DivideState, Character>> pop() {
            if (this.index >= this.input.length()) return Optional.empty();
            final var value = this.input.charAt(this.index);
            this.index++;
            return Optional.of(new Tuple<>(this, value));
        }

        @Override
        public Optional<Tuple<DivideState, Character>> popAndAppendToTuple() {
            return this.pop().map(tuple -> new Tuple<>(tuple.left.append(tuple.right), tuple.right));
        }

        @Override
        public Optional<DivideState> popAndAppendToOptional() {
            return this.popAndAppendToTuple().map(Tuple::left);
        }

        @Override
        public boolean isShallow() {
            return 1 == this.depth;
        }
    }

    private record Tuple<Left, Right>(Left left, Right right) {}

    private record Structure(String name, List<ClassSegment> children) implements RootSegment, ClassSegment {
        @Override
        public String generate() {
            final var joined = this.children.stream()
                                            .map(ClassSegment::generate)
                                            .<CharSequence>map(value -> value)
                                            .collect(Collectors.joining());

            return "struct " + this.name + " {" + joined + "};" + Main.LINE_SEPARATOR;
        }
    }

    private record Placeholder(String input) implements RootSegment, ClassSegment {
        private static String wrap(final String input) {
            return "/*" + input.replace("/*", "start").replace("*/", "end") + "*/";
        }

        @Override
        public String generate() {
            return Placeholder.wrap(this.input);
        }
    }

    private record Method(String header, String params, String content) implements ClassSegment {
        @Override
        public String generate() {
            return Main.LINE_SEPARATOR + "\t" + this.header + "(" + Placeholder.wrap(this.params) + ")" +
                   Placeholder.wrap(this.content);
        }
    }

    private static final String LINE_SEPARATOR = System.lineSeparator();

    private Main() {}

    public static void main(final String[] args) {
        try {
            final var source = Paths.get(".", "src", "java", "magma", "Main.java");
            final var input = Files.readString(source);
            final var target = source.resolveSibling("Main.c");
            final var output = Main.compile(input);
            Files.writeString(target, output);
        } catch (final IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static CharSequence compile(final CharSequence input) {
        return Main.compileStatements(input, Main::compileRootSegment);
    }

    private static CharSequence compileStatements(final CharSequence input, final Function<String, String> mapper) {
        return Main.divide(input).stream().map(mapper).<CharSequence>map(value -> value).collect(Collectors.joining());
    }

    private static String compileRootSegment(final String input) {
        final var strip = input.strip();
        if (strip.startsWith("package ")) return "";
        final var joined = Main.compileRootSegmentValue(strip)
                               .stream()
                               .map(RootSegment::generate)
                               .<CharSequence>map(value -> value)
                               .collect(Collectors.joining());

        return joined + Main.LINE_SEPARATOR;
    }

    private static List<RootSegment> compileRootSegmentValue(final String input) {
        return Main.compileClass("class ", input)
                   .<List<RootSegment>>map(list -> new JavaList<>(
                           list.stream().<RootSegment>map(value -> value).collect(Collectors.toList())))
                   .orElseGet(() -> Lists.of(new Placeholder(input)));
    }

    private static Optional<List<Structure>> compileClass(final String keyword, final String input) {
        if (input.isEmpty() || '}' != input.charAt(input.length() - 1)) return Optional.empty();

        final var withoutEnd = input.substring(0, input.length() - "}".length());
        final var contentStart = withoutEnd.indexOf('{');
        if (0 > contentStart) return Optional.empty();

        final var beforeContent = withoutEnd.substring(0, contentStart);
        final var inputContent = withoutEnd.substring(contentStart + "{".length());
        final var keywordIndex = beforeContent.indexOf(keyword);
        if (0 > keywordIndex) return Optional.empty();

        final var name = beforeContent.substring(keywordIndex + keyword.length()).strip();
        final var segments = Main.divide(inputContent);

        final var result = segments.stream().fold(new Tuple<>(Lists.empty(), Lists.empty()), Main::flattenSegmentTuple);
        final var structure = new Structure(name, result.left);
        return Optional.of(result.right.add(structure));
    }

    private static Tuple<List<ClassSegment>, List<Structure>> flattenSegmentTuple(final Tuple<List<ClassSegment>, List<Structure>> current,
                                                                                  final String input) {
        final var tuple = Main.compileClassSegment(input);
        final var added = current.right.addAll(tuple.right);

        final var maybeSegment = tuple.left;
        if (maybeSegment.isPresent()) {
            final var segment = maybeSegment.get();
            return Main.flattenSegment(segment, current.left, added);
        }

        return new Tuple<>(current.left, added);
    }

    private static Tuple<List<ClassSegment>, List<Structure>> flattenSegment(final ClassSegment segment,
                                                                             final List<ClassSegment> children,
                                                                             final List<Structure> structures) {
        if (segment instanceof final Structure structure) structures.add(structure);
        else children.add(segment);
        return new Tuple<>(children, structures);
    }

    private static Tuple<Optional<ClassSegment>, List<Structure>> compileClassSegment(final String input) {
        return Main.compileClass("interface ", input)
                   .<Tuple<Optional<ClassSegment>, List<Structure>>>map(list -> new Tuple<>(Optional.empty(), list))
                   .or(() -> Main.compileMethod(input))
                   .orElseGet(() -> new Tuple<>(Optional.of(new Placeholder(input)), Lists.empty()));
    }

    private static Optional<Tuple<Optional<ClassSegment>, List<Structure>>> compileMethod(final String input) {
        final var paramStart = input.indexOf('(');
        if (0 > paramStart) return Optional.empty();
        final var header = input.substring(0, paramStart);
        final var withParams = input.substring(paramStart + "(".length());

        final var paramEnd = withParams.indexOf(')');
        if (0 > paramEnd) return Optional.empty();
        final var params = withParams.substring(0, paramEnd);
        final var content = withParams.substring(paramEnd + ")".length());

        return Optional.of(
                new Tuple<>(Optional.of(new Method(Main.compileDefinition(header), params, content)), Lists.empty()));
    }

    private static String compileDefinition(final String input) {
        final var strip = input.strip();
        final var nameSeparator = strip.lastIndexOf(' ');
        if (0 <= nameSeparator) {
            final var before = strip.substring(0, nameSeparator);
            final var name = strip.substring(nameSeparator + " ".length());
            return Placeholder.wrap(before) + " " + name;
        }

        return Placeholder.wrap(strip);
    }

    private static List<String> divide(final CharSequence input) {
        Tuple<Boolean, DivideState> current = new Tuple<>(true, new MutableDivideState(input));
        while (current.left) current = Main.foldAsTuple(current);
        return new JavaList<>(current.right.advance().stream().collect(Collectors.toList()));
    }

    private static Tuple<Boolean, DivideState> foldAsTuple(final Tuple<Boolean, DivideState> current) {
        final var maybePopped = current.right.pop();
        if (maybePopped.isEmpty()) return new Tuple<>(false, current.right);

        final var popped = maybePopped.get();
        return new Tuple<>(true, Main.foldDecorated(popped.left, popped.right));
    }

    private static DivideState foldDecorated(final DivideState state, final char next) {
        return Main.foldSingleQuotes(state, next).orElseGet(() -> Main.foldStatement(state, next));
    }

    private static Optional<DivideState> foldSingleQuotes(final DivideState state, final char next) {
        if ('\'' != next) return Optional.empty();

        final var appended = state.append('\'');
        return appended.popAndAppendToTuple().flatMap(Main::foldEscape).flatMap(DivideState::popAndAppendToOptional);
    }

    private static Optional<DivideState> foldEscape(final Tuple<DivideState, Character> tuple) {
        if ('\\' == tuple.right) return tuple.left.popAndAppendToOptional();
        return Optional.of(tuple.left);
    }

    private static DivideState foldStatement(final DivideState state, final char c) {
        final var appended = state.append(c);
        if (';' == c && appended.isLevel()) return appended.advance();
        if ('}' == c && appended.isShallow()) return appended.advance().exit();
        if ('{' == c) return appended.enter();
        if ('}' == c) return appended.exit();
        return appended;
    }
}
