package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
// ...existing imports...
import static org.junit.jupiter.api.Assertions.fail;

import magma.Result.Ok;
import magma.Result.Err;

public class ExecutorTest {
	@Test
	public void emptyInputReturnsEmpty() {
		assertValid("", "");
	}

	@Test
	public void nonEmptyInputReturnsErr() {
		assertInvalid("data");
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
		assertInvalid("let x : U8 = 10I32; x");
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
		assertInvalid("let x = 10; let x = 10;");
	}

	@Test
	public void typedLetReferencingDifferentSuffixReturnsErr() {
		// let x has suffix U8, let y declares I32 and references x -> should error
		assertInvalid("let x = 10U8; let y : I32 = x;");
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
		assertInvalid("let x = 0; x = 10; x");
	}

	@Test
	public void declarationWithoutInitializerThenAssignReturnsValue() {
		// declare with type but no initializer, then assign and read
		assertValid("let x : I32; x = 10; x", "10");
	}

	@Test
	public void doubleAssignmentToDeclaredVarReturnsErr() {
		// declare without initializer, assign twice -> second assignment should error
		assertInvalid("let x : I32; x = 10; x = 20; x");
	}

	@Test
	public void mutableDeclarationAllowsMultipleAssignments() {
		// let mut without initializer should allow multiple assignments
		assertValid("let mut x : I32; x = 10; x = 20; x", "20");
	}

	@Test
	public void compoundAssignmentPlusEqualsUpdatesMutable() {
		// Test compound assignment x += 10 on a mutable binding
		assertValid("let mut x = 0; x += 10; x", "10");
	}

	@Test
	public void compoundAssignmentOnImmutableIsError() {
		// Using '+=' on an immutable binding should be an error
		assertInvalid("let x = 0; x += 10; x");
	}

	@Test
	public void lessThanComparisonAssignsBoolean() {
		// Comparison should evaluate to true and be stored in the binding
		assertValid("let x = 3 < 4; x", "true");
	}

	@Test
	public void whileLoopIncrementsMutableUntilConditionFalse() {
		// While loop should iterate and update mutable variable
		assertValid("let mut x = 0; while (x < 4) x += 1; x", "4");
	}

	@Test
	public void whileWithZeroConditionIsError() {
		// while condition must be boolean-like; numeric 0 is invalid here
		assertInvalid("while (0) {}");
	}

	@Test
	public void readingDeclaredButUninitializedIsInvalid() {
		// declaring without initializer then reading should be invalid
		assertInvalid("let x : I32; x");
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
		assertInvalid("let mut x = 0; let y : *mut I32 = &mut x; let z : &mut x;");
	}

	@Test
	public void mismatchedSuffixesReturnErr() {
		assertInvalid("1U8 + 2I16");
	}

	@Test
	public void bracedExpressionEvaluatesInner() {
		assertValid("{0}", "0");
	}

	@Test
	public void bracedSequenceEvaluatesStatements() {
		assertValid("{let x = 0; x}", "0");
	}

	@Test
	public void bracedSequenceThenFreeVariableIsError() {
		// A braced sequence that defines a local 'y' should not expose 'y' after the
		// block. Referencing 'y' after the block must be an error.
		assertInvalid("{let y = 10;} y");
	}

	@Test
	public void plusWithBracedOperandsEvaluates() {
		assertValid("{1} + {2}", "3");
	}

	@Test
	public void letThenBracedExpressionReadsVariable() {
		assertValid("let x = 3; {x}", "3");
	}

	@Test
	public void letWithBracedSequenceInitializerEvaluates() {
		assertValid("let x = {let y = 10; y}; x", "10");
	}

	@Test
	public void functionDefinitionAndCallReturnsValue() {
		// Define a simple function and call it
		assertValid("fn get() : I32 => { return 100; } get()", "100");
	}

	@Test
	public void functionDefinitionReturnWithoutSemicolonEvaluates() {
		// Function body uses 'return' without trailing semicolon inside braces
		assertValid("fn get() : I32 => { return 100 } get()", "100");
	}

	@Test
	public void functionWithParamPassesArgumentBack() {
		// Test a function that takes a parameter and returns it
		assertValid("fn pass(value : I32) => { return value; } pass(3 + 4)", "7");
	}

	@Test
	public void functionWithTwoParamsAddsThem() {
		// Test a function that takes two parameters and returns their sum
		assertValid("fn pass(first : I32, second : I32) => { return first + second; } pass(3, 4)", "7");
	}

	@Test
	public void multipleFunctionDefinitionsCallFirst() {
		// Define two functions and call the first one; expect its return value
		assertValid("fn first() : I32 => { return 100; } fn second() : I32 => { return 200; } first()",
				"100");
	}

	@Test
	public void functionReturnAssignedToDifferentTypeReturnsErr() {
		// Define a function that returns I32 then assign to a Bool-typed binding
		// Expect an error due to type mismatch
		assertInvalid("fn first() : I32 => 100; let x : Bool = first();");
	}

	@Test
	public void callingFunctionWithoutRequiredArgIsError() {
		// Define a single-parameter function but call it without args -> should be
		// error
		assertInvalid("fn first(value : I32) : I32 => { return value; } first()");
	}

	@Test
	public void callingFunctionWithWrongArgTypeIsError() {
		// Call function expecting I32 with a boolean literal -> should error
		assertInvalid("fn first(value : I32) : I32 => { return value; } first(true)");
	}

	@Test
	public void expressionBodiedFunctionWithSemicolonEvaluates() {
		// Expression-bodied function should return the expression value even when
		// terminated with a semicolon
		assertValid("fn get() : I32 => 100; get()", "100");
	}

	@Test
	public void functionWithGenericParamPassesArgumentBack() {
		// Support function definitions with generics in the name (e.g. fn pass<T>)
		assertValid("fn pass<T>(value : T) => value; pass(100)", "100");
	}

	@Test
	public void duplicateFunctionDefinitionsReturnError() {
		// Defining the same function twice should return a duplicate-binding error
		assertInvalid("fn first() : I32 => 100; fn first() : I32 => 100;");
	}

	private static void assertValid(String input, String expected) {
		switch (Executor.execute(input)) {
			case Ok(var value) -> assertEquals(expected, value);
			case Err(var error) -> fail(error);
		}
	}

	private static void assertInvalid(String input) {
		switch (Executor.execute(input)) {
			case Ok(var value) -> fail(value);
			case Err(var error) -> assertNotNull(error);
		}
	}
}
