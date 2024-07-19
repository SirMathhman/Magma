package magma;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.stream.Collectors;

class DirectorySourceSetTest {

    private Path root;
    private Path parent;
    private Path child;

    @BeforeEach
    void setUp() throws IOException {
        root = Paths.get(".", "temp");
        parent = root.resolve("parent");
        if (!Files.exists(parent)) Files.createDirectories(parent);

        child = parent.resolve("Child.java");
        if (!Files.exists(child)) Files.createFile(child);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.walkFileTree(root, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    @Test
    void stream() throws IOException {
        var expected = Collections.singleton(new PathUnit(root, child));
        var actual = new DirectorySourceSet(root)
                .stream()
                .collect(Collectors.toSet());

        Assertions.assertIterableEquals(expected, actual);
    }
}