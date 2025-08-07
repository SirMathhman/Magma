import org.junit.jupiter.api.Test;

/**
 * Test class for char type support in the Magma compiler.
 * Tests that the compiler can handle char literals in single quotes and map them to U8 type.
 * Includes tests for edge cases, escape sequences, and special characters to ensure robustness.
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

		// Assert
		String expectedCode = """
				#include <stdint.h>

				int main() {
				    uint8_t a = 'a';
				    uint8_t b = 'b';
				    uint8_t c = '\\n';
				    uint8_t d = '\\t';
				    return 0;
				}""";
		
		TestUtil.assertCompiles(javaCode, expectedCode, "C code should match expected output");
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

		// Assert
		String expectedCode = """
				#include <stdint.h>

				int main() {
				    uint8_t a = 'a';
				    uint8_t b = 'b';
				    uint8_t c = '\\n';
				    uint8_t d = '\\t';
				    return 0;
				}""";
		
		TestUtil.assertCompiles(javaCode, expectedCode, "C code should match expected output");
	}
	
	/**
	 * Test that the compiler can handle all common escape sequences in char literals.
	 * Tests the support for \r, \', \", \\, and \0 escape sequences.
	 */
	@Test
	public void testAllEscapeSequences() {
		// Arrange
		String javaCode = """
				let cr : U8 = '\\r';    // Carriage return
				let sq : U8 = '\\'';    // Single quote
				let dq : U8 = '\\"';    // Double quote
				let bs : U8 = '\\\\';   // Backslash
				let nl : U8 = '\\0';    // Null character""";

		// Assert
		String expectedCode = """
				#include <stdint.h>

				int main() {
				    uint8_t cr = '\\r';
				    uint8_t sq = '\\'';
				    uint8_t dq = '\\"';
				    uint8_t bs = '\\\\';
				    uint8_t nl = '\\0';
				    return 0;
				}""";
		
		TestUtil.assertCompiles(javaCode, expectedCode, "C code should match expected output for all escape sequences");
	}
	
	/**
	 * Test that the compiler can handle special characters in char literals.
	 * Tests the support for non-alphanumeric characters.
	 */
	@Test
	public void testSpecialCharacters() {
		// Arrange
		String javaCode = """
				let excl : U8 = '!';
				let at : U8 = '@';
				let hash : U8 = '#';
				let dollar : U8 = '$';
				let percent : U8 = '%';
				let caret : U8 = '^';
				let amp : U8 = '&';
				let star : U8 = '*';""";

		// Assert
		String expectedCode = """
				#include <stdint.h>

				int main() {
				    uint8_t excl = '!';
				    uint8_t at = '@';
				    uint8_t hash = '#';
				    uint8_t dollar = '$';
				    uint8_t percent = '%';
				    uint8_t caret = '^';
				    uint8_t amp = '&';
				    uint8_t star = '*';
				    return 0;
				}""";
		
		TestUtil.assertCompiles(javaCode, expectedCode, "C code should match expected output for special characters");
	}
	
	/**
	 * Test that the compiler can handle numeric characters in char literals.
	 * Tests the support for digit characters.
	 */
	@Test
	public void testNumericCharacters() {
		// Arrange
		String javaCode = """
				let zero : U8 = '0';
				let one : U8 = '1';
				let five : U8 = '5';
				let nine : U8 = '9';""";

		// Assert
		String expectedCode = """
				#include <stdint.h>

				int main() {
				    uint8_t zero = '0';
				    uint8_t one = '1';
				    uint8_t five = '5';
				    uint8_t nine = '9';
				    return 0;
				}""";
		
		TestUtil.assertCompiles(javaCode, expectedCode, "C code should match expected output for numeric characters");
	}
	
	/**
	 * Test that the compiler can handle boundary values in char literals.
	 * Tests the support for characters at the boundaries of the ASCII range.
	 */
	@Test
	public void testBoundaryValues() {
		// Arrange
		// Note: Some of these might not be directly representable in source code
		// and would need to be handled specially by the compiler
		String javaCode = """
				let space : U8 = ' ';      // ASCII 32 (lowest printable)
				let tilde : U8 = '~';      // ASCII 126 (highest printable)""";

		// Assert
		String expectedCode = """
				#include <stdint.h>

				int main() {
				    uint8_t space = ' ';
				    uint8_t tilde = '~';
				    return 0;
				}""";
		
		TestUtil.assertCompiles(javaCode, expectedCode, "C code should match expected output for boundary values");
	}
}