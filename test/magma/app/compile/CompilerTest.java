package magma.app.compile;

import magma.app.ApplicationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompilerTest {
    @Test
    void multiple() throws ApplicationException {
        var first = Compiler.renderPackage("");
        var second = Compiler.renderImport("second", "Sibling");
        var output = Compiler.compile(first + second);
        assertEquals(second, output);
    }
}
