package magma;

import org.junit.jupiter.api.AfterEach;
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

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class DirectorySourceSetTest {
    public static final Path ROOT = Paths.get(".", "temp");

    @AfterEach
    void tearDown() throws IOException {
        Files.walkFileTree(ROOT, new DeletingVisitor());
    }

    @Test
    void walk() throws IOException {
        Files.createDirectories(ROOT);

        var source = ROOT.resolve("Test.java");
        Files.createFile(source);

        var expected = Collections.singleton(new PathSource(source));
        SourceSet sourceSet = new DirectorySourceSet(ROOT);
        var actual = sourceSet.walk().collect(Collectors.toSet());

        assertIterableEquals(expected, actual);
    }

    private static class DeletingVisitor extends SimpleFileVisitor<Path> {
        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            Files.delete(dir);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            Files.delete(file);
            return FileVisitResult.CONTINUE;
        }
    }
}