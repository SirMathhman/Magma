package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class Main {
    private Main() {}

    public static void main(final String[] args) {
        final var rootDirectory = Paths.get(".", "src", "java");
        try (final var stream = Files.walk(rootDirectory)) {
            final var sources = stream.filter(Files::isRegularFile)
                                      .filter(path -> path.toString().endsWith(".java"))
                                      .collect(Collectors.toSet());

            for (final var source : sources) {
                final var relative = rootDirectory.relativize(source.getParent());

                final var targetParent = Paths.get(".", "src", "node").resolve(relative);
                final var fileName = source.getFileName().toString();
                final var separator = fileName.lastIndexOf('.');
                final var name = fileName.substring(0, separator);

                if (!Files.exists(targetParent)) Files.createDirectories(targetParent);

                final var target = targetParent.resolve(name + ".ts");
                Files.writeString(target, "/**/");
            }
        } catch (final IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
}
