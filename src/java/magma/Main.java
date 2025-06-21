package magma;

import magma.app.LastRule;
import magma.app.MutableState;
import magma.app.PrefixRule;
import magma.app.Rule;
import magma.app.State;
import magma.app.StringRule;
import magma.app.StripRule;
import magma.app.SuffixRule;
import magma.app.list.ListLike;
import magma.app.node.Node;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Main {
    private static final String SEPARATOR = System.lineSeparator();

    private Main() {
    }

    public static void main(final String[] args) {
        final var sourceRoot = Paths.get(".", "src", "java");
        try (final var files = Files.walk(sourceRoot)) {
            final Collector<Path, ?, Set<Path>> toSet = Collectors.toSet();
            final var sources = files.filter(Files::isRegularFile)
                    .filter(path -> {
                        final var pathAsString = path.toString();
                        return pathAsString.endsWith(".java");
                    })
                    .collect(toSet);

            final StringBuilder output = new StringBuilder();
            for (final var source : sources) {
                final var fileName = source.getFileName()
                        .toString();
                final var separator = fileName.lastIndexOf('.');
                final var name = fileName.substring(0, separator);

                final var input = Files.readString(source);
                final var compiled = Main.compile(input, name);

                output.append("class ")
                        .append(name)
                        .append(Main.SEPARATOR)
                        .append(compiled);
            }

            final var target = Paths.get(".", "diagram.puml");
            final var joined = String.join(Main.SEPARATOR, "@startuml", "skinparam linetype ortho", output, "@enduml");
            Files.writeString(target, joined);
        } catch (final IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static String compile(final CharSequence input, final String source) {
        return Main.divide(input)
                .stream()
                .map(segment -> Main.compileRootSegment(segment, source))
                .flatMap(OptionalLike::stream)
                .collect(Collectors.joining());
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
