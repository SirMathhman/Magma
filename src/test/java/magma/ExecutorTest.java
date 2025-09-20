package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExecutorTest {
	@Test
	public void emptyInputReturnsEmpty() throws Exception {
		var res = Executor.execute("");
		assertTrue(res instanceof Result.Ok);
		assertEquals("", ((Result.Ok<String, String>) res).value());
	}

	@Test
	public void nonEmptyInputReturnsErr() {
		var res = Executor.execute("data");
		assertTrue(res instanceof Result.Err);
		assertEquals("Non-empty input not allowed", ((Result.Err<String, String>) res).error());
	}
}
