package magma;

import magma.error.IOError;
import magma.list.ListLike;
import magma.list.ListLikes;
import magma.node.Node;
import magma.option.None;
import magma.option.Option;
import magma.option.Some;
import magma.path.PathLike;
import magma.path.PathLikes;
import magma.result.Ok;
import magma.result.Result;
import magma.rule.LastRule;
import magma.rule.PrefixRule;
import magma.rule.Rule;
import magma.rule.StringRule;
import magma.rule.StripRule;

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

    private static Option<IOError> runWithFiles(final ListLike<PathLike> files) {
        final var sources = files.stream()
                .filter(file -> file.asString()
                        .endsWith(".java"))
                .toList();

        return Main.compileAll(sources)
                .match(Main::writeTarget, Some::new);
    }

    private static Option<IOError> writeTarget(final String compiled) {
        final var target = PathLikes.get(".", "diagram.puml");
        final var output = String.join(Main.SEPARATOR, "@startuml", "skinparam linetype ortho", compiled, "@enduml");
        return target.writeString(output);
    }

    private static Result<String> compileAll(final Iterable<PathLike> sources) {
        Result<StringBuilder> maybeCompiled = new Ok<>(new StringBuilder());
        for (final var source : sources) {
            final var fileName = source.getFileName()
                    .asString();
            final var separator = fileName.lastIndexOf('.');
            final var name = fileName.substring(0, separator);

            final var maybeOutput = source.readString()
                    .map(value -> Main.compile(value, name));

            maybeCompiled = maybeCompiled.flatMap(compiled -> maybeOutput.map(compiled::append));
        }

        return maybeCompiled.map(StringBuilder::toString);
    }

    private static String compile(final String input, final String name) {
        final var segments = input.split(";");

        final var output = new StringBuilder();
        for (final var segment : segments)
            Main.createImportRule(name)
                    .lex(segment)
                    .flatMap(destination -> Main.getRecord(destination.withString("source", name)))
                    .ifPresent(output::append);

        return "class " + name + Main.SEPARATOR + output;
    }

    private static Rule createImportRule(final String name) {
        final var destination = new StringRule("destination");
        return new StripRule(name, new PrefixRule("import ", new LastRule(".", destination)));
    }

    private static Option<String> getRecord(final Node node) {
        if (!ListLikes.of("Function", "Consumer")
                .contains(node.findString("destination")
                        .orElse("")))
            return new Some<>(node.findString("source")
                    .orElse("") + " --> " + node.findString("destination")
                    .orElse("") + Main.SEPARATOR);

        return new None<>();
    }
}
