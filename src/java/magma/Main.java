package magma;

import magma.error.ApplicationError;
import magma.error.CompileError;
import magma.error.IOError;
import magma.list.ListLike;
import magma.list.ListLikes;
import magma.node.Node;
import magma.option.Option;
import magma.option.Some;
import magma.path.PathLike;
import magma.path.PathLikes;
import magma.result.Ok;
import magma.result.Result;
import magma.rule.LastRule;
import magma.rule.PrefixRule;
import magma.rule.Rule;
import magma.rule.StringOk;
import magma.rule.StringRule;
import magma.rule.StripRule;
import magma.rule.SuffixRule;
import magma.string.StringErr;
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
                .flatMapValue(inputs -> Main.compileEntry(inputs)
                        .toResult()
                        .mapErr(ApplicationError::new));
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

            maybeCompiled = maybeCompiled.appendResult(() -> Main.compile(input, name));
        }

        return maybeCompiled;
    }

    private static StringResult compile(final String input, final String name) {
        final var segments = input.split(";");

        StringResult output = new StringOk();
        for (final var segment : segments)
            output = output.appendResult(() -> Main.createImportRule(name)
                    .lex(segment)
                    .generate(destination -> Main.getRecord(destination.withString("source", name))));

        return output.prepend("class " + name + Main.SEPARATOR);
    }

    private static Rule<Node, StringResult> createImportRule(final String name) {
        final var destination = new StringRule("destination");
        return new StripRule(name, new PrefixRule("import ", new LastRule(null, ".", destination)));
    }

    private static StringResult getRecord(final Node node) {
        final var destination = node.findString("destination")
                .orElse("");
        if (Main.isFunctionalInterface(destination))
            return new StringErr(new CompileError("?", "?"));

        return Main.getStringSome(node);
    }

    private static boolean isFunctionalInterface(final String destination) {
        return ListLikes.of("Consumer", "Function", "Supplier")
                .contains(destination);
    }

    private static StringResult getStringSome(final Node node) {
        return new SuffixRule<>(new LastRule(new StringRule("source"), " --> ", new StringRule("destination")),
                Main.SEPARATOR).generate(node);
    }
}
