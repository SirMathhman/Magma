package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CompilerTest {

	@Test
	public void runnerReturnsOkTupleForFive() {
		// Expect Runner.run("5", "") to compile/run and return Ok(("", 5))
		var res = Runner.run("5", "");
		switch (res) {
			case Result.Ok<?, ?> ok -> {
				// destructure the tuple using record deconstruction pattern
				Object val = ok.value();
				if (val instanceof Tuple(String left, Integer right)) {
					assertEquals("", left);
					assertEquals(5, right.intValue());
				} else {
					fail("Expected Tuple but was: " + (val == null ? "null" : val.getClass()));
				}
			}
			case Result.Err<?, ?> err -> {
				fail("Expected Ok but got Err: " + err.error());
			}
			default -> fail("Unexpected result type: " + res);
		}
	}
}
