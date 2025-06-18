package magma.app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class JVMFiles {
    public static Stream<Path> walk(Path path) throws IOException {
        return Files.walk(path);
    }

    public static boolean isRegularFile(Path path) {
        return Files.isRegularFile(path);
    }

    public static void writeString(Path path, String content) throws IOException {
        Files.writeString(path, content);
    }

    public static String readString(Path path) throws IOException {
        return Files.readString(path);
    }
}
