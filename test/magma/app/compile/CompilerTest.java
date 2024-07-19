package magma.app.compile;

import magma.app.ApplicationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class CompilerTest {
    private static void assertCompile(String input, String expected) {
        try {
            var actual = Compiler.compile(input);
            assertEquals(expected, actual);
        } catch (ApplicationException e) {
            fail(e);
        }
    }

    @Test
    void strip() {
        var input = Compiler.renderImport("\n", "second", "");
        var output = Compiler.renderImport("", "second", "");
        assertCompile(input, output);
    }

    @Test
    void multiple() throws ApplicationException {
        var first = Compiler.renderPackage("");
        var second = Compiler.renderImport("", "second", "Sibling");
        assertCompile(first + second, second);
    }
}
