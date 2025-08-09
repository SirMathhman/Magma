package magma.arithmetic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static magma.core.CompileAssert.assertInvalid;
import static magma.core.CompileAssert.assertValid;

/**
 * Simplified tests for type checking in arithmetic expressions.
 * These tests focus on understanding how literals and variables of different types interact.
 */
public class ArithmeticTypeCheckSimpleTest {

	/**
	 * Tests simple type checking scenarios to understand the baseline behavior.
	 */
	@Test
	@DisplayName("Should enforce type checking in simple expressions")
	public void shouldEnforceTypeCheckingInSimpleExpressions() {
		// Different variable types - should fail
		assertInvalid("let a : I16 = 3; let b : I32 = 4; let c = a + b;");

		// Same variable types - should pass
		assertValid("let a : I16 = 3; let b : I16 = 4; let c : I16 = a + b;",
								"int16_t a = 3; int16_t b = 4; int16_t c = a + b;");

		// Variable and literal - should work
		assertValid("let a : I16 = 3; let c : I16 = a + 2;", "int16_t a = 3; int16_t c = a + 2;");
	}

	/**
	 * Tests slightly more complex expressions to isolate the issue.
	 */
	@Test
	@DisplayName("Should enforce type checking in slightly nested expressions")
	public void shouldEnforceTypeCheckingInSlightlyNestedExpressions() {
		// Variable, literal, and parentheses - should work
		assertValid("let a : I16 = 3; let c : I16 = a + (2 + 3);", "int16_t a = 3; int16_t c = a + (2 + 3);");

		// Different types with parentheses - should fail
		assertInvalid("let a : I16 = 3; let b : I32 = 4; let c = a + (b + 2);");

		// Mixed operation with same types - should pass
		assertValid("let a : I16 = 3; let b : I16 = 4; let c : I16 = a + (b * 2);",
								"int16_t a = 3; int16_t b = 4; int16_t c = a + (b * 2);");
	}
}