package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    private Main() {
    }

    public static void main(final String[] args) {
        try {
            Files.writeString(Paths.get(".", "src", "java", "magma", "Main.ts"), "/**/");
        } catch (final IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
}
