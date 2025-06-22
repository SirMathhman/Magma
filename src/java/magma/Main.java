package magma;

import magma.divide.DivideState;
import magma.divide.MutableDivideState;
import magma.error.ApplicationError;
import magma.error.FormattedError;
import magma.error.IOError;
import magma.factory.SimpleResultFactory;
import magma.list.ListLike;
import magma.list.ListLikes;
import magma.node.EverythingNode;
import magma.node.MapNodeFactory;
import magma.node.result.NodeErr;
import magma.node.result.NodeOk;
import magma.node.result.NodeResult;
import magma.option.None;
import magma.option.Option;
import magma.option.Some;
import magma.path.PathLike;
import magma.path.PathLikes;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;
import magma.rule.InfixRule;
import magma.rule.OrRule;
import magma.rule.PrefixRule;
import magma.rule.Rule;
import magma.rule.StringRule;
import magma.rule.StripRule;
import magma.rule.SuffixRule;
import magma.rule.TypeRule;
import magma.string.StringErr;
import magma.string.StringOk;
import magma.string.StringResult;

import java.util.HashMap;
import java.util.Map;

class Main {
    private static final String SEPARATOR = System.lineSeparator();

    private Main() {
    }

    public static void main(final String[] args) {
        PathLikes.get(".", "src", "java")
                .walk()
                .match(Main::runWithFiles, Some::new)
                .ifPresent(error -> System.err.println(error.display()));
    }

    private static Option<ApplicationError> runWithFiles(final ListLike<PathLike> files) {
        final var sources = files.stream()
                .filter(file -> file.asString()
                        .endsWith(".java"))
                .toList();

        return Main.compileAll(sources)
                .match(Main::writeTarget, Some::new);
    }

    private static Option<ApplicationError> writeTarget(final String compiled) {
        final var target = PathLikes.get(".", "diagram.puml");
        final var output = String.join(Main.SEPARATOR, "@startuml", "skinparam linetype ortho", compiled, "@enduml");
        return target.writeString(output)
                .map(ApplicationError::new);
    }

    private static Result<String, ApplicationError> compileAll(final Iterable<PathLike> sources) {
        return Main.readAll(sources)
                .mapErr(ApplicationError::new)
                .flatMapValue(inputs -> Main.compileEntryToResult(inputs)
                        .mapErr(ApplicationError::new));
    }

    private static Result<String, FormattedError> compileEntryToResult(final Map<String, String> inputs) {
        return switch (Main.compileEntry(inputs)) {
            case StringErr(final var error) -> new Err<>(error);
            case StringOk(final var value) -> new Ok<>(value);
        };
    }

    private static Result<Map<String, String>, IOError> readAll(final Iterable<PathLike> sources) {
        Result<Map<String, String>, IOError> maybeInputs = new Ok<>(new HashMap<>());
        for (final var source : sources)
            maybeInputs = Main.attachEntry(source, maybeInputs);
        return maybeInputs;
    }

    private static Result<Map<String, String>, IOError> attachEntry(final PathLike source, final Result<Map<String, String>, IOError> maybeInputs) {
        final var fileName = source.getFileName()
                .asString();
        final var separator = fileName.lastIndexOf('.');
        final var name = fileName.substring(0, separator);

        return maybeInputs.flatMapValue(inputs -> source.readString()
                .mapValue(input -> {
                    inputs.put(name, input);
                    return inputs;
                }));
    }

    private static StringResult compileEntry(final Map<String, String> inputs) {
        StringResult maybeCompiled = new StringOk();
        for (final var source : inputs.entrySet()) {
            final var name = source.getKey();
            final var input = source.getValue();

            maybeCompiled = maybeCompiled.tryAppendResult(() -> Main.compile(input, name));
        }

        return maybeCompiled;
    }

    private static StringResult compile(final CharSequence input, final String name) {
        final var segments = Main.divide(input);
        return segments.stream()
                .<StringResult>reduce(new StringOk(),
                        (output, segment) -> Main.getStringResult(name, output, segment),
                        (_, next) -> next)
                .prepend("class " + name + Main.SEPARATOR);
    }

