package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

public class Main {
    private static final String SEPARATOR = System.lineSeparator();

    private Main() {
    }

    public static void main(final String[] args) {
        try {
            final var input = Files.readString(Paths.get(".", "src", "java", "magma", "Main.java"));
            final var output = Main.compile(input);

            Files.writeString(Paths.get(".", "diagram.puml"),
                    String.join(Main.SEPARATOR,
                            "@startuml",
                            "skinparam linetype ortho",
                            "class Main",
                            output.toString(),
                            "@enduml"));
        } catch (final IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static StringBuilder compile(final String input) {
        final var segments = input.split(";");

        final var output = new StringBuilder();
        for (final var segment : segments)
            Main.compileRootSegment(segment)
                    .ifPresent(output::append);

        return output;
    }

    private static Optional<String> compileRootSegment(final String input) {
        final var strip = input.strip();
        if (strip.startsWith("import ")) {
            final var withoutPrefix = strip.substring("import ".length());
            final var separator = withoutPrefix.lastIndexOf('.');
            if (0 <= separator) {
                final var child = withoutPrefix.substring(separator + ".".length());
                return Optional.of("Main --> " + child + Main.SEPARATOR);
            }
        }

        return Optional.empty();
    }
}
