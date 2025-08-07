import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for type inference in the Magma compiler.
 * Tests that the compiler can infer types from values with type suffixes.
 * Includes tests for edge cases, boundary values, and different literal types to ensure robustness.
 */
public class TypeInferenceTest {

	/**
	 * Test that the compiler can infer types from values with type suffixes.
	 * This tests the support for inferring types from values like "100U64" in our Java to C compiler.
	 */
	@Test
	public void testInferTypesFromValues() {
		// Arrange
		String javaCode = """
				let a = 100I8;
				let b = 200I16;
				let c = 300I32;
				let d = 400I64;
				let e = 500U8;
				let f = 600U16;
				let g = 700U32;
				let h = 800U64;
				let i = 900; // No suffix, should default to I32""";

		// Act
		String cCode = Main.compile(javaCode);

		// Assert
		String expectedCode = """
				#include <stdint.h>

				int main() {
				    int8_t a = 100;
				    int16_t b = 200;
				    int32_t c = 300;
				    int64_t d = 400;
				    uint8_t e = 500;
				    uint16_t f = 600;
				    uint32_t g = 700;
				    uint64_t h = 800;
				    int32_t i = 900;
				    return 0;
				}""";
		
		assertEquals(expectedCode, cCode, "C code should match expected output");
	}
	
	/**
	 * Test that the compiler can infer types from boundary values with type suffixes.
	 * This tests the support for inferring types from min/max values for different integer types.
	 */
	@Test
	public void testInferTypesFromBoundaryValues() {
		// Arrange
		String javaCode = """
				let minI8 = -128I8;
				let maxI8 = 127I8;
				let minU8 = 0U8;
				let maxU8 = 255U8;
				let minI16 = -32768I16;
				let maxI16 = 32767I16;
				let minU16 = 0U16;
				let maxU16 = 65535U16;""";

		// Act
		String cCode = Main.compile(javaCode);

		// Assert
		String expectedCode = """
				#include <stdint.h>

				int main() {
				    int8_t minI8 = -128;
				    int8_t maxI8 = 127;
				    uint8_t minU8 = 0;
				    uint8_t maxU8 = 255;
				    int16_t minI16 = -32768;
				    int16_t maxI16 = 32767;
				    uint16_t minU16 = 0;
				    uint16_t maxU16 = 65535;
				    return 0;
				}""";
		
		assertEquals(expectedCode, cCode, "C code should match expected output for boundary values");
	}
	
	/**
	 * Test that the compiler can infer Bool type from boolean literals.
	 * This tests the support for inferring types from true/false values.
	 */
	@Test
	public void testInferBoolTypeFromLiterals() {
		// Arrange
		String javaCode = """
				let a = true;
				let b = false;""";

		// Act
		String cCode = Main.compile(javaCode);

		// Assert
		String expectedCode = """
				#include <stdint.h>
				#include <stdbool.h>

				int main() {
				    bool a = true;
				    bool b = false;
				    return 0;
				}""";
		
		assertEquals(expectedCode, cCode, "C code should match expected output for boolean literals");
	}
	
	/**
	 * Test that the compiler can infer U8 type from character literals.
	 * This tests the support for inferring types from values in single quotes.
	 */
	@Test
	public void testInferU8TypeFromCharLiterals() {
		// Arrange
		String javaCode = """
				let a = 'a';
				let b = '\\n';
				let c = '!';
				let d = '0';""";

		// Act
		String cCode = Main.compile(javaCode);

		// Assert
		String expectedCode = """
				#include <stdint.h>

				int main() {
				    uint8_t a = 'a';
				    uint8_t b = '\\n';
				    uint8_t c = '!';
				    uint8_t d = '0';
				    return 0;
				}""";
		
		assertEquals(expectedCode, cCode, "C code should match expected output for character literals");
	}
	
	/**
	 * Test that the compiler can handle mixed type declarations in a single program.
	 * This tests the support for inferring different types in the same program.
	 */
	@Test
	public void testMixedTypeDeclarations() {
		// Arrange
		String javaCode = """
				let a = 100I8;
				let b = true;
				let c = 'x';
				let d = 42;""";

		// Act
		String cCode = Main.compile(javaCode);

		// Assert
		String expectedCode = """
				#include <stdint.h>
				#include <stdbool.h>

				int main() {
				    int8_t a = 100;
				    bool b = true;
				    uint8_t c = 'x';
				    int32_t d = 42;
				    return 0;
				}""";
		
		assertEquals(expectedCode, cCode, "C code should match expected output for mixed type declarations");
	}
}