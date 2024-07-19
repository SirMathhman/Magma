package magma;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class ApplicationTest {
    public static final Path SOURCE = resolveExtension("java");
    public static final Path TARGET = resolveExtension("mgs");

    private static Path resolveExtension(String extension) {
        return Paths.get(".", "ApplicationTest." + extension);
    }

    private static boolean doesTargetExist() {
        return Files.exists(TARGET);
    }

    private static void runOrFail() {
        try {
            run();
        } catch (IOException e) {
            fail(e);
        }
    }

    private static void run() throws IOException {
        if (Files.exists(SOURCE)) {
            Files.createFile(TARGET);
        }
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(TARGET);
        Files.deleteIfExists(SOURCE);
    }

    @Test
    void generatesNoTarget() {
        runOrFail();
        assertFalse(doesTargetExist());
    }

    @Test
    void generatesTarget() throws IOException {
        Files.createFile(SOURCE);
        runOrFail();
        assertTrue(doesTargetExist());
    }
}
