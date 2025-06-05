package magma;

import magma.option.Option;
import magma.result.Results;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;

import static magma.TestUtil.writeSource;
import static org.junit.jupiter.api.Assertions.*;

public class TypeScriptStubsSegmentsTest {

    private PathLike generateSegmentStubs() {
        PathLike javaRoot = new JVMPath(assertDoesNotThrow(() -> Files.createTempDirectory("java")));
        PathLike tsRoot = new JVMPath(assertDoesNotThrow(() -> Files.createTempDirectory("ts")));

        writeSource(javaRoot, "test/A.java",
                "package test;\n" +
                "public class A { int foo(){ int x=1; if(x>0){ bar(); } else { baz(); } return x; } void bar(){} void baz(){} }\n");

        Option<IOException> result = TypeScriptStubs.write(javaRoot, tsRoot);
        result.ifPresent(e -> fail(e));
        return tsRoot;
    }

    @Test
    public void copiesAssignmentSegment() {
        PathLike tsRoot = generateSegmentStubs();
        String a = Results.unwrap(tsRoot.resolve("test/A.ts").readString());
        assertTrue(a.contains("let x: number = 1;"));
    }

    @Test
    public void splitsStatementsAcrossLines() {
        PathLike tsRoot = generateSegmentStubs();
        String a = Results.unwrap(tsRoot.resolve("test/A.ts").readString());
        String expected = "let x: number = 1;" + System.lineSeparator()
                + "\t\tif(x>0){" + System.lineSeparator()
                + "\t\t\tbar();" + System.lineSeparator()
                + "\t\t}" + System.lineSeparator()
                + "\t\telse {" + System.lineSeparator()
                + "\t\t\tbaz();" + System.lineSeparator()
                + "\t\t}" + System.lineSeparator()
                + "\t\treturn x;";
        assertTrue(a.contains(expected));
    }
}
