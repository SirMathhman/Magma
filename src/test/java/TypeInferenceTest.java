import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
		assertTrue(cCode.contains("#include <stdint.h>"), "C code should include stdint.h");
		assertTrue(cCode.contains("int main("), "C code should have a main function");
		assertTrue(cCode.contains("int8_t a = 100;"), "C code should infer I8 type from 100I8");
		assertTrue(cCode.contains("int16_t b = 200;"), "C code should infer I16 type from 200I16");
		assertTrue(cCode.contains("int32_t c = 300;"), "C code should infer I32 type from 300I32");
		assertTrue(cCode.contains("int64_t d = 400;"), "C code should infer I64 type from 400I64");
		assertTrue(cCode.contains("uint8_t e = 500;"), "C code should infer U8 type from 500U8");
		assertTrue(cCode.contains("uint16_t f = 600;"), "C code should infer U16 type from 600U16");
		assertTrue(cCode.contains("uint32_t g = 700;"), "C code should infer U32 type from 700U32");
		assertTrue(cCode.contains("uint64_t h = 800;"), "C code should infer U64 type from 800U64");
		assertTrue(cCode.contains("int32_t i = 900;"), "C code should default to I32 for 900 (no suffix)");
		assertTrue(cCode.contains("return 0;"), "C code should return 0");
	}
}