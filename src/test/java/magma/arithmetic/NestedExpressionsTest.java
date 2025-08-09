package magma.arithmetic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static magma.core.CompileAssert.assertInvalid;
import static magma.core.CompileAssert.assertValid;

/**
 * Tests for nested expressions in the Magma compiler.
 * Verifies that the compiler correctly handles expressions with parentheses and nested operations.
 */
public class NestedExpressionsTest {

	/**
	 * Tests nested boolean operations with parentheses.
	 * Verifies that expressions like (true && false) || false are correctly parsed and compiled.
	 */
	@Test
	@DisplayName("Should support nested boolean operations with parentheses")
	public void shouldSupportNestedBooleanOperations() {
		// Test basic nested boolean operation with AND inside OR
		assertValid("let x : Bool = (true && false) || true;", "bool x = (true && false) || true;");

		// Test nested boolean operation with OR inside AND
		assertValid("let x : Bool = true && (false || true);", "bool x = true && (false || true);");

		// Test multi-level nesting
		assertValid("let x : Bool = (true && (false || true)) || false;", "bool x = (true && (false || true)) || false;");

		// Test with boolean variables
		assertValid("let a : Bool = true; let b : Bool = false; let c : Bool = (a && b) || a;",
								"bool a = true; bool b = false; bool c = (a && b) || a;");
	}

	/**
	 * Tests that type checking works correctly with nested boolean operations.
	 * Verifies that all operands in nested boolean expressions must be of Bool type.
	 */
	@Test
	@DisplayName("Should enforce type checking in nested boolean operations")
	public void shouldEnforceTypeCheckingInNestedOperations() {
		// Test with non-Bool type in nested expression (should fail)
		assertInvalid("let num : I32 = 1; let x : Bool = (true && num) || false;");
		assertInvalid("let num : I32 = 1; let x : Bool = true && (num || false);");
	}
}