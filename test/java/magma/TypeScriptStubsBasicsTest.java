package magma;

import magma.option.Option;
import magma.result.Results;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;

import static magma.TestUtil.writeSource;
import static org.junit.jupiter.api.Assertions.*;

public class TypeScriptStubsBasicsTest {

    private PathLike generateStubs() {
        PathLike javaRoot = new JVMPath(assertDoesNotThrow(() -> Files.createTempDirectory("java")));
        PathLike tsRoot = new JVMPath(assertDoesNotThrow(() -> Files.createTempDirectory("ts")));

        writeSource(javaRoot, "test/A.java", "package test;\npublic class A {}\n");
        writeSource(javaRoot, "test/B.java", "package test;\nimport test.A;\npublic class B {}\n");

        Option<IOException> result = TypeScriptStubs.write(javaRoot, tsRoot);
        result.ifPresent(e -> fail(e));
        return tsRoot;
    }

    @Test
    public void createsAStub() {
        PathLike tsRoot = generateStubs();
        assertTrue(tsRoot.resolve("test/A.ts").exists());
    }

    @Test
    public void createsBStub() {
        PathLike tsRoot = generateStubs();
        assertTrue(tsRoot.resolve("test/B.ts").exists());
    }

    @Test
    public void addsImportForDependency() {
        PathLike tsRoot = generateStubs();
        String content = Results.unwrap(tsRoot.resolve("test/B.ts").readString());
        assertTrue(content.contains("import { A } from \"./A\";"));
    }

    @Test
    public void addsImportForLocalDependency() {
        PathLike javaRoot = new JVMPath(assertDoesNotThrow(() -> Files.createTempDirectory("java")));
        PathLike tsRoot = new JVMPath(assertDoesNotThrow(() -> Files.createTempDirectory("ts")));

        writeSource(javaRoot, "test/Result.java", "package test;\npublic interface Result {}\n");
        writeSource(javaRoot, "test/Err.java", "package test;\npublic class Err implements Result {}\n");

        Option<IOException> result = TypeScriptStubs.write(javaRoot, tsRoot);
        result.ifPresent(e -> fail(e));

        String err = Results.unwrap(tsRoot.resolve("test/Err.ts").readString());
        assertTrue(err.contains("import { Result } from \"./Result\";"));
    }

    @Test
    public void stubDeclaresBClass() {
        PathLike tsRoot = generateStubs();
        String content = Results.unwrap(tsRoot.resolve("test/B.ts").readString());
        assertTrue(content.contains("export class B {}"));
    }

    @Test
    public void stubCopiesClasses() {
        PathLike javaRoot = new JVMPath(assertDoesNotThrow(() -> Files.createTempDirectory("java")));
        PathLike tsRoot = new JVMPath(assertDoesNotThrow(() -> Files.createTempDirectory("ts")));

        writeSource(javaRoot, "test/A.java", "package test;\npublic class A<T> {}\n");
        writeSource(javaRoot, "test/I.java", "package test;\npublic interface I<T> {}\n");
        writeSource(javaRoot, "test/R.java", "package test;\npublic record R<T>(T x) {}\n");

        Option<IOException> result = TypeScriptStubs.write(javaRoot, tsRoot);
        result.ifPresent(e -> fail(e));
        long count = Results.unwrap(tsRoot.resolve("test").list()).count();
        assertEquals(3, count);
    }
}
