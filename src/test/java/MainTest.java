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

	/**
	 * Test that the compiler can handle multiple declarations in a single line.
	 * This tests the support for multiple declarations separated by semicolons.
	 */
	@Test
	public void testCompileMultipleDeclarations() {
		// Arrange
		String magmaCode = """
				let x = 100; let y = x;
				let a : I32 = 42; let b : I32 = 84;
				let c = 10; let d : I32 = c; let e = d;""";

		// Act
		String cCode = Main.compile(magmaCode);

		// Assert
		String expectedCode = """
				#include <stdint.h>
				
				int main() {
				    int32_t x = 100;
				    int32_t y = x;
				    int32_t a = 42;
				    int32_t b = 84;
				    int32_t c = 10;
				    int32_t d = c;
				    int32_t e = d;
				    return 0;
				}""";

		assertEquals(expectedCode, cCode, "C code should match expected output");
	}

	/**
	 * Test that the compiler can generate C code for simple assignments.
	 * This tests the support for assigning new values to existing variables.
	 */
	@Test
	public void testCompileSimpleAssignments() {
		// Arrange
		String magmaCode = """
				let x = 100;
				x = 200;""";

		// Act
		String cCode = Main.compile(magmaCode);

		// Assert
		String expectedCode = """
				#include <stdint.h>
				
				int main() {
				    int32_t x = 100;
				    x = 200;
				    return 0;
				}""";

		assertEquals(expectedCode, cCode, "C code should match expected output");
	}

	/**
	 * Test that the compiler can generate C code for assignments with different data types.
	 * This tests the support for assigning values to variables of different types.
	 */
	@Test
	public void testCompileAssignmentsWithDifferentTypes() {
		// Arrange
		String magmaCode = """
				let a : I8 = 10;
				let b : I16 = 20;
				let c : I32 = 30;
				let d : I64 = 40;
				let e : U8 = 50;
				let f : U16 = 60;
				let g : U32 = 70;
				let h : U64 = 80;
				let i : Bool = true;
				
				a = -5;
				b = -15;
				c = -25;
				d = -35;
				e = 45;
				f = 55;
				g = 65;
				h = 75;
				i = false;""";

		// Act
		String cCode = Main.compile(magmaCode);

		// Assert
		String expectedCode = """
				#include <stdint.h>
				#include <stdbool.h>
				
				int main() {
				    int8_t a = 10;
				    int16_t b = 20;
				    int32_t c = 30;
				    int64_t d = 40;
				    uint8_t e = 50;
				    uint16_t f = 60;
				    uint32_t g = 70;
				    uint64_t h = 80;
				    bool i = true;
				    a = -5;
				    b = -15;
				    c = -25;
				    d = -35;
				    e = 45;
				    f = 55;
				    g = 65;
				    h = 75;
				    i = false;
				    return 0;
				}""";

		assertEquals(expectedCode, cCode, "C code should match expected output");
	}

	/**
	 * Test that the compiler can generate C code for assignments with variables.
	 * This tests the support for assigning values from one variable to another.
	 */
	@Test
	public void testCompileAssignmentsWithVariables() {
		// Arrange
		String magmaCode = """
				let x = 100;
				let y = 200;
				let z : I32 = 300;
				
				x = y;
				y = z;
				z = x;""";

		// Act
		String cCode = Main.compile(magmaCode);

		// Assert
		String expectedCode = """
				#include <stdint.h>
				
				int main() {
				    int32_t x = 100;
				    int32_t y = 200;
				    int32_t z = 300;
				    x = y;
				    y = z;
				    z = x;
				    return 0;
				}""";

		assertEquals(expectedCode, cCode, "C code should match expected output");
	}

	/**
	 * Test that the compiler can generate C code for multiple assignments in a single line.
	 * This tests the support for multiple assignments separated by semicolons.
	 */
	@Test
	public void testCompileMultipleAssignmentsInSingleLine() {
		// Arrange
		String magmaCode = """
				let x = 100;
				let y = 200;
				let z = 300;
				
				x = 400; y = 500; z = 600;""";

		// Act
		String cCode = Main.compile(magmaCode);

		// Assert
		String expectedCode = """
				#include <stdint.h>
				
				int main() {
				    int32_t x = 100;
				    int32_t y = 200;
				    int32_t z = 300;
				    x = 400;
				    y = 500;
				    z = 600;
				    return 0;
				}""";

		assertEquals(expectedCode, cCode, "C code should match expected output");
	}

	/**
	 * Test that the compiler can generate C code for assignments with type suffixes.
	 * This tests the support for assigning values with explicit type suffixes.
	 */
	@Test
	public void testCompileAssignmentsWithTypeSuffixes() {
		// Arrange
		String magmaCode = """
				let a : I8 = 10;
				let b : I16 = 20;
				let c : I32 = 30;
				let d : I64 = 40;
				let e : U8 = 50;
				let f : U16 = 60;
				let g : U32 = 70;
				let h : U64 = 80;
				
				a = -5I8;
				b = -15I16;
				c = -25I32;
				d = -35I64;
				e = 45U8;
				f = 55U16;
				g = 65U32;
				h = 75U64;""";

		// Act
		String cCode = Main.compile(magmaCode);

		// Assert
		String expectedCode = """
				#include <stdint.h>
				
				int main() {
				    int8_t a = 10;
				    int16_t b = 20;
				    int32_t c = 30;
				    int64_t d = 40;
				    uint8_t e = 50;
				    uint16_t f = 60;
				    uint32_t g = 70;
				    uint64_t h = 80;
				    a = -5;
				    b = -15;
				    c = -25;
				    d = -35;
				    e = 45;
				    f = 55;
				    g = 65;
				    h = 75;
				    return 0;
				}""";

		assertEquals(expectedCode, cCode, "C code should match expected output");
	}

	/**
	 * Test that the compiler can generate C code for assignments with mixed types.
	 * This tests the support for assigning values between variables of different types.
	 */
	@Test
	public void testCompileAssignmentsWithMixedTypes() {
		// Arrange
		String magmaCode = """
				let a : I32 = 10;
				let b : I64 = 20;
				let c : U32 = 30;
				let d : Bool = true;
				
				a = 100;
				b = a;      // I32 to I64
				c = 200U32;
				a = c;      // U32 to I32
				d = false;""";

		// Act
		String cCode = Main.compile(magmaCode);

		// Assert
		String expectedCode = """
				#include <stdint.h>
				#include <stdbool.h>
				
				int main() {
				    int32_t a = 10;
				    int64_t b = 20;
				    uint32_t c = 30;
				    bool d = true;
				    a = 100;
				    b = a;
				    c = 200;
				    a = c;
				    d = false;
				    return 0;
				}""";

		assertEquals(expectedCode, cCode, "C code should match expected output");
	}

	/**
	 * Test that the compiler can generate C code for chained assignments.
	 * This tests the support for assigning the same value to multiple variables.
	 */
	@Test
	public void testCompileChainedAssignments() {
		// Arrange
		String magmaCode = """
				let x = 100;
				let y = 200;
				let z = 300;
				
				x = 400;
				y = x;
				z = y;""";

		// Act
		String cCode = Main.compile(magmaCode);

		// Assert
		String expectedCode = """
				#include <stdint.h>
				
				int main() {
				    int32_t x = 100;
				    int32_t y = 200;
				    int32_t z = 300;
				    x = 400;
				    y = x;
				    z = y;
				    return 0;
				}""";

		assertEquals(expectedCode, cCode, "C code should match expected output");
	}
}