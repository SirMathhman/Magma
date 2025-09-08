package magma;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CLITest {
	@Test
	public void numericLiteralShouldReturnAsExitCode() throws IOException, InterruptedException {
		// Run the same JVM classpath so tests are hermetic in this module.
		String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
		String classpath = System.getProperty("java.class.path");

		List<String> cmd = new ArrayList<>();
		cmd.add(javaBin);
		cmd.add("-cp");
		cmd.add(classpath);
		cmd.add("magma.CLI");
		cmd.add("5");

		ProcessBuilder pb = new ProcessBuilder(cmd);
		pb.redirectError(Redirect.INHERIT);
		pb.redirectOutput(Redirect.INHERIT);

		Process p = pb.start();
		int code = p.waitFor();

		// Expect the process exit code to be 5 (clamped to 0-255 by CLI
		// implementation).
		assertEquals(5, code, "CLI should exit with code 5 when given literal '5'");
	}

	@Test
	public void numericLiteralWithI32SuffixShouldReturnAsExitCode() throws IOException, InterruptedException {
		// Run the same JVM classpath so tests are hermetic in this module.
		String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
		String classpath = System.getProperty("java.class.path");

		List<String> cmd = new ArrayList<>();
		cmd.add(javaBin);
		cmd.add("-cp");
		cmd.add(classpath);
		cmd.add("magma.CLI");
		cmd.add("5I32");

		ProcessBuilder pb = new ProcessBuilder(cmd);
		pb.redirectError(Redirect.INHERIT);
		pb.redirectOutput(Redirect.INHERIT);

		Process p = pb.start();
		int code = p.waitFor();

		// Expect the process exit code to be 5 when the CLI understands '5I32' as a numeric literal.
		assertEquals(5, code, "CLI should exit with code 5 when given literal '5I32'");
	}
}
