package magma;

import static org.junit.jupiter.api.Assertions.*;

import magma.path.NioPath;
import magma.path.PathLike;
import java.io.File;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

class PathLikeTest {
    @Test
    void resolvesAndRelativizesPaths() {
        PathLike base = NioPath.of("a");
        var child = base.resolve("b");
        assertEquals("a" + File.separator + "b", child.toString());
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
            paths.get(i).deleteIfExists();
        }
    }

    @Test
    void readsAndWritesFilesWithoutExceptions() throws IOException {
        Path root = Files.createTempDirectory("rw");
        Path file = root.resolve("test.txt");
        PathLike path = NioPath.wrap(file);

        var err = path.getParent().createDirectories();
        assertFalse(err.isSome());

        err = path.writeString("hi");
        assertFalse(err.isSome());

        var text = path.readString();
        assertTrue(text.isOk());
        assertEquals("hi", text.value().get());

        path.deleteIfExists();
        NioPath.wrap(root).deleteIfExists();
    }
}
