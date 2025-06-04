package magma;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class GenerateDiagramStubsTest {
    private Path createTempJavaSource(Path root, String relPath, String content) throws IOException {
        Path file = root.resolve(relPath);
        Files.createDirectories(file.getParent());
        Files.writeString(file, content);
        return file;
    }

    @Test
    public void stubGenerationCreatesFiles() throws IOException {
        Path javaRoot = Files.createTempDirectory("java");
        Path tsRoot = Files.createTempDirectory("ts");

        createTempJavaSource(javaRoot, "test/A.java", "package test;\npublic class A {}\n");
        createTempJavaSource(javaRoot, "test/B.java", "package test;\nimport test.A;\npublic class B {}\n");

        Optional<IOException> result = GenerateDiagram.writeTypeScriptStubs(javaRoot, tsRoot);
        if (result.isPresent()) {
            throw result.get();
        }

        Path aTs = tsRoot.resolve("test/A.ts");
        Path bTs = tsRoot.resolve("test/B.ts");
        assertTrue(Files.exists(aTs), "A.ts not created");
        assertTrue(Files.exists(bTs), "B.ts not created");

        String bContent = Files.readString(bTs);
        assertTrue(bContent.contains("import { A } from \"./A\";"), "B.ts missing import of A");
    }
}
