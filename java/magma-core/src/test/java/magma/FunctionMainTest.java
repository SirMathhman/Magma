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
		int code = TestHelper.runCliWithArg("fn main() : I32 => { return 42; }");
		assertEquals(42, code, "CLI should exit with code 42 for fn main returning 42");
	}
}
