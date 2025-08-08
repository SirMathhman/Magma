package magma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Base class for compiler tests that provides common test utilities.
 */
public class BaseCompilerTest {
    /**
     * Asserts that compiling the input produces the expected output.
     */
    protected void assertValid(String input, String output) {
        try {
            String actual = Compiler.compile(input);
            System.out.println("[DEBUG_LOG] Input: '" + input + "'");
            System.out.println("[DEBUG_LOG] Expected: '" + output + "'");
            System.out.println("[DEBUG_LOG] Actual: '" + actual + "'");
            assertEquals(output, actual);
        } catch (CompileException e) {
            System.out.println("[DEBUG_LOG] CompileException: " + e.getMessage());
            fail(e);
        }
    }

    /**
     * Asserts that compiling the input throws a CompileException.
     */
    protected void assertInvalid(String input) {
        assertThrows(CompileException.class, () -> Compiler.compile(input));
    }
}