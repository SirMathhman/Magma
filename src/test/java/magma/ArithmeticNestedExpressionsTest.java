package magma;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static magma.CompileAssert.assertInvalid;
import static magma.CompileAssert.assertValid;

/**
 * Tests for arbitrarily nested arithmetic expressions in the Magma compiler.
 * This class specifically tests the ability to nest arithmetic operations with parentheses
 * to ensure proper precedence and evaluation.
 */
public class ArithmeticNestedExpressionsTest {

	/**
	 * Tests the exact case mentioned in the issue description.
	 * Verifies that expressions like (3 * (2 + 1)) - 4 are correctly parsed and compiled.
	 */
	@Test
	@DisplayName("Should support the issue example: (3 * (2 + 1)) - 4")
	public void shouldSupportIssueExample() {
		assertValid("let x = (3 * (2 + 1)) - 4;", "int32_t x = (3 * (2 + 1)) - 4;");
	}

	/**
	 * Tests deeply nested arithmetic expressions with multiple levels of parentheses.
	 * Verifies that expressions with deep nesting are correctly parsed and compiled.
	 */
	@Test
	@DisplayName("Should support deeply nested arithmetic expressions")
	public void shouldSupportDeeplyNestedExpressions() {
		// Three levels of nesting
		assertValid("let x = (2 * (3 + (4 - 1)));", "int32_t x = (2 * (3 + (4 - 1)));");

		// Four levels of nesting
		assertValid("let x = (7 - (2 * (3 + (4 - 1))));", "int32_t x = (7 - (2 * (3 + (4 - 1))));");
	}

	/**
	 * Tests mixed operators with nested parentheses.
	 * Verifies that expressions combining different operators with nested parentheses
	 * are correctly parsed and compiled.
	 */
	@Test
	@DisplayName("Should support mixed operators with nested parentheses")
	public void shouldSupportMixedOperatorsWithNestedParentheses() {
		// Mix of +, -, * with parentheses
		assertValid("let x = (3 + 4) * (7 - 2);", "int32_t x = (3 + 4) * (7 - 2);");

		// Complex expression with all operators
		assertValid("let x = ((3 + 4) * 2) - ((7 - 2) * 3);", "int32_t x = ((3 + 4) * 2) - ((7 - 2) * 3);");
	}

	/**
	 * Tests variable references inside nested expressions.
	 * Verifies that expressions with variables inside nested parentheses
	 * are correctly parsed and compiled.
	 */
	@Test
	@DisplayName("Should support variable references inside nested expressions")
	public void shouldSupportVariableReferencesInsideNestedExpressions() {
		// Variables inside nested parentheses
		assertValid("let a = 3; let b = 4; let c = (a * (b + 2));",
								"int32_t a = 3; int32_t b = 4; int32_t c = (a * (b + 2));");

		// Complex expression with variables
		assertValid("let a = 3; let b = 4; let c = 5; let d = ((a + b) * (c - 1));",
								"int32_t a = 3; int32_t b = 4; int32_t c = 5; int32_t d = ((a + b) * (c - 1));");
	}

	/**
	 * Tests type checking in nested expressions.
	 * Verifies that type compatibility is enforced even in complex nested expressions.
	 */
	@Test
	@DisplayName("Should enforce type checking in nested expressions")
	public void shouldEnforceTypeCheckingInNestedExpressions() {
		// Different types in nested expression
		assertInvalid("let a : I16 = 3; let b : I32 = 4; let c = (a * (b + 2));");

		// Same types in nested expression
		assertValid("let a : I16 = 3; let b : I16 = 4; let c : I16 = (a * (b + 2));",
								"int16_t a = 3; int16_t b = 4; int16_t c = (a * (b + 2));");
	}
}