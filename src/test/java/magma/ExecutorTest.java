package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
// ...existing imports...
import static org.junit.jupiter.api.Assertions.fail;

public class ExecutorTest {
	@Test
	public void emptyInputReturnsEmpty() {
		assertValid("", "");
	}

	@Test
	public void nonEmptyInputReturnsErr() {
		assertInvalid("data", "Non-empty input not allowed");
	}

	@Test
	public void leadingDigitsAreReturned() {
		assertValid("5U8", "5");
	}

	@Test
	public void simpleAdditionIsEvaluated() {
		assertValid("1 + 2", "3");
	}

	@Test
	public void additionWithSuffixesIsEvaluated() {
		assertValid("1U8 + 2U8", "3");
	}

	@Test
	public void mismatchedSuffixesReturnErr() {
		assertInvalid("1U8 + 2I16", "Mismatched operand suffixes");
	}

	private static void assertValid(String input, String expected) {
		var res = Executor.execute(input);
		switch (res) {
			case Result.Ok(var value) -> assertEquals(expected, value);
			case Result.Err(var error) -> fail(error);
		}
	}

	private static void assertInvalid(String input, String expectedError) {
		var res = Executor.execute(input);
		switch (res) {
			case Result.Ok(var value) -> fail(value);
			case Result.Err(var error) -> assertEquals(expectedError, error);
		}
	}
}
