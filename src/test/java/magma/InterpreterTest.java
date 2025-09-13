package magma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import magma.Result.Err;
import magma.Result.Ok;

public class InterpreterTest {
	@Test
	public void emptyInputReturns() {
		assertSuccess(new Interpreter().interpret(""), "");
	}

	@Test
	public void nonEmptyInputIsError() {
		assertError(new Interpreter().interpret("test"));
	}

	@Test
	public void numericReturns() {
		assertSuccess(new Interpreter().interpret("5"), "5");
	}

	@Test
	public void numWithSuffReturns() {
		// Expect that an input with trailing letters keeps the numeric prefix only
		assertSuccess(new Interpreter().interpret("5U8"), "5");
	}

	@Test
	public void addNumbersReturnsSum() {
		// Simple addition expression with spaces should evaluate to the numeric sum
		assertSuccess(new Interpreter().interpret("1 + 2"), "3");
	}

	@Test
	public void addPrefOperandSum() {
		// Left operand has a suffix; interpreter should use leading numeric prefix
		// ("1U8" -> "1")
		assertSuccess(new Interpreter().interpret("1U8 + 2"), "3");
	}

	@Test
	public void addBothSuffError() {
		// Both operands have suffixes; this should be considered invalid by the
		// interpreter
		assertError(new Interpreter().interpret("1U8 + 2U16"));
	}

	@Test
	public void addSameSuffReturns() {
		// Both operands have the same suffix; interpreter should use leading numeric
		// prefixes
		assertSuccess(new Interpreter().interpret("1U8 + 2U8"), "3");
	}

	@Test
	public void addThreeReturns() {
		// Multiple additions should sum left-to-right (or as a set) and return the
		// total
		assertSuccess(new Interpreter().interpret("1 + 2 + 3"), "6");
	}

	@Test
	public void subTwoReturns() {
		// Simple subtraction should compute the difference
		assertSuccess(new Interpreter().interpret("3 - 2"), "1");
	}

	@Test
	public void subThreeReturns() {
		// Left-associative subtraction: (4 - 2) - 1 == 1
		assertSuccess(new Interpreter().interpret("4 - 2 - 1"), "1");
	}

	@Test
	public void mixedSubAddReturns() {
		// Mixed operations without precedence: (4 - 2) + 1 == 3 using left-to-right
		assertSuccess(new Interpreter().interpret("4 - 2 + 1"), "3");
	}

	@Test
	public void mulTwoReturns() {
		// Simple multiplication should compute the product
		assertSuccess(new Interpreter().interpret("3 * 5"), "15");
	}

	@Test
	public void divTwoReturns() {
		// Simple integer division should compute the quotient
		assertSuccess(new Interpreter().interpret("10 / 2"), "5");
	}

	@Test
	public void parenSingleReturns() {
		// Parentheses around a single literal should evaluate to the inner value
		assertSuccess(new Interpreter().interpret("(1)"), "1");
	}

	// Helper to assert a successful interpretation with expected value
	private void assertSuccess(Result<String, InterpreterError> result, String expected) {
		switch (result) {
			case Err<String, InterpreterError>(InterpreterError error) -> fail(error.display());
			case Ok<String, InterpreterError>(String value) -> assertEquals(expected, value);
		}
	}

	// Helper to assert an error result
	private void assertError(Result<String, InterpreterError> result) {
		switch (result) {
			case Ok<String, InterpreterError>(String value) -> fail("expected error but got success: " + value);
			case Err<String, InterpreterError>(InterpreterError error) -> assertNotNull(error);
		}
	}
}
