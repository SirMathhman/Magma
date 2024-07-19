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
    public static final String MAGMA_EXTENSION = "mgs";
    public static final Path TARGET = resolveExtension(MAGMA_EXTENSION);

    private static Path resolveExtension(String extension) {
        return Paths.get(".", resolve("ApplicationTest", extension));
    }

    private static String resolve(String name, String extension) {
        return name + "." + extension;
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
            var fileName = source.getFileName().toString();
            var separator = fileName.lastIndexOf('.');
            var name = fileName.substring(0, separator);
            Files.createFile(source.resolveSibling(resolve(name, MAGMA_EXTENSION)));
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
