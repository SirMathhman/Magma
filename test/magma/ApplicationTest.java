package magma;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class ApplicationTest {
    public static final String JAVA_EXTENSION = "java";
    public static final String THIS_NAME = "ApplicationTest";
    public static final Path ROOT = Paths.get(".");
    public static final Path TARGET = resolve(Application.MAGMA_EXTENSION);
    public static final Path SOURCE = resolve(JAVA_EXTENSION);
    private SourceSet sourceSet;
    private TargetSet targetSet;

    private static Path resolve(String extension) {
        return ROOT.resolve(ApplicationTest.THIS_NAME + "." + extension);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(TARGET);
        Files.deleteIfExists(SOURCE);
    }

    @BeforeEach
    void setUp() {
        sourceSet = new SingleSourceSet(SOURCE);
        targetSet = new DirectoryTargetSet(ROOT);
    }

    @Test
    void generatesNoTarget() {
        runOrFail();
        assertFalse(Files.exists(TARGET));
    }

    private void runOrFail() {
        try {
            runWithSets();
        } catch (IOException | ApplicationException e) {
            fail(e);
        }
    }

    private void runWithSets() throws IOException, ApplicationException {
        new Application(sourceSet, targetSet).run();
    }

    @Test
    void throwInvalid() throws IOException {
        Files.writeString(SOURCE, "foobar");
        assertThrows(CompileException.class, this::runWithSets);
    }

    @Test
    void generateTarget() throws IOException {
        Files.createFile(SOURCE);
        runOrFail();
        assertTrue(Files.exists(TARGET));
    }
}
