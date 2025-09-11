package magma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class GenericsTest {

	@Test
	public void genericFnPassesValue() {
		Result<String, InterpretError> r = new Interpreter().interpret(
				"fn pass<T>(value : T) => value; pass(100)");
		if (r instanceof Result.Err) {
			fail("Interpreter returned error: " + ((Result.Err<String, InterpretError>) r).error());
		}
		assertEquals("100", ((Result.Ok<String, InterpretError>) r).value());
	}
}
