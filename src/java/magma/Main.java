package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(final String[] args) {
        final var rootDirectory = Paths.get(".", "src", "java");
        try (final var stream = Files.walk(rootDirectory)) {
            final var sources = stream.filter(Files::isRegularFile)
                    .filter(file -> file.toString()
                            .endsWith(".java"))
                    .toList();

            for (final var source : sources) {
                final var fileName = source.getFileName()
                        .toString();
                final var separator = fileName.lastIndexOf('.');
                final var name = fileName.substring(0, separator);

                final var relativeParent = rootDirectory.relativize(source.getParent());
                final var targetParent = Paths.get(".", "src", "windows")
                        .resolve(relativeParent);

                if (!Files.exists(targetParent))
                    Files.createDirectories(targetParent);

                Files.writeString(targetParent.resolve(name + ".c"), "");
                Files.writeString(targetParent.resolve(name + ".h"), "");
            }
        } catch (final IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
}
