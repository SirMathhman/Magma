/*private */class DivideState{/*
        DivideState append(char c);*//*

        DivideState advance();*//*

        Collection<String> unwrap();*//*

        boolean isLevel();*//*

        DivideState exit();*//*

        DivideState enter();*//*

        boolean isShallow();*//*

        Optional<Tuple<DivideState, Character>> pop();*//*

        Optional<Tuple<DivideState, Character>> popAndAppendToTuple();*//*

        Optional<DivideState> popAndAppendToOption();*//*
    */}/*private */class Node{/*
        Node withString(String key, String value);*//*

        Optional<String> findString(String key);*//*

        Node merge(Node other);*//*

        Stream<Map.Entry<String, String>> streamStrings();*//*

        Node retype(String type);*//*

        boolean is(String type);*//*

        Node withNodeList(String key, List<Node> values);*//*

        Optional<List<Node>> findNodeList(String key);*//*

        String display();*//*

        Stream<Map.Entry<String, List<Node>>> streamNodeLists();*//*

        Stream<String> streamPropertyNames();*//*

        Optional<Tuple<Node, String>> removeString(String key);*//*

        boolean hasNoProperties();*//*

        Optional<Tuple<Node, List<Node>>> removeNodeList(String key);*//*

        boolean hasNoName();*//*
    */}/*private */class Rule{/*
        Result<String, FormatError> generate(Node node);*//*

        Result<Node, FormatError> lex(String input);*//*
    */}/*private sealed */class Result<Value, Error> permits Ok, Err{/*
        <Return> Result<Return, Error> flatMapValue(Function<Value, Result<Return, Error>> mapper);*//*

        <Return> Result<Return, Error> mapValue(Function<Value, Return> mapper);*//*

        <Return> Return match(Function<Value, Return> whenOk, Function<Error, Return> whenErr);*//*

        <Return> Result<Value, Return> mapErr(Function<Error, Return> mapper);*//*
    */}/*private */class Context{/*
        String display();*//*
    */}/*private */class Error{/*
        String display();*//*
    */}/*private */class Accumulator<T>{/*
        boolean hasValue();*//*

        Accumulator<T> withValue(T value);*//*

        Accumulator<T> withError(FormatError error);*//*

        <Return> Return match(Function<T, Return> whenOk, Function<List<FormatError>, Return> whenErr);*//*
    */}/*private */class FormatError extends Error{/*
        @Override
        default String display() {
            return format(0);
        }*//*

        String format(int depth);*//*
    */}/*private sealed */class JavaRootSegment permits Whitespace, Package, Import, JavaStructure{/*
    */}/*private sealed */class JavaStructureMember permits JavaStructure, Placeholder{/*
    */}/*@Retention(RetentionPolicy.RUNTIME)
    private @*/class NodeRepr{/*
        String name();*//*
    */}/*private */class TSRootSegment{/*
    */}/*@NodeRepr(name = "whitespace")
    private */class Whitespace() implements JavaRootSegment{/*
    */}/*@NodeRepr(name = "placeholder")
    private */class Placeholder(String value) implements JavaStructureMember{/*
    */}/*@NodeRepr(name = "package")
    private */class Package(String content) implements JavaRootSegment{/*
    */}/*@NodeRepr(name = "import")
    private */class Import(String content) implements JavaRootSegment{/*
    */}/*private */class StringContext(String value) implements Context{/*
        @Override
        public String display() {
            return value;
        }*//*
    */}/*private */class NodeContext(Node value) implements Context{/*
        @Override
        public String display() {
            return value.display();
        }*//*
    */}/*private */class CompileError(String message, Context context, List<FormatError> errors) implements FormatError{/*
        private CompileError(final String message, final Context context) {
            this(message, context, new ArrayList<>());
        }*//*

        @Override
        public String format(final int depth) {
            final var joined = errors.stream()
                    .map(error -> error.format(depth + 1))
                    .map(result -> System.lineSeparator() + "\t".repeat(depth) + result)
                    .collect(Collectors.joining());

            return message + ": " + context.display() + joined;
        }*//*
    */}/*private */class Tuple<Left, Right>(Left left, Right right){/*
    */}/*private static */class MutableDivideState implements DivideState{/*
        private final Collection<String> segments = new ArrayList<>();*//*
        private final CharSequence input;*//*
        private final int length;*//*
        private int depth = 0;*//*
        private StringBuilder buffer = new StringBuilder();*//*
        private int index = 0;*//*

        private MutableDivideState(final CharSequence input) {
            this.input = input;
            length = input.length();
        }*//*

        @Override
        public Collection<String> unwrap() {
            return Collections.unmodifiableCollection(segments);
        }*//*

        @Override
        public boolean isLevel() {
            return 0 == depth;
        }*//*

        @Override
        public DivideState exit() {
            depth--;
            return this;
        }*//*

        @Override
        public DivideState enter() {
            depth++;
            return this;
        }*//*

        @Override
        public boolean isShallow() {
            return 1 == depth;
        }*//*

        @Override
        public Optional<Tuple<DivideState, Character>> pop() {
            if (index < length) {
                final var c = input.charAt(index);
                index++;
                return Optional.of(new Tuple<>(this, c));
            }
            else
                return Optional.empty();
        }*//*

        @Override
        public Optional<Tuple<DivideState, Character>> popAndAppendToTuple() {
            return pop().map(tuple -> new Tuple<>(tuple.left.append(tuple.right), tuple.right));
        }*//*

        @Override
        public Optional<DivideState> popAndAppendToOption() {
            return pop().map(tuple -> tuple.left.append(tuple.right));
        }*//*

        @Override
        public DivideState append(final char c) {
            buffer.append(c);
            return this;
        }*//*

        @Override
        public DivideState advance() {
            segments.add(buffer.toString());
            buffer = new StringBuilder();
            return this;
        }*//*
    */}/*private static final */class MapNode implements Node{/*
        private final Optional<String> maybeType;*//*
        private final Map<String, String> strings;*//*
        private final Map<String, List<Node>> nodeLists;*//*

        private MapNode(final Optional<String> maybeType, final Map<String, String> strings, final Map<String, List<Node>> nodeLists) {
            this.maybeType = maybeType;
            this.strings = strings;
            this.nodeLists = nodeLists;
        }*//*

        private MapNode() {
            this(Optional.empty(), new HashMap<>(), new HashMap<>());
        }*//*

        @Override
        public String toString() {
            return display();
        }*//*

        @Override
        public Node withString(final String key, final String value) {
            strings.put(key, value);
            return this;
        }*//*

        @Override
        public Optional<String> findString(final String key) {
            return Optional.ofNullable(strings.get(key));
        }*//*

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
        }*//*

        @Override
        public Stream<Map.Entry<String, String>> streamStrings() {
            return strings.entrySet()
                    .stream();
        }*//*

        @Override
        public Node retype(final String type) {
            return new MapNode(Optional.of(type), strings, nodeLists);
        }*//*

        @Override
        public boolean is(final String type) {
            return maybeType.isPresent() && maybeType.get()
                    .contentEquals(type);
        }*//*

        @Override
        public Node withNodeList(final String key, final List<Node> values) {
            nodeLists.put(key, values);
            return this;
        }*//*

        @Override
        public Optional<List<Node>> findNodeList(final String key) {
            return Optional.ofNullable(nodeLists.get(key));
        }*//*

        @Override
        public String display() {
            return maybeType.toString() + strings.toString() + nodeLists.toString();
        }*//*

        @Override
        public Stream<Map.Entry<String, List<Node>>> streamNodeLists() {
            return nodeLists.entrySet()
                    .stream();
        }*//*

        @Override
        public Stream<String> streamPropertyNames() {
            return Stream.concat(strings.keySet()
                            .stream(),
                    nodeLists.keySet()
                            .stream());
        }*//*

        @Override
        public Optional<Tuple<Node, String>> removeString(final String key) {
            if (strings.containsKey(key)) {
                final var copy = new HashMap<>(strings);
                final var value = copy.remove(key);
                return Optional.of(new Tuple<>(new MapNode(maybeType, copy, nodeLists), value));
            }

            return Optional.empty();
        }*//*

        @Override
        public boolean hasNoProperties() {
            return strings.isEmpty() && nodeLists.isEmpty();
        }*//*

        @Override
        public Optional<Tuple<Node, List<Node>>> removeNodeList(final String key) {
            if (nodeLists.containsKey(key)) {
                final var copy = new HashMap<>(nodeLists);
                final var value = copy.remove(key);
                return Optional.of(new Tuple<>(new MapNode(maybeType, strings, copy), value));
            }

            return Optional.empty();
        }*//*

        @Override
        public boolean hasNoName() {
            return maybeType.isEmpty();
        }*//*
    */}/*private */class StringRule(String key) implements Rule{/*
        @Override
        public Result<String, FormatError> generate(final Node node) {
            return node.findString(key)
                    .<Result<String, FormatError>>map(Ok::new)
                    .orElseGet(() -> new Err<>(new CompileError("String '" + key + "' not present",
                            new NodeContext(node))));
        }*//*

        @Override
        public Result<Node, FormatError> lex(final String input) {
            return new Ok<>(new MapNode().withString(key(), input));
        }*//*
    */}/*private */class InfixRule(Rule leftRule, String infix, Rule rightRule) implements Rule{/*
        @Override
        public Result<String, FormatError> generate(final Node node) {
            return leftRule.generate(node)
                    .flatMapValue(leftResult -> rightRule.generate(node)
                            .mapValue(rightResult -> leftResult + infix + rightResult));
        }*//*

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
        }*//*
    */}/*private */class SuffixRule(Rule rule, String suffix) implements Rule{/*
        @Override
        public Result<String, FormatError> generate(final Node node) {
            return rule.generate(node)
                    .mapValue(result -> result + suffix);
        }*//*

        @Override
        public Result<Node, FormatError> lex(final String input) {
            if (!input.endsWith(suffix()))
                return new Err<>(new CompileError("Suffix '" + suffix + "' not present", new StringContext(input)));

            final var withoutEnd = input.substring(0, input.length() - suffix().length());
            return rule().lex(withoutEnd);
        }*//*
    */}/*private */class StripRule(Rule rule) implements Rule{/*
        @Override
        public Result<String, FormatError> generate(final Node node) {
            return rule.generate(node);
        }*//*

        @Override
        public Result<Node, FormatError> lex(final String input) {
            final var stripped = input.strip();
            return rule.lex(stripped);
        }*//*
    */}/*private */class TypeRule(String type, Rule rule) implements Rule{/*
        @Override
        public Result<String, FormatError> generate(final Node node) {
            if (node.is(type))
                return rule.generate(node);
            return new Err<>(new CompileError("Type '" + type + "' not present", new NodeContext(node)));
        }*//*

        @Override
        public Result<Node, FormatError> lex(final String input) {
            return rule.lex(input)
                    .mapValue(node -> node.retype(type));
        }*//*
    */}/*private */class PlaceholderRule(Rule rule) implements Rule{/*
        private static String generatePlaceholder(final String input) {
            final var replaced = input.replace("start", "start")
                    .replace("end", "end");

            return "start" + replaced + "end";
        }*//*

        @Override
        public Result<String, FormatError> generate(final Node node) {
            return rule.generate(node)
                    .mapValue(PlaceholderRule::generatePlaceholder);
        }*//*

        @Override
        public Result<Node, FormatError> lex(final String input) {
            return rule.lex(input);
        }*//*
    */}/*private */class OrRule(List<Rule> rules) implements Rule{/*
        @Override
        public Result<String, FormatError> generate(final Node node) {
            return Main.disjoin(rules, rule -> rule.generate(node), new NodeContext(node));
        }*//*

        @Override
        public Result<Node, FormatError> lex(final String input) {
            return Main.disjoin(rules, rule -> rule.lex(input), new StringContext(input));
        }*//*
    */}/*private */class PrefixRule(String prefix, Rule rule) implements Rule{/*
        @Override
        public Result<String, FormatError> generate(final Node node) {
            return rule.generate(node)
                    .mapValue(result -> prefix + result);
        }*//*

        @Override
        public Result<Node, FormatError> lex(final String input) {
            if (input.startsWith(prefix))
                return rule.lex(input.substring(prefix.length()));
            return new Err<>(new CompileError("Prefix '" + prefix + "' not present", new StringContext(input)));
        }*//*
    */}/*private */class DivideRule(String key, Rule rule) implements Rule{/*
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
        }*//*

        private static DivideState fold(final DivideState state, final char c) {
            return DivideRule.foldSingleQuotes(state, c)
                    .orElseGet(() -> DivideRule.foldStatement(state, c));
        }*//*

        private static Optional<DivideState> foldSingleQuotes(final DivideState state, final char c) {
            if ('\'' != c)
                return Optional.empty();

            final var appended = state.append('\'');
            return appended.popAndAppendToTuple()
                    .flatMap(tuple -> '\\' == tuple.right ? tuple.left.popAndAppendToOption() : Optional.of(tuple.left))
                    .flatMap(DivideState::popAndAppendToOption);

        }*//*

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
        }*//*

        @Override
        public Result<String, FormatError> generate(final Node node) {
            return node.findNodeList(key)
                    .map(nodes -> Main.combine(nodes, new StringBuilder(), rule::generate, StringBuilder::append)
                            .mapValue(StringBuilder::toString))
                    .orElseGet(() -> new Err<>(new CompileError("Node list '" + key + "' not present",
                            new NodeContext(node))));
        }*//*

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
        }*//*
    */}/*private */class Ok<Value, Error>(Value value) implements Result<Value, Error>{/*
        @Override
        public <Return> Result<Return, Error> flatMapValue(final Function<Value, Result<Return, Error>> mapper) {
            return mapper.apply(value);
        }*//*

        @Override
        public <Return> Result<Return, Error> mapValue(final Function<Value, Return> mapper) {
            return new Ok<>(mapper.apply(value));
        }*//*

        @Override
        public <Return> Return match(final Function<Value, Return> whenOk, final Function<Error, Return> whenErr) {
            return whenOk.apply(value);
        }*//*

        @Override
        public <Return> Result<Value, Return> mapErr(final Function<Error, Return> mapper) {
            return new Ok<>(value);
        }*//*
    */}/*private */class Err<Value, Error>(Error error) implements Result<Value, Error>{/*
        @Override
        public <Return> Result<Return, Error> flatMapValue(final Function<Value, Result<Return, Error>> mapper) {
            return new Err<>(error);
        }*//*

        @Override
        public <Return> Result<Return, Error> mapValue(final Function<Value, Return> mapper) {
            return new Err<>(error);
        }*//*

        @Override
        public <Return> Return match(final Function<Value, Return> whenOk, final Function<Error, Return> whenErr) {
            return whenErr.apply(error);
        }*//*

        @Override
        public <Return> Result<Value, Return> mapErr(final Function<Error, Return> mapper) {
            return new Err<>(mapper.apply(error));
        }*//*
    */}/*private */class ThrowableError(Throwable throwable) implements Error{/*
        @Override
        public String display() {
            final var writer = new StringWriter();
            throwable.printStackTrace(new PrintWriter(writer));
            return writer.toString();
        }*//*
    */}/*private */class ApplicationError(Error error) implements Error{/*
        @Override
        public String display() {
            return error.display();
        }*//*
    */}/*private */class ImmutableAccumulator<T>(Optional<T> maybeValue, List<FormatError> errors) implements Accumulator<T>{/*
        private ImmutableAccumulator() {
            this(Optional.empty(), new ArrayList<>());
        }*//*

        @Override
        public boolean hasValue() {
            return maybeValue.isPresent();
        }*//*

        @Override
        public Accumulator<T> withValue(final T value) {
            return new ImmutableAccumulator<>(Optional.of(value), errors);
        }*//*

        @Override
        public Accumulator<T> withError(final FormatError error) {
            errors.add(error);
            return this;
        }*//*

        @Override
        public <Return> Return match(final Function<T, Return> whenOk, final Function<List<FormatError>, Return> whenErr) {
            return maybeValue.map(whenOk)
                    .orElseGet(() -> whenErr.apply(errors));
        }*//*
    */}/*private static */class EmptyRule implements Rule{/*
        @Override
        public Result<String, FormatError> generate(final Node node) {
            return new Ok<>("");
        }*//*

        @Override
        public Result<Node, FormatError> lex(final String input) {
            return input.isEmpty() ? new Ok<>(new MapNode()) : new Err<>(new CompileError("Not empty",
                    new StringContext(input)));
        }*//*
    */}/*private static */class LazyRule implements Rule{/*
        private Optional<Rule> maybeRule = Optional.empty();*//*

        @Override
        public Result<String, FormatError> generate(final Node node) {
            return findRule(new NodeContext(node)).flatMapValue(rule -> rule.generate(node));
        }*//*

        private Result<Rule, FormatError> findRule(final Context context) {
            return maybeRule.<Result<Rule, FormatError>>map(Ok::new)
                    .orElseGet(() -> new Err<>(new CompileError("Rule not set", context)));
        }*//*

        @Override
        public Result<Node, FormatError> lex(final String input) {
            return findRule(new StringContext(input)).flatMapValue(rule -> rule.lex(input));
        }*//*

        void set(final Rule rule) {
            maybeRule = Optional.of(rule);
        }*//*
    */}/*@NodeRepr(name = "structure")
    private */class JavaStructure(String name, String modifiers, List<JavaStructureMember> members) implements
            JavaRootSegment,
            JavaStructureMember{/*
    */}/*private */class JavaRoot(List<JavaRootSegment> children){/*
    */}/*@NodeRepr(name = "root")
    private */class TSRoot(List<TSRootSegment> children){/*
    */}/*@NodeRepr(name = "structure")
    private */class TSStructure(String modifiers, String name, List<JavaStructureMember> members) implements
            TSRootSegment{/*
    */}