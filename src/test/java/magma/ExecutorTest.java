package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExecutorTest {
	@Test
	public void emptyInputReturnsEmpty() {
		var res = Executor.execute("");
		assertTrue(res instanceof Result.Ok);
		if (res instanceof Result.Ok ok) {
			assertEquals("", ok.value());
		}
	}

	@Test
	public void nonEmptyInputReturnsErr() {
		var res = Executor.execute("data");
		assertTrue(res instanceof Result.Err);
		if (res instanceof Result.Err err) {
			assertEquals("Non-empty input not allowed", err.error());
		}
	}

	@Test
	public void leadingDigitsAreReturned() {
		var res = Executor.execute("5U8");
		assertTrue(res instanceof Result.Ok);
		if (res instanceof Result.Ok ok) {
			assertEquals("5", ok.value());
		}
	}

	@Test
	public void simpleAdditionIsEvaluated() {
		var res = Executor.execute("1 + 2");
		assertTrue(res instanceof Result.Ok);
		if (res instanceof Result.Ok ok) {
			assertEquals("3", ok.value());
		}
	}
}
