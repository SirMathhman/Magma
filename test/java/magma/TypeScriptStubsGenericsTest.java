package magma;

import magma.option.Option;
import magma.result.Results;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;

import static magma.TestUtil.writeSource;
import static org.junit.jupiter.api.Assertions.*;

public class TypeScriptStubsGenericsTest {

    private PathLike generateGenericStubs() {
        PathLike javaRoot = new JVMPath(assertDoesNotThrow(() -> Files.createTempDirectory("java")));
        PathLike tsRoot = new JVMPath(assertDoesNotThrow(() -> Files.createTempDirectory("ts")));

        writeSource(javaRoot, "test/A.java", "package test;\npublic class A<T> {}\n");
        writeSource(javaRoot, "test/I.java", "package test;\npublic interface I<T> {}\n");
        writeSource(javaRoot, "test/R.java", "package test;\npublic record R<T>(T x) {}\n");

        Option<IOException> result = TypeScriptStubs.write(javaRoot, tsRoot);
        result.ifPresent(e -> fail(e));
        return tsRoot;
    }

    @Test
    public void copiesClassDeclaration() {
        PathLike tsRoot = generateGenericStubs();
        String a = Results.unwrap(tsRoot.resolve("test/A.ts").readString());
        assertTrue(a.contains("export class A<T> {}"));
    }

    @Test
    public void copiesInterfaceDeclaration() {
        PathLike tsRoot = generateGenericStubs();
        String i = Results.unwrap(tsRoot.resolve("test/I.ts").readString());
        assertTrue(i.contains("export interface I<T> {}"));
    }

    @Test
    public void copiesRecordDeclaration() {
        PathLike tsRoot = generateGenericStubs();
        String r = Results.unwrap(tsRoot.resolve("test/R.ts").readString());
        assertTrue(r.contains("export class R<T> {"));
        assertTrue(r.contains("x: T;"));
        assertTrue(r.contains("constructor(x: T)"));
    }

    @Test
    public void processesRecordParameters() {
        PathLike javaRoot = new JVMPath(assertDoesNotThrow(() -> Files.createTempDirectory("java")));
        PathLike tsRoot = new JVMPath(assertDoesNotThrow(() -> Files.createTempDirectory("ts")));

        writeSource(javaRoot, "test/R.java", "package test;\npublic record R(int id, String name) {}\n");

        Option<IOException> result = TypeScriptStubs.write(javaRoot, tsRoot);
        result.ifPresent(e -> fail(e));

        String r = Results.unwrap(tsRoot.resolve("test/R.ts").readString());
        assertTrue(r.contains("id: number;"));
        assertTrue(r.contains("name: string;"));
        assertTrue(r.contains("constructor(id: number, name: string)"));
        assertTrue(r.contains("this.id = id"));
    }

    @Test
    public void stubCopiesMethodsOnGenericClass() {
        PathLike javaRoot = new JVMPath(assertDoesNotThrow(() -> Files.createTempDirectory("java")));
        PathLike tsRoot = new JVMPath(assertDoesNotThrow(() -> Files.createTempDirectory("ts")));

        writeSource(javaRoot, "test/C.java", "package test;\npublic class C<T> { public void foo(){} }\n");

        Option<IOException> result = TypeScriptStubs.write(javaRoot, tsRoot);
        result.ifPresent(e -> fail(e));

        String c = Results.unwrap(tsRoot.resolve("test/C.ts").readString());
        assertTrue(c.contains("foo(): void {"));
    }

    @Test
    public void preservesGenericReturnType() {
        PathLike javaRoot = new JVMPath(assertDoesNotThrow(() -> Files.createTempDirectory("java")));
        PathLike tsRoot = new JVMPath(assertDoesNotThrow(() -> Files.createTempDirectory("ts")));

        writeSource(javaRoot, "test/Base.java", "package test;\npublic class Base<T> {}\n");
        writeSource(javaRoot, "test/Test.java", "package test;\npublic class Test {}\n");
        writeSource(javaRoot, "test/A.java", "package test;\npublic class A { public Base<Test> foo(){return null;} }\n");

        Option<IOException> result = TypeScriptStubs.write(javaRoot, tsRoot);
        result.ifPresent(e -> fail(e));

        String a = Results.unwrap(tsRoot.resolve("test/A.ts").readString());
        assertTrue(a.contains("foo(): Base<Test> {"));
    }

    @Test
    public void convertsPrimitiveGenericArgument() {
        PathLike javaRoot = new JVMPath(assertDoesNotThrow(() -> Files.createTempDirectory("java")));
        PathLike tsRoot = new JVMPath(assertDoesNotThrow(() -> Files.createTempDirectory("ts")));

        writeSource(javaRoot, "test/A.java", "package test;\nimport java.util.Optional;\npublic class A { public Optional<String> foo(){return null;} }\n");

        Option<IOException> result = TypeScriptStubs.write(javaRoot, tsRoot);
        result.ifPresent(e -> fail(e));

        String a = Results.unwrap(tsRoot.resolve("test/A.ts").readString());
        assertTrue(a.contains("foo(): Optional<string> {"));
    }

    @Test
    public void preservesMethodTypeParameter() {
        PathLike javaRoot = new JVMPath(assertDoesNotThrow(() -> Files.createTempDirectory("java")));
        PathLike tsRoot = new JVMPath(assertDoesNotThrow(() -> Files.createTempDirectory("ts")));

        writeSource(javaRoot, "test/A.java", "package test;\npublic class A { public <R> R id(R x){return x;} }\n");

        Option<IOException> result = TypeScriptStubs.write(javaRoot, tsRoot);
        result.ifPresent(e -> fail(e));

        String a = Results.unwrap(tsRoot.resolve("test/A.ts").readString());
        assertTrue(a.contains("id<R>(x: R): R {"));
    }
}