    private static ListLike<String> divide(final CharSequence input) {
        final var segments = ListLikes.<String>empty();
        final var buffer = new StringBuilder();
        final var depth = 0;
        return Main.getStringListLike(input, new MutableDivideState(segments, buffer, depth));
    }

    private static ListLike<String> getStringListLike(final CharSequence input, final DivideState state) {
        var current = state;
        final var length = input.length();
        for (var i = 0; i < length; i++) {
            final var c = input.charAt(i);
            current = Main.fold(current, c);
        }

        return current.advance()
                .toList();
    }

    private static DivideState fold(final DivideState current, final char c) {
        final var appended = current.append(c);
        if (';' == c && appended.isLevel())
            return appended.advance();

        if ('{' == c)
            return appended.enter();

        if ('}' == c)
            return appended.exit();

        return appended;
    }

    private static StringResult getStringResult(final String name, final StringResult output, final String segment) {
        final var lexed = Main.createRootSegmentRule()
                .lex(segment);

        final var generated = (Result<Option<StringResult>, FormattedError>) switch (lexed) {
            case NodeErr(final var error1) -> new Err<>(error1);
            case NodeOk(final EverythingNode value1) -> new Ok<>(Main.transformAndGenerate(name, value1));
        };

        return switch (generated) {
            case Err<Option<StringResult>, FormattedError>(final var error) -> new StringErr(error);
            case Ok<Option<StringResult>, FormattedError>(final var value) ->
                    value instanceof Some(final var result) ? output.appendResult(result) : output;
        };
    }

    private static Option<StringResult> transformAndGenerate(final String name, final EverythingNode destination) {
        final var node = destination.withString("source", name);
        final var destination1 = node.findString("destination")
                .orElse("");

        if (Main.isFunctionalInterface(destination1))
            return new None<>();

        if (node.is("import"))
            return new Some<>(Main.generate(node.retype("dependency")));
        else
            return new None<>();
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> createRootSegmentRule() {
        return new OrRule<>(ListLikes.of(Main.createImportRule("package"),
                Main.createImportRule("import"),
                Main.createStructureRule("record"),
                Main.createStructureRule("interface"),
                Main.createStructureRule("class")));
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> createStructureRule(final String infix) {
        return new InfixRule<>(new StringRule<EverythingNode>("before-keyword",
                new MapNodeFactory(),
                new SimpleResultFactory()),
                infix,
                new StringRule<EverythingNode>("after-keyword", new MapNodeFactory(), new SimpleResultFactory()));
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> createImportRule(final String type) {
        final var destination = new StringRule<EverythingNode>("destination",
                new MapNodeFactory(),
                new SimpleResultFactory());
        final var withParent = new InfixRule<>(new StringRule<EverythingNode>("parent",
                new MapNodeFactory(),
                new SimpleResultFactory()), ".", destination);
        final var parent = new OrRule<>(ListLikes.of(withParent,
                new StringRule<EverythingNode>("value", new MapNodeFactory(), new SimpleResultFactory())));

        return new TypeRule<>(type, new StripRule<>(new PrefixRule<>(type + " ", new SuffixRule<>(parent, ";"))));
    }

    private static boolean isFunctionalInterface(final String destination) {
        return ListLikes.of("Consumer", "Function", "Supplier")
                .contains(destination);
    }

    private static StringResult generate(final EverythingNode node) {
        return new OrRule<>(ListLikes.of(Main.createDependencyRule(), Main.createPlaceholderRule())).generate(node);
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> createPlaceholderRule() {
        return new TypeRule<>("placeholder",
                new StringRule<EverythingNode>("value", new MapNodeFactory(), new SimpleResultFactory()));
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> createDependencyRule() {
        return new TypeRule<>("dependency", new SuffixRule<>(new InfixRule<>(new StringRule<EverythingNode>("source",
                        new MapNodeFactory(),
                        new SimpleResultFactory()),
                " --> ",
                new StringRule<EverythingNode>("destination", new MapNodeFactory(), new SimpleResultFactory())),
                        Main.SEPARATOR));
    }
}
