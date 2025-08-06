import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for the Main compiler class.
 * Tests the basic functionality of compiling Magma to C.
 */
public class MainTest {
	/**
	 * Test that the compiler can generate C code for variable declarations.
	 * This tests the support for basic variable declarations in our Magma to C compiler.
	 */
	@Test
	public void testCompileVariableDeclaration() {
		// Arrange
		String magmaCode = """
				let x : I32 = 0;
				let y : I32 = 42;
				let z : I32 = 100;""";

		// Act
		String cCode = Main.compile(magmaCode);

		// Assert
		String expectedCode = """
				#include <stdint.h>

				int main() {
				    int32_t x = 0;
				    int32_t y = 42;
				    int32_t z = 100;
				    return 0;
				}""";
		
		assertEquals(expectedCode, cCode, "C code should match expected output");
	}

	/**
	 * Test that the compiler can generate C code for all integer types.
	 * This tests the support for I8, I16, I32, I64, U8, U16, U32, and U64 types.
	 */
	@Test
	public void testCompileAllIntegerTypes() {
		// Arrange
		String magmaCode = """
				let a : I8 = -8;
				let b : I16 = -16;
				let c : I32 = -32;
				let d : I64 = -64;
				let e : U8 = 8;
				let f : U16 = 16;
				let g : U32 = 32;
				let h : U64 = 64;""";

		// Act
		String cCode = Main.compile(magmaCode);

		// Assert
		String expectedCode = """
				#include <stdint.h>

				int main() {
				    int8_t a = -8;
				    int16_t b = -16;
				    int32_t c = -32;
				    int64_t d = -64;
				    uint8_t e = 8;
				    uint16_t f = 16;
				    uint32_t g = 32;
				    uint64_t h = 64;
				    return 0;
				}""";
		
		assertEquals(expectedCode, cCode, "C code should match expected output");
	}

	/**
	 * Test that the compiler can generate C code for variable declarations without explicit types.
	 * This tests the support for typeless declarations where the type is inferred (defaulting to I32 for numbers).
	 */
	@Test
	public void testCompileTypelessDeclarations() {
		// Arrange
		String magmaCode = """
				let x = 0;
				let y = 42;
				let z = 100;""";

		// Act
		String cCode = Main.compile(magmaCode);

		// Assert
		String expectedCode = """
				#include <stdint.h>

				int main() {
				    int32_t x = 0;
				    int32_t y = 42;
				    int32_t z = 100;
				    return 0;
				}""";
		
		assertEquals(expectedCode, cCode, "C code should match expected output");
	}

	/**
	 * Test that the compiler can generate C code for Boolean type declarations.
	 * This tests the support for Bool type with true and false values.
	 */
	@Test
	public void testCompileBooleanType() {
		// Arrange
		String javaCode = """
				let a : Bool = true;
				let b : Bool = false;""";

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
		
		assertEquals(expectedCode, cCode, "C code should match expected output");
	}
}