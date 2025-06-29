package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private Main() {
    }

    public static void main(final String[] args) {
        try {
            final var source = Paths.get(".", "src", "java", "magma", "Main.java");
            final var input = Files.readString(source);

            final var targetParent = Paths.get(".", "src", "node", "magma");
            Main.ensureDirectories(targetParent);

            final var target = targetParent.resolve("Main.ts");
            final var output = Main.compile(input);
            Files.writeString(target, output);
        } catch (final IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static void ensureDirectories(final Path targetParent) throws IOException {
        if (!Files.exists(targetParent)) Files.createDirectories(targetParent);
    }

    private static String compile(final CharSequence input) {
        final Collector<CharSequence, ?, String> collector = Collectors.joining();
        return Main.divide(input)
                   .map(Main::compileRootSegment)
                   .map(result -> result + Main.LINE_SEPARATOR)
                   .collect(collector);
    }

    private static String compileRootSegment(final String input) {
        return Main.compileStructure(input).orElseGet(() -> {
            final var stripped = input.strip();
            return Main.generatePlaceholder(stripped);
        });
    }

    private static Optional<String> compileStructure(final String input) {
        final var strip = input.strip();
        final var inputLength = strip.length();
        if (strip.isEmpty() || '}' != strip.charAt(inputLength - 1)) return Optional.empty();
        final var suffixLength = "}".length();
        final var withoutEnd = strip.substring(0, inputLength - suffixLength);

        final var contentStart = withoutEnd.indexOf('{');
        if (0 > contentStart) return Optional.empty();
        final var beforeContent = withoutEnd.substring(0, contentStart);

        final var infixLength = "{".length();
        final var content = withoutEnd.substring(contentStart + infixLength);

        final var genBeforeContent = Main.generatePlaceholder(beforeContent);
        final var genContent = Main.generatePlaceholder(content);
        return Optional.of(genBeforeContent + "{" + genContent + "}");
    }

    private static Stream<String> divide(final CharSequence input) {
        final DivideState initial = ;
        var current = initial;
        while (true) {
            final var maybePopped = initial.pop();
            if (maybePopped.isEmpty()) break;

            final var popped = maybePopped.get();
            current = popped.left();

            final var c = popped.right();
            current = Main.fold(current, c);
        }

        return current.advance().stream();
    }

    private static DivideState fold(final DivideState state, final char c) {
        final var appended = state.append(c);
        if (';' == c && appended.isLevel()) return appended.advance();
        if ('{' == c) return appended.enter();
        if ('}' == c) return appended.exit();
        else return appended;
    }

    private static String generatePlaceholder(final String input) {
        final var replaced = input.replace("/*", "start").replace("*/", "end");
        return "/*" + replaced + "*/";
    }
}
