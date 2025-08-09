package magma.control;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static magma.core.CompileAssert.assertInvalid;
import static magma.core.CompileAssert.assertValid;

/**
 * Tests for if statement implementation in the Magma compiler.
 * Verifies the conversion of if statements from Magma syntax to C syntax.
 */
public class IfStatementTest {

	/**
	 * Tests basic if statement with boolean literal condition.
	 * Tests that if statements with true/false literals are correctly compiled to C.
	 */
	@Test
	@DisplayName("Should compile basic if statement with boolean literal")
	public void shouldCompileBasicIfStatement() {
		// Basic if with true condition
		assertValid("if (true) { let x = 10; }", "if (true) { int32_t x = 10; }");

		// Basic if with false condition
		assertValid("if (false) { let x = 10; }", "if (false) { int32_t x = 10; }");
	}

	/**
	 * Tests if statement with boolean variable as condition.
	 * Verifies that boolean variables can be used as conditions in if statements.
	 */
	@Test
	@DisplayName("Should compile if statement with boolean variable as condition")
	public void shouldCompileIfWithBooleanVariable() {
		assertValid("let condition : Bool = true; if (condition) { let x = 10; }",
								"bool condition = true; if (condition) { int32_t x = 10; }");
	}

	/**
	 * Tests that parentheses and curly braces are required in if statements.
	 * Verifies that syntax errors are caught when required elements are missing.
	 */
	@Test
	@DisplayName("Should require parentheses and curly braces in if statements")
	public void shouldRequireParenthesesAndCurlyBraces() {
		// Missing parentheses
		assertInvalid("if true { let x = 10; }");

		// Missing opening curly brace
		assertInvalid("if (true) let x = 10; }");

		// Missing closing curly brace
		assertInvalid("if (true) { let x = 10;");

		// Missing both curly braces
		assertInvalid("if (true) let x = 10;");
	}

	/**
	 * Tests basic if-else statements.
	 * Verifies that if-else statements with boolean literals are correctly compiled to C.
	 */
	@Test
	@DisplayName("Should compile basic if-else statement")
	public void shouldCompileBasicIfElseStatement() {
		// If-else with true condition
		assertValid("if (true) { let x = 10; } else { let x = 20; }",
								"if (true) { int32_t x = 10; } else { int32_t x = 20; }");

		// If-else with false condition
		assertValid("if (false) { let x = 10; } else { let x = 20; }",
								"if (false) { int32_t x = 10; } else { int32_t x = 20; }");
	}

	/**
	 * Tests if-else statement with boolean variable as condition.
	 * Verifies that boolean variables can be used as conditions in if-else statements.
	 */
	@Test
	@DisplayName("Should compile if-else statement with boolean variable as condition")
	public void shouldCompileIfElseWithBooleanVariable() {
		assertValid("let condition : Bool = true; if (condition) { let x = 10; } else { let x = 20; }",
								"bool condition = true; if (condition) { int32_t x = 10; } else { int32_t x = 20; }");
	}

	/**
	 * Tests that curly braces are required in else statements.
	 * Verifies that syntax errors are caught when required elements are missing.
	 */
	@Test
	@DisplayName("Should require curly braces in else statements")
	public void shouldRequireCurlyBracesInElseStatements() {
		// Missing opening curly brace in else
		assertInvalid("if (true) { let x = 10; } else let x = 20; }");

		// Missing closing curly brace in else
		assertInvalid("if (true) { let x = 10; } else { let x = 20;");

		// Missing both curly braces in else
		assertInvalid("if (true) { let x = 10; } else let x = 20;");
	}
}