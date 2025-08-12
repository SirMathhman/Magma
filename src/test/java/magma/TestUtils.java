
package magma;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import magma.Compiler;
import magma.CompileException;

public class TestUtils {
    public static void assertValid(String input, String output) {
        Compiler compiler = new Compiler();
        String actual = compiler.compile(input);
        assertEquals(output, actual);
    }

    public static void assertInvalid(String input) {
        try {
            new Compiler().compile(input);
            fail("Expected CompileException to be thrown");
        } catch (CompileException e) {
            // expected
        }
    }
}
