package magma;

import magma.option.Option;
import magma.result.Results;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;

import static magma.TestUtil.writeSource;
import static org.junit.jupiter.api.Assertions.*;

public class TypeScriptStubsMethodsTest {

    private PathLike generateMethodStubs() {
        PathLike javaRoot = new JVMPath(assertDoesNotThrow(() -> Files.createTempDirectory("java")));
        PathLike tsRoot = new JVMPath(assertDoesNotThrow(() -> Files.createTempDirectory("ts")));
        writeSource(javaRoot, "test/A.java", "package test;\npublic class A { public void foo(){} public static int bar(){return 0;} public String baz(){return \"\";} }\n");

        Option<IOException> result = TypeScriptStubs.write(javaRoot, tsRoot);
        result.ifPresent(e -> fail(e));
        return tsRoot;
    }

    @Test
    public void copiesInstanceMethod() {
        PathLike tsRoot = generateMethodStubs();
        String a = Results.unwrap(tsRoot.resolve("test/A.ts").readString());
        assertTrue(a.contains("foo(): void {"));
    }

    @Test
    public void copiesStaticMethod() {
        PathLike tsRoot = generateMethodStubs();
        String a = Results.unwrap(tsRoot.resolve("test/A.ts").readString());
        assertTrue(a.contains("static bar(): number {"));
    }

    @Test
    public void copiesBazMethod() {
        PathLike tsRoot = generateMethodStubs();
        String a = Results.unwrap(tsRoot.resolve("test/A.ts").readString());
        assertTrue(a.contains("baz(): string {"));
    }

    @Test
    public void stubsMethodBody() {
        PathLike tsRoot = generateMethodStubs();
        String a = Results.unwrap(tsRoot.resolve("test/A.ts").readString());
        assertTrue(a.contains("return 0;"));
    }

    @Test
    public void convertsBoxedPrimitives() {
        PathLike javaRoot = new JVMPath(assertDoesNotThrow(() -> Files.createTempDirectory("java")));
        PathLike tsRoot = new JVMPath(assertDoesNotThrow(() -> Files.createTempDirectory("ts")));

        writeSource(javaRoot, "test/A.java", "package test;\npublic class A { Integer add(Integer x, Boolean flag){return 0;} }\n");

        Option<IOException> result = TypeScriptStubs.write(javaRoot, tsRoot);
        result.ifPresent(e -> fail(e));

        String a = Results.unwrap(tsRoot.resolve("test/A.ts").readString());
        assertTrue(a.contains("add(x: number, flag: boolean): number {"));
    }

    @Test
    public void preservesParameterTypes() {
        PathLike javaRoot = new JVMPath(assertDoesNotThrow(() -> Files.createTempDirectory("java")));
        PathLike tsRoot = new JVMPath(assertDoesNotThrow(() -> Files.createTempDirectory("ts")));

        writeSource(javaRoot, "test/A.java", "package test;\npublic class A { int add(int x, int y){return 0;} }\n");

        Option<IOException> result = TypeScriptStubs.write(javaRoot, tsRoot);
        result.ifPresent(e -> fail(e));

        String a = Results.unwrap(tsRoot.resolve("test/A.ts").readString());
        assertTrue(a.contains("add(x: number, y: number): number {"));
    }

    @Test
    public void preservesExtendsAndImplements() {
        PathLike javaRoot = new JVMPath(assertDoesNotThrow(() -> Files.createTempDirectory("java")));
        PathLike tsRoot = new JVMPath(assertDoesNotThrow(() -> Files.createTempDirectory("ts")));

        writeSource(javaRoot, "test/I.java", "package test;\npublic interface I {}\n");
        writeSource(javaRoot, "test/Base.java", "package test;\npublic class Base {}\n");
        writeSource(javaRoot, "test/A.java", "package test;\npublic class A extends Base implements I {}\n");

        Option<IOException> result = TypeScriptStubs.write(javaRoot, tsRoot);
        result.ifPresent(e -> fail(e));

        String a = Results.unwrap(tsRoot.resolve("test/A.ts").readString());
        assertTrue(a.contains("export class A extends Base implements I {}"));
    }
}
