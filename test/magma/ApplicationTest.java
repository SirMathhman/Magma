package magma;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ApplicationTest {
    public static final Path SOURCE = resolveByExtension("java");
    public static final Path TARGET = resolveByExtension("mgs");

    private static Path resolveByExtension(String name) {
        return Application.ROOT_DIRECTORY.resolve("Main" + Application.EXTENSION_SEPARATOR + name);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(TARGET);
        Files.deleteIfExists(SOURCE);
    }

    @Test
    void generatesNoTarget() throws IOException {
        new Application(SOURCE).run();
        assertFalse(Files.exists(TARGET));
    }

    @Test
    void generatesTarget() throws IOException {
        Files.createFile(SOURCE);
        new Application(SOURCE).run();
        assertTrue(Files.exists(TARGET));
    }
}
