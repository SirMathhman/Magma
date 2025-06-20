/*package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class Main {
    private Main() {
    }

    public static void main(final String[] args) {
        try {
            final var source = Paths.get(".", "src", "java", "magma", "Main.java");
            final var input = Files.readString(source);

            final var targetParent = Paths.get(".", "src", "node", "magma");
            if (!Files.exists(targetParent))
                Files.createDirectories(targetParent);

            final var target = targetParent.resolve("Main.ts");
            final var replaced = input.replace("start", "start")
                    .replace("start", "end");

            Files.writeString(target, "start" + replaced + "*/");
        } catch (final IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
}
*/