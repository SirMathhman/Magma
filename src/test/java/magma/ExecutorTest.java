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
		// When the input is "let x = 10;" (no trailing reference), we expect an empty
		// Ok
		assertValid("let x = 10;", "");
	}

	@Test
	public void duplicateLetBindingsReturnError() {
		// Two let-bindings for the same identifier in the same input should return an
		// error
		assertInvalid("let x = 10; let x = 10;", "Duplicate binding");
	}

	@Test
	public void typedLetReferencingDifferentSuffixReturnsErr() {
		// let x has suffix U8, let y declares I32 and references x -> should error
		assertInvalid("let x = 10U8; let y : I32 = x;", "Declared type does not match expression suffix");
	}

	@Test
	public void pointerDereferenceReturnsValue() {
		// simple pointer: let x = 10; let y : *I32 = &x; *y -> 10
		assertValid("let x = 10; let y : *I32 = &x; *y", "10");
	}

	@Test
	public void mutableAssignmentReturnsNewValue() {
		// mutable variable: let mut x = 0; x = 10; x -> 10
		assertValid("let mut x = 0; x = 10; x", "10");
	}

	@Test
	public void assigningToImmutableBindingReturnsErr() {
		// assigning to immutable binding should be an error
		assertInvalid("let x = 0; x = 10; x", "Non-empty input not allowed");
	}

	@Test
	public void declarationWithoutInitializerThenAssignReturnsValue() {
		// declare with type but no initializer, then assign and read
		assertValid("let x : I32; x = 10; x", "10");
	}

	@Test
	public void doubleAssignmentToDeclaredVarReturnsErr() {
		// declare without initializer, assign twice -> second assignment should error
		assertInvalid("let x : I32; x = 10; x = 20; x", "Non-empty input not allowed");
	}

	@Test
	public void mutableDeclarationAllowsMultipleAssignments() {
		// let mut without initializer should allow multiple assignments
		assertValid("let mut x : I32; x = 10; x = 20; x", "20");
	}

	@Test
	public void readingDeclaredButUninitializedIsInvalid() {
		// declaring without initializer then reading should be invalid
		assertInvalid("let x : I32; x", "Non-empty input not allowed");
	}

	@Test
	public void booleanLiteralAssignmentAndRead() {
		assertValid("let x = true; x", "true");
	}

	@Test
	public void declaredBoolInitializerReturnsTrue() {
		assertValid("let x : Bool = true; x", "true");
	}

	@Test
	public void simpleIfExpressionEvaluates() {
		assertValid("if (true) 3 else 5", "3");
	}

	@Test
	public void booleanIfExpressionEvaluatesToTrue() {
		assertValid("if (true) true else false", "true");
	}

	@Test
	public void letWithIfInitializerAssignsCorrectly() {
		assertValid("let x = if (true) true else false; x", "true");
	}

	@Test
	public void assignThroughMutableReferenceUpdatesPointee() {
		// let mut x = 0; let y : *mut I32 = &mut x; y = 10; x => 10
		assertValid("let mut x = 0; let y : *mut I32 = &mut x; y = 10; x", "10");
	}

	@Test
	public void invalidAmpersandInDeclaredTypeErrors() {
		assertInvalid("let mut x = 0; let y : *mut I32 = &mut x; let z : &mut x;", "Non-empty input not allowed");
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
