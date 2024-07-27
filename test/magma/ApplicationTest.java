package magma;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ApplicationTest {
    public static final String NAME = "ApplicationTest";
    public static final String MAGMA_EXTENSION = "mgs";
    public static final String EXTENSION_SEPARATOR = ".";
    public static final Path SOURCE = resolveWithExtension(NAME, "java");
    public static final Path TARGET = resolveWithExtension(NAME, MAGMA_EXTENSION);

    private static Path resolveWithExtension(String name, String extension) {
        return Paths.get(".", name + EXTENSION_SEPARATOR + extension);
    }

    private static void run(SourceSet sourceSet) throws IOException {
        var set = sourceSet.walk().collect(Collectors.toSet());

        for (var path : set) {
            runWithSource(path);
        }
    }

    private static void runWithSource(Path source) throws IOException {
        var fileName = source.getFileName().toString();
        var separator = fileName.indexOf(EXTENSION_SEPARATOR);
        var name = fileName.substring(0, separator);
        Files.createFile(resolveWithExtension(name, MAGMA_EXTENSION));
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(TARGET);
        Files.deleteIfExists(SOURCE);
    }

    @Test
    void generatesNoTarget() throws IOException {
        run(new SingleSourceSet(SOURCE));
        assertFalse(Files.exists(TARGET));
    }

    @Test
    void generatesTarget() throws IOException {
        Files.createFile(SOURCE);
        run(new SingleSourceSet(SOURCE));
        assertTrue(Files.exists(TARGET));
    }
}
