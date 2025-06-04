package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

final class TestUtil {
    private TestUtil() {}

    static Path writeSource(Path root, String relPath, String content) throws IOException {
        Path file = root.resolve(relPath);
        Files.createDirectories(file.getParent());
        Files.writeString(file, content);
        return file;
    }

    static Sources sampleSources() {
        String result = "public interface Result {}";
        String ok = "public class Ok implements Result {}";
        String err = "public class Err implements Result {}";
        String gen = "public class GenerateDiagram { Ok ok; Err err; }";
        return new Sources(List.of(result, ok, err, gen));
    }
}
