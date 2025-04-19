package magma;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Main {
    interface Result<T, X> {
        <R> R match(Function<T, R> whenOk, Function<X, R> whenErr);
    }


    interface Option<T> {
        static <T> Option<T> of(T value) {
            return new Some<>(value);
        }

        static <T> Option<T> empty() {
            return new None<>();
        }

        void ifPresent(Consumer<T> ifPresent);

        Option<T> or(Supplier<Option<T>> other);

        T orElseGet(Supplier<T> other);

        <R> Option<R> flatMap(Function<T, Option<R>> mapper);

        <R> Option<R> map(Function<T, R> mapper);

        T orElse(T other);

        Option<T> filter(Predicate<T> filter);

        boolean isPresent();
    }

    interface Error {
        String display();
    }

    record Some<T>(T value) implements Option<T> {
        @Override
        public void ifPresent(Consumer<T> ifPresent) {
            ifPresent.accept(this.value);
        }

        @Override
        public Option<T> or(Supplier<Option<T>> other) {
            return this;
        }

        @Override
        public T orElseGet(Supplier<T> other) {
            return this.value;
        }

        @Override
        public <R> Option<R> flatMap(Function<T, Option<R>> mapper) {
            return mapper.apply(this.value);
        }

        @Override
        public <R> Option<R> map(Function<T, R> mapper) {
            return new Some<>(mapper.apply(this.value));
        }

        @Override
        public T orElse(T other) {
            return this.value;
        }

        @Override
        public Option<T> filter(Predicate<T> filter) {
            return filter.test(this.value) ? this : new None<>();
        }

        @Override
        public boolean isPresent() {
            return true;
        }
    }

    private static class DivideState {
        private final JavaList<String> segments;
        private final String buffer;
        private final int depth;

        private DivideState(JavaList<String> segments, String buffer, int depth) {
            this.segments = segments;
            this.buffer = buffer;
            this.depth = depth;
        }

        public DivideState() {
            this(new JavaList<>(), "", 0);
        }

        private boolean isShallow() {
            return this.depth == 1;
        }

        private boolean isLevel() {
            return this.depth == 0;
        }

        private DivideState append(char c) {
            return new DivideState(this.segments, this.buffer + c, this.depth);
        }

        private DivideState advance() {
            return new DivideState(this.segments.addLast(this.buffer), "", this.depth);
        }

        private DivideState enter() {
            return new DivideState(this.segments, this.buffer, this.depth + 1);
        }

        private DivideState exit() {
            return new DivideState(this.segments, this.buffer, this.depth - 1);
        }
    }

    private record JavaList<T>(List<T> list) {
        public JavaList() {
            this(new ArrayList<>());
        }

        public JavaList<T> addLast(T element) {
            ArrayList<T> copy = new ArrayList<>(this.list);
            copy.add(element);
            return new JavaList<>(copy);
        }

        public JavaList<T> removeLast() {
            return new JavaList<>(this.list.subList(0, this.list.size() - 1));
        }

        public T last() {
            return this.list.getLast();
        }

        public JavaList<T> addFirst(T element) {
            ArrayList<T> copy = new ArrayList<>();
            copy.add(element);
            copy.addAll(this.list);
            return new JavaList<>(copy);
        }
    }

    private record Tuple<A, B>(A left, B right) {
    }

    record CompileState(JavaList<String> structs, JavaList<String> methods, JavaList<String> structNames) {
        public CompileState() {
            this(new JavaList<>(), new JavaList<>(), new JavaList<>());
        }

        public CompileState addMethod(String method) {
            return new CompileState(this.structs, this.methods.addLast(method), this.structNames);
        }

        public CompileState addStruct(String struct) {
            return new CompileState(this.structs.addLast(struct), this.methods, this.structNames);
        }

        public CompileState pushStructName(String structName) {
            return new CompileState(this.structs, this.methods, this.structNames.addLast(structName));
        }

        public CompileState popStructName() {
            return new CompileState(this.structs, this.methods, this.structNames.removeLast());
        }
    }

    record Ok<T, X>(T value) implements Result<T, X> {
        @Override
        public <R> R match(Function<T, R> whenOk, Function<X, R> whenErr) {
            return whenOk.apply(this.value);
        }
    }

    record Err<T, X>(X error) implements Result<T, X> {
        @Override
        public <R> R match(Function<T, R> whenOk, Function<X, R> whenErr) {
            return whenErr.apply(this.error);
        }
    }

    static class None<T> implements Option<T> {
        @Override
        public void ifPresent(Consumer<T> ifPresent) {
        }

        @Override
        public Option<T> or(Supplier<Option<T>> other) {
            return other.get();
        }

        @Override
        public T orElseGet(Supplier<T> other) {
            return other.get();
        }

        @Override
        public <R> Option<R> flatMap(Function<T, Option<R>> mapper) {
            return new None<>();
        }

        @Override
        public <R> Option<R> map(Function<T, R> mapper) {
            return new None<>();
        }

        @Override
        public T orElse(T other) {
            return other;
        }

        @Override
        public Option<T> filter(Predicate<T> filter) {
            return new None<>();
        }

        @Override
        public boolean isPresent() {
            return false;
        }
    }

    record ThrowableError(Throwable throwable) implements Error {
        @Override
        public String display() {
            StringWriter writer = new StringWriter();
            this.throwable.printStackTrace(new PrintWriter(writer));
            return writer.toString();
        }
    }

    record Node(Option<String> maybeType, Map<String, String> strings) {
        public Node() {
            this(Option.empty(), new HashMap<>());
        }

        public Node(String type) {
            this(Option.of(type), new HashMap<>());
        }

        public Node withString(String propertyKey, String propertyValue) {
            this.strings.put(propertyKey, propertyValue);
            return this;
        }

        public Option<String> findString(String propertyKey) {
            if (this.strings.containsKey(propertyKey)) {
                return new Some<>(this.strings.get(propertyKey));
            }
            else {
                return new None<>();
            }
        }

        public boolean is(String type) {
            return this.maybeType.filter(inner -> inner.equals(type)).isPresent();
        }
    }

    public static void main(String[] args) {
        run().ifPresent(error -> System.err.println(error.display()));
    }

    private static Option<Error> run() {
        Path source = Paths.get(".", "src", "java", "magma", "Main.java");
        return readString(source)
                .match(input -> runWithInput(source, input), Option::of)
                .map(ThrowableError::new);
    }

    private static Option<IOException> runWithInput(Path source, String input) {
        Path target = source.resolveSibling("main.c");
        String compile = compile(input);
        return writeString(target, compile).or(Main::build);
    }

    private static Option<IOException> build() {
        return start().match(process -> {
            try {
                process.waitFor();
                return Option.empty();
            } catch (InterruptedException e) {
                return Option.of(new IOException(e));
            }
        }, Option::of);
    }

    private static Result<Process, IOException> start() {
        try {
            return new Ok<>(new ProcessBuilder("cmd.exe", "/c", "build.bat")
                    .inheritIO()
                    .start()
            );
        } catch (IOException e) {
            return new Err<>(e);
        }
    }

    private static Option<IOException> writeString(Path target, String compile) {
        try {
            Files.writeString(target, compile);
            return Option.empty();
        } catch (IOException e) {
            return Option.of(e);
        }
    }

    private static Result<String, IOException> readString(Path path) {
        try {
            return new Ok<>(Files.readString(path));
        } catch (IOException e) {
            return new Err<>(e);
        }
    }

    private static String compile(String input) {
        CompileState methods = new CompileState();
        Tuple<CompileState, String> compiled = generateStatements(parseStatements(methods, input, parseDefault(Main::compileRootSegment)), Main::generateDefault);
        CompileState newState = compiled.left;
        String output = compiled.right;
        String joinedStructs = String.join("", newState.structs.list);
        String joinedMethods = String.join("", newState.methods.list);
        String joined = output + joinedStructs + joinedMethods;

        return joined + "int main(){\n\treturn 0;\n}\n";
    }

    private static Tuple<CompileState, String> generateStatements(Tuple<CompileState, JavaList<Node>> tuple, Function<Node, String> generator) {
        return mergeAll(tuple.left, tuple.right, generator, Main::mergeStatements);
    }

    private static Tuple<CompileState, JavaList<Node>> parseStatements(CompileState methods, String input, BiFunction<CompileState, String, Tuple<CompileState, Node>> parser) {
        return parseAll(methods, input, Main::foldStatementChar, parser);
    }

    private static BiFunction<CompileState, String, Tuple<CompileState, Node>> parseDefault(BiFunction<CompileState, String, Tuple<CompileState, String>> compiler) {
        return (state, s) -> {
            Tuple<CompileState, String> compiled = compiler.apply(state, s);
            return new Tuple<>(compiled.left, parseDefault(compiled.right));
        };
    }

    private static Tuple<CompileState, JavaList<Node>> parseAll(
            CompileState methods,
            String input,
            BiFunction<DivideState, Character, DivideState> folder,
            BiFunction<CompileState, String, Tuple<CompileState, Node>> parser
    ) {
        List<String> segments = divide(input, folder);
        CompileState current = methods;
        JavaList<Node> compiled = new JavaList<Node>();
        for (String segment : segments) {
            Tuple<CompileState, Node> compiledSegment = parser.apply(current, segment);
            current = compiledSegment.left;
            compiled = compiled.addLast(compiledSegment.right);
        }

        return new Tuple<>(current, compiled);
    }

    private static Tuple<CompileState, String> mergeAll(
            CompileState current,
            JavaList<Node> compiled,
            Function<Node, String> generator,
            BiFunction<String, String, String> merger
    ) {
        String output = "";
        for (Node element : compiled.list) {
            output = merger.apply(output, generator.apply(element));
        }

        return new Tuple<>(current, output);
    }

    private static String generateDefault(Node element) {
        return element.findString("value").orElse("");
    }

    private static List<String> divide(String input, BiFunction<DivideState, Character, DivideState> folder) {
        DivideState current = new DivideState();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            current = folder.apply(current, c);
        }

        return current.advance().segments.list;
    }

    private static String mergeStatements(String buffer, String element) {
        return buffer + element;
    }

    private static DivideState foldStatementChar(DivideState current, char c) {
        DivideState appended = current.append(c);
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

    private static Tuple<CompileState, String> compileRootSegment(CompileState methods, String input) {
        String stripped = input.strip();
        if (stripped.startsWith("package ")) {
            return new Tuple<>(methods, "");
        }

        return compileClass(methods, stripped)
                .orElseGet(() -> new Tuple<>(methods, generatePlaceholder(parseDefault(stripped)) + "\n"));
    }

    private static Option<Tuple<CompileState, String>> compileClass(CompileState state, String input) {
        int classIndex = input.indexOf("class ");
        if (classIndex < 0) {
            return Option.empty();
        }
        String modifiers = input.substring(0, classIndex);
        String afterKeyword = input.substring(classIndex + "class ".length());
        int contentStart = afterKeyword.indexOf("{");
        if (contentStart < 0) {
            return Option.empty();
        }
        String name = afterKeyword.substring(0, contentStart).strip();
        String withEnd = afterKeyword.substring(contentStart + "{".length()).strip();
        if (!withEnd.endsWith("}")) {
            return Option.empty();
        }
        String inputContent = withEnd.substring(0, withEnd.length() - "}".length());
        if (!isSymbol(name)) {
            return Option.empty();
        }
        CompileState methods = state.pushStructName(name);
        Tuple<CompileState, String> outputContent = generateStatements(parseStatements(methods, inputContent, parseDefault(Main::compileClassSegment)), Main::generateDefault);
        String generated = generatePlaceholder(parseDefault(modifiers)) + "struct " + name + " {" + outputContent.right + "};\n";
        return Option.of(new Tuple<>(outputContent.left
                .popStructName().addStruct(generated), ""));
    }

    private static boolean isSymbol(String input) {
        if (input.equals("private") || input.equals("record") || input.equals("public")) {
            return false;
        }

        for (int i = 0; i < input.length(); i++) {
            if (!Character.isLetter(input.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static Tuple<CompileState, String> compileClassSegment(CompileState state, String input) {
        return compileClass(state, input)
                .or(() -> compileMethod(state, input))
                .orElseGet(() -> getCompileStateStringTuple(state, input));
    }

    private static Option<Tuple<CompileState, String>> compileMethod(CompileState state, String input) {
        String stripped = input.strip();
        int paramStart = stripped.indexOf("(");
        if (paramStart < 0) {
            return Option.empty();
        }
        String definition = stripped.substring(0, paramStart).strip();
        return compileDefinition(state, definition).flatMap(outputDefinition -> {
            String withParams = stripped.substring(paramStart + "(".length());
            int paramEnd = withParams.indexOf(")");
            if (paramEnd < 0) {
                return Option.empty();
            }
            String oldParameters = withParams.substring(0, paramEnd);
            Tuple<CompileState, JavaList<Node>> tuple = parseValues(outputDefinition.left, oldParameters, parseDefault(Main::compileParameter));
            Tuple<CompileState, String> paramTuple = generateValues(tuple, Main::generateDefault);
            String withBraces = withParams.substring(paramEnd + ")".length()).strip();
            if (!withBraces.startsWith("{") || !withBraces.endsWith("}")) {
                return Option.empty();
            }
            String inputContent = withBraces.substring(1, withBraces.length() - 1);
            CompileState paramState = paramTuple.left;
            String paramOutput = paramTuple.right;

            Tuple<CompileState, String> outputContent = generateStatements(parseStatements(paramState, inputContent, parseDefault(Main::compileStatementOrBlock)), Main::generateDefault);
            String generated = outputDefinition.right + "(" + paramOutput + "){" + outputContent.right + "\n}\n";
            CompileState compileState = outputContent.left.addMethod(generated);
            return Option.of(new Tuple<>(compileState, ""));
        });
    }

    private static Tuple<CompileState, String> generateValues(Tuple<CompileState, JavaList<Node>> tuple, Function<Node, String> generator) {
        return mergeAll(tuple.left, tuple.right, generator, Main::mergeValues);
    }

    private static Tuple<CompileState, JavaList<Node>> parseValues(CompileState state, String input, BiFunction<CompileState, String, Tuple<CompileState, Node>> parser) {
        return parseAll(state, input, Main::foldValueChar, parser);
    }

    private static Tuple<CompileState, String> compileStatementOrBlock(CompileState state, String input) {
        String stripped = input.strip();
        if (stripped.endsWith(";")) {
            String substring = stripped.substring(0, stripped.length() - ";".length());
            Tuple<CompileState, String> compiled = compileStatementValue(state, substring);
            return new Tuple<>(compiled.left, "\n\t" + compiled.right + ";");
        }

        return getCompileStateStringTuple(state, stripped);
    }

    private static Tuple<CompileState, String> compileStatementValue(CompileState state, String input) {
        return parseInvocation(input, state)
                .map(tuple -> new Tuple<>(tuple.left, generateInvocation(tuple.right).orElse("")))
                .orElseGet(() -> getCompileStateStringTuple(state, input));
    }

    private static Option<Tuple<CompileState, Node>> parseInvocation(String input, CompileState state) {
        String stripped = input.strip();
        if (!stripped.endsWith(")")) {
            return Option.empty();
        }
        String withoutEnd = stripped.substring(0, stripped.length() - ")".length());

        int paramStart = -1;
        int depth = 0;
        for (int i = withoutEnd.length() - 1; i >= 0; i--) {
            char c = withoutEnd.charAt(i);
            if (c == '(' && depth == 0) {
                paramStart = i;
                break;
            }
            if (c == ')') {
                depth++;
            }
            if (c == '(') {
                depth--;
            }
        }

        if (paramStart < 0) {
            return Option.empty();
        }
        String callerString = withoutEnd.substring(0, paramStart).strip();
        String argumentsString = withoutEnd.substring(paramStart + "(".length()).strip();

        Tuple<CompileState, Node> parsed1 = parseValue(state, callerString);
        Tuple<CompileState, String> compiledCaller = new Tuple<>(parsed1.left, generateValue(caller));
        Tuple<CompileState, JavaList<Node>> argumentsTuple = parseValues(compiledCaller.left, argumentsString, Main::parseValue);

        Node oldCaller = parsed1.right;
        JavaList<Node> oldArguments = argumentsTuple.right;

        String newCaller;
        JavaList<Node> newArguments;
        if (oldCaller.is("access")) {
            String parent = oldCaller.findString("parent").orElse("");
            newArguments = oldArguments.addFirst(new Node("symbol").withString("value", parent));
            newCaller = compiledCaller.right;
        }
        else {
            newArguments = oldArguments;
            newCaller = compiledCaller.right;
        }

        Tuple<CompileState, JavaList<Node>> withNewArguments = new Tuple<>(argumentsTuple.left, newArguments);

        Tuple<CompileState, String> compiledArguments = generateValues(withNewArguments, Main::generateValue);
        String arguments = compiledArguments.right;

        Node node = new Node("invocation")
                .withString("caller", newCaller)
                .withString("arguments", arguments);

        return Option.of(new Tuple<>(compiledArguments.left, node));
    }

    private static Option<String> generateInvocation(Node node) {
        if (!node.is("invocation")) {
            return Option.empty();
        }

        String caller = node.findString("caller").orElse("");
        String arguments = node.findString("arguments").orElse("");
        return Option.of(caller + "(" + arguments + ")");
    }

    private static String generateValue(Node node) {
        return generateInvocation(node)
                .or(() -> generateAccess(node))
                .or(() -> generateSymbol(node))
                .orElseGet(() -> generatePlaceholder(node));
    }

    private static Tuple<CompileState, Node> parseValue(CompileState state, String input) {
        return parseInvocation(input, state)
                .or(() -> parseDataAccess(state, input))
                .or(() -> parseSymbol(state, input))
                .orElseGet(() -> new Tuple<>(state, parseDefault(input)));
    }

    private static Tuple<CompileState, String> getCompileStateStringTuple(CompileState state, String input) {
        return new Tuple<>(state, generatePlaceholder(parseDefault(input)));
    }

    private static Option<Tuple<CompileState, Node>> parseSymbol(CompileState state, String input) {
        if (isSymbol(input.strip())) {
            return Option.of(new Tuple<>(state, new Node("symbol").withString("value", input.strip())));
        }
        return Option.empty();
    }

    private static Option<String> generateSymbol(Node node) {
        if (node.is("symbol")) {
            return node.findString("value");
        }
        else {
            return Option.empty();
        }
    }

    private static Option<Tuple<CompileState, Node>> parseDataAccess(CompileState state, String input) {
        int propertySeparator = input.lastIndexOf(".");
        if (propertySeparator < 0) {
            return Option.empty();
        }
        String parent = input.substring(0, propertySeparator);
        String property = input.substring(propertySeparator + ".".length());

        Tuple<CompileState, Node> parsed = parseValue(state, parent);
        Tuple<CompileState, String> compiled = new Tuple<>(parsed.left, generateValue(parsed.right));
        return Option.of(new Tuple<>(compiled.left, new Node("access")
                .withString("parent", compiled.right)
                .withString("property", property)));
    }

    private static Option<String> generateAccess(Node node) {
        if (!node.is("access")) {
            return Option.empty();
        }

        String parent0 = node.findString("parent").orElse("");
        String property0 = node.findString("property").orElse("");

        return Option.of(parent0 + "." + property0);
    }

    private static String mergeValues(String cache, String element) {
        if (cache.isEmpty()) {
            return element;
        }
        return cache + ", " + element;
    }

    private static DivideState foldValueChar(DivideState state, Character c) {
        if (c == ',') {
            return state.advance();
        }
        return state.append(c);
    }

    private static Tuple<CompileState, String> compileParameter(CompileState state, String element) {
        return compileDefinition(state, element).orElseGet(() -> getCompileStateStringTuple(state, element));
    }

    private static Option<Tuple<CompileState, String>> compileDefinition(CompileState state, String input) {
        String stripped = input.strip();
        int nameSeparator = stripped.lastIndexOf(" ");
        if (nameSeparator < 0) {
            return Option.empty();
        }
        String beforeName = stripped.substring(0, nameSeparator).strip();
        String name = stripped.substring(nameSeparator + " ".length()).strip();
        String newName;
        if (name.equals("main")) {
            newName = "__main__";
        }
        else {
            newName = name + "_" + state.structNames.last();
        }

        int typeSeparator = beforeName.lastIndexOf(" ");
        if (typeSeparator >= 0) {
            String beforeType = beforeName.substring(0, typeSeparator);
            String type = beforeName.substring(typeSeparator + " ".length());
            String outputType = compileType(type).orElseGet(() -> generatePlaceholder(parseDefault(type)));

            String compiled = generatePlaceholder(parseDefault(beforeType)) + " " + outputType;
            return new Some<>(new Tuple<>(state, compiled + " " + newName));
        }
        else {
            return compileType(beforeName).map(compiled -> {
                return new Tuple<>(state, compiled + " " + newName);
            });
        }
    }

    private static Option<String> compileType(String type) {
        String stripped = type.strip();
        if (stripped.endsWith("[]")) {
            return compileType(stripped.substring(0, stripped.length() - "[]".length()))
                    .map(result -> result + "*");
        }

        if (type.equals("boolean")) {
            return Option.of("int");
        }

        if (type.equals("char")) {
            return Option.of("char");
        }

        if (type.equals("void")) {
            return Option.of("void");
        }

        if (type.equals("String")) {
            return Option.of("char*");
        }

        if (isSymbol(type)) {
            return Option.of("struct " + stripped);
        }
        return Option.empty();
    }

    private static String generatePlaceholder(Node node) {
        String replaced = node.findString("value")
                .orElse("")
                .replace("/*", "<comment-start>")
                .replace("*/", "<comment-end>");

        return "/* " + replaced + " */";
    }

    private static Node parseDefault(String input) {
        return new Node().withString("value", input);
    }
}
