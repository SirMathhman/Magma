package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


class Main {
    private static final String SEPARATOR = System.lineSeparator();

    private Main() {
    }

    public static void main(final String[] args) {
        final var sourceRoot = Paths.get(".", "src", "java");
        Main.walk(sourceRoot)
                .mapValue(Main::filter)
                .flatMapValue(Main::compileSources)
                .match(Main::write, Optionals::of)
                .ifPresent(Throwable::printStackTrace);
    }

    private static OptionalLike<IOException> write(final String output) {
        try {
            final var target = Paths.get(".", "diagram.puml");
            final var joined = String.join(Main.SEPARATOR, "@startuml", "skinparam linetype ortho", output, "@enduml");
            Files.writeString(target, joined);
            return Optionals.empty();
        } catch (final IOException e) {
            return Optionals.of(e);
        }
    }

    private static Result<String, IOException> compileSources(final SetLike<Path> sources) {
        return sources.stream()
                .map(Main::compileSource)
                .collect(new ResultCollector<>(new Joiner()))
                .mapValue(value -> value.orElse(""));
    }

    private static SetLike<Path> filter(final JavaStream<Path> files) {
        final Collector<Path, SetLike<Path>> toSet = new SetCollector<>();
        return files.filter(Files::isRegularFile)
                .filter(path -> {
                    final var pathAsString = path.toString();
                    return pathAsString.endsWith(".java");
                })
                .collect(toSet);
    }

    private static Result<JavaStream<Path>, IOException> walk(final Path sourceRoot) {
        try {
            return new Ok<>(new JavaStream<>(Files.walk(sourceRoot)));
        } catch (final IOException e) {
            return new Err<>(e);
        }
    }

    private static Result<String, IOException> compileSource(final Path source) {
        try {
            final var fileName = source.getFileName()
                    .toString();
            final var separator = fileName.lastIndexOf('.');
            final var name = fileName.substring(0, separator);

            final var input = Files.readString(source);
            final var compiled = Main.compile(input, name);

            return new Ok<>("class " + name + Main.SEPARATOR + compiled);
        } catch (final IOException e) {
            return new Err<>(e);
        }
    }

    private static String compile(final CharSequence input, final String source) {
        return Main.divide(input)
                .stream()
                .map(segment -> Main.compileRootSegment(segment, source))
                .flatMap(OptionalLike::stream)
                .collect(new Joiner())
                .orElse("");
    }

    private static OptionalLike<String> compileRootSegment(final String input, final String name) {
        return Main.createImportRule()
                .lex(input)
                .flatMap(node -> {
                    final Node withSource = node.withString("source", name);
                    return Main.createDependencyRule()
                            .generate(withSource);
                });
    }

    private static Rule createImportRule() {
        return new StripRule(new PrefixRule("import ",
                new SuffixRule(new LastRule(new StringRule("parent"), ".", new StringRule("destination")), ";")));
    }

    private static Rule createDependencyRule() {
        return new SuffixRule(new LastRule(new StringRule("source"), " --> ", new StringRule("destination")),
                Main.SEPARATOR);
    }

    private static ListLike<String> divide(final CharSequence input) {
        final State state = new MutableState();
        final var length = input.length();
        var current = state;
        for (var i = 0; i < length; i++) {
            final var c = input.charAt(i);
            current = Main.fold(current, c);
        }

        return current.advance()
                .segments();
    }

    private static State fold(final State state, final char c) {
        final var appended = state.append(c);
        if (';' == c)
            return appended.advance();
        return appended;
    }
}
