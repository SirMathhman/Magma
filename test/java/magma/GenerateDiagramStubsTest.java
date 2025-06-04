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

        createTempJavaSource(javaRoot, "test/A.java", "package test;\npublic class A {}\n");
        createTempJavaSource(javaRoot, "test/I.java", "package test;\npublic interface I {}\n");
        createTempJavaSource(javaRoot, "test/R.java", "package test;\npublic record R(int x) {}\n");

        Optional<IOException> result = GenerateDiagram.writeTypeScriptStubs(javaRoot, tsRoot);
        if (result.isPresent()) {
            throw result.get();
        }

        String a = Files.readString(tsRoot.resolve("test/A.ts"));
        String i = Files.readString(tsRoot.resolve("test/I.ts"));
        String r = Files.readString(tsRoot.resolve("test/R.ts"));

        assertTrue(a.contains("export class A {}"), "A.ts missing class A");
        assertTrue(i.contains("export interface I {}"), "I.ts missing interface I");
        assertTrue(r.contains("export class R {}"), "R.ts missing record R");
    }

    @Test
    public void stubCopiesMethods() throws IOException {
        Path javaRoot = Files.createTempDirectory("java");
        Path tsRoot = Files.createTempDirectory("ts");

        createTempJavaSource(javaRoot, "test/A.java",
                "package test;\npublic class A { public void foo(){} public static void bar(){} }\n");

        Optional<IOException> result = GenerateDiagram.writeTypeScriptStubs(javaRoot, tsRoot);
        if (result.isPresent()) {
            throw result.get();
        }

        String a = Files.readString(tsRoot.resolve("test/A.ts"));
        assertTrue(a.contains("void foo() {"), "A.ts missing foo method");
        assertTrue(a.contains("void bar() {"), "A.ts missing bar method");
    }
}
