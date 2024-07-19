package magma;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static magma.PathTargetSet.MAGMA_EXTENSION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class ApplicationTest {
    public static final Path SOURCE = resolveExtension("java");
    public static final Path TARGET = resolveExtension(MAGMA_EXTENSION);

    private static Path resolveExtension(String extension) {
        return Paths.get(Compiler.IMPORT_SEPARATOR, PathTargetSet.resolve("ApplicationTest", extension));
    }

    private static boolean doesTargetExist() {
        return Files.exists(TARGET);
    }

    private static void runOrFail() {
        try {
            new Application(new SingletonSourceSet(SOURCE), new PathTargetSet()).run();
        } catch (CompileException e) {
            fail(e);
        }
    }

    private static void runOrFail(String input) {
        try {
            PathTargetSet.writeSafe(SOURCE, input);
            runOrFail();
        } catch (CompileException e) {
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

    private static String renderImport(String parent, String child) {
        return Compiler.IMPORT_KEYWORD_WITH_SPACE + parent + Compiler.IMPORT_SEPARATOR + child + Compiler.STATEMENT_END;
    }

    @ParameterizedTest
    @ValueSource(strings = {"first", "second"})
    void packageStatement(String name) {
        assertRun(Compiler.PACKAGE_KEYWORD_WITH_SPACE + name + Compiler.STATEMENT_END, "");
    }

    @ParameterizedTest
    @ValueSource(strings = {"First", "Second"})
    void importChild(String child) {
        var value = renderImport("parent", child);
        assertRun(value, value);
    }

    @Test
    void importParent() {
        var value = renderImport("test", "Child");
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
        assertFalse(doesTargetExist());
    }

    @Test
    void generatesTarget() {
        runOrFail("");
        assertTrue(doesTargetExist());
    }
}
