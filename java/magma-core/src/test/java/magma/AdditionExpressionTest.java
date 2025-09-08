package magma;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AdditionExpressionTest {
    @Test
    public void simpleAdditionShouldReturnSumAsExitCode() throws IOException, InterruptedException {
        int code = TestHelper.runCliWithArg("5+3");

        // Expect the CLI to evaluate 5+3 and return 8 (clamped to 0-255 by CLI).
        assertEquals(8, code, "CLI should exit with code 8 for expression '5+3'");
    }
}
