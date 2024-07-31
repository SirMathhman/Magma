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
    public static final String EXTENSION_SEPARATOR = ".";
    public static final Path ROOT_DIRECTORY = Paths.get(".");
    public static final Path SOURCE = resolveByExtension("java");
    public static final Path TARGET = resolveByExtension("mgs");

    private static Path resolveByExtension(String name) {
        return ROOT_DIRECTORY.resolve("Main" + EXTENSION_SEPARATOR + name);
    }

    private static void run() throws IOException {
        if (!Files.exists(SOURCE)) return;

        var fileName = SOURCE.getFileName().toString();
        var separator = fileName.lastIndexOf('.');
        var name = fileName.substring(0, separator);
        Files.createFile(ROOT_DIRECTORY.resolve(name + EXTENSION_SEPARATOR + "mgs"));
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
    void generatesTarget() throws IOException {
        Files.createFile(SOURCE);
        run();
        assertTrue(Files.exists(TARGET));
    }
}
