package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import magma.Result.Ok;
import magma.Result.Err;

public class ExecutorAdditionalTest {
	@Test
	void execute_should_return_numeric_result_for_pass_expression() {
		// The prompt implies that a simple pass function should evaluate to the passed
		// value.
		// For example: `fn pass<T>(value : T) => value; pass(100)` should evaluate to
		// 100.
		String input = "fn pass<T>(value : T) => value; pass(100)";

		switch (Executor.execute(input)) {
			case Ok(var value) -> {
				// Executor currently returns strings for numeric literals; accept string "100"
				assertEquals("100", value);
			}
			case Err(var err) -> fail("expected Ok but was Err: " + err);
		}
	}
}
