package magma;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main() throws Exception {
        Path source = Paths.get("src/magma/Main.java");
        Path target = Paths.get("src/magma/Main.mgs");
        byte[] content = Files.readAllBytes(source);
        Files.write(target, content);
    }
}
