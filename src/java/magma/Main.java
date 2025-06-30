package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private Main() {}

    public static void main(final String[] args) {
        try {
            final var target = Paths.get(".", "diagram.puml");
            final var output = String.join(Main.LINE_SEPARATOR, "@startuml", "class Main", "@enduml");
            Files.writeString(target, output);
        } catch (final IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
}
