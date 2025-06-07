package magma;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class MultiFileCompilationTest {
    private static void invokeCompileFile(Path root, Path targetRoot, Path source) throws Exception {
        Method m = Main.class.getDeclaredMethod("compileFile", Path.class, Path.class, Path.class);
        m.setAccessible(true);
        m.invoke(null, root, targetRoot, source);
    }

    @Test
    void compileFileGeneratesMatchingPath() throws Exception {
        Path srcRoot = Files.createTempDirectory("srcRoot");
        Path targetRoot = Files.createTempDirectory("targetRoot");
        Path packageDir = srcRoot.resolve("magma/ast");
        Files.createDirectories(packageDir);
        Path javaFile = packageDir.resolve("Caller.java");
        String content = "package magma.ast; public class Caller {}";
        Files.writeString(javaFile, content);

        invokeCompileFile(srcRoot, targetRoot, javaFile);

        Path tsFile = targetRoot.resolve("magma/ast/Caller.ts");
        assertTrue(Files.exists(tsFile), "expected generated TypeScript file");
    }

    @Test
    void compileMultipleFiles() throws Exception {
        Path srcRoot = Files.createTempDirectory("srcRoot");
        Path targetRoot = Files.createTempDirectory("targetRoot");
        Path dirA = srcRoot.resolve("pkgA");
        Path dirB = srcRoot.resolve("pkgB");
        Files.createDirectories(dirA);
        Files.createDirectories(dirB);
        Path fileA = dirA.resolve("A.java");
        Path fileB = dirB.resolve("B.java");
        Files.writeString(fileA, "package pkgA; public class A {}");
        Files.writeString(fileB, "package pkgB; public class B {}");

        invokeCompileFile(srcRoot, targetRoot, fileA);
        invokeCompileFile(srcRoot, targetRoot, fileB);

        assertTrue(Files.exists(targetRoot.resolve("pkgA/A.ts")));
        assertTrue(Files.exists(targetRoot.resolve("pkgB/B.ts")));
    }
}
