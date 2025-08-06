import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test class for char type support in the Magma compiler.
 * Tests that the compiler can handle char literals in single quotes and map them to U8 type.
 */
public class CharTypeTest {

	/**
	 * Test that the compiler can handle explicit char type declarations.
	 * This tests the support for "let x : U8 = 'a';" syntax in our Java to C compiler.
	 */
	@Test
	public void testExplicitCharTypeDeclarations() {
		// Arrange
		String javaCode = """
				let a : U8 = 'a';
				let b : U8 = 'b';
				let c : U8 = '\\n'; // Newline character
				let d : U8 = '\\t'; // Tab character""";

		// Act
		String cCode = Main.compile(javaCode);

		// Assert
		assertNotNull(cCode, "Compiled C code should not be null");
		
		String expectedCode = """
				#include <stdint.h>

				int main() {
				    uint8_t a = 'a';
				    uint8_t b = 'b';
				    uint8_t c = '\\n';
				    uint8_t d = '\\t';
				    return 0;
				}""";
		
		assertEquals(expectedCode, cCode, "C code should match expected output");
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
		
		String expectedCode = """
				#include <stdint.h>

				int main() {
				    uint8_t a = 'a';
				    uint8_t b = 'b';
				    uint8_t c = '\\n';
				    uint8_t d = '\\t';
				    return 0;
				}""";
		
		assertEquals(expectedCode, cCode, "C code should match expected output");
	}
}