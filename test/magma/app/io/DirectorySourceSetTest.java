package magma.app.io;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class DirectorySourceSetTest {
    private final Path root = Paths.get(".", "temp");
    private final Path child = root.resolve("Test.java");

    @BeforeEach
    void setUp() throws IOException {
        if (!Files.exists(root)) Files.createDirectories(root);
        if (!Files.exists(child)) Files.createFile(child);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(child);
        Files.deleteIfExists(root);
    }

    @Test
    void test() throws IOException {
        var expected = Collections.singleton(new PathUnit(root, child));
        var actual = new DirectorySourceSet(root).streamPaths()
                .collect(Collectors.toSet());

        assertIterableEquals(expected, actual);
    }
}