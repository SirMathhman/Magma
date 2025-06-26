package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    private Main() {
    }

    public static void main(final String[] args) {
        try {
            final var targetParent = Paths.get(".", "src", "node", "magma");
            if (!Files.exists(targetParent))
                Files.createDirectories(targetParent);

            final var target = targetParent.resolve("Main.ts");
            Files.writeString(target, "");
        } catch (final IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
}
