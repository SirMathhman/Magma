package magma;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FunctionMainTest {
    @Test
    public void fnMainReturningLiteralShouldReturnThatValue() throws IOException, InterruptedException {
        String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        String classpath = System.getProperty("java.class.path");

        List<String> cmd = new ArrayList<>();
        cmd.add(javaBin);
        cmd.add("-cp");
        cmd.add(classpath);
        cmd.add("magma.CLI");
        // Use the compact function syntax the simple pipeline will recognize.
        cmd.add("fn main() : I32 => { return 42; }");

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectError(Redirect.INHERIT);
        pb.redirectOutput(Redirect.INHERIT);

        Process p = pb.start();
        int code = p.waitFor();

        assertEquals(42, code, "CLI should exit with code 42 for fn main returning 42");
    }
}
