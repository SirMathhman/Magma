package magma;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static magma.CompileAssert.assertInvalid;
import static magma.CompileAssert.assertValid;

/**
 * Tests for while statement implementation in the Magma compiler.
 * Verifies the conversion of while statements from Magma syntax to C syntax.
 */
public class WhileStatementTest {

	/**
	 * Tests basic while statement with boolean literal condition.
	 * Tests that while statements with true/false literals are correctly compiled to C.
	 */
	@Test
	@DisplayName("Should compile basic while statement with boolean literal")
	public void shouldCompileBasicWhileStatement() {
		// Basic while with true condition
		assertValid("while (true) { let x = 10; }", "while (true) { int32_t x = 10; }");

		// Basic while with false condition
		assertValid("while (false) { let x = 10; }", "while (false) { int32_t x = 10; }");
	}

	/**
	 * Tests while statement with boolean variable as condition.
	 * Verifies that boolean variables can be used as conditions in while statements.
	 */
	@Test
	@DisplayName("Should compile while statement with boolean variable as condition")
	public void shouldCompileWhileWithBooleanVariable() {
		assertValid("let condition : Bool = true; while (condition) { let x = 10; }",
								"bool condition = true; while (condition) { int32_t x = 10; }");
	}

	/**
	 * Tests that parentheses and curly braces are required in while statements.
	 * Verifies that syntax errors are caught when required elements are missing.
	 */
	@Test
	@DisplayName("Should require parentheses and curly braces in while statements")
	public void shouldRequireParenthesesAndCurlyBraces() {
		// Missing parentheses
		assertInvalid("while true { let x = 10; }");

		// Missing opening curly brace
		assertInvalid("while (true) let x = 10; }");

		// Missing closing curly brace
		assertInvalid("while (true) { let x = 10;");

		// Missing both curly braces
		assertInvalid("while (true) let x = 10;");
	}
}