import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for string handling in the Magma compiler.
 * Tests that strings are treated as arrays of U8 with a fixed size.
 * Includes tests for edge cases and special characters to ensure robustness.
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
	 * Test that the compiler can handle strings with all common escape sequences.
	 * Tests the support for \n, \t, \r, \', \", and \\ escape sequences.
	 */
	@Test
	public void testStringWithAllEscapeSequences() {
		// Arrange
		String magmaCode = "let escapes : [U8; 6] = \"\\n\\t\\r\\'\\\"\\\\\";";;

		// Act
		String cCode = Main.compile(magmaCode);

		// Assert
		String expectedCode = """
				#include <stdint.h>
				
				int main() {
				    uint8_t escapes[6] = {'\\n', '\\t', '\\r', '\\'', '\\"', '\\\\'};
				    return 0;
				}""";

		assertEquals(expectedCode, cCode, "C code should match expected output for escape sequences");
	}
	
	/**
	 * Test that the compiler can handle a very long string.
	 * Tests the support for strings with many characters.
	 */
	@Test
	public void testVeryLongString() {
		// Arrange
		String magmaCode = "let longString : [U8; 50] = \"This is a very long string to test the compiler's handling\";";

		// Act
		String cCode = Main.compile(magmaCode);

		// Assert
		String expectedCode = """
				#include <stdint.h>
				
				int main() {
				    uint8_t longString[50] = {'T', 'h', 'i', 's', ' ', 'i', 's', ' ', 'a', ' ', 'v', 'e', 'r', 'y', ' ', 'l', 'o', 'n', 'g', ' ', 's', 't', 'r', 'i', 'n', 'g', ' ', 't', 'o', ' ', 't', 'e', 's', 't', ' ', 't', 'h', 'e', ' ', 'c', 'o', 'm', 'p', 'i', 'l', 'e', 'r', '\\''', 's', ' ', 'h', 'a', 'n', 'd', 'l', 'i', 'n', 'g'};
				    return 0;
				}""";

		assertEquals(expectedCode, cCode, "C code should match expected output for very long string");
	}
	
	/**
	 * Test that the compiler can handle strings with special characters.
	 * Tests the support for non-alphanumeric characters.
	 */
	@Test
	public void testStringWithSpecialCharacters() {
		// Arrange
		String magmaCode = "let special : [U8; 10] = \"!@#$%^&*()\";";

		// Act
		String cCode = Main.compile(magmaCode);

		// Assert
		String expectedCode = """
				#include <stdint.h>
				
				int main() {
				    uint8_t special[10] = {'!', '@', '#', '$', '%', '^', '&', '*', '(', ')'};
				    return 0;
				}""";

		assertEquals(expectedCode, cCode, "C code should match expected output for string with special characters");
	}
	
	/**
	 * Test that the compiler can handle strings with mixed regular characters and escape sequences.
	 * Tests the support for strings that combine normal characters with escape sequences.
	 */
	@Test
	public void testStringWithMixedContent() {
		// Arrange
		String magmaCode = "let mixed : [U8; 11] = \"Hello\\nWorld\\t!\";";

		// Act
		String cCode = Main.compile(magmaCode);

		// Assert
		String expectedCode = """
				#include <stdint.h>
				
				int main() {
				    uint8_t mixed[11] = {'H', 'e', 'l', 'l', 'o', '\\n', 'W', 'o', 'r', 'l', 'd', '\\t', '!'};
				    return 0;
				}""";

		assertEquals(expectedCode, cCode, "C code should match expected output for string with mixed content");
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