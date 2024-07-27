package magma.app;

import magma.compile.CompileException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class CompilerTest {
    private static void assertCompile(String input, String expected) {
        try {
            var actual = new Compiler().compile(input);
            assertEquals(expected, actual);
        } catch (CompileException e) {
            fail(e);
        }
    }

    @Test
    void importStatement() {
        var statement = "import first;";
        assertCompile(statement, statement);
    }

    @Test
    void packageStatement() {
        assertCompile("package test;", "");
    }
}