import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class ApplicationTest {
    private static final String PRELUDE = "extern fn readInt() : I32; ";

    @Test
    void pass() throws Exception {
        assertValid("readInt()", "100", 100);
    }

    @Test
    void add() {
        assertValid("readInt() + readInt()", "100\r\n42", 142);
    }

    @Test
    void subtract() {
        assertValid("readInt() - readInt()", "100\r\n42", 58);
    }

    @Test
    void multiply() {
        assertValid("readInt() * readInt()", "100\r\n42", 4200);
    }

    private void assertValid(String input, String stdIn, int exitCode) {
        try {
            int exit = Runner.run(PRELUDE + input, stdIn);
            assertEquals(exitCode, exit);
        } catch (RunnerException e) {
            fail(e);
        }
    }
}
