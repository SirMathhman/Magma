package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private Main() {
    }

    public static void main(final String[] args) {
        final var rootDirectory = Paths.get(".", "src", "java");
        try (final var stream = Files.walk(rootDirectory)) {
            final var sources = stream.filter(Files::isRegularFile)
                    .filter(file -> file.toString()
                            .endsWith(".java"))
                    .toList();

            Main.runWithSources(rootDirectory, sources);
        } catch (final IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static void runWithSources(final Path rootDirectory, final Iterable<Path> sources) throws IOException {
        for (final var source : sources)
            Main.runWithSource(rootDirectory, source);
    }

    private static void runWithSource(final Path rootDirectory, final Path source) throws IOException {
        final var fileName = source.getFileName()
                .toString();
        final var separator = fileName.lastIndexOf('.');
        final var name = fileName.substring(0, separator);

        final var relativeParent = rootDirectory.relativize(source.getParent());
        final var namespace = Main.computeNamespace(relativeParent);

        final var targetParent = Paths.get(".", "src", "windows")
                .resolve(relativeParent);

        if (!Files.exists(targetParent))
            Files.createDirectories(targetParent);

        final var input = Files.readString(source);
        final var segments = Main.divide(input);

        final var output = new StringBuilder();
        for (final var segment : segments)
            output.append(Main.generatePlaceholder(segment));

        final var targetContent = "#include \"" + name + ".h\"" + System.lineSeparator() + output;
        Files.writeString(targetParent.resolve(name + ".c"), targetContent);

        final var joined = String.join("_", namespace);
        final var withName = joined + "_" + name;
        final var headerContent = String.join(System.lineSeparator(),
                "#ifndef " + withName,
                "#define " + withName,
                "#endif");

        Files.writeString(targetParent.resolve(name + ".h"), headerContent);
    }

    private static List<String> divide(final CharSequence input) {
        final MutableState mutableState = new State();
        final var length = input.length();
        var current = mutableState;
        for (var i = 0; i < length; i++) {
            final var c = input.charAt(i);
            current = Main.fold(current, c);
        }

        return current.advance()
                .unwrap();
    }

    private static MutableState fold(final MutableState mutableState, final char c) {
        final var appended = mutableState.append(c);
        if (';' == c)
            return appended.advance();
        return appended;
    }

    private static String generatePlaceholder(final String input) {
        final var replaced = input.replace("/*", "start")
                .replace("*/", "end");

        return "/*" + replaced + "*/";
    }

    private static List<String> computeNamespace(final Path relativeParent) {
        final List<String> namespace = new ArrayList<>();
        final var nameCount = relativeParent.getNameCount();
        for (var i = 0; i < nameCount; i++)
            namespace.add(relativeParent.getName(i)
                    .toString());
        return namespace;
    }
}
