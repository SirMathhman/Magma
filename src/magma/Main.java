package magma;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main() throws Exception {
        Files.createFile(Paths.get("src/magma/Main.mgs"));
    }
}
