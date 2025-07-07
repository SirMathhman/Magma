/* import java.io.IOException; */
/* import java.nio.file.Files; */
/* import java.nio.file.Path; */
/* import java.nio.file.Paths; */
/* import java.util.List; */
/* import java.util.stream.Collectors; */
/* import java.util.stream.Stream; */
/* public class Main {
    private Main() {}

    public static void main(final String[] args) {
        final var rootDirectory = Paths.get(".", "src", "java");
        try (final var stream = Files.walk(rootDirectory)) {
            Main.runWithSources(rootDirectory, stream);
        } catch (final IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static void runWithSources(final Path rootDirectory, final Stream<Path> stream) throws IOException {
        final var sources = stream.filter(Files::isRegularFile)
                                  .filter(path -> path.toString().endsWith(".java"))
                                  .collect(Collectors.toSet());

        for (final var source : sources) Main.runWithSource(rootDirectory, source);
    }

    private static void runWithSource(final Path rootDirectory, final Path source) throws IOException {
        final var relative = rootDirectory.relativize(source.getParent());

        final var targetParent = Paths.get(".", "src", "node").resolve(relative);
        final var fileName = source.getFileName().toString();
        final var separator = fileName.lastIndexOf('.');
        final var name = fileName.substring(0, separator);

        if (!Files.exists(targetParent)) Files.createDirectories(targetParent);

        final var target = targetParent.resolve(name + ".ts");
        final var input = Files.readString(source);
        Files.writeString(target, Main.compile(input));
    }

    private static String compile(final CharSequence input) {
        final var segments = Main.divide(input);

        final var output = new StringBuilder();
        for (final var segment : segments) output.append(Main.compileRootSegment(segment));
        return output.toString();
    }

    private static List<String> divide(final CharSequence input) {
        DivideState current = new MutableDivideState();
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

    private static String compileRootSegment(final String input) {
        final var strip = input.strip();
        if (strip.startsWith("package ")) return "";
        return Main.generatePlaceholder(strip) + System.lineSeparator();
    }

    private static String generatePlaceholder(final String input) {
        final var replaced = input.replace("start", "start").replace("end", "end");
        return "start " + replaced + " end";
    }
} */
