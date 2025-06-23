package magma;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.RecordComponent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    private interface DivideState {
        DivideState append(char c);

        DivideState advance();

        Collection<String> unwrap();

        boolean isLevel();

        DivideState exit();

        DivideState enter();

        boolean isShallow();

        Optional<Tuple<DivideState, Character>> pop();

        Optional<Tuple<DivideState, Character>> popAndAppendToTuple();

        Optional<DivideState> popAndAppendToOption();
    }

    private interface Node {
        Node withString(String key, String value);

        Optional<String> findString(String key);

        Node merge(Node other);

        Stream<Map.Entry<String, String>> streamStrings();

        Node retype(String type);

        boolean is(String type);

        Node withNodeList(String key, List<Node> values);

        Optional<List<Node>> findNodeList(String key);

        String display();

        Stream<Map.Entry<String, List<Node>>> streamNodeLists();

        Stream<String> streamPropertyNames();

        Optional<Tuple<Node, String>> removeString(String key);

        boolean hasNoProperties();

        Optional<Tuple<Node, List<Node>>> removeNodeList(String key);

        boolean hasNoName();
    }

    private interface Rule {
        Result<String, FormatError> generate(Node node);

        Result<Node, FormatError> lex(String input);
    }

    private sealed interface Result<Value, Error> permits Ok, Err {
        <Return> Result<Return, Error> flatMapValue(Function<Value, Result<Return, Error>> mapper);

        <Return> Result<Return, Error> mapValue(Function<Value, Return> mapper);

        <Return> Return match(Function<Value, Return> whenOk, Function<Error, Return> whenErr);

        <Return> Result<Value, Return> mapErr(Function<Error, Return> mapper);
    }

    private interface Context {
        String display();
    }

    private interface Error {
        String display();
    }

    private interface Accumulator<T> {
        boolean hasValue();

        Accumulator<T> withValue(T value);

        Accumulator<T> withError(FormatError error);

        <Return> Return match(Function<T, Return> whenOk, Function<List<FormatError>, Return> whenErr);
    }

    private interface FormatError extends Error {
        @Override
        default String display() {
            return format(0);
        }

        String format(int depth);
    }

    private sealed interface JavaRootSegment permits Whitespace, Package, Import, JavaStructure {
    }

    private sealed interface JavaStructureMember permits JavaStructure, Placeholder {
    }

    @Retention(RetentionPolicy.RUNTIME)
    private @interface NodeRepr {
        String name();
    }

    private interface TSRootSegment {
    }

    @NodeRepr(name = "whitespace")
    private record Whitespace() implements JavaRootSegment {
    }

    @NodeRepr(name = "placeholder")
    private record Placeholder(String value) implements JavaStructureMember {
    }

    @NodeRepr(name = "package")
    private record Package(String content) implements JavaRootSegment {
    }

    @NodeRepr(name = "import")
    private record Import(String content) implements JavaRootSegment {
    }

    private record StringContext(String value) implements Context {
        @Override
        public String display() {
            return value;
        }
    }

    private record NodeContext(Node value) implements Context {
        @Override
        public String display() {
            return value.display();
        }
    }

    private record CompileError(String message, Context context, List<FormatError> errors) implements FormatError {
        private CompileError(final String message, final Context context) {
            this(message, context, new ArrayList<>());
        }

        @Override
        public String format(final int depth) {
            final var joined = errors.stream()
                    .map(error -> error.format(depth + 1))
                    .map(result -> System.lineSeparator() + "\t".repeat(depth) + result)
                    .collect(Collectors.joining());

            return message + ": " + context.display() + joined;
        }
    }

    private record Tuple<Left, Right>(Left left, Right right) {
    }

    private static class MutableDivideState implements DivideState {
        private final Collection<String> segments = new ArrayList<>();
        private final CharSequence input;
        private final int length;
        private int depth = 0;
        private StringBuilder buffer = new StringBuilder();
        private int index = 0;

        private MutableDivideState(final CharSequence input) {
            this.input = input;
            length = input.length();
        }

        @Override
        public Collection<String> unwrap() {
            return Collections.unmodifiableCollection(segments);
        }

        @Override
        public boolean isLevel() {
            return 0 == depth;
        }

        @Override
        public DivideState exit() {
            depth--;
            return this;
        }

        @Override
        public DivideState enter() {
            depth++;
            return this;
        }

        @Override
        public boolean isShallow() {
            return 1 == depth;
        }

        @Override
        public Optional<Tuple<DivideState, Character>> pop() {
            if (index < length) {
                final var c = input.charAt(index);
                index++;
                return Optional.of(new Tuple<>(this, c));
            }
            else
                return Optional.empty();
        }

        @Override
        public Optional<Tuple<DivideState, Character>> popAndAppendToTuple() {
            return pop().map(tuple -> new Tuple<>(tuple.left.append(tuple.right), tuple.right));
        }

        @Override
        public Optional<DivideState> popAndAppendToOption() {
            return pop().map(tuple -> tuple.left.append(tuple.right));
        }

        @Override
        public DivideState append(final char c) {
            buffer.append(c);
            return this;
        }

        @Override
        public DivideState advance() {
            segments.add(buffer.toString());
            buffer = new StringBuilder();
            return this;
        }
    }

    private static final class MapNode implements Node {
        private final Optional<String> maybeType;
        private final Map<String, String> strings;
        private final Map<String, List<Node>> nodeLists;

        private MapNode(final Optional<String> maybeType, final Map<String, String> strings, final Map<String, List<Node>> nodeLists) {
            this.maybeType = maybeType;
            this.strings = strings;
            this.nodeLists = nodeLists;
        }

        private MapNode() {
            this(Optional.empty(), new HashMap<>(), new HashMap<>());
        }

        @Override
        public String toString() {
            return display();
        }

        @Override
        public Node withString(final String key, final String value) {
            strings.put(key, value);
            return this;
        }

        @Override
        public Optional<String> findString(final String key) {
            return Optional.ofNullable(strings.get(key));
        }

        @Override
        public Node merge(final Node other) {
            final var withStrings = other.streamStrings()
                    .<Node>reduce(this,
                            (node, entry) -> node.withString(entry.getKey(), entry.getValue()),
                            (_, next) -> next);

            return other.streamNodeLists()
                    .reduce(withStrings,
                            (node, entry) -> node.withNodeList(entry.getKey(), entry.getValue()),
                            (_, next) -> next);
        }

        @Override
        public Stream<Map.Entry<String, String>> streamStrings() {
            return strings.entrySet()
                    .stream();
        }

        @Override
        public Node retype(final String type) {
            return new MapNode(Optional.of(type), strings, nodeLists);
        }

        @Override
        public boolean is(final String type) {
            return maybeType.isPresent() && maybeType.get()
                    .contentEquals(type);
        }

        @Override
        public Node withNodeList(final String key, final List<Node> values) {
            nodeLists.put(key, values);
            return this;
        }

        @Override
        public Optional<List<Node>> findNodeList(final String key) {
            return Optional.ofNullable(nodeLists.get(key));
        }

        @Override
        public String display() {
            return maybeType.toString() + strings.toString() + nodeLists.toString();
        }

        @Override
        public Stream<Map.Entry<String, List<Node>>> streamNodeLists() {
            return nodeLists.entrySet()
                    .stream();
        }

        @Override
        public Stream<String> streamPropertyNames() {
            return Stream.concat(strings.keySet()
                            .stream(),
                    nodeLists.keySet()
                            .stream());
        }

        @Override
        public Optional<Tuple<Node, String>> removeString(final String key) {
            if (strings.containsKey(key)) {
                final var copy = new HashMap<>(strings);
                final var value = copy.remove(key);
                return Optional.of(new Tuple<>(new MapNode(maybeType, copy, nodeLists), value));
            }

            return Optional.empty();
        }

        @Override
        public boolean hasNoProperties() {
            return strings.isEmpty() && nodeLists.isEmpty();
        }

        @Override
        public Optional<Tuple<Node, List<Node>>> removeNodeList(final String key) {
            if (nodeLists.containsKey(key)) {
                final var copy = new HashMap<>(nodeLists);
                final var value = copy.remove(key);
                return Optional.of(new Tuple<>(new MapNode(maybeType, strings, copy), value));
            }

            return Optional.empty();
        }

        @Override
        public boolean hasNoName() {
            return maybeType.isEmpty();
        }
    }

    private record StringRule(String key) implements Rule {
        @Override
        public Result<String, FormatError> generate(final Node node) {
            return node.findString(key)
                    .<Result<String, FormatError>>map(Ok::new)
                    .orElseGet(() -> new Err<>(new CompileError("String '" + key + "' not present",
                            new NodeContext(node))));
        }

        @Override
        public Result<Node, FormatError> lex(final String input) {
            return new Ok<>(new MapNode().withString(key(), input));
        }
    }

    private record InfixRule(Rule leftRule, String infix, Rule rightRule) implements Rule {
        @Override
        public Result<String, FormatError> generate(final Node node) {
            return leftRule.generate(node)
                    .flatMapValue(leftResult -> rightRule.generate(node)
                            .mapValue(rightResult -> leftResult + infix + rightResult));
        }

        @Override
        public Result<Node, FormatError> lex(final String input) {
            final var index = input.indexOf(infix());
            if (0 > index)
                return new Err<>(new CompileError("Infix '" + infix + "' not present", new StringContext(input)));

            final var beforeContent = input.substring(0, index);
            final var leftResult = leftRule.lex(beforeContent);

            final var content = input.substring(index + infix().length());
            final var rightResult = rightRule.lex(content);

            return leftResult.flatMapValue(leftValue -> rightResult.mapValue(leftValue::merge));
        }
    }

    private record SuffixRule(Rule rule, String suffix) implements Rule {
        @Override
        public Result<String, FormatError> generate(final Node node) {
            return rule.generate(node)
                    .mapValue(result -> result + suffix);
        }

        @Override
        public Result<Node, FormatError> lex(final String input) {
            if (!input.endsWith(suffix()))
                return new Err<>(new CompileError("Suffix '" + suffix + "' not present", new StringContext(input)));

            final var withoutEnd = input.substring(0, input.length() - suffix().length());
            return rule().lex(withoutEnd);
        }
    }

    private record StripRule(Rule rule) implements Rule {
        @Override
        public Result<String, FormatError> generate(final Node node) {
            return rule.generate(node);
        }

        @Override
        public Result<Node, FormatError> lex(final String input) {
            final var stripped = input.strip();
            return rule.lex(stripped);
        }
    }

    private record TypeRule(String type, Rule rule) implements Rule {
        @Override
        public Result<String, FormatError> generate(final Node node) {
            if (node.is(type))
                return rule.generate(node);
            return new Err<>(new CompileError("Type '" + type + "' not present", new NodeContext(node)));
        }

        @Override
        public Result<Node, FormatError> lex(final String input) {
            return rule.lex(input)
                    .mapValue(node -> node.retype(type));
        }
    }

    private record PlaceholderRule(Rule rule) implements Rule {
        private static String generatePlaceholder(final String input) {
            final var replaced = input.replace("/*", "start")
                    .replace("*/", "end");

            return "/*" + replaced + "*/";
        }

        @Override
        public Result<String, FormatError> generate(final Node node) {
            return rule.generate(node)
                    .mapValue(PlaceholderRule::generatePlaceholder);
        }

        @Override
        public Result<Node, FormatError> lex(final String input) {
            return rule.lex(input);
        }
    }

    private record OrRule(List<Rule> rules) implements Rule {
        @Override
        public Result<String, FormatError> generate(final Node node) {
            return Main.disjoin(rules, rule -> rule.generate(node), new NodeContext(node));
        }

        @Override
        public Result<Node, FormatError> lex(final String input) {
            return Main.disjoin(rules, rule -> rule.lex(input), new StringContext(input));
        }
    }

    private record PrefixRule(String prefix, Rule rule) implements Rule {
        @Override
        public Result<String, FormatError> generate(final Node node) {
            return rule.generate(node)
                    .mapValue(result -> prefix + result);
        }

        @Override
        public Result<Node, FormatError> lex(final String input) {
            if (input.startsWith(prefix))
                return rule.lex(input.substring(prefix.length()));
            return new Err<>(new CompileError("Prefix '" + prefix + "' not present", new StringContext(input)));
        }
    }

    private record DivideRule(String key, Rule rule) implements Rule {
        private static Collection<String> divide(final CharSequence input) {
            DivideState state = new MutableDivideState(input);
            final var current = state;
            while (true) {
                final var maybeNext = state.pop();
                if (maybeNext.isEmpty())
                    break;

                final var next = maybeNext.get();
                state = next.left;
                final var c = next.right;

                state = DivideRule.fold(state, c);
            }

            return current.advance()
                    .unwrap();
        }

        private static DivideState fold(final DivideState state, final char c) {
            return DivideRule.foldSingleQuotes(state, c)
                    .orElseGet(() -> DivideRule.foldStatement(state, c));
        }

        private static Optional<DivideState> foldSingleQuotes(final DivideState state, final char c) {
            if ('\'' != c)
                return Optional.empty();

            final var appended = state.append('\'');
            return appended.popAndAppendToTuple()
                    .flatMap(tuple -> '\\' == tuple.right ? tuple.left.popAndAppendToOption() : Optional.of(tuple.left))
                    .flatMap(DivideState::popAndAppendToOption);

        }

        private static DivideState foldStatement(final DivideState state, final char c) {
            final var appended = state.append(c);
            if (';' == c && appended.isLevel())
                return appended.advance();
            if ('}' == c && appended.isShallow())
                return appended.exit()
                        .advance();
            if ('{' == c)
                return appended.enter();
            if ('}' == c)
                return appended.exit();
            return appended;
        }

        @Override
        public Result<String, FormatError> generate(final Node node) {
            return node.findNodeList(key)
                    .map(nodes -> Main.combine(nodes, new StringBuilder(), rule::generate, StringBuilder::append)
                            .mapValue(StringBuilder::toString))
                    .orElseGet(() -> new Err<>(new CompileError("Node list '" + key + "' not present",
                            new NodeContext(node))));
        }

        @Override
        public Result<Node, FormatError> lex(final String input) {
            final var divisions = DivideRule.divide(input);
            return Main.combine(divisions, new ArrayList<Node>(), rule::lex, (nodes, node) -> {
                        nodes.add(node);
                        return nodes;
                    })
                    .mapValue(oldChildren -> {
                        final Node node = new MapNode();
                        return node.withNodeList(key, oldChildren);
                    });
        }
    }

    private record Ok<Value, Error>(Value value) implements Result<Value, Error> {
        @Override
        public <Return> Result<Return, Error> flatMapValue(final Function<Value, Result<Return, Error>> mapper) {
            return mapper.apply(value);
        }

        @Override
        public <Return> Result<Return, Error> mapValue(final Function<Value, Return> mapper) {
            return new Ok<>(mapper.apply(value));
        }

        @Override
        public <Return> Return match(final Function<Value, Return> whenOk, final Function<Error, Return> whenErr) {
            return whenOk.apply(value);
        }

        @Override
        public <Return> Result<Value, Return> mapErr(final Function<Error, Return> mapper) {
            return new Ok<>(value);
        }
    }

    private record Err<Value, Error>(Error error) implements Result<Value, Error> {
        @Override
        public <Return> Result<Return, Error> flatMapValue(final Function<Value, Result<Return, Error>> mapper) {
            return new Err<>(error);
        }

        @Override
        public <Return> Result<Return, Error> mapValue(final Function<Value, Return> mapper) {
            return new Err<>(error);
        }

        @Override
        public <Return> Return match(final Function<Value, Return> whenOk, final Function<Error, Return> whenErr) {
            return whenErr.apply(error);
        }

        @Override
        public <Return> Result<Value, Return> mapErr(final Function<Error, Return> mapper) {
            return new Err<>(mapper.apply(error));
        }
    }

    private record ThrowableError(Throwable throwable) implements Error {
        @Override
        public String display() {
            final var writer = new StringWriter();
            throwable.printStackTrace(new PrintWriter(writer));
            return writer.toString();
        }
    }

    private record ApplicationError(Error error) implements Error {
        @Override
        public String display() {
            return error.display();
        }
    }

    private record ImmutableAccumulator<T>(Optional<T> maybeValue, List<FormatError> errors) implements Accumulator<T> {
        private ImmutableAccumulator() {
            this(Optional.empty(), new ArrayList<>());
        }

        @Override
        public boolean hasValue() {
            return maybeValue.isPresent();
        }

        @Override
        public Accumulator<T> withValue(final T value) {
            return new ImmutableAccumulator<>(Optional.of(value), errors);
        }

        @Override
        public Accumulator<T> withError(final FormatError error) {
            errors.add(error);
            return this;
        }

        @Override
        public <Return> Return match(final Function<T, Return> whenOk, final Function<List<FormatError>, Return> whenErr) {
            return maybeValue.map(whenOk)
                    .orElseGet(() -> whenErr.apply(errors));
        }
    }

    private static class EmptyRule implements Rule {
        @Override
        public Result<String, FormatError> generate(final Node node) {
            return new Ok<>("");
        }

        @Override
        public Result<Node, FormatError> lex(final String input) {
            return input.isEmpty() ? new Ok<>(new MapNode()) : new Err<>(new CompileError("Not empty",
                    new StringContext(input)));
        }
    }

    private static class LazyRule implements Rule {
        private Optional<Rule> maybeRule = Optional.empty();

        @Override
        public Result<String, FormatError> generate(final Node node) {
            return findRule(new NodeContext(node)).flatMapValue(rule -> rule.generate(node));
        }

        private Result<Rule, FormatError> findRule(final Context context) {
            return maybeRule.<Result<Rule, FormatError>>map(Ok::new)
                    .orElseGet(() -> new Err<>(new CompileError("Rule not set", context)));
        }

        @Override
        public Result<Node, FormatError> lex(final String input) {
            return findRule(new StringContext(input)).flatMapValue(rule -> rule.lex(input));
        }

        void set(final Rule rule) {
            maybeRule = Optional.of(rule);
        }
    }

    @NodeRepr(name = "structure")
    private record JavaStructure(String name, String modifiers, List<JavaStructureMember> members) implements
            JavaRootSegment,
            JavaStructureMember {
    }

    private record JavaRoot(List<JavaRootSegment> children) {
    }

    @NodeRepr(name = "root")
    private record TSRoot(List<TSRootSegment> children) {
    }

    @NodeRepr(name = "structure")
    private record TSStructure(String modifiers, String name, List<JavaStructureMember> members) implements
            TSRootSegment {
    }

    private Main() {
    }

    public static void main(final String[] args) {
        final var source = Paths.get(".", "src", "java", "magma", "Main.java");
        final var target = source.resolveSibling("Main.ts");
        final var result = Main.run(source, target);
        result.ifPresent(error -> System.err.println(error.display()));
    }

    private static Optional<ApplicationError> run(final Path source, final Path target) {
        return Main.readString(source)
                .mapErr(ApplicationError::new)
                .match(input -> Main.compileAndWrite(target, input), Optional::of);
    }

    private static Optional<ApplicationError> compileAndWrite(final Path target, final String input) {
        return Main.compile(input)
                .mapErr(ApplicationError::new)
                .match(output -> Main.writeString(target, output)
                        .map(ApplicationError::new), Optional::of);
    }

    private static Optional<ThrowableError> writeString(final Path target, final CharSequence output) {
        try {
            Files.writeString(target, output);
            return Optional.empty();
        } catch (final IOException e) {
            return Optional.of(new ThrowableError(e));
        }
    }

    private static Result<String, ThrowableError> readString(final Path source) {
        try {
            return new Ok<>(Files.readString(source));
        } catch (final IOException e) {
            return new Err<>(new ThrowableError(e));
        }
    }

    private static Result<String, FormatError> compile(final String input) {
        return Main.createJavaRootRule()
                .lex(input)
                .flatMapValue(root -> Main.deserialize(JavaRoot.class, root))
                .mapValue(Main::modifyRoot)
                .flatMapValue(Main::serialize)
                .flatMapValue(children -> Main.createTSRootRule()
                        .generate(children));
    }

    private static <Value> Result<Node, FormatError> serialize(final Value node) {
        final Class<?> clazz = node.getClass();

        final var annotation = clazz.getAnnotation(NodeRepr.class);
        Result<Node, FormatError> maybeCurrent = new Ok<>(new MapNode());
        for (final var field : clazz.getDeclaredFields())
            maybeCurrent = maybeCurrent.flatMapValue(current -> {
                final var name = field.getName();
                try {
                    final var value = field.get(node);
                    if (value instanceof final List<?> list)
                        return Main.combine(list, new ArrayList<Node>(), Main::serialize, (objects, node1) -> {
                                    objects.add(node1);
                                    return objects;
                                })
                                .mapValue(serializedArguments -> {
                                    return current.withNodeList(name, serializedArguments);
                                });

                    if (value instanceof final String string)
                        return new Ok<>(current.withString(name, string));

                    return new Err<>(new CompileError("Unknown type",
                            new StringContext(value.getClass()
                                    .getName())));
                } catch (final IllegalAccessException e) {
                    return new Err<>(new CompileError("Failed to get field", new StringContext(name)));
                }
            });

        return maybeCurrent.mapValue(inner -> inner.retype(annotation.name()));
    }

    private static <Type> Result<Type, FormatError> deserialize(final Class<Type> clazz, final Node node) {
        return Main.disjoin(List.<Supplier<Result<Type, FormatError>>>of(() -> Main.deserializeRecord(clazz, node),
                () -> Main.deserializeInterface(clazz, node)), Supplier::get, new NodeContext(node));
    }

    private static <Type> Result<Type, FormatError> deserializeRecord(final Class<Type> clazz, final Node node) {
        if (!clazz.isRecord())
            return new Err<>(new CompileError("Not a record", new StringContext(clazz.getName())));

        final var components = clazz.getRecordComponents();

        Result<Tuple<Node, List<Object>>, FormatError> maybeCurrent = new Ok<>(new Tuple<>(node, new ArrayList<>()));

        for (final var component : components)
            maybeCurrent = maybeCurrent.flatMapValue(current -> {
                return Main.getTupleCompileErrorResult(node, component, current);
            });

        return maybeCurrent.flatMapValue(current -> {
            if (current.left.hasNoProperties()) {
                final var arguments = current.right;

                final var constructorParamTypes = Arrays.stream(components)
                        .map(RecordComponent::getType)
                        .toArray(Class<?>[]::new);

                try {
                    final var constructor = clazz.getDeclaredConstructor(constructorParamTypes);
                    final var instance = constructor.newInstance(arguments.toArray());
                    return new Ok<>(instance);
                } catch (final NoSuchMethodException | InstantiationException | IllegalAccessException |
                               InvocationTargetException e) {
                    return new Err<>(new CompileError("Failed to instantiate", new NodeContext(node)));
                }
            }

            final var joined = current.left.streamPropertyNames()
                    .collect(Collectors.joining(", "));

            return new Err<>(new CompileError("Fields remain: [" + joined + "]", new NodeContext(node)));
        });

    }

    private static <Type> Result<Type, FormatError> deserializeInterface(final Class<Type> clazz, final Node node) {
        if (!clazz.isInterface())
            return new Err<>(new CompileError("Not an interface", new StringContext(clazz.getName())));

        final var permittedSubclasses = clazz.getPermittedSubclasses();
        if (null == permittedSubclasses)
            return new Err<>(new CompileError("No permitted classes present on interface",
                    new StringContext(clazz.getName())));

        if (node.hasNoName())
            return new Err<>(new CompileError("Node has no name", new NodeContext(node)));

        return Main.disjoin(Arrays.asList(permittedSubclasses),
                        permittedSubclass -> Main.getObjectFormatErrorResult(node, permittedSubclass),
                        new NodeContext(node))
                .mapValue(clazz::cast);
    }

    private static Result<?, FormatError> getObjectFormatErrorResult(final Node node, final Class<?> permittedSubclass) {
        final var name = permittedSubclass.getSimpleName();

        final var annotation = permittedSubclass.getAnnotation(NodeRepr.class);
        if (null == annotation)
            return new Err<>(new CompileError("No annotation present", new StringContext(permittedSubclass.getName())));

        final var nodeName = annotation.name();
        if (node.is(nodeName))
            return Main.deserialize(permittedSubclass, node);

        return new Err<>(new CompileError("Node name was not equal to '" + name + "'", new NodeContext(node)));
    }

    private static Result<Tuple<Node, List<Object>>, FormatError> getTupleCompileErrorResult(final Node node, final RecordComponent component, final Tuple<Node, List<Object>> current) {
        final var currentNode = current.left;
        final var currentArguments = current.right;

        final var name = component.getName();
        final Class<?> type = component.getType();
        final var genericType = component.getGenericType();

        if (type.isAssignableFrom(List.class) && genericType instanceof final ParameterizedType parameterizedType) {
            final var nodeType = parameterizedType.getActualTypeArguments()[0];
            if (nodeType instanceof final Class<?> clazz0)
                return Main.pruneNodeList(currentNode, name)
                        .flatMapValue(oldValuesTuple -> Main.getTupleFormatErrorResult(clazz0,
                                oldValuesTuple,
                                currentArguments));
        }

        if (type.isAssignableFrom(Node.class)) {

        }

        if (type.isAssignableFrom(String.class))
            return Main.pruneString(current.left, name)
                    .mapValue(pruned -> {
                        currentArguments.add(pruned.right);
                        return new Tuple<>(pruned.left, currentArguments);
                    });

        return new Err<>(new CompileError("?", new StringContext("?")));
    }

    private static Result<Tuple<Node, List<Object>>, FormatError> getTupleFormatErrorResult(final Class<?> clazz0, final Tuple<Node, List<Node>> oldValuesTuple, final List<Object> currentArguments) {
        Result<List<Object>, FormatError> maybeNewValues = new Ok<>(new ArrayList<>());

        for (final var oldValue : oldValuesTuple.right) {
            final Result<?, FormatError> maybeArgument = Main.deserialize(clazz0, oldValue)
                    .mapValue(clazz0::cast);
            maybeNewValues = maybeNewValues.flatMapValue(newValues -> {
                return maybeArgument.mapValue(argument -> {
                    newValues.add(argument);
                    return newValues;
                });
            });
        }

        return maybeNewValues.mapValue(newValues -> {
            currentArguments.add(newValues);
            return new Tuple<>(oldValuesTuple.left, currentArguments);
        });
    }

    private static Result<Tuple<Node, String>, FormatError> pruneString(final Node node, final String key) {
        return node.removeString(key)
                .<Result<Tuple<Node, String>, FormatError>>map(Ok::new)
                .orElseGet(() -> new Err<>(new CompileError("String '" + key + "' not present",
                        new NodeContext(node))));
    }

    private static Result<Tuple<Node, List<Node>>, FormatError> pruneNodeList(final Node current, final String name) {
        return current.removeNodeList(name)
                .<Result<Tuple<Node, List<Node>>, FormatError>>map(Ok::new)
                .orElseGet(() -> new Err<>(new CompileError("Node list '" + name + "' not present",
                        new NodeContext(current))));
    }

    private static TSRoot modifyRoot(final JavaRoot root) {
        final var structures = root.children.stream()
                .map(Main::modifyRootSegment)
                .flatMap(Collection::stream)
                .<TSRootSegment>map(value -> value)
                .toList();

        return new TSRoot(structures);
    }

    private static List<TSStructure> modifyRootSegment(final JavaRootSegment segment) {
        return switch (segment) {
            case final JavaStructure structure -> Main.modifyStructure(structure);
            default -> Collections.emptyList();
        };
    }

    private static List<TSStructure> modifyStructure(final JavaStructure structure) {
        final var list = structure.members.stream()
                .map(Main::modifyStructureMember)
                .flatMap(Collection::stream)
                .toList();

        final List<TSStructure> copy = new ArrayList<>(list);
        copy.add(Main.parseStructure(structure));
        return copy;
    }

    private static List<TSStructure> modifyStructureMember(final JavaStructureMember member) {
        return switch (member) {
            case final JavaStructure structure -> Main.modifyStructure(structure);
            default -> Collections.emptyList();
        };
    }

    private static TSStructure parseStructure(final JavaStructure structure) {
        return new TSStructure(structure.modifiers, structure.name, structure.members);
    }

    private static Rule createTSRootRule() {
        return Main.Statements(Main.createStructureRule(), "children");
    }

    private static Rule createJavaRootRule() {
        return Main.Statements(Main.createJavaRootSegmentRule(), "children");
    }

    private static Rule Statements(final Rule rule, final String key) {
        return new DivideRule(key, rule);
    }

    private static Rule createJavaRootSegmentRule() {
        final var structureRule = Main.createStructureRule();
        return new OrRule(List.of(new TypeRule("whitespace", new StripRule(new EmptyRule())),
                Main.createLocationRule("package"),
                Main.createLocationRule("import"),
                structureRule));
    }

    private static Rule createStructureRule() {
        final var structureRule = new LazyRule();
        final var modifiers = new PlaceholderRule(new StringRule("modifiers"));
        final var name = new StripRule(new StringRule("name"));

        final var rules = Stream.of("class ", "interface ", "record ")
                .<Rule>map(infix -> new InfixRule(modifiers, infix, name))
                .toList();

        final var beforeChildren = new OrRule(rules);

        final var children = Main.Statements(Main.createStructureMemberRule(structureRule), "members");
        structureRule.set(new TypeRule("structure",
                new StripRule(new SuffixRule(new InfixRule(beforeChildren, "{", children), "}"))));
        return structureRule;
    }

    private static Rule createLocationRule(final String type) {
        return new TypeRule(type, new StripRule(new PrefixRule(type + " ", new StringRule("content"))));
    }

    private static Rule createStructureMemberRule(final Rule structureRule) {
        return new OrRule(List.of(structureRule,
                new TypeRule("placeholder", new PlaceholderRule(new StringRule("value")))));
    }

    private static <Element, Value> Result<Value, FormatError> disjoin(final List<Element> elements, final Function<Element, Result<Value, FormatError>> mapper, final Context context) {
        return elements.stream()
                .map(mapper)
                .<Accumulator<Value>>reduce(new ImmutableAccumulator<>(), (accumulator, result) -> {
                    if (accumulator.hasValue())
                        return accumulator;
                    return result.match(accumulator::withValue, accumulator::withError);
                }, (_, next) -> next)
                .<Result<Value, FormatError>>match(Ok::new,
                        errors -> new Err<>(new CompileError("Invalid combination", context, errors)));
    }

    private static <Element, Value, Elements> Result<Elements, FormatError> combine(final Collection<Element> elements, final Elements initial, final Function<Element, Result<Value, FormatError>> mapper, final BiFunction<Elements, Value, Elements> folder) {
        return elements.stream()
                .map(mapper)
                .<Result<Elements, FormatError>>reduce(new Ok<>(initial),
                        (maybeBuffer, maybeElement) -> maybeBuffer.flatMapValue(buffer -> maybeElement.mapValue(value -> folder.apply(
                                buffer,
                                value))),
                        (_, next) -> next);
    }
}
