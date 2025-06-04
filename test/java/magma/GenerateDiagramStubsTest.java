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

    @Test
    public void stubCopiesClasses() throws IOException {
        Path javaRoot = Files.createTempDirectory("java");
        Path tsRoot = Files.createTempDirectory("ts");

        createTempJavaSource(javaRoot, "test/A.java", "package test;\npublic class A<T> {}\n");
        createTempJavaSource(javaRoot, "test/I.java", "package test;\npublic interface I<T> {}\n");
        createTempJavaSource(javaRoot, "test/R.java", "package test;\npublic record R<T>(T x) {}\n");

        Optional<IOException> result = GenerateDiagram.writeTypeScriptStubs(javaRoot, tsRoot);
        if (result.isPresent()) {
            throw result.get();
        }

        String a = Files.readString(tsRoot.resolve("test/A.ts"));
        String i = Files.readString(tsRoot.resolve("test/I.ts"));
        String r = Files.readString(tsRoot.resolve("test/R.ts"));

        assertTrue(a.contains("export class A<T> {}"), "A.ts missing class A<T>");
        assertTrue(i.contains("export interface I<T> {}"), "I.ts missing interface I<T>");
        assertTrue(r.contains("export class R<T> {}"), "R.ts missing record R<T>");
    }
}
