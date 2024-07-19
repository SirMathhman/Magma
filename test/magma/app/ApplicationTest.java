package magma.app;

import magma.app.compile.Compiler;
import magma.app.io.PathTargetSet;
import magma.app.io.SingletonSourceSet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static magma.app.io.PathTargetSet.MAGMA_EXTENSION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class ApplicationTest {
    public static final Path SOURCE = resolveExtension("java");
    public static final Path TARGET = resolveExtension(MAGMA_EXTENSION);
    public static final Path TARGET_ROOT = Paths.get(".");

    private static Path resolveExtension(String extension) {
        return Paths.get(Compiler.IMPORT_SEPARATOR, PathTargetSet.resolve("ApplicationTest", extension));
    }

    private static void runOrFail() {
        try {
            new Application(new SingletonSourceSet(SOURCE), new PathTargetSet(TARGET_ROOT)).run();
        } catch (ApplicationException e) {
            fail(e);
        }
    }

    private static void runOrFail(String input) {
        try {
            PathTargetSet.writeSafe(SOURCE, input);
            runOrFail();
        } catch (ApplicationException e) {
            fail(e);
        }
    }

    private static void assertRun(String input, String output) {
        try {
            runOrFail(input);
            assertEquals(output, Files.readString(TARGET));
        } catch (IOException e) {
            fail(e);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"first", "second"})
    void packageStatement(String name) {
        assertRun(Compiler.renderPackage(name), "");
    }

    @ParameterizedTest
    @ValueSource(strings = {"First", "Second"})
    void importChild(String child) {
        var value = Compiler.renderImport("", "parent", child);
        assertRun(value, value);
    }

    @Test
    void importParent() {
        var value = Compiler.renderImport("", "test", "Child");
        assertRun(value, value);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(TARGET);
        Files.deleteIfExists(SOURCE);
    }

    @Test
    void generatesNoTarget() {
        runOrFail();
        assertFalse(Files.exists(TARGET));
    }

    @Test
    void generatesTarget() {
        runOrFail("");
        assertTrue(Files.exists(TARGET));
    }
}
