import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApplicationTest {
    @Test
    void runnerReturns100ForReadIntProgram() throws Exception {
        int exit = Runner.run("extern fn readInt() : I32; readInt()", "100\n");
        assertEquals(100, exit, "Runner should return the integer read from stdin as exit code");
    }
}
