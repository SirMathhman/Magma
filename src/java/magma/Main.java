package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Main {
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
        final var relativeParent = sourceDirectory.relativize(source.getParent());
        final var fileName = source.getFileName().toString();
        final var separator = fileName.lastIndexOf('.');
        final var name = fileName.substring(0, separator);
        final var targetParent = Paths.get(".", "src", "node").resolve(relativeParent);
        if (!Files.exists(targetParent)) Files.createDirectories(targetParent);

        final var target = targetParent.resolve(name + ".ts");
        final var input = Files.readString(source);
        final var segments = Main.divide(input);
        final var output = new StringBuilder();
        for (final var segment : segments) output.append(Main.compileRootSegment(segment));
        final var csq = output.toString();
        Files.writeString(target, csq);
    }

    private static String compileRootSegment(final String input) {
        final var strip = input.strip();
        if (strip.startsWith("package ") || strip.startsWith("import ")) return "";
        return Main.compileClass(strip).orElseGet(() -> Main.generatePlaceholder(strip));
    }

    private static Optional<String> compileClass(final String strip) {
        return Main.compileSuffix(strip, "}");
    }

    private static Optional<String> compileSuffix(final String input, final String suffix) {
        if (!input.endsWith(suffix)) return Optional.empty();

        final var withoutEnd = input.substring(0, input.length() - suffix.length());
        return Main.compileInfix(withoutEnd, "{");
    }

    private static Optional<String> compileInfix(final String withoutEnd, final String infix) {
        final var contentStart = withoutEnd.indexOf(infix);
        if (0 > contentStart) return Optional.empty();

        final var beforeContent = withoutEnd.substring(0, contentStart);
        final var content = withoutEnd.substring(contentStart + infix.length());
        return Optional.of(Main.generatePlaceholder(beforeContent) + "{" + Main.generatePlaceholder(content) + "}");
    }

    private static List<String> divide(final CharSequence input) {
        var current = (DivideState) new MutableDivideState();
        for (var i = 0; i < input.length(); i++) {
            final var c = input.charAt(i);
            current = Main.fold(current, c);
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
        final var replaced = input.replace("/*", "start").replace("*/", "end");
        return "/*" + replaced + "*/";
    }
}
