/*package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        try {
            final var source = Paths.get(".", "src", "magma", "Main.java");
            final var input = Files.readString(source);
            final var target = source.resolveSibling("Main.c");
            Files.writeString(target, generatePlaceholder(input));
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static String generatePlaceholder(String input) {
        return "start" + input
                .replace("start", "start")
                .replace("end", "end") + "end";
    }
}
*/