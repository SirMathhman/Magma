package magma.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class ApplicationTest {
    public static final Path SOURCE = resolveByExtension("java");
    public static final Path TARGET = resolveByExtension("mgs");

    private static Path resolveByExtension(String name) {
        return Application.ROOT_DIRECTORY.resolve("Main" + Application.EXTENSION_SEPARATOR + name);
    }

    private static void runWithSource() {
        try {
            runWithSourceExceptionally();
        } catch (ApplicationException e) {
            fail(e);
        }
    }

    private static void runWithSourceExceptionally() throws ApplicationException {
        new Application(new SingletonSourceSet(SOURCE)).run();
    }

    @Test
    void invalidate() throws IOException {
        Files.writeString(SOURCE, "test");
        assertThrows(ApplicationException.class, ApplicationTest::runWithSourceExceptionally);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(TARGET);
        Files.deleteIfExists(SOURCE);
    }

    @Test
    void generatesNoTarget() {
        runWithSource();
        assertFalse(Files.exists(TARGET));
    }

    @Test
    void generatesTarget() throws IOException {
        Files.writeString(SOURCE, "");
        runWithSource();
        assertTrue(Files.exists(TARGET));
    }
}
