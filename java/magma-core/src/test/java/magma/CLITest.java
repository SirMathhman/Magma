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
	int code = TestHelper.runCliWithArg("5");

		// Expect the process exit code to be 5 (clamped to 0-255 by CLI
		// implementation).
		assertEquals(5, code, "CLI should exit with code 5 when given literal '5'");
	}

	@Test
	public void numericLiteralWithI32SuffixShouldReturnAsExitCode() throws IOException, InterruptedException {
	int code = TestHelper.runCliWithArg("5I32");

		// Expect the process exit code to be 5 when the CLI understands '5I32' as a numeric literal.
		assertEquals(5, code, "CLI should exit with code 5 when given literal '5I32'");
	}
}
