package magma;

import org.junit.jupiter.api.Test;

// ...existing assertions are provided by TestUtils; no direct static imports needed

public class InterpreterFeatureTest {
	@Test
	public void simpleAddition() {
		TestUtils.assertValid("1 + 2", "3");
	}

	@Test
	public void literalInteger() {
		TestUtils.assertValid("5", "5");
	}

	@Test
	public void blockExprReturns5() {
		// New acceptance: a block expression with a single expression should return
		// that expression's value
		TestUtils.assertValid("{5}", "5");
	}

	@Test
	public void typedAddition() {
		TestUtils.assertValid("1U8 + 2U8", "3");
	}

	@Test
	public void typedUntypedAdd() {
		// New acceptance: untyped literal can be added to a typed literal if it fits
		// the width
		TestUtils.assertValid("1U8 + 2", "3");
	}

	@Test
	public void untypedTypedAdd() {
		// Symmetric acceptance: typed operand may be on the right-hand side
		TestUtils.assertValid("1 + 2U8", "3");
	}

	@Test
	public void letBindingLookup() {
		// New feature: let binding with type annotation and subsequent lookup
		TestUtils.assertValid("let x : I32 = 3; x", "3");
	}

	@Test
	public void letExprReturns5() {
		// let with RHS expression and subsequent lookup should evaluate the expression
		TestUtils.assertValid("let x = 3 + 2; x", "5");
	}

	@Test
	public void letMutAssign100() {
		// Mutable let should allow assignment and subsequent lookup should reflect the
		// new value
		TestUtils.assertValid("let mut x = 0; x = 100; x", "100");
	}

	@Test
	public void assignImmutableErr() {
		// Assigning to an immutable let should be invalid
		TestUtils.assertInvalid("let x = 0; x = 100; x");
	}

	@Test
	public void mismatchedTypedErr() {
		// Mixed unsigned/signed width should be invalid per new acceptance criteria
		TestUtils.assertInvalid("1U8 + 2I32");
	}

	@Test
	public void letTypedMismatchErr() {
		// Assigning a typed literal with different signedness/width to a typed let
		// should be invalid
		TestUtils.assertInvalid("let x : U8 = 3I32; x");
	}

	@Test
	public void letOnlyReturnsEmpty() {
		// A program that contains only a let-binding (no final expression) should
		// return an empty string
		TestUtils.assertValid("let x : U8 = 3;", "");
	}

	@Test
	public void mutLetTypedMismatch() {
		// Even for mutable lets, assigning a typed RHS literal with a different suffix
		// should be invalid
		TestUtils.assertInvalid("let mut x : U8 = 0; x = 100I32; x");
	}

	@Test
	public void declareThenAssign() {
		// Declaration with annotation but no initializer followed by assignment should
		// be allowed
		TestUtils.assertValid("let x : I32; x = 100; x", "100");
	}

	@Test
	public void declareReadInvalid() {
		// Declaration with annotation but no initializer followed by a read should be
		// invalid
		TestUtils.assertInvalid("let x : I32; x");
	}

	@Test
	public void literalTrue() {
		// Boolean literal true should be accepted and returned as "true"
		TestUtils.assertValid("true", "true");
	}

	@Test
	public void ifAssigns() {
		TestUtils.assertValid("let x : I32; if (true) x = 3 else x = 5; x", "3");
	}

	@Test
	public void ifAssignsWithBlocks() {
		TestUtils.assertValid("let x : I32; if (true) {x = 3;} else {x = 5;} x", "3");
	}

	@Test
	public void compAssign() {
		// New acceptance: compound assignment should work for mutable numeric vars
		TestUtils.assertValid("let mut x = 0; x += 1; x", "1");
	}

	@Test
	public void compAssignImmErr() {
		// Using '+=' on an immutable let should be invalid
		TestUtils.assertInvalid("let x = 0; x += 1; x");
	}

	@Test
	public void compAssignBoolErr() {
		// Using '+=' on a Bool-initialized variable should be invalid
		TestUtils.assertInvalid("let mut x = true; x += 1; x");
	}

	@Test
	public void whileLoopSum() {
		// New acceptance: while loop with mutable vars and compound assignment
		String src = "let mut sum = 0; let mut counter = 0; while (counter < 4) { sum += counter; counter += 1; } sum";
		TestUtils.assertValid(src, "6");
	}

	@Test
	public void fnReturnAndCall() {
		String src = "fn get() : I32 => { return 100; } get()";
		TestUtils.assertValid(src, "100");
	}

	@Test
	public void fnNestedCall() {
		String src = "fn get() : I32 => { return 100; } fn getAnother() : I32 => { return get(); } getAnother()";
		TestUtils.assertValid(src, "100");
	}

	@Test
	public void fnParamCall() {
		String src = "fn pass(param : I32) : I32 => { return param; } pass(100)";
		TestUtils.assertValid(src, "100");
	}

	@Test
	public void fnTwoParamCall() {
		// Expect the function to accept two parameters and return the first
		String src = "fn first(a : I32, b : I32) : I32 => { return a; } first(100, 200)";
		TestUtils.assertValid(src, "100");
	}

	@Test
	public void fnOmitReturnType() {
		String src = "fn get() => { return 100; } get()";
		TestUtils.assertValid(src, "100");
	}

	@Test
	public void fnNoBraceBody() {
		// Compact form: no braces around the body, single return expression
		String src = "fn get() => return 100; get()";
		TestUtils.assertValid(src, "100");
	}

	@Test
	public void fnCompactNoRet() {
		// New acceptance: compact form without the 'return' keyword
		String src = "fn get() => 100; get()";
		TestUtils.assertValid(src, "100");
	}

	@Test
	public void fnDupParamInvalid() {
		// Invalid case: duplicate parameter names should be rejected
		String src = "fn first(a : I32, a : I32) : I32 => { return first; } first(100, 200)";
		TestUtils.assertInvalid(src);
	}

	@Test
	public void fnWrongArityInvalid() {
		// Invalid case: calling a 1-arg function with zero args should be rejected
		String src = "fn pass(param : I32) : I32 => { return param; } pass()";
		TestUtils.assertInvalid(src);
	}

	@Test
	public void fnArgTypeMismatch() {
		// Invalid case: passing a boolean literal to a function expecting I32
		String src = "fn pass(param : I32) : I32 => { return param; } pass(true)";
		TestUtils.assertInvalid(src);
	}
}
