package magma;

import java.io.IOException;
import java.nio.file.Files;
import magma.PathLike;
import java.util.List;

final class TestUtil {
    private TestUtil() {}

    static PathLike writeSource(PathLike root, String relPath, String content) {
        PathLike file = root.resolve(relPath);
        try {
            Files.createDirectories(file.getParent().unwrap());
            Files.writeString(file.unwrap(), content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
