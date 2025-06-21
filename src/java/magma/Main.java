package magma;

import java.nio.file.Files;
import java.nio.file.Path;

class Main {
    private static final String SEPARATOR = System.lineSeparator();

    private Main() {
    }

    public static void main(final String[] args) {
        final var sourceRoot = Paths.get(".", "src", "java");
        sourceRoot.walk()
                .mapValue(Main::filter)
                .flatMapValue(Main::compileSources)
                .match(Main::write, Optionals::of)
                .ifPresent(error -> System.err.println(error.display()));
    }

    private static OptionalLike<IOError> write(final String output) {
        final var target = Paths.get(".", "diagram.puml");
        final var joined = String.join(Main.SEPARATOR, "@startuml", "skinparam linetype ortho", output, "@enduml");
        return target.writeString(joined);
    }

    private static Result<String, IOError> compileSources(final SetLike<Path> sources) {
        return sources.stream()
                .map(Main::compileSource)
                .collect(new ResultCollector<>(new Joiner()))
                .mapValue(value -> value.orElse(""));
    }

    private static SetLike<Path> filter(final StreamLike<Path> files) {
        return files.filter(Files::isRegularFile)
                .filter(path -> path.toString()
                        .endsWith(".java"))
                .collect(new SetCollector<Path>());
    }

    private static Result<String, IOError> compileSource(final Path source) {
        final var fileName = source.getFileName()
                .toString();

        final var separator = fileName.lastIndexOf('.');
        final var name = fileName.substring(0, separator);

        return new JavaPath(source).readString()
                .mapValue(input -> {
                    final var compiled = Main.compile(input, name);
                    return "class " + name + Main.SEPARATOR + compiled;
                });
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
