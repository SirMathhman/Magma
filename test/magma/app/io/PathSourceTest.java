package magma.app.io;

import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PathSourceTest {

    @Test
    void streamNamespace() {
        var root = Paths.get(".");
        var example = root.resolve("first")
                .resolve("second")
                .resolve("Third.java");

        var expected = List.of("first", "second");
        var actual = new PathSource(root, example)
                .streamNamespace()
                .toList();

        assertIterableEquals(expected, actual);
    }
}