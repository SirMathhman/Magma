package magma;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class ApplicationTest {
    public static final String MAGMA_EXTENSION = "mgs";
    public static final Path SOURCE = resolveExtension("java");
    public static final Path TARGET = resolveExtension(MAGMA_EXTENSION);

    private static Path resolveExtension(String extension) {
        return Paths.get(Compiler.IMPORT_SEPARATOR, resolve("ApplicationTest", extension));
    }

    private static String resolve(String name, String extension) {
        return name + Compiler.IMPORT_SEPARATOR + extension;
    }

    private static boolean doesTargetExist() {
        return Files.exists(TARGET);
    }

    private static void runOrFail() {
        try {
            run(new SingletonSourceSet(SOURCE));
        } catch (CompileException e) {
            fail(e);
        }
    }

    private static void run(SourceSet sourceSet) throws CompileException {
        var stream = sourceSet.stream();
        for (var source : stream.toList()) {
            var name = source.computeName();
            var current = Paths.get(Compiler.IMPORT_SEPARATOR);

            var namespace = source.computeNamespace();
            for (var segment : namespace.toList()) {
                current = current.resolve(segment);
            }

            var input = readSafe(source);
            var output = Compiler.compile(input);

            var target = current.resolve(resolve(name, MAGMA_EXTENSION));
            writeSafe(target, output);
        }
    }

    private static void writeSafe(Path target, String output) throws CompileException {
        try {
            Files.writeString(target, output);
        } catch (IOException e) {
            throw new CompileException(e);
        }
    }

    private static String readSafe(Source source) throws CompileException {
        try {
            return source.read();
        } catch (IOException e) {
            throw new CompileException(e);
        }
    }

    private static void runOrFail(String input) {
        try {
            writeSafe(SOURCE, input);
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
