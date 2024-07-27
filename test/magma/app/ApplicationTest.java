package magma.app;

import magma.app.io.DirectoryTargetSet;
import magma.app.io.PathSource;
import magma.app.io.SingleSourceSet;
import magma.app.io.TargetSet;
import magma.app.compile.ApplicationException;
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
    public static final String NAME = "ApplicationTest";
    public static final Path SOURCE = resolveWithExtension("java");
    public static final SingleSourceSet DEFAULT_SOURCE_SET = new SingleSourceSet(SOURCE);
    public static final Path TARGET = resolveWithExtension(DirectoryTargetSet.MAGMA_EXTENSION);
    public static final TargetSet DEFAULT_TARGET_SET = new DirectoryTargetSet(Paths.get("."));

    private static Path resolveWithExtension(String extension) {
        return Paths.get(".", ApplicationTest.NAME + PathSource.EXTENSION_SEPARATOR + extension);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(TARGET);
        Files.deleteIfExists(SOURCE);
    }

    @Test
    void generatesNoTarget() {
        runWithDefault();
        assertFalse(Files.exists(TARGET));
    }

    private static void runWithDefault() {
        try {
            new Application(DEFAULT_SOURCE_SET, DEFAULT_TARGET_SET).run();
        } catch (ApplicationException e) {
            fail(e);
        }
    }

    @Test
    void generatesTarget() throws IOException {
        Files.createFile(SOURCE);
        runWithDefault();
        assertTrue(Files.exists(TARGET));
    }
}
