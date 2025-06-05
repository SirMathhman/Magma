package magma;

import java.io.IOException;
import magma.PathLike;
import java.util.List;

final class TestUtil {
    private TestUtil() {}

    static PathLike writeSource(PathLike root, String relPath, String content) {
        PathLike file = root.resolve(relPath);
        try {
            var dirResult = file.getParent().createDirectories();
            var dirTuple = dirResult.toTuple(null);
            if (dirTuple.left()) {
                throw dirTuple.right();
            }
            var writeResult = file.writeString(content);
            var writeTuple = writeResult.toTuple(null);
            if (writeTuple.left()) {
                throw writeTuple.right();
            }
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
