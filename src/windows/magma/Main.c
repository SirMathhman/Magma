/*package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class Main {
    private Main() {}

    public static void main(final String[] args) {
        final var sourceDirectory = Paths.get(".", "src", "java");
        try (final var stream = Files.walk(sourceDirectory)) {
            final var sources = stream.filter(Files::isRegularFile)
                                      .filter(path -> path.toString().endsWith(".java"))
                                      .collect(Collectors.toSet());

            Main.runWithSources(sources, sourceDirectory);
        } catch (final IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static void runWithSources(final Iterable<Path> sources, final Path sourceDirectory) throws IOException {
        for (final var source : sources) Main.runWithSource(source, sourceDirectory);
    }

    private static void runWithSource(final Path source, final Path sourceDirectory) throws IOException {
        final var parent = source.getParent();
        final var relativeParent = sourceDirectory.relativize(parent);

        final var targetDirectory = Paths.get(".", "src", "windows");
        final var targetParent = targetDirectory.resolve(relativeParent);
        if (!Files.exists(targetParent)) Files.createDirectories(targetParent);

        final var fileName = source.getFileName().toString();
        final var separator = fileName.lastIndexOf('.');
        final var name = fileName.substring(0, separator);

        final var target = targetParent.resolve(name + ".c");
        final var replaced = Files.readString(source).replace("start", "start").replace("end", "end");

        Files.writeString(target, "start" + replaced + "start");
    }
}
/*