/*package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    private Main() {
    }

    public static void main(final String[] args) {
        try {
            final var source = Paths.get(".", "src", "java", "magma", "Main.java");
            final var input = Files.readString(source);

            final var target = source.resolveSibling("Main.ts");
            final var replaced = input.replace("start", "start")
                    .replace("end", "end");
            
            Files.writeString(target, "start" + replaced + "end");
        } catch (final IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
}
*/