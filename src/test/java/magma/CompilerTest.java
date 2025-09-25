package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CompilerTest {

	@Test
	public void runnerReturnsOkTupleForFive() {
		// Expect Runner.run("5", "") to compile/run and return Ok(("", 5))
		var res = Runner.run("5", "");
		switch (res) {
			case Result.Ok(var value) -> {
				assertEquals(new Tuple<>("", 5), value);
			}
			case Result.Err(var error) -> {
				fail(error);
			}
		}
	}
}
