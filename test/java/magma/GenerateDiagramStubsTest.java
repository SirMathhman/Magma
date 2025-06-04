package magma;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static magma.TestUtil.writeSource;

import static org.junit.jupiter.api.Assertions.*;

public class GenerateDiagramStubsTest {

    private Path generateStubs() throws IOException {
        Path javaRoot = Files.createTempDirectory("java");
        Path tsRoot = Files.createTempDirectory("ts");

        writeSource(javaRoot, "test/A.java", "package test;\npublic class A {}\n");
        writeSource(javaRoot, "test/B.java", "package test;\nimport test.A;\npublic class B {}\n");

        Optional<IOException> result = GenerateDiagram.writeTypeScriptStubs(javaRoot, tsRoot);
        if (result.isPresent()) {
            throw result.get();
        }
        return tsRoot;
    }

    @Test
    public void createsAStub() throws IOException {
        Path tsRoot = generateStubs();
        assertTrue(Files.exists(tsRoot.resolve("test/A.ts")));
    }

    @Test
    public void createsBStub() throws IOException {
        Path tsRoot = generateStubs();
        assertTrue(Files.exists(tsRoot.resolve("test/B.ts")));
    }

    @Test
    public void addsImportForDependency() throws IOException {
        Path tsRoot = generateStubs();
        String content = Files.readString(tsRoot.resolve("test/B.ts"));
        assertTrue(content.contains("import { A } from \"./A\";"));
    }

    private Path generateGenericStubs() throws IOException {
        Path javaRoot = Files.createTempDirectory("java");
        Path tsRoot = Files.createTempDirectory("ts");

        writeSource(javaRoot, "test/A.java", "package test;\npublic class A<T> {}\n");
        writeSource(javaRoot, "test/I.java", "package test;\npublic interface I<T> {}\n");
        writeSource(javaRoot, "test/R.java", "package test;\npublic record R<T>(T x) {}\n");

        Optional<IOException> result = GenerateDiagram.writeTypeScriptStubs(javaRoot, tsRoot);
        if (result.isPresent()) {
            throw result.get();
        }
        return tsRoot;
    }

    @Test
    public void copiesClassDeclaration() throws IOException {
        Path tsRoot = generateGenericStubs();
        String a = Files.readString(tsRoot.resolve("test/A.ts"));
        assertTrue(a.contains("export class A<T> {}"));
    }

    @Test
    public void copiesInterfaceDeclaration() throws IOException {
        Path tsRoot = generateGenericStubs();
        String i = Files.readString(tsRoot.resolve("test/I.ts"));
        assertTrue(i.contains("export interface I<T> {}"));
    }

    @Test
    public void copiesRecordDeclaration() throws IOException {
        Path tsRoot = generateGenericStubs();
        String r = Files.readString(tsRoot.resolve("test/R.ts"));
        assertTrue(r.contains("export class R<T> {}"));
    }

    private Path generateMethodStubs() throws IOException {
        Path javaRoot = Files.createTempDirectory("java");
        Path tsRoot = Files.createTempDirectory("ts");
        writeSource(javaRoot, "test/A.java",
                "package test;\npublic class A { public void foo(){} public static int bar(){return 0;} public String baz(){return \"\";} }\n");

        Optional<IOException> result = GenerateDiagram.writeTypeScriptStubs(javaRoot, tsRoot);
        if (result.isPresent()) {
            throw result.get();
        }
        return tsRoot;
    }

    @Test
    public void copiesInstanceMethod() throws IOException {
        Path tsRoot = generateMethodStubs();
        String a = Files.readString(tsRoot.resolve("test/A.ts"));
        assertTrue(a.contains("foo(): void {"));
    }

    @Test
    public void copiesStaticMethod() throws IOException {
        Path tsRoot = generateMethodStubs();
        String a = Files.readString(tsRoot.resolve("test/A.ts"));
        assertTrue(a.contains("foo(): void {"), "A.ts missing foo method");
        assertTrue(a.contains("bar(): int {"), "A.ts missing bar method");
        assertTrue(a.contains("baz(): String {"), "A.ts missing baz method");
    }
}
