package magma.app;

import magma.compile.CompileException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompilerTest {

    @Test
    void packageStatement() throws CompileException {
        var actual = new Compiler().compile("package test;");
        assertEquals("", actual);
    }
}