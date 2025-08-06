import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test class for array handling in the Magma compiler.
 * Tests the compilation of Magma arrays to C arrays.
 */
public class ArrayTest {
	/**
	 * Test that the compiler can generate C code for a simple array declaration with the new syntax.
	 * Tests array declaration and initialization with the syntax: let myArray : [U8, 3] = [1, 2, 3];
	 */
	@Test
	public void testCompileArrayDeclaration() {
		// Arrange
		String javaCode = "let myArray : [U8, 3] = [1, 2, 3];";
		// Act
		String cCode = Main.compile(javaCode);

		// Assert
		assertNotNull(cCode, "Compiled C code should not be null");
		
		String expectedCode = """
				#include <stdint.h>

				int main() {
				    uint8_t myArray[3] = {1, 2, 3};
				    return 0;
				}""";
		
		assertEquals(expectedCode, cCode, "C code should match expected output");
	}

	/**
	 * Test that the compiler can generate C code for arrays of different types and sizes.
	 */
	@Test
	public void testCompileArraysOfDifferentTypes() {
		// Arrange
		String javaCode = """
				let byteArray : [U8, 4] = [10, 20, 30, 40];
				let intArray : [I32, 2] = [100, 200];
				let boolArray : [Bool, 3] = [true, false, true];""";

		// Act
		String cCode = Main.compile(javaCode);

		// Assert
		assertNotNull(cCode, "Compiled C code should not be null");
		
		String expectedCode = """
				#include <stdint.h>
				#include <stdbool.h>

				int main() {
				    uint8_t byteArray[4] = {10, 20, 30, 40};
				    int32_t intArray[2] = {100, 200};
				    bool boolArray[3] = {true, false, true};
				    return 0;
				}""";
		
		assertEquals(expectedCode, cCode, "C code should match expected output");
	}
	
	/**
	 * Test that the compiler can generate C code for a 2D array declaration.
	 * Tests array declaration and initialization with the syntax: let matrix : [I32, 2, 3] = [[1, 2, 3], [4, 5, 6]];
	 */
	@Test
	public void testCompile2DArrayDeclaration() {
		// Arrange
		String javaCode = "let matrix : [I32, 2, 3] = [[1, 2, 3], [4, 5, 6]];";
		
		// Act
		String cCode = Main.compile(javaCode);
		
		// Debug output
		System.out.println("Generated C code for 2D array:");
		System.out.println(cCode);
		
		// Assert
		assertNotNull(cCode, "Compiled C code should not be null");
		
		String expectedCode = """
				#include <stdint.h>

				int main() {
				    int32_t matrix[2][3] = {{1, 2, 3}, {4, 5, 6}};
				    return 0;
				}""";
		
		assertEquals(expectedCode, cCode, "C code should match expected output");
	}
	
	/**
	 * Test that the compiler can generate C code for a 3D array declaration.
	 * Tests array declaration and initialization with the syntax: 
	 * let cube : [I32, 2, 2, 2] = [[[1, 2], [3, 4]], [[5, 6], [7, 8]]];
	 */
	@Test
	public void testCompile3DArrayDeclaration() {
		// Arrange
		String javaCode = "let cube : [I32, 2, 2, 2] = [[[1, 2], [3, 4]], [[5, 6], [7, 8]]];";
		
		// Act
		String cCode = Main.compile(javaCode);
		
		// Assert
		assertNotNull(cCode, "Compiled C code should not be null");
		
		String expectedCode = """
				#include <stdint.h>

				int main() {
				    int32_t cube[2][2][2] = {{{1, 2}, {3, 4}}, {{5, 6}, {7, 8}}};
				    return 0;
				}""";
		
		assertEquals(expectedCode, cCode, "C code should match expected output");
	}
}