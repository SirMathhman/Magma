package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Main {
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private Main() {}

    public static void main(final String[] args) {
        final var sourceDirectory = Paths.get(".", "src", "java");
        try (final var stream = Files.walk(sourceDirectory)) {
            final var sources = stream.filter(Files::isRegularFile)
                                      .filter(path -> path.toString().endsWith(".java"))
                                      .collect(Collectors.toSet());

            Main.runWithSources(sourceDirectory, sources);
        } catch (final IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static void runWithSources(final Path sourceDirectory, final Iterable<Path> sources) throws IOException {
        for (final var source : sources) Main.runWithSource(sourceDirectory, source);
    }

    private static void runWithSource(final Path sourceDirectory, final Path source) throws IOException {
        final var fileName = source.getFileName().toString();
        final var separator = fileName.lastIndexOf('.');
        final var name = fileName.substring(0, separator);

        final var relativeParent = sourceDirectory.relativize(source.getParent());
        final var targetDirectory = Paths.get(".", "src", "node");
        final var targetParent = targetDirectory.resolve(relativeParent);
        if (!Files.exists(targetParent)) Files.createDirectories(targetParent);

        final var target = targetParent.resolve(name + ".ts");
        final var input = Files.readString(source);

        final var segments = Main.divide(input);

        final var output = new StringBuilder();
        for (final var segment : segments) output.append(Main.compileRootSegment(segment));

        Files.writeString(target, output);
    }

    private static String compileRootSegment(final String input) {
        final var strip = input.strip();
        if (strip.startsWith("package ")) return "";
        return Main.compileClass(input).orElseGet(() -> Main.generatePlaceholder(input.strip()) + Main.LINE_SEPARATOR);
    }

    private static Optional<String> compileClass(final String input) {
        final var strip = input.strip();
        if (strip.isEmpty() || '}' != strip.charAt(strip.length() - 1)) return Optional.empty();
        final var withoutEnd = strip.substring(0, strip.length() - "}".length());

        final var i = withoutEnd.indexOf('{');
        if (0 > i) return Optional.empty();
        final var substring = withoutEnd.substring(0, i);
        final var substring1 = withoutEnd.substring(i + "{".length());

        return Optional.of(Main.generatePlaceholder(substring) + "{" + Main.generatePlaceholder(substring1) + "}" +
                           Main.LINE_SEPARATOR);

    }

    private static List<String> divide(final CharSequence input) {
        DivideState current = new MutableDivideState(input);
        while (true) {
            final var maybePopped = current.pop();
            if(maybePopped.isEmpty()) break;

            final var popped = maybePopped.get();
            current = Main.fold(popped.left(), popped.right());
        }

        return current.advance().stream().toList();
    }

    private static DivideState fold(final DivideState state, final char c) {
        final var appended = state.append(c);
        if (';' == c && appended.isLevel()) return appended.advance();
        if ('{' == c) return appended.enter();
        if ('}' == c) return appended.exit();
        return appended;
    }

    private static String generatePlaceholder(final String input) {
        return "/*" + input.replace("/*", "start").replace("*/", "end") + "*/";
    }
}
