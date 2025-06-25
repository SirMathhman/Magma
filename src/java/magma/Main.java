package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

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
                final var namespace = new ArrayList<String>();
                for (var i = 0; i < relativeParent.getNameCount(); i++)
                    namespace.add(relativeParent.getName(i)
                            .toString());

                final var targetParent = Paths.get(".", "src", "windows")
                        .resolve(relativeParent);

                if (!Files.exists(targetParent))
                    Files.createDirectories(targetParent);

                final var targetContent = "#include \"" + name + ".h\"" + System.lineSeparator();
                Files.writeString(targetParent.resolve(name + ".c"), targetContent);

                final var joined = String.join("_", namespace);
                final var withName = joined + "_" + name;
                final var headerContent = String.join(System.lineSeparator(),
                        "#ifndef " + withName,
                        "#define " + withName,
                        "#endif");

                Files.writeString(targetParent.resolve(name + ".h"), headerContent);
            }
        } catch (final IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
}
