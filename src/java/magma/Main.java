package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    private Main() {
    }

    public static void main(final String[] args) {
        try {
            final var target = Paths.get(".", "diagram.puml");
            final var separator = System.lineSeparator();
            final var joined = String.join(separator, "@startuml", "class Main", "@enduml");
            Files.writeString(target, joined);
        } catch (final IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
}
