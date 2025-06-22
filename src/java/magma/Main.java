package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    private Main() {
    }

    public static void main(final String[] args) {
        try {
            Files.writeString(Paths.get(".", "diagram.puml"),
                    String.join(System.lineSeparator(), "@startuml", "class Main", "@enduml"));
        } catch (final IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
}
