package magma;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ApplicationTest {
    public static final String MAGMA_EXTENSION = "mgs";
    public static final String JAVA_EXTENSION = "java";
    public static final String THIS_NAME = "ApplicationTest";
    public static final Path TARGET = resolve(THIS_NAME, MAGMA_EXTENSION);
    public static final Path SOURCE = resolve(THIS_NAME, JAVA_EXTENSION);

    private static void run() throws IOException {
        if (!Files.exists(SOURCE)) return;
        var fileName = SOURCE.getFileName().toString();
        var separator = fileName.indexOf('.');
        var name = fileName.substring(0, separator);
        Files.createFile(resolve(name, MAGMA_EXTENSION));
    }

    private static Path resolve(String name, String extension) {
        return Paths.get(".", name + "." + extension);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(TARGET);
        Files.deleteIfExists(SOURCE);
    }

    @Test
    void generatesNoTarget() throws IOException {
        run();
        assertFalse(Files.exists(TARGET));
    }

    @Test
    void generateTarget() throws IOException {
        Files.createFile(SOURCE);
        run();
        assertTrue(Files.exists(TARGET));
    }
}
