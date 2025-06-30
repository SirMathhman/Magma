package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    private Main() {}

    public static void main(final String[] args) {
        try {
            final var target = Paths.get(".", "diagram.puml");
            Files.writeString(target, "");
        } catch (final IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
}
