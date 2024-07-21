package magma;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DirectoryTargetSetTest {
    public static final Path ROOT = Paths.get(".", "temp");

    @Test
    void writeTarget() throws IOException {
        var targetSet = new DirectoryTargetSet(ROOT);
        var unit = new InlineUnit(List.of("magma"), "Test", "");
        targetSet.writeTarget(unit, "");
        assertTrue(Files.exists(ROOT.resolve("magma", "Test.mgs")));
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.walkFileTree(ROOT, new SimpleFileVisitor<>() {
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
}