import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for char type support in the Magma compiler.
 * Tests that the compiler can handle char literals in single quotes and map them to U8 type.
 */
public class CharTypeTest {

	/**
	 * Test that the compiler can handle explicit char type declarations.
	 * This tests the support for "let x : Char = 'a';" syntax in our Java to C compiler.
	 */
	@Test
	public void testExplicitCharTypeDeclarations() {
		// Arrange
		String javaCode = """
				let a : Char = 'a';
				let b : Char = 'b';
				let c : Char = '\\n'; // Newline character
				let d : Char = '\\t'; // Tab character""";

		// Act
		String cCode = Main.compile(javaCode);

		// Assert
		assertNotNull(cCode, "Compiled C code should not be null");
		assertTrue(cCode.contains("#include <stdint.h>"), "C code should include stdint.h");
		assertTrue(cCode.contains("int main("), "C code should have a main function");
		assertTrue(cCode.contains("uint8_t a = 'a';"), "C code should map Char to uint8_t");
		assertTrue(cCode.contains("uint8_t b = 'b';"), "C code should map Char to uint8_t");
		assertTrue(cCode.contains("uint8_t c = '\\n';"), "C code should handle escape sequences");
		assertTrue(cCode.contains("uint8_t d = '\\t';"), "C code should handle escape sequences");
		assertTrue(cCode.contains("return 0;"), "C code should return 0");
	}

	/**
	 * Test that the compiler can infer char type from char literals in single quotes.
	 * This tests the support for "let x = 'a';" syntax in our Java to C compiler.
	 */
	@Test
	public void testInferCharTypeFromLiterals() {
		// Arrange
		String javaCode = """
				let a = 'a';
				let b = 'b';
				let c = '\\n'; // Newline character
				let d = '\\t'; // Tab character""";

		// Act
		String cCode = Main.compile(javaCode);

		// Assert
		assertNotNull(cCode, "Compiled C code should not be null");
		assertTrue(cCode.contains("#include <stdint.h>"), "C code should include stdint.h");
		assertTrue(cCode.contains("int main("), "C code should have a main function");
		assertTrue(cCode.contains("uint8_t a = 'a';"), "C code should infer Char type from 'a'");
		assertTrue(cCode.contains("uint8_t b = 'b';"), "C code should infer Char type from 'b'");
		assertTrue(cCode.contains("uint8_t c = '\\n';"), "C code should handle escape sequences");
		assertTrue(cCode.contains("uint8_t d = '\\t';"), "C code should handle escape sequences");
		assertTrue(cCode.contains("return 0;"), "C code should return 0");
	}
}