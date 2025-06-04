package magma;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import magma.option.Option;

import static magma.TestUtil.writeSource;

import static org.junit.jupiter.api.Assertions.*;

public class TypeScriptStubsTest {

    private Path generateStubs() throws IOException {
        Path javaRoot = Files.createTempDirectory("java");
        Path tsRoot = Files.createTempDirectory("ts");

        writeSource(javaRoot, "test/A.java", "package test;\npublic class A {}\n");
        writeSource(javaRoot, "test/B.java", "package test;\nimport test.A;\npublic class B {}\n");

        Option<IOException> result = TypeScriptStubs.write(javaRoot, tsRoot);
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

    @Test
    public void stubDeclaresBClass() throws IOException {
        Path tsRoot = generateStubs();
        String content = Files.readString(tsRoot.resolve("test/B.ts"));
        assertTrue(content.contains("export class B {}"));
    }
  
    @Test
    public void stubCopiesClasses() throws IOException {
        Path javaRoot = Files.createTempDirectory("java");
        Path tsRoot = Files.createTempDirectory("ts");

        writeSource(javaRoot, "test/A.java", "package test;\npublic class A<T> {}\n");
        writeSource(javaRoot, "test/I.java", "package test;\npublic interface I<T> {}\n");
        writeSource(javaRoot, "test/R.java", "package test;\npublic record R<T>(T x) {}\n");

        Option<IOException> result = TypeScriptStubs.write(javaRoot, tsRoot);
        if (result.isPresent()) {
            throw result.get();
        }
        long count = Files.list(tsRoot.resolve("test")).count();
        assertEquals(3, count);
    }

    @Test
    public void copiesClassDeclaration() throws IOException {
        Path tsRoot = generateGenericStubs();
        String a = Files.readString(tsRoot.resolve("test/A.ts"));
        assertTrue(a.contains("export class A<T> {}"));
    }

    private Path generateGenericStubs() throws IOException {
        Path javaRoot = Files.createTempDirectory("java");
        Path tsRoot = Files.createTempDirectory("ts");

        writeSource(javaRoot, "test/A.java", "package test;\npublic class A<T> {}\n");
        writeSource(javaRoot, "test/I.java", "package test;\npublic interface I<T> {}\n");
        writeSource(javaRoot, "test/R.java", "package test;\npublic record R<T>(T x) {}\n");

        Option<IOException> result = TypeScriptStubs.write(javaRoot, tsRoot);
        if (result.isPresent()) {
            throw result.get();
        }
        return tsRoot;
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

        Option<IOException> result = TypeScriptStubs.write(javaRoot, tsRoot);
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
        assertTrue(a.contains("static bar(): number {"), "A.ts missing static bar method");
    }

    @Test
    public void copiesBazMethod() throws IOException {
        Path tsRoot = generateMethodStubs();
        String a = Files.readString(tsRoot.resolve("test/A.ts"));
        assertTrue(a.contains("baz(): string {"), "A.ts missing baz method");
    }

    @Test
    public void stubCopiesMethodsOnGenericClass() throws IOException {
        Path javaRoot = Files.createTempDirectory("java");
        Path tsRoot = Files.createTempDirectory("ts");

        writeSource(javaRoot, "test/C.java",
                "package test;\npublic class C<T> { public void foo(){} }\n");

        Option<IOException> result = TypeScriptStubs.write(javaRoot, tsRoot);
        if (result.isPresent()) {
            throw result.get();
        }

        String c = Files.readString(tsRoot.resolve("test/C.ts"));
        assertTrue(c.contains("foo(): void {"), "C.ts missing foo method");
    }

    @Test
    public void preservesGenericReturnType() throws IOException {
        Path javaRoot = Files.createTempDirectory("java");
        Path tsRoot = Files.createTempDirectory("ts");

        writeSource(javaRoot, "test/Base.java", "package test;\npublic class Base<T> {}\n");
        writeSource(javaRoot, "test/Test.java", "package test;\npublic class Test {}\n");
        writeSource(javaRoot, "test/A.java",
                "package test;\npublic class A { public Base<Test> foo(){return null;} }\n");

        Option<IOException> result = TypeScriptStubs.write(javaRoot, tsRoot);
        if (result.isPresent()) {
            throw result.get();
        }

        String a = Files.readString(tsRoot.resolve("test/A.ts"));
        assertTrue(a.contains("foo(): Base<Test> {"));
    }

    @Test
    public void convertsPrimitiveGenericArgument() throws IOException {
        Path javaRoot = Files.createTempDirectory("java");
        Path tsRoot = Files.createTempDirectory("ts");

        writeSource(javaRoot, "test/A.java",
                "package test;\nimport java.util.Optional;\npublic class A { public Optional<String> foo(){return null;} }\n");

        Option<IOException> result = TypeScriptStubs.write(javaRoot, tsRoot);
        if (result.isPresent()) {
            throw result.get();
        }

        String a = Files.readString(tsRoot.resolve("test/A.ts"));
        assertTrue(a.contains("foo(): Optional<string> {"));
    }

    @Test
    public void preservesParameterTypes() throws IOException {
        Path javaRoot = Files.createTempDirectory("java");
        Path tsRoot = Files.createTempDirectory("ts");

        writeSource(javaRoot, "test/A.java",
                "package test;\npublic class A { int add(int x, int y){return 0;} }\n");

        Option<IOException> result = TypeScriptStubs.write(javaRoot, tsRoot);
        if (result.isPresent()) {
            throw result.get();
        }

        String a = Files.readString(tsRoot.resolve("test/A.ts"));
        assertTrue(a.contains("add(x: number, y: number): number {"));
    }

    @Test
    public void preservesMethodTypeParameter() throws IOException {
        Path javaRoot = Files.createTempDirectory("java");
        Path tsRoot = Files.createTempDirectory("ts");

        writeSource(javaRoot, "test/A.java",
                "package test;\npublic class A { public <R> R id(R x){return x;} }\n");

        Option<IOException> result = TypeScriptStubs.write(javaRoot, tsRoot);
        if (result.isPresent()) {
            throw result.get();
        }

        String a = Files.readString(tsRoot.resolve("test/A.ts"));
        assertTrue(a.contains("id<R>(x: R): R {"));
    }

    @Test
    public void preservesExtendsAndImplements() throws IOException {
        Path javaRoot = Files.createTempDirectory("java");
        Path tsRoot = Files.createTempDirectory("ts");

        writeSource(javaRoot, "test/I.java", "package test;\npublic interface I {}\n");
        writeSource(javaRoot, "test/Base.java", "package test;\npublic class Base {}\n");
        writeSource(javaRoot, "test/A.java",
                "package test;\npublic class A extends Base implements I {}\n");

        Option<IOException> result = TypeScriptStubs.write(javaRoot, tsRoot);
        if (result.isPresent()) {
            throw result.get();
        }

        String a = Files.readString(tsRoot.resolve("test/A.ts"));
        assertTrue(a.contains("export class A extends Base implements I {}"));
    }
}
