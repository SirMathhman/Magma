import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for the Main class.
 */
public class MainTest {
	/**
	 * Test the processString method in the Main class.
	 */
	@Test
	public void let() {
		// Test case 4: Magma variable declaration with I32 type
		assertEquals("int32_t value = 0;", Main.compile("let value : I32 = 0;"));
	}
}