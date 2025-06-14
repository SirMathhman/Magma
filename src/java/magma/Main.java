package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        try {
            final var target = Paths.get(".", "diagram.puml");
            Files.createFile(target);
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
}