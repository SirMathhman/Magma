package magma;

import magma.option.Option;
import magma.result.Results;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;

import static magma.TestUtil.writeSource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TypeScriptStubsTest {

    private PathLike generateStubs() {
        PathLike javaRoot;
        PathLike tsRoot;
        try {
            javaRoot = new JVMPath(Files.createTempDirectory("java"));
            tsRoot = new JVMPath(Files.createTempDirectory("ts"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        writeSource(javaRoot, "test/A.java", "package test;\npublic class A {}\n");
        writeSource(javaRoot, "test/B.java", "package test;\nimport test.A;\npublic class B {}\n");

        Option<IOException> result = TypeScriptStubs.write(javaRoot, tsRoot);
        if (result.isPresent()) {
            throw new RuntimeException(result.get());
        }
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
        PathLike javaRoot;
        PathLike tsRoot;
        try {
            javaRoot = new JVMPath(Files.createTempDirectory("java"));
            tsRoot = new JVMPath(Files.createTempDirectory("ts"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        writeSource(javaRoot, "test/Result.java", "package test;\npublic interface Result {}\n");
        writeSource(javaRoot, "test/Err.java", "package test;\npublic class Err implements Result {}\n");

        Option<IOException> result = TypeScriptStubs.write(javaRoot, tsRoot);
        if (result.isPresent()) {
            throw new RuntimeException(result.get());
        }

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
        PathLike javaRoot;
        PathLike tsRoot;
        try {
            javaRoot = new JVMPath(Files.createTempDirectory("java"));
            tsRoot = new JVMPath(Files.createTempDirectory("ts"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        writeSource(javaRoot, "test/A.java", "package test;\npublic class A<T> {}\n");
        writeSource(javaRoot, "test/I.java", "package test;\npublic interface I<T> {}\n");
        writeSource(javaRoot, "test/R.java", "package test;\npublic record R<T>(T x) {}\n");

        Option<IOException> result = TypeScriptStubs.write(javaRoot, tsRoot);
        if (result.isPresent()) {
            throw new RuntimeException(result.get());
        }
        long count = Results.unwrap(tsRoot.resolve("test").list()).count();
        assertEquals(3, count);
    }

    @Test
    public void copiesClassDeclaration() {
        PathLike tsRoot = generateGenericStubs();
        String a = Results.unwrap(tsRoot.resolve("test/A.ts").readString());
        assertTrue(a.contains("export class A<T> {}"));
    }

    private PathLike generateGenericStubs() {
        PathLike javaRoot;
        PathLike tsRoot;
        try {
            javaRoot = new JVMPath(Files.createTempDirectory("java"));
            tsRoot = new JVMPath(Files.createTempDirectory("ts"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        writeSource(javaRoot, "test/A.java", "package test;\npublic class A<T> {}\n");
        writeSource(javaRoot, "test/I.java", "package test;\npublic interface I<T> {}\n");
        writeSource(javaRoot, "test/R.java", "package test;\npublic record R<T>(T x) {}\n");

        Option<IOException> result = TypeScriptStubs.write(javaRoot, tsRoot);
        if (result.isPresent()) {
            throw new RuntimeException(result.get());
        }
        return tsRoot;
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
        assertTrue(r.contains("export class R<T> {}"));
    }

    private PathLike generateMethodStubs() {
        PathLike javaRoot;
        PathLike tsRoot;
        try {
            javaRoot = new JVMPath(Files.createTempDirectory("java"));
            tsRoot = new JVMPath(Files.createTempDirectory("ts"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writeSource(javaRoot, "test/A.java",
                "package test;\npublic class A { public void foo(){} public static int bar(){return 0;} public String baz(){return \"\";} }\n");

        Option<IOException> result = TypeScriptStubs.write(javaRoot, tsRoot);
        if (result.isPresent()) {
            throw new RuntimeException(result.get());
        }
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
        assertTrue(a.contains("static bar(): number {"), "A.ts missing static bar method");
    }

    @Test
    public void copiesBazMethod() {
        PathLike tsRoot = generateMethodStubs();
        String a = Results.unwrap(tsRoot.resolve("test/A.ts").readString());
        assertTrue(a.contains("baz(): string {"), "A.ts missing baz method");
    }

    @Test
    public void stubCopiesMethodsOnGenericClass() {
        PathLike javaRoot;
        PathLike tsRoot;
        try {
            javaRoot = new JVMPath(Files.createTempDirectory("java"));
            tsRoot = new JVMPath(Files.createTempDirectory("ts"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        writeSource(javaRoot, "test/C.java",
                "package test;\npublic class C<T> { public void foo(){} }\n");

        Option<IOException> result = TypeScriptStubs.write(javaRoot, tsRoot);
        if (result.isPresent()) {
            throw new RuntimeException(result.get());
        }

        String c = Results.unwrap(tsRoot.resolve("test/C.ts").readString());
        assertTrue(c.contains("foo(): void {"), "C.ts missing foo method");
    }

    @Test
    public void preservesGenericReturnType() {
        PathLike javaRoot;
        PathLike tsRoot;
        try {
            javaRoot = new JVMPath(Files.createTempDirectory("java"));
            tsRoot = new JVMPath(Files.createTempDirectory("ts"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        writeSource(javaRoot, "test/Base.java", "package test;\npublic class Base<T> {}\n");
        writeSource(javaRoot, "test/Test.java", "package test;\npublic class Test {}\n");
        writeSource(javaRoot, "test/A.java",
                "package test;\npublic class A { public Base<Test> foo(){return null;} }\n");

        Option<IOException> result = TypeScriptStubs.write(javaRoot, tsRoot);
        if (result.isPresent()) {
            throw new RuntimeException(result.get());
        }

        String a = Results.unwrap(tsRoot.resolve("test/A.ts").readString());
        assertTrue(a.contains("foo(): Base<Test> {"));
    }

    @Test
    public void convertsPrimitiveGenericArgument() {
        PathLike javaRoot;
        PathLike tsRoot;
        try {
            javaRoot = new JVMPath(Files.createTempDirectory("java"));
            tsRoot = new JVMPath(Files.createTempDirectory("ts"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        writeSource(javaRoot, "test/A.java",
                "package test;\nimport java.util.Optional;\npublic class A { public Optional<String> foo(){return null;} }\n");

        Option<IOException> result = TypeScriptStubs.write(javaRoot, tsRoot);
        if (result.isPresent()) {
            throw new RuntimeException(result.get());
        }

        String a = Results.unwrap(tsRoot.resolve("test/A.ts").readString());
        assertTrue(a.contains("foo(): Optional<string> {"));
    }

    @Test
    public void preservesParameterTypes() {
        PathLike javaRoot;
        PathLike tsRoot;
        try {
            javaRoot = new JVMPath(Files.createTempDirectory("java"));
            tsRoot = new JVMPath(Files.createTempDirectory("ts"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        writeSource(javaRoot, "test/A.java",
                "package test;\npublic class A { int add(int x, int y){return 0;} }\n");

        Option<IOException> result = TypeScriptStubs.write(javaRoot, tsRoot);
        if (result.isPresent()) {
            throw new RuntimeException(result.get());
        }

        String a = Results.unwrap(tsRoot.resolve("test/A.ts").readString());
        assertTrue(a.contains("add(x: number, y: number): number {"));
    }

    @Test
    public void preservesMethodTypeParameter() {
        PathLike javaRoot;
        PathLike tsRoot;
        try {
            javaRoot = new JVMPath(Files.createTempDirectory("java"));
            tsRoot = new JVMPath(Files.createTempDirectory("ts"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        writeSource(javaRoot, "test/A.java",
                "package test;\npublic class A { public <R> R id(R x){return x;} }\n");

        Option<IOException> result = TypeScriptStubs.write(javaRoot, tsRoot);
        if (result.isPresent()) {
            throw new RuntimeException(result.get());
        }

        String a = Results.unwrap(tsRoot.resolve("test/A.ts").readString());
        assertTrue(a.contains("id<R>(x: R): R {"));
    }

    @Test
    public void preservesExtendsAndImplements() {
        PathLike javaRoot;
        PathLike tsRoot;
        try {
            javaRoot = new JVMPath(Files.createTempDirectory("java"));
            tsRoot = new JVMPath(Files.createTempDirectory("ts"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        writeSource(javaRoot, "test/I.java", "package test;\npublic interface I {}\n");
        writeSource(javaRoot, "test/Base.java", "package test;\npublic class Base {}\n");
        writeSource(javaRoot, "test/A.java",
                "package test;\npublic class A extends Base implements I {}\n");

        Option<IOException> result = TypeScriptStubs.write(javaRoot, tsRoot);
        if (result.isPresent()) {
            throw new RuntimeException(result.get());
        }

        String a = Results.unwrap(tsRoot.resolve("test/A.ts").readString());
        assertTrue(a.contains("export class A extends Base implements I {}"));
    }
}
