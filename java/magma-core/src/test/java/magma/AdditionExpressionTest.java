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
        String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        String classpath = System.getProperty("java.class.path");

        List<String> cmd = new ArrayList<>();
        cmd.add(javaBin);
        cmd.add("-cp");
        cmd.add(classpath);
        cmd.add("magma.CLI");
        // Provide the raw source as a single argument. CLI currently tokenizes the
        // argument verbatim, so use no spaces to match SimpleLexer patterns.
        cmd.add("5+3");

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectError(Redirect.INHERIT);
        pb.redirectOutput(Redirect.INHERIT);

        Process p = pb.start();
        int code = p.waitFor();

        // Expect the CLI to evaluate 5+3 and return 8 (clamped to 0-255 by CLI).
        assertEquals(8, code, "CLI should exit with code 8 for expression '5+3'");
    }
}
