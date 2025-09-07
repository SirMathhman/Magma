package magma;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InterpreterTest {
	@Test
	public void empty() {
		assertValid("", "");
	}

	@Test
	public void interpretFive() {
		assertValid("5", "5");
	}

	@Test
	public void interpretI32() {
		assertValid("5I32", "5");
	}

	@Test
	public void interpretI8() {
		assertValid("5I8", "5");
	}

	@Test
	public void interpretI16() {
		assertValid("5I16", "5");
	}

	@Test
	public void interpretI32_again() {
		assertValid("5I32", "5");
	}

	@Test
	public void interpretI64() {
		assertValid("5I64", "5");
	}

	@Test
	public void interpretU8() {
		assertValid("5U8", "5");
	}

	@Test
	public void interpretU16() {
		assertValid("5U16", "5");
	}

	@Test
	public void interpretU32() {
		assertValid("5U32", "5");
	}

	@Test
	public void interpretU64() {
		assertValid("5U64", "5");
	}

	@Test
	public void interpretAddition() {
		assertValid("2 + 3", "5");
	}

	@Test
	public void interpretTestThrows() {
		Interpreter interpreter = new Interpreter();
		Result<String, InterpretError> result = interpreter.interpret("test");
		if (result instanceof Err rawErr) {
			var e = rawErr.error();
			if (e instanceof InterpretError iie) {
				assertEquals("'test' is not a valid input", iie.display());
			} else {
				fail("Expected InterpretError inside Err, got: " + e.getClass().getSimpleName());
			}
		} else {
			fail("Expected Err but got: " + result.getClass().getSimpleName());
		}
	}

	private void assertValid(String input, String expected) {
		Interpreter interpreter = new Interpreter();
		Result<String, InterpretError> result = interpreter.interpret(input);
		if (result instanceof Ok ok) {
			assertEquals(expected, ok.value());
		} else {
			fail("Expected Ok but got: " + result.getClass().getSimpleName());
		}
	}
}
