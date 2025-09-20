package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
	public void letBindingWithAdditionAndSuffixesIsEvaluated() {
		// exercise a sequence with a let-binding and then referencing the variable
		assertValid("let x = 1U8 + 2U8; x", "3");
	}

	@Test
	public void typedLetBindingWithAdditionAndSuffixesIsEvaluated() {
		// allow an optional type annotation in the let-binding
		assertValid("let x : U8 = 1U8 + 2U8; x", "3");
	}

	@Test
	public void typedLetBindingWithMismatchedDeclaredTypeReturnsErr() {
		// declared type U8 does not match RHS suffix I32
		assertInvalid("let x : U8 = 10I32; x", "Declared type does not match expression suffix");
	}

	@Test
	public void letBindingWithoutReferenceReturnsEmpty() {
		// When the input is "let x = 10;" (no trailing reference), we expect an empty Ok
		switch (Executor.execute("let x = 10;")) {
			case Result.Ok(var value) -> assertEquals("", value);
			case Result.Err(var error) -> fail(error);
		}
	}

	@Test
	public void duplicateLetBindingsReturnError() {
		// Two let-bindings for the same identifier in the same input should return an error
		switch (Executor.execute("let x = 10; let x = 10;")) {
			case Result.Ok(var value) -> fail(value);
			case Result.Err(var error) -> assertEquals("Duplicate binding", error);
		}
	}

	@Test
	public void typedLetReferencingDifferentSuffixReturnsErr() {
		// let x has suffix U8, let y declares I32 and references x -> should error
		switch (Executor.execute("let x = 10U8; let y : I32 = x;")) {
			case Result.Ok(var value) -> fail(value);
			case Result.Err(var error) -> assertEquals("Declared type does not match expression suffix", error);
		}
	}

	@Test
	public void mismatchedSuffixesReturnErr() {
		assertInvalid("1U8 + 2I16", "Mismatched operand suffixes");
	}

	private static void assertValid(String input, String expected) {
		switch (Executor.execute(input)) {
			case Result.Ok(var value) -> assertEquals(expected, value);
			case Result.Err(var error) -> fail(error);
		}
	}

	private static void assertInvalid(String input, String expectedError) {
		switch (Executor.execute(input)) {
			case Result.Ok(var value) -> fail(value);
			case Result.Err(var error) -> assertNotNull(error);
		}
	}
}
