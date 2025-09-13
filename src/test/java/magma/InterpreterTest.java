package magma;

import magma.Result.Err;
import magma.Result.Ok;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InterpreterTest {
	@Test
	public void emptyInputReturns() {
		switch (new Interpreter().interpret("")) {
			case Err<String, InterpreterError>(InterpreterError error) -> fail("expected success but got error: " + error);
			case Ok<String, InterpreterError>(String value) -> {
				assertEquals("", value);
			}
		}
	}

	@Test
	public void nonEmptyInputIsError() {
		switch (new Interpreter().interpret("test")) {
			case Ok<String, InterpreterError>(String value) -> fail("expected error but got success: " + value);
			case Err<String, InterpreterError>(InterpreterError error) -> {
				assertNotNull(error);
			}
		}
	}

	@Test
	public void numericReturns() {
		switch (new Interpreter().interpret("5")) {
			case Err<String, InterpreterError>(InterpreterError error) -> fail("expected success but got error: " + error);
			case Ok<String, InterpreterError>(String value) -> {
				assertEquals("5", value);
			}
		}
	}
}
