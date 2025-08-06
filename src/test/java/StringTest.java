import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for string handling in the Magma compiler.
 * Tests that strings are treated as arrays of U8 with a fixed size.
 */
public class StringTest {

	/**
	 * Test that the compiler can handle explicit string declarations as arrays of U8.
	 * This tests the support for "let myString : [U8; 5] = "hello";" syntax in our Magma to C compiler.
	 */
	@Test
	public void testExplicitStringDeclaration() {
		// Arrange
		String magmaCode = "let myString : [U8; 5] = \"hello\";";

		// Act
		String cCode = Main.compile(magmaCode);

		// Assert
		String expectedCode = """
				#include <stdint.h>
				
				int main() {
				    uint8_t myString[5] = {'h', 'e', 'l', 'l', 'o'};
				    return 0;
				}""";

		assertEquals(expectedCode, cCode, "C code should match expected output");
	}

	/**
	 * Test that the compiler can handle string literals with the new syntax.
	 * This tests the support for "let myString : [U8; 5] = "hello";" syntax in our Magma to C compiler.
	 */
	@Test
	public void testExplicitStringDeclarationNewSyntax() {
		// Arrange
		String magmaCode = "let myString : [U8; 5] = \"hello\";";

		// Act
		String cCode = Main.compile(magmaCode);

		// Assert
		String expectedCode = """
				#include <stdint.h>
				
				int main() {
				    uint8_t myString[5] = {'h', 'e', 'l', 'l', 'o'};
				    return 0;
				}""";

		assertEquals(expectedCode, cCode, "C code should match expected output");
	}

	/**
	 * Test that the compiler can handle multiple string declarations.
	 */
	@Test
	public void testMultipleStringDeclarations() {
		// Arrange
		String magmaCode = """
				let greeting : [U8; 5] = "hello";
				let name : [U8; 5] = "world";
				let empty : [U8; 0] = "";
				let special : [U8; 4] = "a\\nb\\t";""";

		// Act
		String cCode = Main.compile(magmaCode);

		// Assert
		String expectedCode = """
				#include <stdint.h>
				
				int main() {
				    uint8_t greeting[5] = {'h', 'e', 'l', 'l', 'o'};
				    uint8_t name[5] = {'w', 'o', 'r', 'l', 'd'};
				    uint8_t empty[0] = {};
				    uint8_t special[4] = {'a', '\\n', 'b', '\\t'};
				    return 0;
				}""";

		assertEquals(expectedCode, cCode, "C code should match expected output");
	}
}