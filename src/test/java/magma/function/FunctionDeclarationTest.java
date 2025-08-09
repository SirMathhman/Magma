package magma.function;

import org.junit.jupiter.api.Test;

import static magma.core.CompileAssert.assertInvalid;
import static magma.core.CompileAssert.assertValid;

/**
 * Tests for function declaration syntax in Magma.
 */
public class FunctionDeclarationTest {

	@Test
	public void testEmptyFunctionDeclaration() {
		// Test the basic empty function declaration syntax
		assertValid("fn empty() : Void => {}", "void empty(){}");
	}

	@Test
	public void testFunctionDeclarationWithoutReturnType() {
		// Function declarations without return type should default to Void
		assertValid("fn noReturn() => {}", "void noReturn(){}");
	}

	@Test
	public void testFunctionDeclarationWithoutBody() {
		// Function declarations must have a body
		assertInvalid("fn noBody() : Void =>");
	}

	@Test
	public void testFunctionDeclarationWithoutArrow() {
		// Function declarations must include the arrow syntax
		assertInvalid("fn noArrow() : Void {}");
	}

	@Test
	public void testFunctionDeclarationWithInvalidName() {
		// Function names must be valid identifiers
		assertInvalid("fn 123invalid() : Void => {}");
	}

	@Test
	public void testFunctionDeclarationWithI16ReturnType() {
		// Test function with I16 return type and a return statement
		String input = "fn simple() : I16 => {return 0;}";
		String expected = "int16_t simple(){return 0;}";
		assertValid(input, expected);
	}
}