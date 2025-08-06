import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for the Main class.
 */
public class MainTest {
	/**
	 * Test the basic variable declaration with I32 type.
	 */
	@Test
	public void let() {
		// Test case: Magma variable declaration with I32 type
		assertEquals("int32_t value = 0;", Main.compile("let value : I32 = 0;"));
	}
	
	/**
	 * Test that declaration names are correctly preserved during compilation.
	 */
	@Test
	public void testDeclarationNames() {
		// Test with different variable names
		assertEquals("int32_t counter = 0;", Main.compile("let counter : I32 = 0;"));
		assertEquals("int32_t x = 0;", Main.compile("let x : I32 = 0;"));
		assertEquals("int32_t myVariable = 0;", Main.compile("let myVariable : I32 = 0;"));
		assertEquals("int32_t _temp = 0;", Main.compile("let _temp : I32 = 0;"));
		assertEquals("int32_t camelCaseVar = 0;", Main.compile("let camelCaseVar : I32 = 0;"));
	}
}