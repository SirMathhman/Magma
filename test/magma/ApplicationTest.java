package magma;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class ApplicationTest {
    public static final String MAGMA_EXTENSION = "mgs";
    public static final String IMPORT_KEYWORD_WITH_SPACE = "import ";
    public static final String IMPORT_SEPARATOR = ".";
    public static final Path SOURCE = resolveExtension("java");
    public static final Path TARGET = resolveExtension(MAGMA_EXTENSION);
    public static final String STATEMENT_END = ";";
    public static final String PACKAGE_KEYWORD_WITH_SPACE = "package ";

    private static Path resolveExtension(String extension) {
        return Paths.get(IMPORT_SEPARATOR, resolve("ApplicationTest", extension));
    }

    private static String resolve(String name, String extension) {
        return name + IMPORT_SEPARATOR + extension;
    }

    private static boolean doesTargetExist() {
        return Files.exists(TARGET);
    }

    private static void runOrFail() {
        try {
            run(new SingletonSourceSet(SOURCE));
        } catch (IOException e) {
            fail(e);
        }
    }

    private static void run(SourceSet sourceSet) throws IOException {
        var stream = sourceSet.stream();
        for (var source : stream.toList()) {
            var name = source.computeName();
            var current = Paths.get(IMPORT_SEPARATOR);

            var namespace = source.computeNamespace();
            for (var segment : namespace.toList()) {
                current = current.resolve(segment);
            }

            var input = source.read();
            var output = compileImport(input).orElse("");

            Files.writeString(current.resolve(resolve(name, MAGMA_EXTENSION)), output);
        }
    }

    private static Optional<String> compileImport(String input) {
        if (!input.startsWith(IMPORT_KEYWORD_WITH_SPACE)) return Optional.empty();
        var afterKeyword = input.substring(IMPORT_KEYWORD_WITH_SPACE.length());

        if (!afterKeyword.endsWith(STATEMENT_END)) return Optional.empty();
        return Optional.of(input);
    }

    private static void runOrFail(String input) throws IOException {
        Files.writeString(SOURCE, input);
        runOrFail();
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
        return IMPORT_KEYWORD_WITH_SPACE + parent + IMPORT_SEPARATOR + child + STATEMENT_END;
    }

    @ParameterizedTest
    @ValueSource(strings = {"first", "second"})
    void packageStatement(String name) {
        assertRun(PACKAGE_KEYWORD_WITH_SPACE + name + STATEMENT_END, "");
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
    void generatesTarget() throws IOException {
        runOrFail("");
        assertTrue(doesTargetExist());
    }
}
