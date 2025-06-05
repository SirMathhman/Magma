package magma;

import magma.PathLike;
import java.util.List;
import org.junit.jupiter.api.Assertions;

final class TestUtil {
    private TestUtil() {}

    static PathLike writeSource(PathLike root, String relPath, String content) {
        PathLike file = root.resolve(relPath);
        file.getParent().createDirectories().ifPresent(Assertions::fail);
        file.writeString(content).ifPresent(Assertions::fail);
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
