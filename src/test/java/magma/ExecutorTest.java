package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class ExecutorTest {
	@Test
	public void emptyInputReturnsEmpty() {
		var res = Executor.execute("");
		switch (res) {
			case Result.Ok(var value) -> assertEquals("", value);
			case Result.Err(var error) -> fail(error);
		}
	}

	@Test
	public void nonEmptyInputReturnsErr() {
		var res = Executor.execute("data");
		switch (res) {
			case Result.Ok(var value) -> fail(value);
			case Result.Err(var error) -> assertEquals("Non-empty input not allowed", error);
		}
	}

	@Test
	public void leadingDigitsAreReturned() {
		var res = Executor.execute("5U8");
		assertTrue(res instanceof Result.Ok);
		switch (res) {
			case Result.Ok(var value) -> assertEquals("5", value);
			case Result.Err(var error) -> fail(error);
		}
	}

	@Test
	public void simpleAdditionIsEvaluated() {
		var res = Executor.execute("1 + 2");
		switch (res) {
			case Result.Ok(var value) -> assertEquals("3", value);
			case Result.Err(var error) -> fail(error);
		}
	}

	@Test
	public void additionWithSuffixesIsEvaluated() {
		var res = Executor.execute("1U8 + 2U8");
		switch (res) {
			case Result.Ok(var value) -> assertEquals("3", value);
			case Result.Err(var error) -> fail(error);
		}
	}

	@Test
	public void mismatchedSuffixesReturnErr() {
		var res = Executor.execute("1U8 + 2I16");
		switch (res) {
			case Result.Ok(var value) -> fail(value);
			case Result.Err(var error) -> assertEquals("Mismatched operand suffixes", error);
		}
	}
}
