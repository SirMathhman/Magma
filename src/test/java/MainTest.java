import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
		assertNotNull(cCode, "Compiled C code should not be null");
		assertTrue(cCode.contains("int main("), "C code should have a main function");
		assertTrue(cCode.contains("int32_t x = 0;"), "C code should declare and initialize x");
		assertTrue(cCode.contains("int32_t y = 42;"), "C code should declare and initialize y");
		assertTrue(cCode.contains("int32_t z = 100;"), "C code should declare and initialize z");
		assertTrue(cCode.contains("return 0;"), "C code should return 0");
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
		assertNotNull(cCode, "Compiled C code should not be null");
		assertTrue(cCode.contains("#include <stdint.h>"), "C code should include stdint.h");
		assertTrue(cCode.contains("int main("), "C code should have a main function");
		assertTrue(cCode.contains("int8_t a = -8;"), "C code should declare and initialize I8 variable");
		assertTrue(cCode.contains("int16_t b = -16;"), "C code should declare and initialize I16 variable");
		assertTrue(cCode.contains("int32_t c = -32;"), "C code should declare and initialize I32 variable");
		assertTrue(cCode.contains("int64_t d = -64;"), "C code should declare and initialize I64 variable");
		assertTrue(cCode.contains("uint8_t e = 8;"), "C code should declare and initialize U8 variable");
		assertTrue(cCode.contains("uint16_t f = 16;"), "C code should declare and initialize U16 variable");
		assertTrue(cCode.contains("uint32_t g = 32;"), "C code should declare and initialize U32 variable");
		assertTrue(cCode.contains("uint64_t h = 64;"), "C code should declare and initialize U64 variable");
		assertTrue(cCode.contains("return 0;"), "C code should return 0");
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
		assertNotNull(cCode, "Compiled C code should not be null");
		assertTrue(cCode.contains("#include <stdint.h>"), "C code should include stdint.h");
		assertTrue(cCode.contains("int main("), "C code should have a main function");
		assertTrue(cCode.contains("int32_t x = 0;"), "C code should declare and initialize x with default I32 type");
		assertTrue(cCode.contains("int32_t y = 42;"), "C code should declare and initialize y with default I32 type");
		assertTrue(cCode.contains("int32_t z = 100;"), "C code should declare and initialize z with default I32 type");
		assertTrue(cCode.contains("return 0;"), "C code should return 0");
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
		assertNotNull(cCode, "Compiled C code should not be null");
		assertTrue(cCode.contains("#include <stdbool.h>"), "C code should include stdbool.h");
		assertTrue(cCode.contains("int main("), "C code should have a main function");
		assertTrue(cCode.contains("bool a = true;"), "C code should declare and initialize a Bool variable with true");
		assertTrue(cCode.contains("bool b = false;"), "C code should declare and initialize a Bool variable with false");
		assertTrue(cCode.contains("return 0;"), "C code should return 0");
	}
}