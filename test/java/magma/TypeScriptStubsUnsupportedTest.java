package magma;

import magma.option.Option;
import magma.result.Results;
import magma.ParseException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;

import static magma.TestUtil.writeSource;
import static org.junit.jupiter.api.Assertions.*;

public class TypeScriptStubsUnsupportedTest {

    @Test
    public void addsFixmeForInstanceof() {
        PathLike javaRoot = new JVMPath(assertDoesNotThrow(() -> Files.createTempDirectory("java")));
        PathLike tsRoot = new JVMPath(assertDoesNotThrow(() -> Files.createTempDirectory("ts")));

        writeSource(javaRoot, "test/A.java", "package test;\npublic class A { boolean check(Object x){ return x instanceof String; } }\n");

        Option<IOException> result = TypeScriptStubs.write(javaRoot, tsRoot);
        assertTrue(result.isPresent());
        assertTrue(result.get() instanceof ParseException);
    }

    @Test
    public void ignoresSwitchStatement() {
        PathLike javaRoot = new JVMPath(assertDoesNotThrow(() -> Files.createTempDirectory("java")));
        PathLike tsRoot = new JVMPath(assertDoesNotThrow(() -> Files.createTempDirectory("ts")));

        writeSource(javaRoot, "test/A.java", "package test;\npublic class A { int foo(int x){ switch(x){ case 1: return 1; default: return 0; } } }\n");

        Option<IOException> result = TypeScriptStubs.write(javaRoot, tsRoot);
        result.ifPresent(e -> fail(e));

        String a = Results.unwrap(tsRoot.resolve("test/A.ts").readString());
        assertFalse(a.contains("switch"));
    }
}
