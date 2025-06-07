package magma;

import static org.junit.jupiter.api.Assertions.assertEquals;

import magma.path.NioPath;
import magma.path.PathLike;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PathLikeTest {
    @Test
    void resolvesAndRelativizesPaths() {
        PathLike base = NioPath.of("a");
        var child = base.resolve("b");
        assertEquals("a/b", child.toString());
        var rel = base.relativize(child);
        assertEquals("b", rel.toString());
        assertEquals("a", child.getParent().toString());
    }

    @Test
    void walksDirectories() throws IOException {
        Path root = Files.createTempDirectory("walks");
        Path sub = root.resolve("sub");
        Files.createDirectories(sub);
        Path file = sub.resolve("A.java");
        Files.writeString(file, "class A {}");

        PathLike start = NioPath.wrap(root);
        var result = start.walk();
        assertTrue(result.isOk());
        List<PathLike> paths = new ArrayList<>(result.value().get());
        assertTrue(paths.stream().anyMatch(p -> p.toString().endsWith("A.java")));

        for (var i = paths.size() - 1; i >= 0; i--) {
            ((NioPath) paths.get(i)).deleteIfExists();
        }
    }
}
