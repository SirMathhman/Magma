package magma;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import magma.option.Option;

import static magma.TestUtil.writeSource;

import static org.junit.jupiter.api.Assertions.*;

public class TypeScriptStubsTest {

    private Path generateStubs() {
        Path javaRoot;
        Path tsRoot;
        try {
            javaRoot = Files.createTempDirectory("java");
            tsRoot = Files.createTempDirectory("ts");
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
        Path tsRoot = generateStubs();
        assertTrue(Files.exists(tsRoot.resolve("test/A.ts")));
    }

    @Test
    public void createsBStub() {
        Path tsRoot = generateStubs();
        assertTrue(Files.exists(tsRoot.resolve("test/B.ts")));
    }

    @Test
    public void addsImportForDependency() {
        Path tsRoot = generateStubs();
        String content;
        try {
            content = Files.readString(tsRoot.resolve("test/B.ts"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertTrue(content.contains("import { A } from \"./A\";"));
    }

    @Test
    public void stubDeclaresBClass() {
        Path tsRoot = generateStubs();
        String content;
        try {
            content = Files.readString(tsRoot.resolve("test/B.ts"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertTrue(content.contains("export class B {}"));
    }
  
    @Test
    public void stubCopiesClasses() {
        Path javaRoot;
        Path tsRoot;
        try {
            javaRoot = Files.createTempDirectory("java");
            tsRoot = Files.createTempDirectory("ts");
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
        long count;
        try {
            count = Files.list(tsRoot.resolve("test")).count();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals(3, count);
    }

    @Test
    public void copiesClassDeclaration() {
        Path tsRoot = generateGenericStubs();
        String a;
        try {
            a = Files.readString(tsRoot.resolve("test/A.ts"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertTrue(a.contains("export class A<T> {}"));
    }

    private Path generateGenericStubs() {
        Path javaRoot;
        Path tsRoot;
        try {
            javaRoot = Files.createTempDirectory("java");
            tsRoot = Files.createTempDirectory("ts");
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
        Path tsRoot = generateGenericStubs();
        String i;
        try {
            i = Files.readString(tsRoot.resolve("test/I.ts"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertTrue(i.contains("export interface I<T> {}"));
    }

    @Test
    public void copiesRecordDeclaration() {
        Path tsRoot = generateGenericStubs();
        String r;
        try {
            r = Files.readString(tsRoot.resolve("test/R.ts"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertTrue(r.contains("export class R<T> {}"));
    }

    private Path generateMethodStubs() {
        Path javaRoot;
        Path tsRoot;
        try {
            javaRoot = Files.createTempDirectory("java");
            tsRoot = Files.createTempDirectory("ts");
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
        Path tsRoot = generateMethodStubs();
        String a;
        try {
            a = Files.readString(tsRoot.resolve("test/A.ts"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertTrue(a.contains("foo(): void {"));
    }

    @Test
    public void copiesStaticMethod() {
        Path tsRoot = generateMethodStubs();
        String a;
        try {
            a = Files.readString(tsRoot.resolve("test/A.ts"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertTrue(a.contains("static bar(): number {"), "A.ts missing static bar method");
    }

    @Test
    public void copiesBazMethod() {
        Path tsRoot = generateMethodStubs();
        String a;
        try {
            a = Files.readString(tsRoot.resolve("test/A.ts"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertTrue(a.contains("baz(): string {"), "A.ts missing baz method");
    }

    @Test
    public void stubCopiesMethodsOnGenericClass() {
        Path javaRoot;
        Path tsRoot;
        try {
            javaRoot = Files.createTempDirectory("java");
            tsRoot = Files.createTempDirectory("ts");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        writeSource(javaRoot, "test/C.java",
                "package test;\npublic class C<T> { public void foo(){} }\n");

        Option<IOException> result = TypeScriptStubs.write(javaRoot, tsRoot);
        if (result.isPresent()) {
            throw new RuntimeException(result.get());
        }

        String c;
        try {
            c = Files.readString(tsRoot.resolve("test/C.ts"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertTrue(c.contains("foo(): void {"), "C.ts missing foo method");
    }

    @Test
    public void preservesGenericReturnType() {
        Path javaRoot;
        Path tsRoot;
        try {
            javaRoot = Files.createTempDirectory("java");
            tsRoot = Files.createTempDirectory("ts");
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

        String a;
        try {
            a = Files.readString(tsRoot.resolve("test/A.ts"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertTrue(a.contains("foo(): Base<Test> {"));
    }

    @Test
    public void convertsPrimitiveGenericArgument() {
        Path javaRoot;
        Path tsRoot;
        try {
            javaRoot = Files.createTempDirectory("java");
            tsRoot = Files.createTempDirectory("ts");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        writeSource(javaRoot, "test/A.java",
                "package test;\nimport java.util.Optional;\npublic class A { public Optional<String> foo(){return null;} }\n");

        Option<IOException> result = TypeScriptStubs.write(javaRoot, tsRoot);
        if (result.isPresent()) {
            throw new RuntimeException(result.get());
        }

        String a;
        try {
            a = Files.readString(tsRoot.resolve("test/A.ts"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertTrue(a.contains("foo(): Optional<string> {"));
    }

    @Test
    public void preservesParameterTypes() {
        Path javaRoot;
        Path tsRoot;
        try {
            javaRoot = Files.createTempDirectory("java");
            tsRoot = Files.createTempDirectory("ts");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        writeSource(javaRoot, "test/A.java",
                "package test;\npublic class A { int add(int x, int y){return 0;} }\n");

        Option<IOException> result = TypeScriptStubs.write(javaRoot, tsRoot);
        if (result.isPresent()) {
            throw new RuntimeException(result.get());
        }

        String a;
        try {
            a = Files.readString(tsRoot.resolve("test/A.ts"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertTrue(a.contains("add(x: number, y: number): number {"));
    }

    @Test
    public void preservesMethodTypeParameter() {
        Path javaRoot;
        Path tsRoot;
        try {
            javaRoot = Files.createTempDirectory("java");
            tsRoot = Files.createTempDirectory("ts");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        writeSource(javaRoot, "test/A.java",
                "package test;\npublic class A { public <R> R id(R x){return x;} }\n");

        Option<IOException> result = TypeScriptStubs.write(javaRoot, tsRoot);
        if (result.isPresent()) {
            throw new RuntimeException(result.get());
        }

        String a;
        try {
            a = Files.readString(tsRoot.resolve("test/A.ts"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertTrue(a.contains("id<R>(x: R): R {"));
    }

    @Test
    public void preservesExtendsAndImplements() {
        Path javaRoot;
        Path tsRoot;
        try {
            javaRoot = Files.createTempDirectory("java");
            tsRoot = Files.createTempDirectory("ts");
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

        String a;
        try {
            a = Files.readString(tsRoot.resolve("test/A.ts"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertTrue(a.contains("export class A extends Base implements I {}"));
    }
}
