package magma.app.compile;

import magma.app.ApplicationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompilerTest {
    @Test
    void multiple() throws ApplicationException {
        var first = Compiler.renderImport("first", "Child");
        var second = Compiler.renderImport("second", "Sibling");
        var expected = first + second;
        var output = Compiler.compile(expected);
        assertEquals(expected, output);
    }
}