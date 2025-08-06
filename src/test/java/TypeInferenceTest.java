import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test class for type inference in the Magma compiler.
 * Tests that the compiler can infer types from values with type suffixes.
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
		assertNotNull(cCode, "Compiled C code should not be null");
		
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
}